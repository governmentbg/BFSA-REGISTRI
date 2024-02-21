package com.ib.babhregs.experimental;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.ib.indexui.system.Constants;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.Multilang;
import com.ib.system.utils.SysClassifUtils;

public class ImportClassifsFormi {

	public static void main(String[] args) {  
		
		String numbers = "0123456789";
		
		
		
		ArrayList<SystemClassif> raw = new ArrayList<SystemClassif>();
		
		ArrayList<Integer> levels = new  ArrayList<Integer>();
		levels.add(-1);
		SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
		
		
		
		int codeClassif = 523;
		boolean haveExtCode = false;
		boolean doSave = true;
		
		
		boolean hasNums = true;
		
        try {
            File file = new File("D:\\Номенклатури по ал. 5.docx");            
            
            
            
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);

            List<XWPFTable> tables = document.getTables();
        
            System.out.println(tables.size());
            
            String nom = "";
            String name = "";
            String data = "";
            int startIndex = 1;
            for (XWPFTable table : tables) {
            	
            	startIndex--;
            	System.out.println("-----------------------------------------------------------");
            	
            	for (XWPFTableRow row : table.getRows()) {            		
            		String col1 = "X";
            		if (startIndex == 0) {
            			col1 = row.getCell(startIndex+0).getText().replace("\r", "").replace("\n", "").replace("\t", "").trim();
            		}
            			
            		String col2 = row.getCell(startIndex+1).getText().replace("\r", "").replace("\n", "").replace("\t", "").trim();
            		
            		if (col2 != null && !col2.isEmpty()) {
            			System.out.println(nom + ". " + name );
            			System.out.println(data);
            			nom = col1;
            			name = col2;
            			data = "";
            		}
            		
            		String col5 = "";
            		String col3 = row.getCell(startIndex+2).getText().replace("\r", "").replace("\n", "").replace("\t", "").trim();
            		String col4 = row.getCell(startIndex+3).getText().replace("\r", "").replace("\n", "").replace("\t", "").trim();
            		
            		try {
            			col5 = row.getCell(startIndex+4).getText().replace("\r", "").replace("\n", "").replace("\t", "").trim();
            		}catch (Exception e) {
            			
            		}
            		
            		if (!col4.equals("Код")) {
            			data += col4 + "\t" + col5 + "\r\n";
            		}
            		
            		//System.out.println(col1 + "\t" + col2 + "\t" + col3 + "\t" + col4+ "\t" + col5 );
            		
            		
            	}
            	//break;
            }
        
        
		
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
	}
	

}
