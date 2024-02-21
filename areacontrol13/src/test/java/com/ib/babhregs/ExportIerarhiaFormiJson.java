package com.ib.babhregs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.dao.SystemClassifOpisDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemClassifOpis;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.SysClassifUtils;

public class ExportIerarhiaFormiJson {

	public static void main(String[] args) {
		
		try {
		
			SystemData sd = new SystemData();
			
			
			String tempDir= "D:\\_Заяления";
			
			
			
			
			
			
			Integer classifId = 699;
			
			SystemClassifOpis opisVod = new SystemClassifOpisDAO(ActiveUser.DEFAULT).findById(classifId); //JPA.getUtil().getEntityManager().find(SystemClassifOpis.class, list.getCodeClassifVod());
			System.out.println(opisVod.getTranslations().get(0).getTekst());
			String nameClassif = opisVod.getTranslations().get(0).getTekst();
			
			nameClassif = nameClassif.replace("/", "_");
			
			List<SystemClassif>  classif = sd.getSysClassification(classifId, new Date(), 1);
			SysClassifUtils.doSortClassifPrev(classif);
			
			String fullPath = tempDir + "\\"+classifId + "_" + nameClassif;
			File fDir = new File(fullPath);
			
			if (! fDir.exists()) {
				boolean result = fDir.mkdir();
				if (! result) {
					System.out.println("Директория " + fullPath + " не може да бъде създадена !!!");
					System.exit(0);
				}
			}
			
			
		    String filename = "classif_"+classifId+"_preview.txt";
			if (classif != null ) {
				String txtString = "";
				for (SystemClassif item : classif) {
					
					for (int i = 1; i < item.getLevelNumber(); i++) {
						txtString += "\t";
					}
					
					txtString += item.getTekst() + "\t" + "(код " + item.getCode() +")\r\n";
					
				}
				
				System.out.println(txtString);
				FileUtils.writeBytesToFile(fullPath+"\\"+filename, txtString.getBytes());
			}
			
			String itemName = "root";
			Integer itemCode = 0;
			
			doRecursiveExport(fullPath, classif, itemName, itemCode);
		
		
		
		System.out.println("-----------------------------------------------------------");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
	
		
	}

	private static void doRecursiveExport(String fullPath, List<SystemClassif> classif, String itemName,Integer itemCode) throws IOException {
		
			ArrayList<SystemClassif> children = new ArrayList<SystemClassif>();
			String fname = fullPath + "\\classif_" + classif.get(0).getCodeClassif()+"_childrenOfCode_"+itemCode+ ".json";
			if (itemCode == 0) {
				fname = fullPath + "\\classif_" + classif.get(0).getCodeClassif()+"_root.json";
			}
			
			for (SystemClassif item : classif) {
				if (item.getCodeParent() == itemCode) {
					children.add(item);
				}
			}
			
			if (children.size() > 0) {
				String json = "values = [";
				for ( SystemClassif item : children) {
					json += "\r\n\t{value: '"+ item.getCode()+"', label: '"+item.getTekst()+"'},";
					doRecursiveExport(fullPath, classif, item.getTekst(), item.getCode());
				}
				if (json.endsWith(",")) {
					json = json.substring(0, json.length()-1);
				}
				json += "\r\n]";
				FileUtils.writeBytesToFile(fname, json.getBytes());
				System.out.println(json);
				System.out.println();
				System.out.println();
			}
			
		
	}

}
