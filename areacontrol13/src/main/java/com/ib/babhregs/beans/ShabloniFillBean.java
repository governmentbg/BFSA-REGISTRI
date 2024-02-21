package com.ib.babhregs.beans;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.ShablonLogicDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ShablonBookmark;
import com.ib.babhregs.db.dto.ShablonBookmark.BookmarkTypes;
import com.ib.babhregs.db.dto.ShablonBookmark.FillComponents;
import com.ib.babhregs.db.dto.ShablonBookmark.FillStrategies;
import com.ib.babhregs.db.dto.ShablonLogic;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.babhregs.udostDocs.AdvancedUdostMethods;
import com.ib.babhregs.udostDocs.CustomFillMethods;
import com.ib.babhregs.udostDocs.DefaultValueMethods;
import com.ib.babhregs.udostDocs.UdostDocumentCreator;
import com.ib.babhregs.udostDocs.UdostDokumentMethods;
import com.ib.babhregs.udostDocs.UdostDokumentOnCompleteMethods;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.ValidationUtils;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PF;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named("shabloniFill")
@ViewScoped
public class ShabloniFillBean extends IndexUIbean {

	private static final long serialVersionUID = 7060411966376808L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ShabloniFillBean.class);
	public static final String EMPTY_METHOD_NAME = "getEmpty"; 
	
	private transient ShablonLogicDAO shabloniDao;
	private transient FilesDAO filesDao;
	private List<Object[]> shabloniList;
	private Object selectedShablon;
	private List<String> metodi;
	private List<String> advMetodi;
	private List<String> onCompleteMetodi;
	private List<String> defaultValueMetodi;
	private List<String> customMetodi;
	private ShablonLogic shablonLogic;

	/** bookmarks, които са записани в настоящия shablonLogic */
	private List<ShablonBookmark> bookmarks;
	private Map<String, String> oldIncorrectNames;
	
	/** имена на bookmark-овете, които ги има в заредения doc файл */
	private List<String> bookmarkLabels;
	
	private Map<String, ShablonBookmark> bookmarkMap;
	
	// за панела с търсене по име на метод
	private String selectedMethodToCheckUsage;
	
	// за модалния за преименуване на метод
	private List<String> currentlyUsedMethodNames;
	private String methodNameToRename;
	private String methodNewName;
	
	// за модалния за визуализиране на шаблон
	private UdostDocumentCreator creator;
	private List<Object[]> documentsThatUseShablon;
	private Object[] selectedDocumentToRender;
	private String documentHtml;

	private List<String> passiveBookmarks;
	private boolean saveClickedOnce;
	private Set<String> componentsWithErrors;
	
	
	@PostConstruct
	public void init() {

		this.shabloniDao = new ShablonLogicDAO(getUserData());
		this.filesDao = new FilesDAO(getUserData());
		
		try {
			this.shabloniList = this.shabloniDao.findTemplates();
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на файлове-шаблони в базата.", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			scrollToMessages();
		} 
		
		this.metodi = Stream.of(UdostDokumentMethods.class.getDeclaredMethods())
			.map(Method::getName)
			.filter(m -> !m.contains("lambda$"))
			.collect(Collectors.toList());

		this.advMetodi = Stream.of(AdvancedUdostMethods.class.getDeclaredMethods())
			.map(Method::getName)
			.filter(m -> !m.contains("lambda$"))
			.collect(Collectors.toList());
		
		this.onCompleteMetodi = Stream.of(UdostDokumentOnCompleteMethods.class.getDeclaredMethods())
				.map(Method::getName)
				.filter(m -> !m.contains("lambda$"))
				.collect(Collectors.toList());
		
		this.defaultValueMetodi = Stream.of(DefaultValueMethods.class.getDeclaredMethods())
				.map(Method::getName)
				.filter(m -> !m.contains("lambda$"))
				.collect(Collectors.toList());
		
		this.customMetodi = Stream.of(CustomFillMethods.class.getDeclaredMethods())
				.map(Method::getName)
				.filter(m -> !m.contains("lambda$"))
				.collect(Collectors.toList());
	}
	
	public void actionSave() {
		for(String label : this.bookmarkMap.keySet()) {
			LOGGER.info(this.bookmarkMap.get(label).toString());
		}
		
		boolean valid = true;
		if(this.componentsWithErrors == null) {
			this.componentsWithErrors = new HashSet<>();
		}
		else {
			this.componentsWithErrors.clear();
		}

		for(int i = 0; i < this.bookmarkLabels.size(); i++) {
			String label = this.bookmarkLabels.get(i);
			ShablonBookmark bookmark = this.bookmarkMap.get(label);

			valid &= validateBookmark(bookmark, i);
		}
		
		if(!valid) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Таблицата не е попълнена изцяло!");
			scrollToMessages();
			return;
		}
		
		saveConfirmed();
	}

	private boolean validateBookmark(ShablonBookmark bookmark, int tableIndex) {
		boolean valid = true;
		this.saveClickedOnce = true;

		if(bookmark.getFillStrategy() == FillStrategies.NORMAL) {
			if (!ValidationUtils.isNotBlank(bookmark.getMethodName())) {
				valid = false;
				this.componentsWithErrors.add(tableIndex + ":fillMethod");
			}
			if (bookmark.getBookmarkType() == null) {
				valid = false;
				this.componentsWithErrors.add(tableIndex + ":fillType");
			}
		}
		else if(bookmark.getFillStrategy() == FillStrategies.ADVANCED) {
			if (!ValidationUtils.isNotBlank(bookmark.getMethodName())) {
				valid = false;
				this.componentsWithErrors.add(tableIndex + ":fillMethodAdv");
			}
			if(!ValidationUtils.isNotBlank(bookmark.getDescription())) {
				valid = false;
				this.componentsWithErrors.add(tableIndex + ":description");
			}
			if(bookmark.getFillComponent() == null) {
				valid = false;
				this.componentsWithErrors.add(tableIndex + ":fillComponent");
			}
		}

		return valid;
	}
	
	public void saveConfirmed() {
		try {
			this.shablonLogic.getBookmarks().clear();
			this.shablonLogic.getBookmarks().addAll(new ArrayList<>(this.bookmarkMap.values()));
			JPA.getUtil().runInTransaction(() -> {
					this.shablonLogic = shabloniDao.save(this.shablonLogic);
					this.bookmarks = this.shablonLogic.getBookmarks();
					makeBookmarksMap(); // рефреш на обектите, ако има промени по файла и новозаписани
					this.oldIncorrectNames.clear();
				});
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));
		}
		catch (BaseException e) {
			LOGGER.error("Грешка при запис на ShablonLogic", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при запис в базата");
			scrollToMessages();
		}
	}

	public void changeFillStrategy(String label) {
		ShablonBookmark bookmark = this.bookmarkMap.get(label);
		switch(bookmark.getFillStrategy()) {
			case NORMAL : {
				bookmark.setDescription(null);
				this.passiveBookmarks.remove(label);
				removeBookmarkNameFromFillOther(label);
				break;
			}
			case ADVANCED : {
				bookmark.setMethodName(null);
				bookmark.setBookmarkType(null);
				this.passiveBookmarks.remove(label);
				removeBookmarkNameFromFillOther(label);
				break;
			}
			case PASSIVE : {
				bookmark.setDescription(null);
				bookmark.setMethodName(null);
				bookmark.setBookmarkType(null);
				this.passiveBookmarks.add(label);
				break;
			}
		}
	}

	private void removeBookmarkNameFromFillOther(String label) {
		this.bookmarkMap.forEach((k, v) -> {
			if(v.getFillsAlso() != null && v.getFillsAlso().contains(label)) {
				v.getFillsAlso().remove(label);
			}
		});
	}
	
	public void actionDelete() {
		try {
			JPA.getUtil().runInTransaction(() -> shabloniDao.delete(this.shablonLogic));
			this.shablonLogic = null;
			this.bookmarks = null;
			this.selectedShablon = null;
			this.bookmarkMap = null;
			this.selectedMethodToCheckUsage = null;
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Изтриването е успешно");
		}
		catch (BaseException e) {
			LOGGER.error("Грешка при изтриване на ShablonLogic", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при изтриване от базата");
			scrollToMessages();
		}
	}
	
	public void uploadNewShablon(FileUploadEvent event) {
		
        try {
        	UploadedFile file = event.getFile();
			Files f = this.filesDao.findById(Integer.valueOf((String) this.selectedShablon));
			
			f.setContent(file.getContent());
			f.setContentType(file.getContentType());
			JPA.getUtil().runInTransaction(() -> this.filesDao.save(f));
			
			actionChooseShablon();
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Файлът е записан");
        }
        catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        	JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при работа с базата");
        	scrollToMessages();
        }
        
	}
	
	public void actionChooseShablon() {

//		if(this.shablonLogic != null && this.shablonLogic.getId() != null) {
//			JPA.getUtil().getEntityManager().detach(this.shablonLogic);
//		}
		this.saveClickedOnce = false;
		try {
			Integer id = Integer.valueOf((String) this.selectedShablon);
			this.shablonLogic = this.shabloniDao.findByFileId(id);
			
			if(this.shablonLogic == null) {
				this.shablonLogic = new ShablonLogic();
				this.shablonLogic.setFileId(id);
				this.bookmarks = new ArrayList<>();
				this.shablonLogic.setBookmarks(this.bookmarks);
			}
			else {
				JPA.getUtil().getEntityManager().detach(this.shablonLogic); // za da ne omeshva stari i nezapisani stoinosti
				this.shablonLogic = this.shabloniDao.findByFileId(id);
				this.bookmarks = this.shablonLogic.getBookmarks();
			}
			
			this.bookmarkLabels = getBookmarkLabelsFromWordFile();
			makeBookmarksMap();
			highlightErrors();
		}
		catch(DbErrorException e) {
			LOGGER.error("Грешка при работа с базата", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			scrollToMessages();
		}
		catch(Exception e) {
			LOGGER.error("Грешка при инициализиране на Aspose лиценз", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			scrollToMessages();
		}
		
	}
	
	/**
	 * При зареждане на шаблон, проверява дали записаните имена на методи са още валидни.
	 * Ако в букмарк е бил записано име на метод, но после методът е бил преименуван в UdostDokumentMethods,
	 * ще огради името в червено, за да се избере новото име.
	 */
	private void highlightErrors() {
		if(this.oldIncorrectNames == null) {
			this.oldIncorrectNames = new HashMap<>();
		}
		else {
			this.oldIncorrectNames.clear();
		}
		
		for(int i = 0; i < this.bookmarkLabels.size(); i++) {
			String label = this.bookmarkLabels.get(i);
			ShablonBookmark bookmark = this.bookmarkMap.get(label);
			
			if(bookmark.getId() != null && bookmark.getFillStrategy() == FillStrategies.NORMAL && (!this.metodi.contains(bookmark.getMethodName()))) {
				this.oldIncorrectNames.put(label, bookmark.getMethodName());
				bookmark.setMethodName(null);
				String script = "$(document.getElementById('formShabloniFill:logicTable:" + i + ":fillMethod')).addClass('ui-state-error')";
				PrimeFaces.current().executeScript(script);
			}
		}
	}
	
	public void shablonLinkClicked(Object fileId) {
		this.selectedShablon = ((BigInteger)fileId).toString();
		actionChooseShablon();
	}
	
	private List<String> getBookmarkLabelsFromWordFile() throws Exception {
		final List<String> labels = new ArrayList<>();
		Files file = this.filesDao.findById(this.shablonLogic.getFileId());
		Document wordFile = getWordFile(file.getContent());
		wordFile.getRange()
		.getBookmarks()
		.forEach(b -> {
			if(!b.getName().startsWith("_") && !b.getName().equals(ShablonLogic.DUBLIKAT_BM_LABEL)) {
				labels.add(b.getName());
			}
		});
		return labels;
	}
	
	private Document getWordFile(byte[] bytes) throws Exception {
		License license = new License();
		String nameLicense="Aspose.Words.lic";
		InputStream inp = getClass().getClassLoader().getResourceAsStream(nameLicense);
		license.setLicense(inp);
		InputStream input = new ByteArrayInputStream(bytes);
		return new Document(input);
	}

	/**
	 * Този метод е много важен
	 */
	private void makeBookmarksMap() {

		if(this.bookmarkMap == null) {
			this.bookmarkMap = new HashMap<>();
		}
		else {
			this.bookmarkMap.clear();
		}

		if(this.passiveBookmarks == null) {
			this.passiveBookmarks = new ArrayList<>();
		}
		else {
			this.passiveBookmarks.clear();
		}
		
		for(String label : this.bookmarkLabels) {
			ShablonBookmark bookmark = this.bookmarks
					.stream()
					.filter(b -> b.getLabel().equals(label))
					.findFirst()
					.orElse(null);
			// във файла има bm, който липсва в досегашната логика (добавен е впоследствие във файла)
			if(bookmark == null) {
				bookmark = new ShablonBookmark();
				bookmark.setLabel(label);
			}

			if(bookmark.getFillStrategy() == FillStrategies.PASSIVE) {
				this.passiveBookmarks.add(label);
			}
			
			this.bookmarkMap.put(label, bookmark);
		}
		
		// тук проверявам обратното - ако в базата има вече попълнен bm, който после е изтрит от уърдовския файл
		boolean shouldDeleteBookmarks = false;
		for(int i = 0; i < this.bookmarks.size(); i++) {
			ShablonBookmark bookmark = this.bookmarks.get(i);
			String label = this.bookmarkLabels
				.stream()
				.filter(b -> b.equals(bookmark.getLabel()))
				.findFirst()
				.orElse(null);
			
			if(label == null) {
				// bookmark е изтрит от файла; изтривам го и от логиката
				this.bookmarks.set(i, null);
				shouldDeleteBookmarks = true;
			}
		}
		if(shouldDeleteBookmarks) {
			try {
				this.bookmarks.removeIf(b -> b == null);
				JPA.getUtil().runInTransaction(() -> {
					this.shabloniDao.save(this.shablonLogic);
				});
			}
			catch (BaseException e) {
				LOGGER.error("Грешка при работа с базата", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
				scrollToMessages();
			}
		}
	}
	
	public void actionRenameClicked() {
		this.methodNameToRename = null;
		this.methodNewName = null;
		
		loadCurrentlyUsedMethodNames();
		PrimeFaces.current().executeScript("PF('dialogRenameMethod').show();");
	}

	public void renameMethod() {
		
		boolean valid = true;
		
		if(!ValidationUtils.isNotBlank(this.methodNameToRename)) {
			valid = false;
			String script = "$(document.getElementById('formShabloniFill:methodNameToRename')).addClass('ui-state-error')";
			PrimeFaces.current().executeScript(script);
		}
		
		if(!ValidationUtils.isNotBlank(this.methodNewName)) {
			valid = false;
			String script = "$(document.getElementById('formShabloniFill:selectMethodNewName')).addClass('ui-state-error')";
			PrimeFaces.current().executeScript(script);
		}
		
		if(valid) {
			try {
				JPA.getUtil().runInTransaction(() -> {
						int count = shabloniDao.renameUsedMethod(this.methodNameToRename, this.methodNewName);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, String.format("Методът %s е преименуван на %d места", this.methodNewName, count));
					});
				PrimeFaces.current().executeScript("PF('dialogRenameMethod').hide();");
				if(this.selectedShablon != null) {
					actionChooseShablon();
				}
			}
			catch (BaseException e) {
				LOGGER.error("Грешка при смяна на имена на методи в базата", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при смяна на имена на методи в базата");
			}
		}
	}
	
	public void actionRenderTestDocument() {
		System.out.println("Render document");
		
		try {
			this.documentsThatUseShablon = shabloniDao.getDocAndVpisvaneThatUseShablon(Integer.valueOf((String) this.selectedShablon));
			this.documentHtml = null;
			this.selectedDocumentToRender = null;
			PF.current().executeScript("PF('dialogRenderDocument').show()");
		}
		catch (DbErrorException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на документи");
			LOGGER.error("Грешка при зареждане на документи", e);
		}
	}
	
	public void onRenderDocumentRowSelect() {
		try {
			Vpisvane v = new VpisvaneDAO(getUserData()).findById(this.selectedDocumentToRender[1]);
			Doc doc = new DocDAO(getUserData()).findById(this.selectedDocumentToRender[0]);
			this.creator = new UdostDocumentCreator((SystemData) getSystemData(), (UserData) getUserData(), v, doc, false);
			
			this.creator.getShabloni().stream()
				.filter(f -> Objects.equals(f.getId(), Integer.valueOf((String) this.selectedShablon)))
				.findFirst()
				.ifPresent(this.creator::setChosenShablon);
			
			this.creator.gatherData();
			this.creator.fillBookmarksInShablon();
			this.documentHtml = this.creator.getDocumentAsHtmlString();
		}
		catch(Exception e) {
			this.documentHtml = "<div>Грешка при попълване на удостоверителен документ</div>";
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при попълване на удостоверителен документ");
			LOGGER.error("Грешка при попълване на удостоверителен документ", e);
		}
	}
	
	public boolean showWarningIcon() {
		if(this.selectedShablon == null) {
			return false;
		}
		
		if(this.shablonLogic.getId() == null || this.bookmarkMap == null) {
			return true;
		}
		
		for(Map.Entry<String, ShablonBookmark> entry : this.bookmarkMap.entrySet()) {
			ShablonBookmark currentBookmark = entry.getValue();
			if(currentBookmark.getMethodName() != null && currentBookmark.getMethodName().equals(EMPTY_METHOD_NAME)) {
				return true;
			}
			if(currentBookmark.getId() == null) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean showErrorIcon() {
		return (this.oldIncorrectNames) != null && (!this.oldIncorrectNames.isEmpty());
	}
	
	public void loadCurrentlyUsedMethodNames() {
		try {
			this.currentlyUsedMethodNames = this.shabloniDao.findCurrentlyUsedMethodNames()
					.stream().map(n -> (String) n)
					.collect(Collectors.toList());
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на имена на методи в базата", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на имена на методи в базата");
		}
	}
	
	public void setTheEmptyMethod(String bookmarkLabel, int tableIndex) {
		this.bookmarkMap.get(bookmarkLabel).setMethodName(EMPTY_METHOD_NAME);
		this.bookmarkMap.get(bookmarkLabel).setBookmarkType(BookmarkTypes.STRING);
		if(this.componentsWithErrors != null) {
			this.componentsWithErrors.remove(tableIndex + ":fillMethod");
			this.componentsWithErrors.remove(tableIndex + ":fillType");
		}
	}
	
	public String getIconClassForBmType(ShablonBookmark.BookmarkTypes bmType) {
		if(bmType == null) {
			return "";
		}
			
		switch (bmType) {
			case STRING : return "fas fa-font";
			case LIST : return "fas fa-list";
			case TABLE : return "fas fa-table";
			case TREE : return "fas fa-tree";
			case KILLER : return "fas fa-cut";
			default : return "";
		}
	}

	public boolean componentHasError(String compName) {
		return this.componentsWithErrors != null
				&& this.componentsWithErrors.contains(compName);
	}

	public boolean expandedRow(int index) {
		return this.componentsWithErrors != null
				&& this.componentsWithErrors.stream()
					.filter(s -> Integer.parseInt(s.substring(0, s.indexOf(":"))) == index)
					.anyMatch(s -> s.contains("description") || s.contains("fillComponent"));
	}
	
	public void downloadDoc() {
		try {
			byte[] wordFileAsBytes = this.creator.getDocumentAsBytes();
			String filename = this.selectedShablon + " Удостоверителен документ.docx";
			downloadFile(wordFileAsBytes, filename);
		}
		catch(Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Грешка при сваляне на удостоверителен документ");
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void exportJson() {
		JsonMapper mapper = new JsonMapper();
		
		byte[] bytes = null;
		try {
			List<Map<String, Object>> exportData = new ArrayList<>();
			
			List<ShablonLogic> shablonLogicList = this.shabloniDao.findAll();
			for(ShablonLogic logic : shablonLogicList) {
				
				Files file = this.filesDao.findById(logic.getFileId());
				
				Map<String, Object> shablonData = new HashMap<>();
				shablonData.put("id", logic.getId());
				shablonData.put("fileId", logic.getFileId());
				shablonData.put("filename", file.getFilename());
				shablonData.put("bookmarks", logic.getBookmarks());
				
				exportData.add(shablonData);
			}
			
			bytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportData);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		catch(DbErrorException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Грешка при работа с базата");
			LOGGER.error(e.getMessage(), e);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		downloadFile(bytes, "bookmarks_export_" + sdf.format(new Date()) + ".json");
	}
	
	private void downloadFile(byte[] bytes, String filename) {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();

			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			String agent = request.getHeader("user-agent");
			
			String codedfilename = "";

			if (null != agent && (agent.contains("MSIE") || agent.contains("Mozilla") && agent.contains("rv:11") || agent.contains("Edge"))) {
				codedfilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
			}
			else if (null != agent && agent.contains("Mozilla")) {
				codedfilename = MimeUtility.encodeText(filename, "UTF8", "B");
			}
			else {
				codedfilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
			}

			externalContext.setResponseHeader("Content-Type", "application/x-download");
			externalContext.setResponseHeader("Content-Length", bytes.length + "");
			externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
			externalContext.getResponseOutputStream().write(bytes);

			facesContext.responseComplete();
		}
		catch(Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Грешка при сваляне на удостоверителен документ");
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void scrollToMessages() {
		PrimeFaces.current().executeScript("scrollToErrors()");
	}


	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	
	public List<Object[]> getShabloniList() {
		return shabloniList;
	}

	public void setShabloniList(List<Object[]> shabloniList) {
		this.shabloniList = shabloniList;
	}

	public List<String> getMetodi() {
		return metodi;
	}

	public List<String> getAdvMetodi() {
		return advMetodi;
	}
	
	public List<String> getOnCompleteMetodi() {
		return onCompleteMetodi;
	}
	
	public List<String> getDefaultValueMetodi() {
		return defaultValueMetodi;
	}
	
	public List<String> getCustomMetodi() {
		return customMetodi;
	}

	public Object getSelectedShablon() {
		return selectedShablon;
	}

	public void setSelectedShablon(Object selectedShablon) {
		this.selectedShablon = selectedShablon;
	}

	public List<String> getBookmarkLabels() {
		return bookmarkLabels;
	}

	public void setBookmarkLabels(List<String> bookmarkLabels) {
		this.bookmarkLabels = bookmarkLabels;
	}

	public Map<String, ShablonBookmark> getBookmarkMap() {
		return bookmarkMap;
	}

	public void setBookmarkMap(Map<String, ShablonBookmark> bookmarkMap) {
		this.bookmarkMap = bookmarkMap;
	}

	public String getSelectedMethodToCheckUsage() {
		return selectedMethodToCheckUsage;
	}

	public void setSelectedMethodToCheckUsage(String selectedMethodToCheckUsage) {
		this.selectedMethodToCheckUsage = selectedMethodToCheckUsage;
	}
	
	public BookmarkTypes[] getBookmarkTypes() {
        return BookmarkTypes.values();
    }

	public FillComponents[] getFillComponents() {
		return FillComponents.values();
	}

	public ShablonLogic getShablonLogic() {
		return shablonLogic;
	}

	public String getMethodNameToRename() {
		return methodNameToRename;
	}

	public void setMethodNameToRename(String methodNameToRename) {
		this.methodNameToRename = methodNameToRename;
	}

	public List<String> getCurrentlyUsedMethodNames() {
		return currentlyUsedMethodNames;
	}

	public void setCurrentlyUsedMethodNames(List<String> currentlyUsedMethodNames) {
		this.currentlyUsedMethodNames = currentlyUsedMethodNames;
	}

	public String getMethodNewName() {
		return methodNewName;
	}

	public void setMethodNewName(String methodNewName) {
		this.methodNewName = methodNewName;
	}

	public List<Object[]> getDocumentsThatUseShablon() {
		return documentsThatUseShablon;
	}

	public void setDocumentsThatUseShablon(List<Object[]> documentsThatUseShablon) {
		this.documentsThatUseShablon = documentsThatUseShablon;
	}

	public String getDocumentHtml() {
		return documentHtml;
	}

	public void setDocumentHtml(String documentHtml) {
		this.documentHtml = documentHtml;
	}

	public Object[] getSelectedDocumentToRender() {
		return selectedDocumentToRender;
	}

	public void setSelectedDocumentToRender(Object[] selectedDocumentToRender) {
		this.selectedDocumentToRender = selectedDocumentToRender;
	}

	public Map<String, String> getOldIncorrectNames() {
		return oldIncorrectNames;
	}

	public void setOldIncorrectNames(Map<String, String> oldIncorrectNames) {
		this.oldIncorrectNames = oldIncorrectNames;
	}

	public List<String> getPassiveBookmarks() {
		return this.passiveBookmarks;
	}

	public boolean isSaveClickedOnce() {
		return saveClickedOnce;
	}

	public Set<String> getComponentsWithErrors() {
		return componentsWithErrors;
	}

	public List<Object[]> getShabloniWhereMethodIsUsed() {
		try {
			if(this.selectedMethodToCheckUsage != null) {
				return this.shabloniDao.findMethodUsage(this.selectedMethodToCheckUsage);
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене по името на метод", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене по името на метод");
			scrollToMessages();
			return null;
		}
	}
	
	public List<String> getUnusedMethods() {
		this.loadCurrentlyUsedMethodNames();
		return this.metodi
			.stream()
			.filter(m -> !currentlyUsedMethodNames.contains(m))
			.collect(Collectors.toList());
	}
	
	public List<Object[]> getUsedEmptyMethod() {
		try {
			return this.shabloniDao.findMethodUsage(EMPTY_METHOD_NAME);
		}
		catch (DbErrorException e) {
			LOGGER.error("Грешка при търсене на имена на методи в базата", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на имена на методи в базата");
			scrollToMessages();
			return null;
		}
	}
	
}
