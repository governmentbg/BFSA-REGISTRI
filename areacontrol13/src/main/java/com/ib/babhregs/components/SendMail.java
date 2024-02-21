package com.ib.babhregs.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataSource;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.util.ByteArrayDataSource;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.BabhClassifAdapter;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.mail.Mailer;
import com.ib.system.mail.Mailer.Content;

@FacesComponent
public class SendMail extends UINamingContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7140484336053301188L;
	public static final String	UIBEANMESSAGES = "ui_beanMessages";
	public static final String	LABELS = "labels";
	static Properties props=new Properties();
	static SystemData sd;
	static String clientId = null;	
	private static Integer ID_REGISTRATURE = -1;//1;
	private static final String MAILBOX="DEFAULT";//"DEFAULT";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);
	
	
	static {
		sd = (SystemData) JSFUtils.getManagedBean("systemData");
		try {
			props = sd.getMailProp(ID_REGISTRATURE, MAILBOX);
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private enum PropertyKeys {
		CODEOBJ, CODESLUJ, EMAIL, SUBJECT, TEXTMAIL, uploadFilesList, attachedBytes
	} 
	
	public void initM() {
		setCodeSluj((Integer) getAttributes().get("codeSluj"));
		FacesContext context = FacesContext.getCurrentInstance();
		clientId =  getClientId(context);	
	}
	
	private boolean checkFileds() {
		boolean sending = true;
	    if (clientId != null ) { 
			if(getEmail() == null || getEmail().trim().isEmpty()) {
				JSFUtils.addMessage(clientId + ":email", FacesMessage.SEVERITY_ERROR, "Моля, въведете e-мейл!");
				sending = false;
			}
			if(getSubject() == null || getSubject().trim().isEmpty()) {
				JSFUtils.addMessage(clientId + ":subject", FacesMessage.SEVERITY_ERROR, "Моля, въведете Относно!");
				sending = false;
			}
			if(getTextMail() == null || getTextMail().trim().isEmpty()) {
				JSFUtils.addMessage(clientId + ":mailText", FacesMessage.SEVERITY_ERROR, "Моля, въведете текст на съобщението!");
				sending = false;
			}
			if(props == null) {
				JSFUtils.addMessage("", FacesMessage.SEVERITY_ERROR, "Липсват системни настройки за изпращане на съобщението!");
				sending = false;
			}
	    }
	    return sending;
	}
	
	public void sendMail() {
		if(!checkFileds())
			return;
		
		
		setAttachedBytes(new ArrayList<>());
		Mailer mailer = new Mailer();
		for (Files upLoadedFile : getUploadFilesList()) {
			ByteArrayDataSource ds = new ByteArrayDataSource(upLoadedFile.getContent(), upLoadedFile.getContentType());
			ds.setName(upLoadedFile.getFilename());
			getAttachedBytes().add(ds);
		}
		try {
			String mailFrom = "";
			SystemClassif sluj = sd.decodeItemLite(BabhConstants.CODE_CLASSIF_ADMIN_STR, getCodeSluj(), BabhConstants.CODE_LANG_BG, new Date(), false);
	        if (sluj != null) {
	            mailFrom = getMailFromSystemClassif(sluj,  BabhConstants.CODE_LANG_BG, new Date());
	        }
	        mailFrom = props.getProperty("mail.from"); // override докато стане ясно от кога ще се изпращат мейлите!!!
	        
			mailer.sent(Content.PLAIN, props, props.getProperty("user.name"), props.getProperty("user.password"),
					mailFrom, "Бългаска Агенция за безопасност на храните", 
					getEmail(), getSubject(), getTextMail(), getAttachedBytes() );
			
			JSFUtils.addInfoMessage("Успешно изпращане на съобщението!");
			setSubject("");
			setTextMail("");
			setEmail("");
			getAttachedBytes().clear();
			getUploadFilesList().clear();
			
			String remoteCommnad = (String) getAttributes().get("onComplete");
			if (remoteCommnad != null && !"".equals(remoteCommnad)) {
				PrimeFaces.current().executeScript(remoteCommnad);
			}
			PrimeFaces.current().executeScript("PF('" + clientId + ":eMail').hide();");
		} catch (AddressException e) {
			LOGGER.error(e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Неуспешно изпращане на съобщението! Грешка в е-мейл адреса!");
			//setUploadFilesList(null);
			getAttachedBytes().clear();
		} catch (InvalidParameterException e) {
			LOGGER.error(e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Неуспешно изпращане на съобщението!");
			//setUploadFilesList(null);
			getAttachedBytes().clear();
		} catch (MessagingException e) {
			LOGGER.error(e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Неуспешно изпращане на съобщението!");
			//setUploadFilesList(null);
			getAttachedBytes().clear();
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Неуспешно изпращане на съобщението! Не може да се намери и-мейл на дирекцията, от който да се изпрати съобщението!");
			getAttachedBytes().clear();
		}
		
	}
	
	private String getMailFromSystemClassif(SystemClassif classif, Integer lang, Date date) throws DbErrorException {
	    String mail = null;
	    if (classif != null) {
	        SystemClassif zveno = sd.decodeItemLite(BabhConstants.CODE_CLASSIF_ADMIN_STR, classif.getCodeParent(), lang, date, true);
	        if (zveno != null) {
	            mail = (String) zveno.getSpecifics()[BabhClassifAdapter.ADM_STRUCT_INDEX_CONTACT_EMAIL];
	            if ( (mail == null || mail.trim().isEmpty() ) && zveno.getCodeParent() != 0) {
	                // Recursive call to get email from parent
	                mail = getMailFromSystemClassif(zveno, lang, date);
	            } 
	        }
	    }

	    return mail;
	}
	
	public Integer getCodeObj() {
		return (Integer) getStateHelper().eval(PropertyKeys.CODEOBJ);		
	}
	
	public void setCodeObj(Integer codeObj) {
		getStateHelper().put(PropertyKeys.CODEOBJ, codeObj);		
	}
	public Integer getCodeSluj() {
		return (Integer) getStateHelper().eval(PropertyKeys.CODESLUJ);		
	}
	
	public void setCodeSluj(Integer codeSluj) {
		getStateHelper().put(PropertyKeys.CODESLUJ, codeSluj);		
	}
	
	public String getEmail() {
		return (String) getStateHelper().eval(PropertyKeys.EMAIL);	
	}
	
	public void setEmail(String email) {
		getStateHelper().put(PropertyKeys.EMAIL, email);		
	}
	
	public String getSubject() {
		return (String) getStateHelper().eval(PropertyKeys.SUBJECT, "");	
	}
	
	public void setSubject(String subject) {
		getStateHelper().put(PropertyKeys.SUBJECT, subject);		
	}
	
	public String getTextMail() {
		return (String) getStateHelper().eval(PropertyKeys.TEXTMAIL, null);	
	}
	
	public void setTextMail(String textMail) {
		getStateHelper().put(PropertyKeys.TEXTMAIL, textMail);		
	}
	
	public ArrayList<DataSource> getAttachedBytes() {
		return (ArrayList<DataSource>) getStateHelper().eval(PropertyKeys.attachedBytes);	
	}

	public void setAttachedBytes(ArrayList<DataSource> attachedBytes) {
		getStateHelper().put(PropertyKeys.attachedBytes,  attachedBytes);
	}

	public ArrayList<Files> getUploadFilesList() {
		return (ArrayList<Files>) getStateHelper().eval(PropertyKeys.uploadFilesList, new ArrayList<>());	
	}

	public void setUploadFilesList(ArrayList<Files> uploadFilesList) {
		getStateHelper().put(PropertyKeys.uploadFilesList,  uploadFilesList);
	}

}
