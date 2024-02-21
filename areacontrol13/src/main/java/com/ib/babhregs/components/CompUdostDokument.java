package com.ib.babhregs.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ShablonBookmark.FillStrategies;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.babhregs.udostDocs.UdostDocumentCreator;
import com.ib.babhregs.udostDocs.UdostDocumentCreator.MissingSystemSettingException;
import com.ib.babhregs.udostDocs.UdostDokumentOnCompleteMethods;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.DbErrorException;

@FacesComponent(value = "compUdostDokument", createTag = true)
public class CompUdostDokument extends UINamingContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompUdostDokument.class);
	
	private enum PropertyKeys {
		DOC, VPISVANE, DUBLIKAT
	}
	
	private UserData userData = null;
	private SystemData systemData = null;
	private UdostDocumentCreator creator;
	private String html;
	private boolean editing;
	private Integer chosenShablon;
	
	public void initComponent() {
		System.out.println("COMPONENT INIT");
		Integer docId = (Integer) getAttributes().get("docId");
		Integer vpisvaneId = (Integer) getAttributes().get("vpisvaneId");
		boolean dublikat = (boolean) getAttributes().get("isDublikat");
		
		Doc doc = null;
		Vpisvane vpisvane = null;
		
		try {
			doc = new DocDAO(this.userData).findById(docId);
			vpisvane = new VpisvaneDAO(this.userData).findById(vpisvaneId);
			
			if(doc == null) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при отваряне на компонент за удостоверителен документ");
				LOGGER.error("На CompUdostDokoment не е подаден Doc");
				return;
			}
			else if (vpisvane == null) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при отваряне на компонент за удостоверителен документ");
				LOGGER.error("На CompUdostDokoment не е подадено Vpisvane");
				return;
			}
		}
		catch(DbErrorException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при отваряне на компонент за удостоверителен документ");
			LOGGER.error("Грешка при работа с базата");
			return;
		}
		
		setDoc(doc);
		setVpisvane(vpisvane);
		setDublikat(dublikat);
		this.editing = false;
		this.chosenShablon = null;
		this.html = null;
		
		try {
			this.creator = new UdostDocumentCreator(getSystemData(), getUserData(), getVpisvane(), getDoc(), isDublikat());
			if(!this.creator.needsToChooseShablon()) {
				generateDanni();
			}
			PrimeFaces.current().executeScript("PF('udost').show()");
			PrimeFaces.current().ajax().update(getClientId());
		}
		catch(MissingSystemSettingException e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Към документа няма въведени шаблони");
			LOGGER.error(e.getMessage(), e);
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onChooseShablon() {
		Files shablon = this.creator.getShabloni()
				.stream()
				.filter(f -> f.getId().equals(this.chosenShablon))
				.findFirst()
				.orElse(null);
		this.creator.setChosenShablon(shablon);
		generateDanni();
	}

	public void generateDanni() {
		try {
			this.creator.gatherData();
			this.creator.fillBookmarksInShablon();
			this.html = creator.getDocumentAsHtmlString();
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			LOGGER.error(e.getMessage(), e);
			PrimeFaces.current().executeScript("PF('udost').hide()");
		}
	}
	
	public void finishEditing() {
		try {
			this.creator.gatherAdvancedBookmarksData();

			creator.fillBookmarksInShablon();
			this.html = creator.getDocumentAsHtmlString();
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
		finally {
			this.editing = false;
		}
	}
	
	public void cancelEditing() {
		this.editing = false;
	}
	
	public void generateDoc() {
		try {
			if(getAttributes().get("idRegister") == null) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не е подаден ИД на регистър");
				return;
			}
			
			Integer idRegister = (Integer) getAttributes().get("idRegister");
			this.creator.saveFilledShablon(idRegister);
			callActionAfterGenerate();
		}
		catch (Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при запис на удостоверителен документ");
			LOGGER.error("Грешка при запис на удостоверителен документ", e);
		}
	}
	
	public void downloadDoc() {

		if(this.creator.getSavedUdostDokument() == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Документът не е записан в базата");
			return;
		}
		
		Files file = this.creator.getSavedUdostDokument();
		
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();

			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			String agent = request.getHeader("user-agent");

			String codedfilename = "";

			if (null != agent && (-1 != agent.indexOf("MSIE") || -1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11") || -1 != agent.indexOf("Edge"))) {
				codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
			}
			else if (null != agent && -1 != agent.indexOf("Mozilla")) {
				codedfilename = MimeUtility.encodeText(file.getFilename(), "UTF8", "B");
			}
			else {
				codedfilename = URLEncoder.encode(file.getFilename(), "UTF8");
			}

			externalContext.setResponseHeader("Content-Type", "application/x-download");
			externalContext.setResponseHeader("Content-Length", file.getContent().length + "");
			externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
			externalContext.getResponseOutputStream().write(file.getContent());

			facesContext.responseComplete();
		}
		catch(Exception e) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Грешка при сваляне на удостоверителен документ");
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void callActionAfterGenerate() {
		final UdostDokumentOnCompleteMethods onCompleteMethods = new UdostDokumentOnCompleteMethods(this.getVpisvane(), this.getDoc(), this.userData, this.systemData);
		this.creator.getBookmarks().forEach(b -> {
			String methodName = b.getOnCompleteMethodName();
			if(methodName != null) {				
				try {
					Method fillerMethod = UdostDokumentOnCompleteMethods.class.getMethod(methodName, Object.class);
					// Букмарковете с fillComponent=ADM_STRUCT ще подават на метода си кода на лицето от структурата
					fillerMethod.invoke(onCompleteMethods, this.creator.getAdvancedBookmarkValues().get(b.getLabel()));
				}
				catch (NoSuchMethodException e) {
					LOGGER.error("В класа UdostDokumentOnCompleteMethods не съществува метод " + methodName, e);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error("Грешка при извикване на метод UdostDokumentOnCompleteMethods." + methodName, e);
				}
			}
		});
		
		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		MethodExpression o = (MethodExpression) getAttributes().get("actionAfterGenerate");
		if(o != null) {
			o.invoke(elContext, null);
		}
	}
	
	public boolean showApplyEditButton() {
		if(this.creator != null) {
			return this.creator.getBookmarks().stream().anyMatch(b -> b.getFillStrategy().equals(FillStrategies.ADVANCED));
		}
		else return false;
	}
	
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	

	private UserData getUserData() {
		if (this.userData == null) {
			this.userData = (UserData) JSFUtils.getManagedBean("userData");
		}
		return this.userData;
	}
	
	private SystemData getSystemData() {
		if (this.systemData == null) {
			this.systemData =  (SystemData) JSFUtils.getManagedBean("systemData");
		}
		return this.systemData;
	}
	
	public Doc getDoc() {
		Doc eval = (Doc) getStateHelper().eval(PropertyKeys.DOC, null);
		return eval != null ? eval : null;
	}
	
	public void setDoc(Doc doc) {
		getStateHelper().put(PropertyKeys.DOC, doc);
	}
	
	public Vpisvane getVpisvane() {
		Vpisvane eval = (Vpisvane) getStateHelper().eval(PropertyKeys.VPISVANE, null);
		return eval != null ? eval : null;
	}
	
	public void setVpisvane(Vpisvane vpisvane) {
		getStateHelper().put(PropertyKeys.VPISVANE, vpisvane);
	}
	
	public boolean isDublikat() {
		boolean eval = (boolean) getStateHelper().eval(PropertyKeys.DUBLIKAT, false);
		return eval;
	}
	
	public void setDublikat(boolean dublikat) {
		getStateHelper().put(PropertyKeys.DUBLIKAT, dublikat);
	}
	
	public UdostDocumentCreator getCreator() {
		return creator;
	}
	
	public String getHtml() {
		return html;
	}
	
	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public Integer getChosenShablon() {
		return chosenShablon;
	}

	public void setChosenShablon(Integer chosenShablon) {
		this.chosenShablon = chosenShablon;
	}
	
}
