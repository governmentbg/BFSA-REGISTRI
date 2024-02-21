package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.OezReg;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link OezReg}
 *
 * @author belev
 */
public class OezRegDAO extends AbstractDAO<OezReg> {

	/** @param user */
	public OezRegDAO(ActiveUser user) {
		super(OezReg.class, user);
	}

	/**
	 * + разни lazy
	 */
	@Override
	public OezReg findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		OezReg entity = super.findById(id);
		if (entity == null) {
			return entity;
		}

		if (entity.getObektDeinostLica().size() > 0) { // lazy, но трябва и лицето да се вдигне
			ReferentDAO referentDao = new ReferentDAO(getUser());

			for (ObektDeinostLica obektDeinostLica : entity.getObektDeinostLica()) {
				if (obektDeinostLica.getCodeRef() != null) {
					obektDeinostLica.setReferent(referentDao.findByCodeRef(obektDeinostLica.getCodeRef()));
				}
			}
		}
		entity.getOezHarakt().size(); // lazy
		entity.getOezSubOez().size(); // lazy TODO може да трябва да се врътне и за OezSubOezHarakt или да се направят eager
		entity.getOezNomKadastar().size();
		return entity;
	}

	/** */
	@Override
	public OezReg save(OezReg entity) throws DbErrorException {
		OezReg saved = save(entity, new ArrayList<>());
		return saved;
	}

	/**
	 * Запис на ОЕЗ Рег. и всевъзможните му логики. Подават се и текущо записаните лица, защото има логики по проверка на еднакви
	 * лица.
	 *
	 * @param entity
	 * @param allReferents
	 * @return
	 * @throws DbErrorException
	 */
	OezReg save(OezReg entity, List<Referent> allReferents) throws DbErrorException {
		
		boolean newOez = false;
		if (entity.getId() == null) { 
			newOez = true;
		}
		
		if (entity.getOezHarakt() == null) {
			entity.setOezHarakt(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getOezSubOez() == null) {
			entity.setOezSubOez(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		Map<Integer, Referent> referentMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getObektDeinostLica() == null) {
			entity.setObektDeinostLica(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (ObektDeinostLica obektDeinostLica : entity.getObektDeinostLica()) {

				if (obektDeinostLica.getReferent() != null && obektDeinostLica.getReferent().getRefType() != null) {
					obektDeinostLica.setReferent(referentDao.save(obektDeinostLica.getReferent(), allReferents, true));

					obektDeinostLica.setCodeRef(obektDeinostLica.getReferent().getCode());

					referentMap.put(obektDeinostLica.getCodeRef(), obektDeinostLica.getReferent());
				}
				if (obektDeinostLica.getDateBeg() == null) {
					obektDeinostLica.setDateBeg(new Date());
				}
			}
		}

		entity.fixEmptyStringValues();

		OezReg saved = super.save(entity);

		// ObektDeinostLica минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getObektDeinostLica() != null && !saved.getObektDeinostLica().isEmpty()) {
			for (ObektDeinostLica obektDeinostLica : saved.getObektDeinostLica()) {
				obektDeinostLica.setReferent(referentMap.get(obektDeinostLica.getCodeRef()));
			}
		}
		
		return saved;
	}
	
	/** Метода проверява дали вече няма въведено Оез със същият рег.номер стар
	 * @param regNomOezStar , idOez
	 * @return idOEZ or null
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Integer findOezByRegNomStar(String regNomOezStar ,Integer idOez) throws DbErrorException {
		
		try {	
			String sql = "SELECT ID FROM OBEKT_DEINOST WHERE REG_NOMER_STAR like :RNOM";
			if(idOez!=null) {
				sql +=" AND ID<>:IDO";
			}
			Query q = createNativeQuery(sql);
			q.setParameter("RNOM", regNomOezStar.trim());
			if(idOez!=null) {
				q.setParameter("IDO", idOez);
			}
			ArrayList<Object> rez = (ArrayList<Object>) q.getResultList();
			
			if(rez==null || rez.isEmpty()) {
				return null;
			} else {
				return  SearchUtils.asInteger(rez.get(0));
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на ОЕЗ po рег. номер стар!", e);
		}		
	}
	

	/** Метода проверява дали вече няма въведено Оез със същият рег.номер TODO za momenta otpada
	 * @param regNomOez , idOez
	 * @return idOEZ or null
	 * @throws DbErrorException
	 */
//	@SuppressWarnings("unchecked")
//	public Integer findOezByRegNom(String regNomOez ,Integer idOez) throws DbErrorException {
//		
//		try {	
//			String sql = "SELECT ID FROM OBEKT_DEINOST WHERE REG_NOM like :RNOM";
//			if(idOez!=null) {
//				sql +=" AND ID<>:IDO";
//			}
//			Query q = createNativeQuery(sql);
//			q.setParameter("RNOM", regNomOez.trim());
//			if(idOez!=null) {
//				q.setParameter("IDO", idOez);
//			}
//			ArrayList<Integer> rez = (ArrayList<Integer>) q.getResultList();
//			
//			if(rez==null || rez.isEmpty()) {
//				return null;
//			} else {
//				return rez.get(0);
//			}
//		} catch (Exception e) {
//			throw new DbErrorException("Грешка при търсене на ОЕЗ po рег. номер нов!", e);
//		}		
//	}
	
	/** Метода генерира и записва рег номер по регламент ОЕЗ и наименованието на ОЕЗ ако няма вече въведено друго
	 * @param  idOez
	 * @return String regNomerRglament
	 * @throws DbErrorException
	 */
	public String generateAndSaveRegNomReglament(OezReg oez, String migRegNomer) throws DbErrorException {
		
			//***********генерираме рег.номер регламент взето от Анималс *********************
			String regNomRegl;
		
			if (SearchUtils.isEmpty(migRegNomer)) {
				String prefBg = "BG";
				
				String identOez = oez.getId().toString();
				for (int i = 0; i < 12 - oez.getId().toString().length(); i++)
					identOez = "0" + identOez;
				
				
				//ako nqkoga nadhwyrli poweche ot 10 simwola identifikatora
				if(identOez.length()>12) {
					identOez = identOez.substring(0, 12);
				}
				
				regNomRegl = prefBg + identOez;

			} else {
				regNomRegl = migRegNomer;
			}
			
			
			//--------------------------------------генериране имe на оез обект ----------------------------
			
			String oezName = oez.getNaimenovanie();
			
			if(oezName== null || !oezName.isEmpty()) {
				oezName =  regNomRegl + "";
				if(oez.getRegNomerStar()!=null && !oez.getRegNomerStar().isEmpty()) {
					oezName +=" ("+oez.getRegNomerStar()+")";
				}
				
				boolean sobst = false;
				for(ObektDeinostLica  lice: oez.getObektDeinostLica()) {
					if(lice.getDateEnd()!=null && lice.getRole()!= null && lice.getRole().intValue() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK) {
						oezName += " "+lice.getReferent().getIme();
						sobst = true;
						break;
					}
				}
				if(!sobst) { // няма намерен собственик ще търсим оператор
					for(ObektDeinostLica  lice: oez.getObektDeinostLica()) {
						if(lice.getDateEnd()!=null && lice.getRole()!= null && lice.getRole().intValue() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OPERATOR) {
							oezName += " "+lice.getReferent().getIme();
							break;
						}
					}
				}
				oez.setNaimenovanie(oezName);
			}
			//---------------------------------------------------------------------------------------------
			
			try {
				createNativeQuery("UPDATE obekt_deinost SET reg_nom= ?1 ,naimenovanie =?2 WHERE ID = ?3") //
					.setParameter(1, regNomRegl).setParameter(2, oezName).setParameter(3, oez.getId()) //
					.executeUpdate();
				
			} catch (Exception e) {
				throw new DbErrorException("Грешка при запис на рег. номер регламент.", e);
			}
			
			return regNomRegl;
		
	}
	
	
}
