package com.ib.babhregs.migr.bfsa;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ib.babhregs.db.dto.TempVMP;
import com.ib.babhregs.db.dto.TempVMPEU;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.FileUtils;

public class TestReadEURegister {
	
	
	




	public static void main4(String[] args) {
		
		try {
			
			processVLP("https://medicines.health.europa.eu/veterinary/en/600000092131", 1);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public static void main(String[] args) {
		
		try {
			byte[] bytes = FileUtils.getBytesFromFile(new File("D:\\_VLP\\linksEU.txt"));
			
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
	
	private static String  getByPath(Element el, String path) {
		
		String[] items = path.split("/");
		Element temp = el.clone();
		
		for (String item : items) {
			Elements elements = temp.getElementsByClass(item);
			if (elements == null || elements.size() != 1) {
				return "";
			}else {
				temp = elements.get(0);				
			}	 
		}
		
		return temp.text();
		
	}
	
	

	private static void processVLP(String httpsUrl, Integer id) {
		
		try {
			
			System.out.println("-------------------------------------------------------------------");
			TempVMPEU vlp = new TempVMPEU();
			
			// Connect to the URL and parse the HTML content using Jsoup
            Document document = processURL(httpsUrl);
            
            String medCode = httpsUrl.substring(httpsUrl.lastIndexOf("/")+1);
            System.out.println("MedCode: " + medCode);
            vlp.setCode(medCode);
            
            Elements elements = document.getElementsByClass("field products__extra-field-upd-products-medicine-name label-display-inline multiple");
            
            String namesCoded="";
            if (elements.size() == 1) {
            	elements = elements.get(0).getElementsByClass("field__item");
            	
            	for (Element div : elements) {
            		System.out.println("Име: " + div.text());
            		namesCoded += div.text()+"|";
            	}
            	vlp.setNaims(namesCoded);
            	System.out.println("NamesCoded: " + namesCoded);            	
            	System.out.println();
            }else {
            	System.out.println("1****************************************************** ERORR **********************************************");
            }
            
            
            elements = document.getElementsByClass("upd-products-active-substance-strength");
        	String allSubs = "";
        	if (elements.size() == 1) {
        		elements = elements.select("li");
        		
        		
        		for (Element el : elements) {
        			
        			String subsName = getByPath(el, "field upd-substance__title label-display-hidden no-label/field__items col-12 col-md/field__item");
        			System.out.println("SUBS name: " + subsName);
        			
        			String subsStrength = getByPath(el, "field upd-ingredient-strength__field-upd-strength-num-value label-display-hidden no-label/field__item");
        			System.out.println("SUBS strength: " + subsStrength);
        			        			
        			String subsUnits = getByPath(el, "field upd-ingredient-strength__field-upd-strength-num-units label-display-hidden no-label/field__items col-12 col-md/field__item");
        			System.out.println("SUBS units: " + subsUnits);
        			
        			String subsDenom = getByPath(el, "field upd-ingredient-strength__field-upd-strength-denom-units label-display-hidden no-label/field__items col-12 col-md/field__item");
        			System.out.println("SUBS denom: " + subsDenom);
        			
        			String subsCoded = subsName + "|" + subsStrength + "|" + subsUnits + "|" + subsDenom;
        			System.out.println("SUB Coded: " + subsCoded);
        			allSubs += subsCoded + "\r\n";
        			System.out.println();
        		}
        		
        		
        	}else {
            	System.out.println("2****************************************************** ERORR **********************************************");
            }
            
            vlp.setSubs(allSubs);
            
            
            
            try {
				JPA.getUtil().begin();
				 JPA.getUtil().getEntityManager().persist(vlp);
				 JPA.getUtil().commit();
				 JPA.getUtil().getEntityManager().detach(vlp);
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
