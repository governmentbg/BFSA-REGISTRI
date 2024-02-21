package bg.bulsi.bfsa.dto.index;

import java.io.Serializable;

/** 
 * Клас, описващ прикачен файл, към документ
 * @author Vasil Shulev
 * @version 1.2
 * @since  01.05.2023
 */
public class FileWS  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2413241445629071733L;
	
	/** Системен идентификатор на файл  */
	private Integer id;		  
	
	/** MIME тип на файла  */
	private String fileType;
	
	/** Име на файл  */
	private String fileName;
	
	/** Съдържание  */
	private byte[] fileContent;
	
	
	
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFileContent() {		
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
	

}
