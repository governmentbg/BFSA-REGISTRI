package com.ib.babhregs.udostDocs;

import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ShablonBookmark;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.exceptions.DbErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedUdostMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedUdostMethods.class);
    private static final String DB_ERROR_MSG = "Грешка при работа с базата";

    private final Vpisvane vpisvane;
    private final Doc doc;
    private final UserData userData;
    private final SystemData systemData;
    private final Date date;

    public AdvancedUdostMethods(Vpisvane vpisvane, Doc doc, UserData userData, SystemData systemData) {
        this.vpisvane = vpisvane;
        this.doc = doc;
        this.userData = userData;
        this.systemData = systemData;
        this.date = new Date();
    }
    
    /**
     * Получава букмарк за 'подписал' и лице от административната структура
     * и попълва както букмарка, така и тези във fillsAlso списъка му.
     * 
     * ВАЖНО - имената на букмарковете във fillsAlso списъка трябва да отговарят на следните критерии:
     * 
     * <ul>
     * 	<li>Букмарковете за име на подписалия на КИРИЛИЦА трябва да се казват 
     * 		podpisal или podpisalCyr и могат да имат числа накрая - podpisal2, podpisal3, podpisalCyr4, podpisalCyr5...
     *  </li>
     *  <li>Букмарковете за име на подписалия на ЛАТИНИЦА трябва да се казват 
     * 		podpisalLat и могат да имат числа накрая - podpisalLat4, podpisalLat5...
     *  </li>
     *  <li>Букмарковете за длъжността на подписалия трябва да се казват 
     * 		dlajnost и могат да имат числа накрая - dlajnost2, dlajnost3...
     *  </li>
     * </uk> 
     * @param codeAdmStr кода на лицето от административната структура
     * @param bookmark букмаркът, който се попълва
     * @return мап с имената на букмарковете за попълване и стойностите им:
     * 	{podpisal=Иван Иванов, podpisal2=Иван Иванов, podpisalLat=Ivan Ivanov, dlajnost=Служител, ...}
     */
    public Map<String, String> getPodpisalImenaIDlajnostCyrLat(Object codeAdmStr, ShablonBookmark bookmark) {
    	Map<String, String> map = new HashMap<>();

        if(codeAdmStr != null) {
            try {
            	ReferentDAO dao = new ReferentDAO(this.userData);
            	Referent r = dao.findByCode((int)codeAdmStr, new Date(), false);

                String nameCyr = r.getRefName();
                String nameLat = r.getRefLatin();
                String position = this.systemData.decodeItem(
                        BabhConstants.CODE_CLASSIF_POSITION,
                        r.getEmplPosition(),
                        this.userData.getCurrentLang(),
                        date);
                
                map.put(bookmark.getLabel(), nameCyr);
                
                String regexCyr = "^(podpisal(Cyr)?)(\\d*)$";
        		String regexLat = "^(podpisalLat)(\\d*)$";
        		String regexDlajn = "^(dlajnost)(\\d*)$";
                Pattern patternCyr = Pattern.compile(regexCyr);
                Pattern patternLat = Pattern.compile(regexLat);
                Pattern patternDlajn = Pattern.compile(regexDlajn);
                
                if(bookmark.getFillsAlso() != null) {
	                for(String anotherBookmark : bookmark.getFillsAlso()) {
	                	Matcher matcher = patternCyr.matcher(anotherBookmark);
	                	if(matcher.matches()) {
	                		map.put(anotherBookmark, nameCyr);
	                	}
	                	
	                	matcher = patternLat.matcher(anotherBookmark);
	                	if(matcher.matches()) {
	                		map.put(anotherBookmark, nameLat);
	                	}
	                	
	                	matcher = patternDlajn.matcher(anotherBookmark);
	                	if(matcher.matches()) {
	                		map.put(anotherBookmark, position);
	                	}
	                }
                }
            }
            catch(DbErrorException e) {
                LOGGER.error(DB_ERROR_MSG, e);
            }
        }
        else {
        	map.put(bookmark.getLabel(), null);
        	bookmark.getFillsAlso().forEach(label -> {
        		map.put(label, null);
        	});
        }
        
        return map;
    }
}
