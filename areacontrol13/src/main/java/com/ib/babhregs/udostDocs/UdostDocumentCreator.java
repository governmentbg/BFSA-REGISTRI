package com.ib.babhregs.udostDocs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.aspose.words.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.ShablonLogicDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.db.dto.ShablonBookmark;
import com.ib.babhregs.db.dto.ShablonBookmark.BookmarkTypes;
import com.ib.babhregs.db.dto.ShablonBookmark.FillStrategies;
import com.ib.babhregs.db.dto.ShablonLogic;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.DbErrorException;

/**
 * Клас, който служи за попълване на уърдовски шаблони на удостоверителни документи.
 * <br/>
 * След като се създаде обектът, автоматично зарежда наличните шаблони. Нужно е да
 * се провери предварително дали има повече от един ({@link #needsToChooseShablon()})
 * и да се сетне ({@link #setChosenShablon(Files)}).
 * </br>
 * След това се вика {@link #gatherData()}, за да се заредят текстовите стойности.
 * Всички стойности се запазват в мапа {@link #getValues()}, а празните са в
 * {@link #getNullValues()}.
 * </br>
 * За да се създаде попълнен шаблон, се вика {@link #fillBookmarksInShablon()}.
 * </br>
 * Шаблонът може да се върне като html стринг с {@link #getDocumentAsHtmlString()}
 * след като е попълнен.
 * </br>
 * Записът на попълнения шаблон като файл в базата става с {@link #saveFilledShablon}.
 *  След запис файлът може да бъде достъпен с {@link #getSavedUdostDokument()}. 
 * 
 * @author n.kanev
 */
