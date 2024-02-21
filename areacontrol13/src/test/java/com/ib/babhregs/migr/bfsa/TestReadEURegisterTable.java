package com.ib.babhregs.migr.bfsa;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ib.babhregs.db.dto.TempVMP;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;

public class TestReadEURegisterTable {

	public static void main(String[] args) {
		
		Integer i = 155;
		String url = "https://medicines.health.europa.eu/veterinary/en/search?f[0]=authorised_in%3A8256&page=";
		
		boolean hasMore = true;
		while (hasMore) {
			String tekUrl = url;			
			hasMore = processPage(tekUrl+i, i);
			i ++;
		}

	}
	
	
	

	private static boolean processPage(String httpsUrl, Integer id) {
		
		
			
		boolean hasMore = false;
		
		// Connect to the URL and parse the HTML content using Jsoup
        Document document = processURL(httpsUrl);
        
                    
        Elements allRows = document.select("div.field.products__extra-field-upd-products-product-title-linked.label-display-hidden.no-label.multiple");
        //System.out.println("td.list-titlet******************* " + allRows.size());
        
        for (int j = 0; j < allRows.size(); j++) {
        	Element el = allRows.get(j);
        	Element firstLink = el.select("a").first();

            if (firstLink != null) {
                // Get the value of the "href" attribute
                String hrefValue = firstLink.attr("href");               
                System.out.println("https://medicines.health.europa.eu/veterinary" + hrefValue);
            } else {
                System.out.println("errrrrr");
            }
        }
        
       
        if (allRows.size() == 10) {
        	return true;
        }else {
        	return false;
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
