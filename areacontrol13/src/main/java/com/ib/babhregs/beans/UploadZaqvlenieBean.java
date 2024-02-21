package com.ib.babhregs.beans;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.utils.X;

@Named
@ViewScoped
public class UploadZaqvlenieBean extends IndexUIbean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7767572564756526112L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadZaqvlenieBean.class);

	private List<Files> filesList;
	private Integer selectedVidDoc;
	private String lastFileName;
	FilesDAO filesDao;

	@PostConstruct
	void initData() {
		Date decodeDate=new Date();
	}
	public void changeVidDoc() {
		System.err.println(selectedVidDoc);
	}
	
	public void listenerPrime(FileUploadEvent event)  {
		UploadedFile item = event.getFile();
		String filename = item.getFileName();
		
		if(isISO88591Encoded(filename)){
			filename = new String(filename.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		}
		
		X<Files> x = X.empty();
		
		Files files = new Files();
		files.setFilename(filename);
		files.setContentType(item.getContentType());
		files.setContent(item.getContent());
		
		lastFileName=null;
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Очаква се запис все още не е готово! Обърнете се към Васко." );	
//		try {
//			JPA.getUtil().runInTransaction(() -> { 
//				//vpObj.setCountFiles(filesList == null ? 0 : filesList.size());
//			
//				lastFileName=new EgovMessagesDAO(getUserData()).loadZaiavlenieTest(files, selectedVidDoc, new Date(), getSystemData());
//				
//			});
//			
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );	
//			
//		} catch (ObjectInUseException e) {
//			LOGGER.error("Грешка при запис на документа! ObjectInUseException "); 
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
//		} catch (BaseException e) {			
//			LOGGER.error("Грешка при запис на документа! BaseException", e);				
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
//		} catch (Exception e) {
//			LOGGER.error("Грешка при запис на документа! ", e);					
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
//		}
		if (lastFileName!=null) {
			lastFileName="Последно прикачен файл: "+filename+" рег. №: "+lastFileName;	
		}
		
	
		 
	}
	
	/**
	 * Само за wildfly за да не изкарва името на файла на маймуница
	 * @param text
	 * @return
	 */
	private boolean isISO88591Encoded(String text) {
	    String checked = new String(text.getBytes( StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
		return checked.equals(text);

	}


	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Integer getSelectedVidDoc() {
		return selectedVidDoc;
	}

	public void setSelectedVidDoc(Integer selectedVidDoc) {
		this.selectedVidDoc = selectedVidDoc;
	}
	public String getLastFileName() {
		return lastFileName;
	}
	public void setLastFileName(String lastFileName) {
		this.lastFileName = lastFileName;
	}

}