public class UdostDocumentCreator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UdostDocumentCreator.class);
	private static final String EMPTY_VALUE = "";
	private static final String DUBLIKAT_TEKST = "ДУБЛИКАТ";
	
	public static final String LIST_DELIMITER = "<._.>";
	public static final String TABLE_ROW_DELIMITER = "<o_O>";
	public static final String TREE_ATTRIBUTES_DELIMITER = "<*_*>";
	public static final String DONT_CHANGE_BOOKMARK_FLAG = "<x_x>";

	private static final String WHITE_BULLET = "\u25e6"; // ◦
	private static final String BLACK_BULLET = "\u2022"; // •

	// Данни, които се подават отвън при инициализиране
	private final SystemData sd;
	private final UserData ud;
	private final Doc doc;
	private final boolean dublikat;
	
	// private данни, които се създават тук
	private DocDAO docDao;
	private List<Files> shabloni;
	private Files chosenShablon;
	private ShablonLogic shablonLogic;
	private List<ShablonBookmark> bookmarks;
	private Document shablonDocument;
	private Map<String, String> values;
	private Set<String> nullValues;
	private Files savedUdostDokument;
	private final UdostDokumentMethods methodsObject;
	private final AdvancedUdostMethods advMethodsObject;
	private final DefaultValueMethods defaultMethodsObject;
	private final CustomFillMethods customMethodsObject;
	private Map<String, Object> advancedBookmarkValues;
	
	public UdostDocumentCreator(SystemData sd, UserData ud, Vpisvane vpisvane, Doc doc, boolean dublikat) 
			throws MissingSystemSettingException, DbErrorException, NoShablonException {
		this.sd = sd;
		this.ud = ud;
		this.doc = doc;
		this.dublikat = dublikat;
		this.methodsObject = new UdostDokumentMethods(vpisvane, doc, ud, sd);
		this.advMethodsObject = new AdvancedUdostMethods(vpisvane, doc, ud, sd);
		this.defaultMethodsObject = new DefaultValueMethods(vpisvane, doc, ud, sd);
		this.customMethodsObject = new CustomFillMethods(vpisvane, doc, ud, sd);
		initialize();
	}
	
	/**
	 * Намира шаблоните към дадения документ. 
	 * Ако е един, го зарежда в {@link #chosenShablon}.
	 * Ако са повече от един, {@link #needsToChooseShablon()} връща true
	 * и трябва да се извика методът {@link #setChosenShablon(Files)}.
	 * Наличните шаблони могат да се вземат с {@link #getShabloni()}.
	 * 
	 * @throws MissingSystemSettingException
	 * @throws DbErrorException
	 * @throws NoShablonException
	 */
	private void initialize() throws MissingSystemSettingException, DbErrorException, NoShablonException {
		this.docDao = new DocDAO(this.ud);
		
		Object[] settings = docDao.findDocSettings(
				this.ud.getRegistratura(),
				doc.getDocVid(),
				this.sd);
		
		if (settings == null || settings[4] == null) {
			LOGGER.error("Към документа липсва системна настройка FILE_OBJECTS.OBJECT_ID; docVid=" + doc.getDocVid());
			throw new MissingSystemSettingException("Към документа липсва системна настройка FILE_OBJECTS.OBJECT_ID");
		}
		Integer codeSetting = (Integer) settings[4];
		
		FilesDAO filesDao = new FilesDAO(this.ud);		
		this.shabloni = filesDao.selectByFileObjectDop(codeSetting, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC_VID_SETT);
		
		if(this.shabloni == null || this.shabloni.isEmpty()) {
			LOGGER.error("Към документа няма въведени шаблони; objectId: " + codeSetting
					+ "; objectCode: " + BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC_VID_SETT);
			throw new NoShablonException("Към документа няма въведени шаблони");
		}
		else if(this.shabloni.size() == 1) {
			this.chosenShablon = this.shabloni.get(0);
		}
	}
	
	/**
	 * Събира данните за шаблона и ги запазва във {@link #values}.
	 * Ако стойност липсва, името на съответния букмарк отива в {@link #nullValues}.
	 * 
	 * @throws NoSuchMethodException в шаблона е зададен метод, който липсва в класа {@link UdostDokumentMethods}
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException грешка при извикване на метод от {@link UdostDokumentMethods}
	 * @throws InvocationTargetException
	 * @throws DbErrorException
	 */
	public void gatherData() 
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DbErrorException {
		if(this.chosenShablon == null) {
			LOGGER.error("Документът има повече от един налични шаблона и не е извикан методът setChosenShablon, за да се избере кой да се попълни");
			throw new NoChosenShablonException("Документът има повече от един налични шаблона и не е извикан методът setChosenShablon, за да се избере кой да се попълни");
		}
		
		ShablonLogicDAO shablonLogicDao = new ShablonLogicDAO(ud);
		this.shablonLogic = shablonLogicDao.findByFileId(this.chosenShablon.getId());
		
		if(this.shablonLogic == null) {
			throw new MissingBookmarkLogicException("Не е настроена логика на попълване на шаблона");
		}
		this.bookmarks = this.shablonLogic.getBookmarks();
		
		this.values = new HashMap<>();
		this.nullValues = new HashSet<>();
		this.advancedBookmarkValues = new HashMap<>();

		// Зареждане на данните за документа
		// Нормалните букмаркове се попълват директно, но ADVANCED се слагат в мапа advancedBookmarksValues
		// и ще се покажат на екрана, за да им се прикачат компоненти за попълване.
		for(ShablonBookmark bookmark : this.bookmarks) {
			try {
				if(bookmark.getFillStrategy() == ShablonBookmark.FillStrategies.NORMAL) {
					Method fillerMethod = UdostDokumentMethods.class.getMethod(bookmark.getMethodName());
					Object returnValue = fillerMethod.invoke(this.methodsObject);
					if (returnValue != null) {
						String result = (String) returnValue;
						this.values.put(bookmark.getLabel(), result);
					}
					else {
						this.nullValues.add(bookmark.getLabel());
					}
				}

				else if(bookmark.getFillStrategy() == ShablonBookmark.FillStrategies.ADVANCED) {
					if(bookmark.getDefaultValueMethodName() != null) {
						Method fillerMethod = DefaultValueMethods.class.getMethod(bookmark.getDefaultValueMethodName());
						Object returnValue = fillerMethod.invoke(this.defaultMethodsObject);
						this.advancedBookmarkValues.put(bookmark.getLabel(), returnValue);
						this.fillAdvancedBookmark(bookmark, returnValue);
					}
					else {
						this.advancedBookmarkValues.put(bookmark.getLabel(), null);
					}
				}

				else if(bookmark.getFillStrategy() == ShablonBookmark.FillStrategies.PASSIVE) {
					// TODO ?
				}
			}
			catch (NoSuchMethodException e) {
				LOGGER.error("В класа UdostDokumentMethods не съществува метод " + bookmark.getMethodName());
				throw e;
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("Грешка при извикване на метод UdostDokumentMethods." + bookmark.getMethodName());
				throw e;
			}
		}
	}

	// TODO tova da se vika kato e gotova redakciata i se klikne butona
	public void gatherAdvancedBookmarksData() {
		this.advancedBookmarkValues.forEach((key, value) -> {
			ShablonBookmark bookmark = this.bookmarks.stream().filter(b -> b.getLabel().equals(key)).findFirst().orElse(null);
			fillAdvancedBookmark(bookmark, value);
		});
	}
	
	private void fillAdvancedBookmark(ShablonBookmark bookmark, Object value) {
		try {
			Method fillerMethod = AdvancedUdostMethods.class.getMethod(bookmark.getMethodName(), Object.class, ShablonBookmark.class);
			Object returnValues = fillerMethod.invoke(this.advMethodsObject, value, bookmark);
			if (returnValues != null) {
				Map<String, String> map = (Map<String, String>) returnValues;
				map.forEach((key2, value2) -> {
					if(value2 == null) {
						this.nullValues.add(key2);
						this.values.remove(key2);
					}
					else {
						this.values.put(key2, value2);
						this.nullValues.remove(key2);
					}
				});
			}
		}
		catch (NoSuchMethodException e) {
			LOGGER.error("В класа AdvancedUdostMethods не съществува метод " + bookmark.getMethodName(), e);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Грешка при извикване на метод AdvancedUdostMethods." + bookmark.getMethodName(), e);
		}
	}
	
	/**
	 * Записва данните от {@link #values} в уърдовския файл.
	 * Преди да се извика методът, трябва да е извикан {@link #gatherData()}.
	 * @throws Exception ако има грешка с Aspose
	 */
	public void fillBookmarksInShablon() throws Exception {
		
		// за да хвърли грешка, ако не е бил викнат преди това gatherData()
		getValues();
		
		License license = new License();
		String nameLicense = "Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		
		Files shablonWithContent = new FilesDAO(ud).findById(this.chosenShablon.getId());
		InputStream input = new ByteArrayInputStream(shablonWithContent.getContent());
		this.shablonDocument = new Document(input);
		
		// тези букмаркове са били вградени в друг по-голям букмарк, който е бил изтрит
		// сега са null и трябва да ги пропуснем
		Set<String> killedBookmarks = getKilledBookmarks();

		for(ShablonBookmark bookmark : this.bookmarks) {
			String bookmarkLabel = bookmark.getLabel();
			
			if(killedBookmarks.contains(bookmarkLabel)) {
				continue;
			}

			Bookmark b = this.shablonDocument.getRange().getBookmarks().get(bookmarkLabel);
			if(b != null) {
				if(bookmark.getFillStrategy() == FillStrategies.CUSTOM_IMPL) {
					Method fillerMethod = CustomFillMethods.class.getMethod(bookmark.getMethodName(), Document.class, ShablonBookmark.class);
					fillerMethod.invoke(this.customMethodsObject, this.shablonDocument, bookmark);
				}
				else if(!this.values.containsKey(bookmarkLabel)) {
					b.setText(EMPTY_VALUE);
				}
				else {
					if (bookmark.getBookmarkType() != null) {
						switch (bookmark.getBookmarkType()) {
							case STRING: {
								this.fillStringBookmark(b, bookmarkLabel);
								break;
							}
							case LIST: {
								this.fillListBookmark(bookmarkLabel);
								break;
							}
							case TABLE: {
								this.fillTableBookmark(b, bookmarkLabel);
								break;
							}
							case TREE: {
								this.fillTreeBookmark(bookmarkLabel);
								break;
							}
							case KILLER: {
								if (!this.values.get(bookmarkLabel).equals(DONT_CHANGE_BOOKMARK_FLAG)) {
									b.setText("");
								}
							}
							default:
								break;
						}
					}
					else {
						this.fillStringBookmark(b, bookmarkLabel);
					}
				}
			}
			else {
				LOGGER.error("Документът не съдържа bookmark с името " + bookmarkLabel);
				throw new WrongBookmarkNameException("Документът не съдържа bookmark с името " + bookmarkLabel);
			}
		}
		
		if(this.dublikat) {
			Bookmark b = this.shablonDocument.getRange().getBookmarks().get(ShablonLogic.DUBLIKAT_BM_LABEL);
			if(b != null) {
				b.setText(DUBLIKAT_TEKST);
			}
		}
	}
	
	/**
	 * Връща съдържанието на шаблона като html, за да се покаже на екрана.
	 * Трябва да се извика след като се попълнят данните.
	 * @return html String
	 * @throws Exception
	 */
	public String getDocumentAsHtmlString() throws Exception {
		String html = "";
		
		try(ByteArrayOutputStream htmlStream = new ByteArrayOutputStream()) {
			HtmlSaveOptions saveOptions = new HtmlSaveOptions(SaveFormat.HTML);
			saveOptions.setPrettyFormat(true);
			saveOptions.setExportOriginalUrlForLinkedImages(true);
			saveOptions.setExportRoundtripInformation(false);
			saveOptions.setExportFontResources(false);
			saveOptions.setExportImagesAsBase64(true);
			
			this.shablonDocument.save(htmlStream, saveOptions);
			html = htmlStream.toString();
		}
		catch(NullPointerException e) {
			throw new ValuesNotGeneratedException("Не са извикани методите gatherData и fillBookmarksInShablon");
		}
		catch (Exception e) {
			LOGGER.error("Грешка при запис на HTML");
			throw e;
		}
		
		return html;
	}
	
	/**
	 * Записва шаблона в таблицата Files. Може да се вземе оттук с {@link #getSavedUdostDokument()}.
	 * 
	 * @throws Exception
	 */
	public void saveFilledShablon(Integer idRegister) throws Exception {
		if(this.savedUdostDokument != null) {
			throw new UdostDocAlreadySavedException("Удостоверителният документ вече е записан с метода saveFilledShablon");
		}
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
		
		try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			this.shablonDocument.save(stream, SaveFormat.DOCX);
			
			String docType = this.sd.decodeItem(
					BabhConstants.CODE_CLASSIF_DOC_VID,
					this.doc.getDocVid(),
					this.ud.getCurrentLang(),
					date);
			
			Files f = new Files();
			f.setContentType(chosenShablon.getContentType());
			f.setContent(stream.toByteArray());
			f.setFilename(String.format("%s_%s.docx", docType, sdf.format(date)));
			f.setOfficial(BabhConstants.CODE_ZNACHENIE_DA);
			f.setFileInfo(docType);
			f.setFileType(BabhConstants.CODE_ZNACHENIE_DOC_VID_ATTACH_UD);
			
			JPA.getUtil().runInTransaction(() -> 
				this.savedUdostDokument = new FilesDAO(this.ud)
				.saveFileObject(
					f, 
					this.doc.getId(),
					BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC));
		}
		catch (Exception e) {
			LOGGER.error("Грешка при запис на удостоверителен документ", e);
			throw e;
		}
	}
	
	private void fillStringBookmark(Bookmark b, String bookmarkLabel) throws Exception {
		b.setText(this.values.get(bookmarkLabel));
	}
	
	private void fillListBookmark(String bookmarkLabel) throws Exception {
		String[] values = this.values.get(bookmarkLabel).split(LIST_DELIMITER);
		DocumentBuilder builder = new DocumentBuilder(this.shablonDocument);
		builder.moveToBookmark(bookmarkLabel);
		builder.getListFormat().setList(this.shablonDocument.getLists().add(ListTemplate.NUMBER_ARABIC_DOT));
		
		for(int i = 0; i < values.length; i++) {
			String string = values[i];
			if(string != null) {
				builder.write(string.toString().trim());
				if(i != values.length - 1) {
					builder.writeln();
				}
			}
		}
	}
	
	private void fillTableBookmark(Bookmark b, String bookmarkLabel) {
		CompositeNode node = b.getBookmarkStart().getParentNode();
		Table targetTable = null;
		while(node != null) {
        	if(node instanceof Table) {
        		targetTable = (Table) node;
        		break;
        	}
        	node = node.getParentNode();
        }
		if(targetTable == null) {
			// TODO error message
			return;
		}
		
		// изтриват се всички празни редове след антетката
		int initialTableRows = targetTable.getRows().getCount();
		for(int i = 1; i < initialTableRows; i++) {
    		Row row = targetTable.getRows().get(1);
    		targetTable.getRows().remove(row);
    	}
		
		DocumentBuilder builder = new DocumentBuilder(this.shablonDocument);
		String dataString = this.values.get(bookmarkLabel);
		List<String> rowsList = Arrays.asList(dataString.split(Pattern.quote(TABLE_ROW_DELIMITER), -1));
		
		for(String dataRowString : rowsList) {
    		Row row = new Row(this.shablonDocument);
    		targetTable.appendChild(row);
    		List<String> cellsList = Arrays.asList(dataRowString.split(Pattern.quote(LIST_DELIMITER), -1));
    		
    		for(String dataCell : cellsList) {
    			Cell cell = new Cell(this.shablonDocument);
    			row.appendChild(cell);
    			cell.appendChild(new Paragraph(this.shablonDocument));
    			builder.moveTo(cell.getFirstParagraph());
    			builder.write(dataCell);
    		}
    	}
	}
	
	private void fillTreeBookmark(String bookmarkLabel) throws Exception {
		DocumentBuilder builder = new DocumentBuilder(this.shablonDocument);
		builder.moveToBookmark(bookmarkLabel);
		
		String dataString = this.values.get(bookmarkLabel);
		String[] treeRows = dataString.split(Pattern.quote(LIST_DELIMITER));
		
		builder.getCurrentParagraph().getParagraphFormat().setLineSpacingRule(LineSpacingRule.MULTIPLE);
		builder.getCurrentParagraph().getParagraphFormat().setLineSpacing(12);
		builder.writeln();
		
		for(String rowString : treeRows) {
			String[] rowData = rowString.split(Pattern.quote(TREE_ATTRIBUTES_DELIMITER));
			String text = rowData[0];
			int indentLevel = Integer.parseInt(rowData[1]);
			int isMain = Integer.parseInt(rowData[2]);
			
			if(isMain == BabhConstants.CODE_ZNACHENIE_DA) {
				builder.getFont().setBold(true);
			}
			else {
				builder.getFont().setItalic(true);
			}
			// ляво подравняване с по 1 см за всяко следващо ниво
			builder.getCurrentParagraph().getParagraphFormat().setLeftIndent(indentLevel * ConvertUtil.millimeterToPoint(10));
			if(isMain == BabhConstants.CODE_ZNACHENIE_DA) {
				builder.write(BLACK_BULLET + " ");
			}
			else {
				builder.write(WHITE_BULLET + " ");
			}
			builder.write(text);
			
			builder.getFont().setBold(false);
			builder.getFont().setItalic(false);
			builder.writeln();
			
		}
	}
	
	private Set<String> getKilledBookmarks() {
		Set<String> killedBookmarks = new HashSet<>();
		
		this.bookmarks
			.stream()
			.filter(b -> b.getBookmarkType() != null && b.getBookmarkType().equals(BookmarkTypes.KILLER))
			.forEach(b -> {
				String[] killed = this.values.get(b.getLabel()).split(Pattern.quote(LIST_DELIMITER));
				killedBookmarks.addAll(Arrays.asList(killed));
			});
		
		return killedBookmarks;
	}
	
	
	/* * * * * * * * * * * * EXCEPTIONS * * * * * * * * * * * * * * * * * * * */
	
	public static class NoShablonException extends RuntimeException {

		private static final long serialVersionUID = 2132541975496907997L;

		public NoShablonException(String message) {
			super(message);
		}
	}
	
	public static class MissingSystemSettingException extends RuntimeException {

		private static final long serialVersionUID = -5121616202710447595L;

		public MissingSystemSettingException(String message) {
			super(message);
		}
	}
	
	public static class NoChosenShablonException extends RuntimeException {
		
		private static final long serialVersionUID = -6946524623808084727L;

		public NoChosenShablonException(String message) {
			super(message);
		}
	}
	
	public static class ValuesNotGeneratedException extends RuntimeException {

		private static final long serialVersionUID = -4186991690642212462L;

		public ValuesNotGeneratedException(String message) {
			super(message);
		}
	}
	
	public static class MissingBookmarkLogicException extends RuntimeException {

		private static final long serialVersionUID = -1252968140800725865L;

		public MissingBookmarkLogicException(String message) {
			super(message);
		}
	}
	
	public static class WrongBookmarkNameException extends RuntimeException {

		private static final long serialVersionUID = -1276078540644875185L;

		public WrongBookmarkNameException(String message) {
			super(message);
		}
	}
	
	public static class UdostDocAlreadySavedException extends RuntimeException {

		private static final long serialVersionUID = -5555961037426741406L;

		public UdostDocAlreadySavedException(String message) {
			super(message);
		}
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * @return true, ако към документа има повече от един наличен шаблон,
	 * и не е избран нито един, който да се използва.
	 */
	public boolean needsToChooseShablon() {
		return this.shabloni.size() > 1 
				&& this.chosenShablon == null;
	}
	
	/**
	 * @return наличните за документа шаблони.
	 */
	public List<Files> getShabloni() {
		return this.shabloni;
	}

	/**
	 * Ако {@link #needsToChooseShablon()} върне true, трябва с този метод
	 * да се избере кой наличен шаблон да се използва. Иначе методите за 
	 * попълване ще хвърлят {@link NoChosenShablonException}.
	 * @param chosenShablon 
	 */
	public void setChosenShablon(@NotNull Files chosenShablon) {
		this.chosenShablon = chosenShablon;
	}
	
	public Files getChosenShablon() {
		return this.chosenShablon;
	}
	
	/**
	 * @return Името на букмарковете във файла, за които 
	 * намерената стойност от метода в {@link UdostDokumentMethods} е null.
	 */
	public Set<String> getNullValues() {
		if(this.nullValues == null) {
			throw new ValuesNotGeneratedException("Не е извикан методът gatherData");
		}
		return this.nullValues;
	}
	
	/**
	 * @return Мап, в който са стойностите, които ще се попълнят в шаблона.
	 * Ключът е името на букмарка в уърдовския файл.
	 */
	public Map<String, String> getValues() {
		if(this.values == null) {
			throw new ValuesNotGeneratedException("Не е извикан методът gatherData");
		}
		return this.values;
	}
	
	/**
	 * @return списък с букмарковете в избрания шаблон
	 */
	public List<ShablonBookmark> getBookmarks() {
		if(this.bookmarks == null) {
			throw new ValuesNotGeneratedException("Не е извикан методът gatherData");
		}
		return this.bookmarks;
	}

	/**
	 * @return Индекския файл, в който е запаметен попълненият шаблон.
	 * Ако не е извикан {@link #saveFilledShablon(Integer)}, ще върне null.
	 */
	public Files getSavedUdostDokument() {
		return savedUdostDokument;
	}
	
	public byte[] getDocumentAsBytes() throws Exception {
		try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			this.shablonDocument.save(stream, SaveFormat.DOCX);
			return stream.toByteArray();
		}
	}

	public Map<String, Object> getAdvancedBookmarkValues() {
		return advancedBookmarkValues;
	}
}
