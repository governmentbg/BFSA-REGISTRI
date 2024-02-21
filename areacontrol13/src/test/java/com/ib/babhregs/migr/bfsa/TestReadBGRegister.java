package com.ib.babhregs.migr.bfsa;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ib.babhregs.db.dto.TempVMP;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.FileUtils;

public class TestReadBGRegister {

	public static void main(String[] args) {
		
		try {
			byte[] bytes = FileUtils.getBytesFromFile(new File("D:\\_VLP\\links.txt"));
			
			String tekst = new String(bytes);
			String[] links = tekst.split("\r\n");
 			
			
			for (int i = 0; i < links.length; i++) {
				String url = links[i];
				System.out.println(i + " -> " + url);
				processVLP(url, i);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	

	private static void processVLP(String httpsUrl, Integer id) {
		
		try {
			
			System.out.println("-------------------------------------------------------------------");
			
			// Connect to the URL and parse the HTML content using Jsoup
            Document document = processURL(httpsUrl);
            
            TempVMP vmp = new TempVMP();
            vmp.setId(id+1);
            // Find all paragraphs with the specified class
            Elements paragraphs = document.select("p.text-info");
            // Loop through the selected paragraphs and print their text
            for (Element paragraph : paragraphs) {
                //System.out.println(paragraph.text());
                
                String label = paragraph.html().substring(0, paragraph.html().indexOf("<br>"));
                
                
                
                Element span = paragraph.select("span.field-value").first();
                
                String value = "N/A";
                if (span != null) {
                	value = span.text();
                }
                
                System.out.println(label + ": " + value);
                
                vmp.setPic(httpsUrl);
                
                switch(label) {
                case "Външна опаковка":
                  vmp.setVanshnaOpakovka(value);
                  break;
                case "Наименование":
                  vmp.setNaim(value);	
                  break;
                case "№ на разрешително за търговия":
                    vmp.setNomLicens(value);	
                    break;
                case "Дата на издаване на разрешително за търговия":
                    vmp.setDatLicens(value);	
                    break;
                case "Вид на разрешително за търговия":
                    vmp.setVidLicens(value);	
                    break;
                case "Срок на валидност":
                    vmp.setSrokValid(value);	
                    break;
                case "Статус на разрешителното за търговия":
                    vmp.setStatusLicens(value);	
                    break;
                case "ATC":
                    vmp.setAtc(value);	
                    break;
                case "№ на становище":
                    vmp.setNomStan(value);	
                    break;
                case "Дата на становище":
                    vmp.setDatStan(value);	
                    break;
                case "Активно вещество":
                    vmp.setSubs(value);	
                    break;
                case "Количество на активното вещество":
                    vmp.setKolichestvo(value);	
                    break;
                case "Помощни вещества":
                    vmp.setEksp(value);	
                    break;
                case "Фармацевтична форма":
                    vmp.setForma(value);	
                    break;
                case "Начин на приложение":
                    vmp.setNachinPril(value);	
                    break;
                case "Първична опаковка":
                    vmp.setOpakovka(value);	
                    break;
                case "Срок на годност":
                    vmp.setSrokGodnost(value);	
                    break;
                case "Режим на отпускане":
                    vmp.setRejim(value);	
                    break;
                case "Видове животни":
                    vmp.setVidJiv(value);	
                    break;
                case "Карентен срок":
                    vmp.setKarentenSrok(value);	
                    break;
                case "Производител":
                    vmp.setProizvoditel(value);	
                    break;
                case "Адрес на производителя":
                    vmp.setAdresProizvoditel(value);	
                    break;
                case "Отговорник за освобождаване на партидата (ООП)":
                    vmp.setOop(value);	
                    break;
                case "Адрес на ООП":
                    vmp.setAdresOop(value);	
                    break;
                case "Притежател на разрешението за търговия (ПРТ)":
                    vmp.setPritel(value);	
                    break;
                case "Адрес на ПРТ":
                    vmp.setAdresPritejatel(value);	
                    break;
                }   
                
				
                
                
              

                
                
            }
            
            try {
				JPA.getUtil().begin();
				 JPA.getUtil().getEntityManager().persist(vmp);
				 JPA.getUtil().commit();
				 JPA.getUtil().getEntityManager().detach(vmp);
			} catch (DbErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		
	}




	private static Document processURL(String httpsUrl) {
		
		Document doc = null;
		int timeout = 1;
		while (doc == null) {
			try {
				try {
					Thread.sleep(timeout*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				doc = Jsoup.connect(httpsUrl).get();
			} catch (IOException e) {				
				e.printStackTrace();
				timeout = timeout*2;
				System.out.println("********************* Seting timeout to " + timeout);
			}
		}
		
		return doc;
	}

}
