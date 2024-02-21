package com.ib.babhregs.experimental;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.ib.indexui.system.Constants;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.Multilang;
import com.ib.system.utils.SysClassifUtils;

public class ImportClassifs {

	public static void main(String[] args) {  
		
		String numbers = "0123456789";
		
		
		
		ArrayList<SystemClassif> raw = new ArrayList<SystemClassif>();
		
		ArrayList<Integer> levels = new  ArrayList<Integer>();
		levels.add(-1);
		SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
		
		
		
		int codeClassif = 677;
		
		boolean haveExtCode = false;
		boolean doSave = true;
		boolean hasNums = false;
		
		
		
        try {
            File file = new File("D:\\classif\\animals.docx");
            
            
            
            
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            XWPFDocument document = new XWPFDocument(fis);

            List<XWPFParagraph> paragraphs = document.getParagraphs();

            String intString = "";
            int intentOld = -10000;
            for (XWPFParagraph para : paragraphs) {
            	
            	
//            	System.out.println(para.getNumLevelText());
//            	System.out.println(para.getNumID());
//            	System.out.println(para.getNumStartOverride());
//            	System.out.println("-----------------------------------");
            	
            	SystemClassif item = new SystemClassif();
            	
            	String value = para.getText();
            	String extCode = null;
            	
            	int intent = 0;
            	
            	if (! hasNums ) {
            	
	            	int hanging = 0;
	            	if (para.getIndentationHanging() != -1) {
	            		hanging = para.getIndentationHanging();
	            	}
	            	
	            	int first = 0;
	            	if (para.getFirstLineIndent() != -1) {
	            		first = para.getFirstLineIndent();
	            	}
	            	
	            	int left = 0;
	            	if (para.getIndentationLeft() != -1) {
	            		left = para.getIndentationLeft();
	            	}
	            	
	            	intent = left + first - hanging;
            	}else {
            		//System.out.println(para.getNumLevelText());
            		String bullet = ""+para.getNumIlvl();
            		bullet = bullet.replace("%", "");
            		//System.out.println(bullet + "\t" + para.getText());
            		intent = para.getNumIlvl().intValue();
            	}
            	if (!levels.contains(intent)) {
            		levels.add(intent);
            	}
            	
            	//System.out.println("*** Current level is " + levels.indexOf(intent));
            	
            	if (intentOld < intent) {
            		intString = intString + "\t";
            	}else {
            		if (intentOld > intent) {
            			intString = intString.substring(1);
            		}
            	}
            	
                //System.out.println(intString + value );
            	
            	if (haveExtCode) {
                
	                for (int i = 0; i < value.length()-1; i ++) {
	                	String tek = value.substring(i,i+1);
	            		if (numbers.contains(tek)) {
	            			extCode = value.substring(i).trim();
	            			value = value.substring(0,i-1);
	            			
	            		}
	                }
	                
            	}
            	value = value.trim();
                item.setLevelNumber(levels.indexOf(intent));
                item.setTekst(value);
                item.setCodeExt(extCode);
                raw.add(item);
                //System.out.println(intent + ":\t" + intString + value + "|"+ extCode + "|"+ levels.indexOf(intent));
                
                
                
                intentOld = intent;
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        int code = 0;
        int curPar = 0;
        
        int curPrev = 0;
        
        SystemClassif prevItem = new SystemClassif();
        prevItem.setLevelNumber(-1);
        prevItem.setCode(0);
        
        for (int level = 1; level < levels.size(); level ++) {
        	//System.out.println("Process Level " + level);
        	
        	for (SystemClassif item : raw) {
        		
        		if (item.getLevelNumber() == level ) {
        			
        			if (prevItem.getLevelNumber()+1 == item.getLevelNumber()) {
        				//Слизаме едно ниво;
        				curPar = prevItem.getCode();
        				curPrev = 0;
        			}
        			
        			code++;
        			item.setCode(code);
        			item.setCodeParent(curPar);
        			item.setCodePrev(curPrev);
        			
        			curPrev = code;
        			
        			
        		}
        		
        		
        		prevItem = item;
        	}
        	
        	
        }
        
        
		try {
			SysClassifUtils.doSortClassifPrev(raw);
			
			System.out.println("Sort is correct !!!! ");
		} catch (UnexpectedResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		 
        for (SystemClassif item : raw) {
        	for (int i = 1; i < item.getLevelNumber(); i ++) {
        		System.out.print("\t");
        	}
        	System.out.println(item.getCode() + "|"  + item.getTekst() + "|"+ item.getCodeParent() + "|"+item.getCodePrev());
        }
        
        if (doSave) {
	        try {
				JPA.getUtil().begin();
				
				for (SystemClassif item : raw) {
					item.setCodeClassif(codeClassif);
					item.setDateOt(new Date());
					Multilang lang = new Multilang();
					lang.setTekst(item.getTekst());
					lang.setLang(Constants.CODE_DEFAULT_LANG);
					item.getTranslations().add(lang);
					sdao.doSimpleSave(item);
					
				}
				
				JPA.getUtil().commit();
			} catch (Exception e) {
				JPA.getUtil().rollback();
				e.printStackTrace();
			}
        }
        
        
        
		
	}
	

}
