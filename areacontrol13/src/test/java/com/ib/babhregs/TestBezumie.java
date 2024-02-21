package com.ib.babhregs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.indexui.system.Constants;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.Multilang;
import com.ib.system.utils.SysClassifUtils;

public class TestBezumie {

	public static void main(String[] args) {
		SystemData sd = new SystemData();
		
		Integer rootCode = 6;
		Integer lastChild = 30;
		Integer levelNumber = 3;
		
		
		SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
		
		
		
		
		try {
			
			List<SystemClassif> docs = sd.getSysClassification(104, new Date(), 1);
			SysClassifUtils.doSortClassifPrev(docs);
			
			
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dat = sdf.parse("01.01.1900 00:00:00");
			
			List<SystemClassif> uslugi = sd.getSysClassification(521, new Date(), 1);
			
			SysClassifUtils.doSortClassifPrev(uslugi);
			
			ArrayList<String>  tips = new ArrayList<String>();
			//Заявление за …..“, „заявление за промяна на обстоятелствата на…..“ и „заявление за заличаване на …..“.
			tips.add("Заявление за ");
			tips.add("Заявление за промяна на обстоятелствата на ");
			tips.add("Заявление за заличаване на ");
			
			
			JPA.getUtil().begin();
			
			
			Integer rootPrev = lastChild.intValue();
			for (SystemClassif tek : uslugi) {
				if (tek.getLevelNumber() == 1) {
					System.out.println(tek.getTekst());
					SystemClassif tekNew = new SystemClassif();
					tekNew.setId(null);
					tekNew.setCodeParent(rootCode);
					tekNew.setCodePrev(rootPrev);
					tekNew.setCodeClassif(104);
					tekNew.setDateOt(dat);
					tekNew.setLevelNumber(levelNumber);
					//tekNew.setCode(sdao.generateCode(104));
					
					
					
					Multilang langBG1 = new Multilang();
				    langBG1.setTekst(tek.getTekst());
				    langBG1.setLang(Constants.CODE_DEFAULT_LANG);
				    tekNew.getTranslations().add(langBG1);
					
				    sdao.doSimpleSave(tekNew);
				    
				    System.out.println("---> " + tekNew.getId());
				    System.out.println("*** > " + tekNew.getCode());
				    
				    rootPrev = tekNew.getCode();
				    
				    int childPrev = 0;
				    for (SystemClassif child : uslugi) {
				    	if (child.getCodeParent() == tek.getCode()) {
				    		
				    		for (String tip : tips) {
				    		
					    		SystemClassif newChild = new SystemClassif();
					    		newChild.setId(null);
					    		newChild.setCodeParent(tekNew.getCode());
					    		newChild.setCodePrev(childPrev);
					    		newChild.setCodeClassif(104);
					    		newChild.setDateOt(dat);
					    		newChild.setLevelNumber(levelNumber+1);
					    		//newChild.setCode(sdao.generateCode(104));
					    		
					    		String name = child.getTekst();
					    		name = tip  + name.substring(0,1).toLowerCase()+name.substring(1);
					    		
					    		System.out.println(name);
																
								Multilang langBG = new Multilang();
								langBG.setTekst(name);
								langBG.setLang(Constants.CODE_DEFAULT_LANG);
								newChild.getTranslations().add(langBG);
								
							    sdao.doSimpleSave(newChild);
							    
							    childPrev = newChild.getCode();
				    		}
				    	}
				    }
				    
					
				}
			}
			
			JPA.getUtil().commit();
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JPA.getUtil().rollback();
		}finally {
			JPA.getUtil().closeConnection();
		}
		
		

	}

}
