package com.ib.babhregs.udostDocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.BaseException;

public class UdostDokumentOnCompleteMethods {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UdostDokumentOnCompleteMethods.class);
	private static final String DB_ERROR_MSG = "Грешка при работа с базата";
	
	private final Vpisvane vpisvane;
    private final Doc doc;
    private final UserData userData;
    private final SystemData systemData;

    public UdostDokumentOnCompleteMethods(Vpisvane vpisvane, Doc doc, UserData userData, SystemData systemData) {
        this.vpisvane = vpisvane;
        this.doc = doc;
        this.userData = userData;
        this.systemData = systemData;
    }
    
    public void setPodpisalInDoc(Object codeAdmStruct) {
    	this.doc.setPodpisal((Integer) codeAdmStruct);
    	DocDAO docDao = new DocDAO(this.userData);
    	
    	
		try {
			JPA.getUtil().runInTransaction(() -> {
				docDao.save(this.doc);
			});
		} 
		catch(BaseException e) {
            LOGGER.error(DB_ERROR_MSG, e);
        }
    }
}
