package com.ib.babhregs.db.dao;

import java.util.List;

import javax.persistence.Query;

import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;

import com.ib.system.exceptions.DbErrorException;

public class RegisterOptionsDAO extends AbstractDAO<RegisterOptions> {
	
	
    public RegisterOptionsDAO(ActiveUser user) {
        super(user);
    }


    public RegisterOptions save(RegisterOptions entity) throws DbErrorException {
        return super.save(entity);
    }
    
    /**********
     *  ТЪРСИ НАСТРОЙКА ПО ПОДАДЕНО ИД НА РЕГИСТЪР 
     *  
     *  @param idRegister
     *  @return RegisterOptions
     *  @author s.arnaudova
     * **********/
    @SuppressWarnings("unchecked")
    public RegisterOptions findByIdRegister(Integer idRegister) throws DbErrorException {
    	try {
			
    		Query query = createQuery("SELECT r FROM RegisterOptions r WHERE r.registerId = :id") .setParameter("id", idRegister);
    		
			List<RegisterOptions> list = query.getResultList();
			if (list.isEmpty()) {
				return null;
			}
			return list.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbErrorException("Грешка при търсене на настройка по регистър!", e);
		}
    }
    
    
    /**********
     *  ПРОВЕРЯВА ДАЛИ ИЗБРАНИЯ ВХОДЕН ДОКУМЕНТ ВЕЧЕ Е БИЛ ЗАПИСАН 
     *  
     *  @param vidServiceCode
     *  @return boolean
     *  @author s.arnaudova
     * **********/
    @SuppressWarnings("unchecked")
    public boolean checkDocIn(Integer vidDocCode) throws DbErrorException {
    	boolean containsdocAlreadySaved = false;
    	try {
    		
    		Query query = createQuery("SELECT r FROM RegisterOptionsDocsIn r WHERE r.vidDoc = :docCode") .setParameter("docCode", vidDocCode);
    		
    		List<RegisterOptionsDocsIn> resultList = query.getResultList();
    		if (!resultList.isEmpty()) {
    			containsdocAlreadySaved = true;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DbErrorException("Грешка при проверка на въведените входни документи!", e);
		}
    	
		return containsdocAlreadySaved;
    	
    }
}
