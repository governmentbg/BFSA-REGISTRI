package com.ib.babhregs.udostDocs;

import static com.ib.system.utils.ValidationUtils.isNotBlank;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.beans.AdmStruct;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.RegistraturaDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Registratura;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.exceptions.DbErrorException;

public class DefaultValueMethods {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultValueMethods.class);
    private static final String DB_ERROR_MSG = "Грешка при работа с базата";

    private final Vpisvane vpisvane;
    private final Doc doc;
    private final UserData userData;
    private final SystemData systemData;

    public DefaultValueMethods(Vpisvane vpisvane, Doc doc, UserData userData, SystemData systemData) {
        this.vpisvane = vpisvane;
        this.doc = doc;
        this.userData = userData;
        this.systemData = systemData;
    }
    
    /**
     * Методът търси лицето с длъжност Изп. директор (за рег. БАБХ) или Зам. Изп. Директор (за ОДБХ)
     * за настоящата регистратура. Ако има няколко вкарани, избира първото лице в списъка.
     * @return
     */
    public Integer getDefaultPodpisal() {

    	if(this.doc.getPodpisal() != null) {
    		return this.doc.getPodpisal();
    	}
    	
    	ReferentDAO refDao = new ReferentDAO(this.userData);
    	
    	try {
    		List referentCodes = null;
	    	if(this.vpisvane.getRegistraturaId() == BabhConstants.CODE_REGISTRATURA_BABH) {
	    		referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_IZP_DIREKTOR);
			}
	    	else {
	    		referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_DIREKTOR_ODBH);
	    	}
	    	
	    	if(referentCodes != null && !referentCodes.isEmpty()) {
	    		return ((BigInteger) referentCodes.get(0)).intValue();
	    	}
	    	else return null;
    	}
    	catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
    }
    
    /**
     * Някои документи са подписани по подразбиране от Зам. Изп. Директор.
     * Методът търси лицето с длъжност "" към регистратурата.
     * Ако не намери министър, връща празно.
     * @return
     */
    public Integer getDefaultPodpisalZamIzpDirektor() {
    	if(this.doc.getPodpisal() != null) {
    		return this.doc.getPodpisal();
    	}
    	
    	ReferentDAO refDao = new ReferentDAO(this.userData);
    	
    	try {
	    	List referentCodes = null;
	    	referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_ZAM_IZP_DIREKTOR);
	    	
	    	if(referentCodes == null || referentCodes.isEmpty()) {
	    		return null;
	    	}
	    	else {
	    		return ((BigInteger) referentCodes.get(0)).intValue();
	    	}
    	}
    	catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
    }
    
    /**
     * Някои документи са подписани по подразбиране от министър.
     * Методът търси лицето с длъжност министър към регистратурата.
     * Ако не намери министър, търси Изп. директор (за рег. БАБХ) или Зам. Изп. Директор (за ОДБХ)
     * @return
     */
    public Integer getDefaultPodpisalMinister() {
    	if(this.doc.getPodpisal() != null) {
    		return this.doc.getPodpisal();
    	}
    	
    	ReferentDAO refDao = new ReferentDAO(this.userData);
    	
    	try {
	    	List referentCodes = null;
	    	referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_MINISTER);
	    	
	    	if(referentCodes == null) {
	    		if(this.vpisvane.getRegistraturaId() == BabhConstants.CODE_REGISTRATURA_BABH) {
	    			referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_IZP_DIREKTOR);
	    		}
	    		else {
	    			referentCodes = refDao.getReferentByPosition(this.vpisvane.getRegistraturaId(), BabhConstants.CODE_ZNACHENIE_DLAJN_DIREKTOR_ODBH);
	    		}
	    	}
	    	
	    	if(referentCodes != null && !referentCodes.isEmpty()) {
	    		return ((BigInteger) referentCodes.get(0)).intValue();
	    	}
	    	else return null;
    	}
    	catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
    }
    
}
