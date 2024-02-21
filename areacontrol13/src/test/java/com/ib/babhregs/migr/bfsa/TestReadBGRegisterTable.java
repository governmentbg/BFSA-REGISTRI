package com.ib.babhregs.migr.bfsa;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ib.babhregs.db.dto.TempVMP;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;

public class TestReadBGRegisterTable {

	public static void main(String[] args) {
		
		String url = "https://kvmp.bfsa.bg/index.php/bg/registers/veterinarnomedicinski-producty";
		Integer i = 0;
		boolean hasMore = true;
		while (hasMore) {
			String tekUrl = url;
			if (i > 0) {
				tekUrl += "?start="+i;
			}
			hasMore = processPage(tekUrl, i);
			i = i + 30;
		}

	}
	
	
	

	private static boolean processPage(String httpsUrl, Integer id) {
		
		
			
		boolean hasMore = false;
		
		// Connect to the URL and parse the HTML content using Jsoup
        Document document = processURL(httpsUrl);
        
                    
        Elements allRows = document.select("td.list-title");
        //System.out.println("td.list-titlet******************* " + allRows.size());
        
        for (int j = 0; j < allRows.size(); j++) {
        	Element el = allRows.get(j);
        	Element firstLink = el.select("a").first();

            if (firstLink != null) {
                // Get the value of the "href" attribute
                String hrefValue = firstLink.attr("href");               
                System.out.println("https://kvmp.bfsa.bg" + hrefValue);
            } else {
                System.out.println("errrrrr");
            }
        }
        
       
        if (allRows.size() == 30) {
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
