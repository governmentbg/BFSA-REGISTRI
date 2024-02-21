package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.OezReg;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link Vpisvane}
 */
public class VpisvaneDAO extends AbstractDAO<Vpisvane> {
	/** DAO for {@link VpisvaneAccess} */
	static class VpisvaneAccessDAO extends AbstractDAO<VpisvaneAccess> {

		/** @param user */
		protected VpisvaneAccessDAO(ActiveUser user) {
			super(VpisvaneAccess.class, user);
		}
	}

	/** DAO for {@link VpisvaneDoc} */
	static class VpisvaneDocDAO extends AbstractDAO<VpisvaneDoc> {

		/** @param user */
		protected VpisvaneDocDAO(ActiveUser user) {
			super(VpisvaneDoc.class, user);
		}

		/**
		 * При правене на нова връзка на док към вписване, ако по документа не върви процедура се стартира нова (ако е дефинирана) спрямо
		 * х-ки на вид документ.
		 *
		 * @param entity
		 * @param systemData
		 * @return
		 * @throws DbErrorException
		 * @throws ObjectInUseException
		 */
		VpisvaneDoc save(VpisvaneDoc entity, Doc doc, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
			if (entity.getId() == null && doc.getProcedura() == null) { // ще се гледа за процедури
				// TODO неясно кога може да се реши, че документ може да мине по втора процедура, но тогава ще трябва да се гледа и статуса на
				// процедурата
				DocDAO docDao = new DocDAO(getUser());
				Object[] sett = docDao.findDocSettings(doc.getRegistraturaId(), doc.getDocVid(), systemData);

				if (sett != null && doc.getDocType() != null) {
					Integer procDefId = null;
					if (doc.getDocType().intValue() == BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN) {
						procDefId = (Integer) sett[5]; // [5]-PROC_DEF_IN
					} else if (doc.getDocType().intValue() == BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN) {
						procDefId = (Integer) sett[6]; // [6]-PROC_DEF_OWN
					} else if (doc.getDocType().intValue() == BabhConstants.CODE_ZNACHENIE_DOC_TYPE_WRK) {
						procDefId = (Integer) sett[7]; // [7]-PROC_DEF_WORK
					} else {
						LOGGER.error("VpisvaneDoc.save - unknown doc_type={}", doc.getDocType());
					}
					if (procDefId != null) {
						doc.setProcedura(procDefId);
						docDao.save(doc);

						new ProcExeDAO(getUser()).startProc(procDefId, doc.getId(), systemData);
					}
				}
			}

			VpisvaneDoc saved = super.save(entity);
			return saved;
		}
	}

	/** DAO for {@link VpisvaneStatus} */
	static class VpisvaneStatusDAO extends AbstractDAO<VpisvaneStatus> {

		/** @param user */
		protected VpisvaneStatusDAO(ActiveUser user) {
			super(VpisvaneStatus.class, user);
		}
	}

	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(VpisvaneDAO.class);

	/** @param user */
	public VpisvaneDAO(ActiveUser user) {
		super(Vpisvane.class, user);
	}

	/**
	 * Добавя заявление за промяна/заличаване към вписване.
	 *
	 * @param docId
	 * @param vpisvaneId
	 * @param systemData
	 * @throws DbErrorException
	 * @throws InvalidParameterException
	 * @throws ObjectInUseException
	 */
	public void addZaqavlenie(Integer docId, Integer vpisvaneId, BaseSystemData systemData) throws DbErrorException, InvalidParameterException, ObjectInUseException {
		if (docId == null || vpisvaneId == null) {
			throw new InvalidParameterException("Непълни данни: docId=" + docId + "; vpisvaneId=" + vpisvaneId);
		}
		DocDAO docDao = new DocDAO(getUser());

		Doc doc = docDao.findById(docId);
		if (doc == null) {
			throw new InvalidParameterException("Не е намерен документ с ИД=" + docId);
		}
		if (doc.getDocType() != null && !doc.getDocType().equals(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN)) {
			throw new InvalidParameterException("Избраният документ с ИД=" + docId + " не е входящ.");
		}
		if (doc.getStatus() != null && doc.getStatus().equals(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN)) {
			throw new InvalidParameterException("Избраният документ с ИД=" + docId + " вече е обработен.");
		}

		Vpisvane vpisvane = findById(vpisvaneId);
		if (vpisvane == null) {
			throw new InvalidParameterException("Не е намерено вписване с ИД=" + vpisvaneId);
		}

		doc.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN);
		doc.setStatusDate(new Date());

		doc.setLicenziantType(vpisvane.getLicenziantType());
		doc.setIdLicenziant(vpisvane.getIdLicenziant());

		doc.setVpisvaneId(vpisvane.getId());
		docDao.save(doc);

		VpisvaneDoc vpDoc = new VpisvaneDoc();
		vpDoc.setIdDoc(doc.getId());
		vpDoc.setIdVpisvane(vpisvane.getId());

		new VpisvaneDocDAO(getUser()).save(vpDoc, doc, systemData);

		if (Objects.equals(vpisvane.getVpLocked(), SysConstants.CODE_ZNACHENIE_DA)) {
			vpisvane.setVpLocked(SysConstants.CODE_ZNACHENIE_NE);
			save(vpisvane);
		}

		syncAccessFromDoc(vpisvane.getId(), vpisvane.getLicenziant(), docDao.findDocAccess(doc.getId())); // и малко достъп пращам към вписването
	}

	/**
	 * Филтър за справка по код на лице.<br>
	 * [0]-id_vpisvane<br>
	 * [1]-licenziant (текст)<br>
	 * [2]-id_register<br>
	 * [3]-status<br>
	 * [4]-date_status<br>
	 * [5]-reg_nom_result<br>
	 * [6]-date_result<br>
	 * [7]-dein_vid !!! {@link SystemData#decodeItems(Integer, String, Integer, Date)}<br>
	 * [8]-predmet !!! {@link SystemData#decodePredmet(String, Integer, Date)} <br>
	 * [9]-obekt !!! {@link SystemData#decodeObekt(String, Integer, Date)}<br>
	 *
	 * @param codeRef
	 * @param vpisvaneId ако има нещо то това вписване не се връща !!! и се гледа лицето да е само лицензиант
	 * @return
	 * @throws InvalidParameterException
	 */
	public SelectMetadata createSelectReportLice(Integer codeRef, Integer vpisvaneId) throws InvalidParameterException {
		if (codeRef == null) {
			throw new InvalidParameterException("Моля, изберете лице.");
		}

		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		select.append(" select distinct v.id a0, v.licenziant a1 ");
		select.append(" , v.id_register a2, v.status a3, v.date_status a4, v.reg_nom_result a5, v.date_result a6 ");
		select.append(" , r.dein_vid a7, r.predmet a8 ");
		select.append(" , ( select STRING_AGG (COALESCE(od.naimenovanie,'') || '^#^' || COALESCE(od.address,'') || '^#^' || COALESCE(od.nas_mesto,0) || '^#^' || COALESCE(od.darj,0) ");
		select.append(" ,          '|#|') ");
		select.append("     from obekt_deinost od where od.id = ANY (r.od_id_arr) ");
		select.append("   ) a9 ");
		select.append(" , v.code_page a10 ");

		from.append(" from vpisvane v ");
		from.append(" inner join v_spec_report_lice r     on r.vp_id = v.id ");

		if (vpisvaneId == null) {
			from.append(" left outer join doc d               on d.doc_id = v.id_zaqavlenie ");
			from.append(" left outer join event_deinost_jiv_lice dein_jiv_lice on dein_jiv_lice.event_deinost_jiv_id = r.dein_id and r.oblast = 1 ");
			from.append(" left outer join event_deinost_vlp_lice dein_vlp_lice on dein_vlp_lice.event_deinost_vlp_id = r.dein_id and r.oblast = 2 ");
			from.append(" left outer join obekt_deinost_lica od_lice  on od_lice.obekt_deinost_id     = ANY (r.od_id_arr) ");
			from.append(" left outer join mps_lice                    on mps_lice.mps_id              = r.mps_id ");
			from.append(" left outer join vlp_lice                    on vlp_lice.vlp_id              = r.vlp_id ");
		}

		where.append(" where ");

		if (vpisvaneId != null) { // изключваме вписването и слагаме само роля лицензиант
			where.append(" v.id <> " + vpisvaneId + " ");
			where.append(" and (v.id_licenziant = :codeRef and v.licenziant_type = 2) "); // -- лицензиант

		} else { // търсенето е по всички роли на лице във вписване
			where.append(" ( ");
			where.append(" d.code_ref_corresp = :codeRef "); // -- представляващо лице
			where.append(" or v.code_ref_vpisvane = :codeRef "); // -- заявител
			where.append(" or (v.id_licenziant = :codeRef and v.licenziant_type = 2) "); // -- лицензиант
			where.append(" or dein_jiv_lice.code_ref    = :codeRef "); // -- лице в дейности с животни
			where.append(" or dein_vlp_lice.code_ref    = :codeRef "); // -- лице в дейности с влп
			where.append(" or od_lice.code_ref     = :codeRef "); // -- лице в обект
			where.append(" or mps_lice.code_ref    = :codeRef "); // -- лице в мпс
			where.append(" or vlp_lice.code_ref    = :codeRef "); // -- лице в влп
			where.append(" ) ");
		}

		SelectMetadata sm = new SelectMetadata();

		sm.setSqlCount(" select count(distinct v.id) " + from + where);
		sm.setSql("" + select + from + where);
		sm.setSqlParameters(Collections.singletonMap("codeRef", codeRef));

		return sm;
	}

	/**
	 * Изтриване на вписване. На заявленията се сменя статуса. Всичко останало се изтрива. За лицата трябва да се изтрият ако никъде
	 * не се използват. </br>
	 * Лица не се трият. Лицата могат да се трият през кореспонденти и там има валидация за участие по обекти.
	 */
	@Override
	public void delete(Vpisvane entity) throws DbErrorException, ObjectInUseException {
		if (!Objects.equals(entity.getStatus(), BabhConstants.STATUS_VP_WAITING)) {
			// за сега го пускам само за първия статус, защото в следващите приемам, че не е записано по погрешка
			throw new DbErrorException("Позволено е изтриване на вписване само ако е в статус 'в обработка'");
		}

		//
		// vpisvane_status - изтриване
		List<VpisvaneStatus> vpStatusList = findVpisvaneStatusList(entity.getId());
		VpisvaneStatusDAO vpStatusDao = new VpisvaneStatusDAO(getUser());
		for (VpisvaneStatus vpStatus : vpStatusList) {
			vpStatusDao.delete(vpStatus);
		}

		//
		// vpisvane_doc - изтриване
		// doc - собсвтени - изтриване
		// doc - входящи - сменя се статус
		List<VpisvaneDoc> vpDocList = findVpisvaneDocObjList(entity.getId());
		VpisvaneDocDAO vpDocDao = new VpisvaneDocDAO(getUser());
		DocDAO docDao = new DocDAO(getUser());
		for (VpisvaneDoc vpDoc : vpDocList) {
			vpDocDao.delete(vpDoc);

			Doc doc = docDao.findById(vpDoc.getIdDoc());
			if (doc == null) {
				continue; // ако има няма някакви глупости в БД
			}
			if (Objects.equals(doc.getDocType(), BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN)) {
				boolean other = findZaqavlenieVpisvaneOther(doc.getId(), entity.getId());
				if (!other) {
					docDao.resetZaqavlenie(doc, entity.getId());
				}
			} else {
				docDao.delete(doc);
			}
		}

		// TODO ако се трият дейности и те имат обекти, които са само за тях може и обектите да се трият !

		//
		if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
			// event_deinost_jiv +++
			EventDeinJivDAO deinDao = new EventDeinJivDAO(getUser());

			EventDeinJiv dein = deinDao.findByVpisvaneId(entity.getId());
			if (dein != null) {
				deinDao.delete(dein);
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
			// event_deinost_furaji +++
			EventDeinostFurajiDAO deinDao = new EventDeinostFurajiDAO(getUser());

			EventDeinostFuraji dein = deinDao.findByVpisvaneId(entity.getId());
			if (dein != null) {
				deinDao.delete(dein);
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
			// event_deinost_vlp +++
			EventDeinostVlpDAO deinDao = new EventDeinostVlpDAO(getUser());

			EventDeinostVlp dein = deinDao.findByVpisvaneId(entity.getId());
			if (dein != null) {
				deinDao.delete(dein);
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
			// obekt_deinost /vlz +++

			String used = findObektDeinostUsed(entity.getIdLicenziant());
			if (used != null) {
				throw new ObjectInUseException(used);
			}

			boolean other = findLicenziantVpisvaneOther(entity.getIdLicenziant(), entity.getLicenziantType(), entity.getId());
			if (!other) {
				ObektDeinostDAO vlzDao = new ObektDeinostDAO(getUser());

				ObektDeinost vlz = vlzDao.findById(entity.getIdLicenziant());
				if (vlz != null) {
					vlzDao.delete(vlz);
				}
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
			// vlp +++
			VlpDAO vlpDao = new VlpDAO(getUser());

			Vlp vlp = vlpDao.findByVpisvaneId(entity.getId());
			if (vlp != null) {
				vlpDao.delete(vlp);
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS) //
				|| Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
			// mps +++

			String used = findMpsUsed(entity.getIdLicenziant());
			if (used != null) {
				throw new ObjectInUseException(used);
			}

			boolean other = findLicenziantVpisvaneOther(entity.getIdLicenziant(), entity.getLicenziantType(), entity.getId());
			if (!other) {
				MpsDAO mpsDao = new MpsDAO(getUser());

				Mps mps = mpsDao.findById(entity.getIdLicenziant());
				if (mps != null) {
					mpsDao.delete(mps);
				}
			}

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
			// obekt_deinost /oez +++

			String used = findObektDeinostUsed(entity.getIdLicenziant());
			if (used != null) {
				throw new ObjectInUseException(used);
			}

			boolean other = findLicenziantVpisvaneOther(entity.getIdLicenziant(), entity.getLicenziantType(), entity.getId());
			if (!other) {
				OezRegDAO oezRegDao = new OezRegDAO(getUser());

				OezReg oezReg = oezRegDao.findById(entity.getIdLicenziant());
				if (oezReg != null) {
					oezRegDao.delete(oezReg);
				}
			}
		}

		// referent1 +++ - само ако не се използват !?!?
		// referent2 +++ - само ако не се използват !?!?
		// други referent-и +++ - само ако не се използват !?!?

		//
		// vpisvane - изтриване
		super.delete(entity);
	}

	/**
	 * Изтриване на статус.<br>
	 * Има логика със синхронизиране на статус на вписването
	 *
	 * @param vpisvane
	 * @param allStatusList
	 * @param todelId
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public Vpisvane deleteVpisvaneStatus(Vpisvane vpisvane, List<VpisvaneStatus> allStatusList, Integer todelId) throws DbErrorException, ObjectInUseException {
		VpisvaneStatusDAO vpisvaneStatusDao = new VpisvaneStatusDAO(getUser());

		VpisvaneStatus todel = vpisvaneStatusDao.findById(todelId);
		if (todel == null) {
			return vpisvane;
		}
		if (Objects.equals(todel.getStatus(), BabhConstants.STATUS_VP_VPISAN)) {
			throw new ObjectInUseException("Изтриването на статус Вписан не е позволено.");
		}

		vpisvaneStatusDao.delete(todel);

		if (allStatusList == null || allStatusList.isEmpty()) {
			return vpisvane; // не би трябвало да има вариянт, но все пак
		}

		int todelIndex = -1; // трябва да знам кой статус трия
		for (int i = 0; i < allStatusList.size(); i++) {
			if (Objects.equals(allStatusList.get(i).getId(), todelId)) {
				todelIndex = i;
				break;
			}
		}

		if (todelIndex > 0 && todelIndex == allStatusList.size() - 1) {
			// трием последния и трябва да оправим вписването с данните от предпоследния

			VpisvaneStatus prev = allStatusList.get(todelIndex - 1);

			vpisvane.setStatus(prev.getStatus());
			vpisvane.setDateStatus(prev.getDateStatus());

			vpisvane = save(vpisvane);
		}

		if (todelIndex != -1) {
			allStatusList.remove(todelIndex); // махам го и може и да не се тегли списъка отновно
		}
		return vpisvane;
	}

	/**
	 * + разните му не JPA каши и jpa-lazy
	 *
	 * За да се изтеглят данни за referent2 трябва да се използва {@link ReferentDAO#findByCodeRef(Object)}, където се подава
	 * {@link Vpisvane#getCodeRefVpisvane()}
	 */
	@Override
	public Vpisvane findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		Vpisvane entity = super.findById(id);
		if (entity == null) {
			return entity;
		}

		if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {

			EventDeinJiv dein = new EventDeinJivDAO(getUser()).findByVpisvaneId(entity.getId());
			entity.setEventDeinJiv(dein);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {

			EventDeinostFuraji dein = new EventDeinostFurajiDAO(getUser()).findByVpisvaneId(entity.getId());
			entity.setEventDeinostFuraji(dein);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {

			EventDeinostVlp dein = new EventDeinostVlpDAO(getUser()).findByVpisvaneId(entity.getId());
			entity.setEventDeinostVlp(dein);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {

			ObektDeinost obektDeinost = new ObektDeinostDAO(getUser()).findById(entity.getIdLicenziant(), true); // + лицата
			entity.setObektDeinost(obektDeinost);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {

			Vlp vlp = new VlpDAO(getUser()).findByVpisvaneId(entity.getId());
			entity.setVlp(vlp);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS) //
				|| Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {

			Mps mps = new MpsDAO(getUser()).findById(entity.getIdLicenziant());
			entity.setMps(mps);

		} else if (Objects.equals(entity.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {

			OezReg oezReg = new OezRegDAO(getUser()).findById(entity.getIdLicenziant());
			entity.setOezReg(oezReg);

		} else {
			LOGGER.error("UNKNOWN Vpisvane.getCodePage={}", entity.getCodePage());
		}

		return entity;
	}

	/**
	 * Последното заявление по това вписванр. </br>
	 * [0]-doc_id</br>
	 * [1]-rn_doc</br>
	 * [2]-doc_date</br>
	 * [3]-doc_vid</br>
	 *
	 * @param vpisvaneId
	 * @param exludeFirst <code>true</code> изключва се заявлението за първоначално вписване
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Object[] findLastIncomeDoc(Integer vpisvaneId, boolean exludeFirst) throws DbErrorException {
		if (vpisvaneId == null) {
			return null;
		}
		List<Object[]> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select d.doc_id, d.rn_doc, d.doc_date, d.doc_vid ");
			sql.append(" from vpisvane v ");
			sql.append(" inner join vpisvane_doc vd on vd.id_vpisvane = v.id ");
			sql.append(" inner join doc d on d.doc_id = vd.id_doc ");
			sql.append(" where v.id = ?1 and d.doc_type = ?2 ");
			if (exludeFirst) {
				sql.append(" and v.id_zaqavlenie <> d.doc_id "); // то винаги стои във вписването
			}
			sql.append(" order by d.doc_date desc, d.date_reg desc ");

			list = createNativeQuery(sql.toString()) //
					.setParameter(1, vpisvaneId).setParameter(2, BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN) //
					.setMaxResults(1).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на последно заявление за id_vpisvane=" + vpisvaneId, e);
		}
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * Документи за конкретно вписване, по които има процедура. Сортира ги по дата на стартиране на процедура. </br>
	 * [0]-doc_id</br>
	 * [1]-rn_doc</br>
	 * [2]-doc_date</br>
	 * [3]-doc_vid</br>
	 * [4]-proc_exe.exe_id</br>
	 * [5]-proc_exe.def_id</br>
	 * [6]-proc_exe.status</br>
	 * [7]-proc_exe.begin_date</br>
	 * [8]-proc_exe.srok_date</br>
	 *
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findProcDocList(Integer vpisvaneId) throws DbErrorException {
		if (vpisvaneId == null) {
			return new ArrayList<>();
		}
		List<Object[]> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select d.doc_id, d.rn_doc, d.doc_date, d.doc_vid ");
			sql.append(" , p.exe_id, p.def_id, p.status, p.begin_date, p.srok_date ");
			sql.append(" from vpisvane_doc vd ");
			sql.append(" inner join doc d on d.doc_id = vd.id_doc ");
			sql.append(" inner join proc_exe p on p.doc_id = d.doc_id ");
			sql.append(" where vd.id_vpisvane = ?1 ");
			sql.append(" order by p.begin_date desc ");

			list = createNativeQuery(sql.toString()).setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на документи, по които има процедура за id_vpisvane=" + vpisvaneId, e);
		}
		return list;
	}

	/**
	 * Намира последната стартирана процедура за документите по вписването. </br>
	 * [0]-exe_id </br>
	 * [1]-def_id </br>
	 *
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	public Integer[] findProcExeIdent(Integer vpisvaneId) throws DbErrorException {
		Integer[] exeIdent = null;
		if (vpisvaneId == null) {
			return exeIdent;
		}

		try { // в момента търси за последното изпълнение на процедура за документите по вписването
				// ако трябва да търси за последния документ е по различен селекта, но към момента смятам, че и двете са ОК, докато няма две
				// активни процедури, но тогава пак не е ясно коя трябва да се покаже

			StringBuilder sql = new StringBuilder();
			sql.append(" select e.exe_id, e.def_id from vpisvane_doc vd ");
			sql.append(" inner join proc_exe e on e.doc_id = vd.id_doc ");
			sql.append(" where vd.id_vpisvane = ?1 order by e.exe_id desc ");

			@SuppressWarnings("unchecked")
			List<Object[]> rows = createNativeQuery(sql.toString()).setParameter(1, vpisvaneId).getResultList();
			if (!rows.isEmpty()) {
				exeIdent = new Integer[] { SearchUtils.asInteger(rows.get(0)[0]), SearchUtils.asInteger(rows.get(0)[1]) };
			}

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на етапи на процедура за id_vpisvane=" + vpisvaneId, e);
		}

		return exeIdent;
	}

	/**
	 * Намира регистратурата, която се намира на територията на този код по ЕКАТТЕ
	 *
	 * @param ekatte
	 * @return
	 */
	public Integer findRegistraturaByEkatte(Integer ekatte) {
		if (ekatte == null) {
			return null;
		}
		if (ekatte.intValue() == BabhConstants.CODE_EKATTE_SOFIA) {
			return BabhConstants.CODE_REGISTRATURA_SOFIA_GRAD;
		}

		Integer registratura = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select obl.oblast, r.registratura_id ");
			sql.append(" from ekatte_att att ");
			sql.append(" inner join ekatte_oblasti obl on obl.oblast = att.oblast ");
			sql.append(" inner join registraturi r on r.ekatte = obl.ekatte ");
			sql.append(" where att.ekatte = ?1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString()).setParameter(1, ekatte).getResultList();
			if (!rows.isEmpty()) {
				String oblast = (String) rows.get(0)[0];
				if ("SOF".equals(oblast)) { // SOF 68134 София (столица)
					registratura = BabhConstants.CODE_REGISTRATURA_SOFIA_GRAD;
				} else if ("SFO".equals(oblast)) { // SFO 68134 София
					registratura = BabhConstants.CODE_REGISTRATURA_SOFIA_OBLAST;
				} else {
					registratura = SearchUtils.asInteger(rows.get(0)[1]);
				}
			}
		} catch (Exception e) { // няма да се определи
			LOGGER.error("Грешка при търсене на регистратура за ЕКАТТЕ=" + ekatte, e);
		}
		return registratura;
	}

	/**
	 * Връща данни за УД(което е на реда на вписване) + информация за файл със подадения тип.</br>
	 * [0]-doc_id</br>
	 * [1]-rn_doc</br>
	 * [2]-doc_date</br>
	 * [3]-doc_vid</br>
	 * [4]-file_id</br>
	 *
	 * @param vpisvaneId
	 * @param fileType
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Object[] findUdostDocIdentFile(Integer vpisvaneId, Integer fileType) throws DbErrorException {
		if (vpisvaneId == null || fileType == null) {
			return null;
		}
		List<Object[]> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select distinct d.doc_id, d.rn_doc, d.doc_date, d.doc_vid, file_id ");
			sql.append(" from vpisvane v ");
			sql.append(" inner join doc d on d.doc_id = v.id_result ");
			sql.append(" left outer join file_objects fo on fo.object_id = d.doc_id and fo.object_code = ?1 and fo.file_type = ?2 ");
			sql.append(" where v.id = ?3 ");

			list = createNativeQuery(sql.toString()) //
					.setParameter(1, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC).setParameter(2, fileType) //
					.setParameter(3, vpisvaneId) //
					.setMaxResults(1).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на УД за id_vpisvane=" + vpisvaneId, e);
		}
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * Намира сумата за плащане по вписване от всички неплатени заявления или за конкретно заявление (docId)</br>
	 * Ако върне null значи няма за плащене, а ако върне е следното:</br>
	 * [0]-pay_amount - SUM</br>
	 * [1]-pay_amount_real - SUM (може да е нулл)</br>
	 * значи има за плащане
	 *
	 * @param vpisvaneId
	 * @param docId
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Object[] findUnpaidTaxInfo(Integer vpisvaneId, Integer docId) throws DbErrorException {
		if (vpisvaneId == null && docId == null) {
			return null; // трябва поне едно от двете да е подадено
		}

		// TODO -- плаващите не са ясни и ако остене колона plateno, дали не може да реши този проблем
//		3. да махнем колоната от таблица doc  - plateno (платено - да/не) – излишна е
//		4. За неплатени да се считат тези заявление, които са в статус - очаква обработка или обработени, типа на плащане не е "няма такса", и сравнението на двете полета за суми – тук има игра, ако таксата е плаваща и още не е определена
//		5. в вписъка със заявления – да сложим допълнителна колона – да е ясно дали е платено или не, т.е да е ясно какво е положението с таксите

		List<Object[]> list;
		try {
			Integer idObj;

			StringBuilder sql = new StringBuilder();
			sql.append(" select sum(d.pay_amount) pay_amount, sum(d.pay_amount_real) pay_amount_real ");
			sql.append(" from doc d ");
			if (vpisvaneId != null) {
				idObj = vpisvaneId;
				sql.append(" inner join vpisvane_doc vd on vd.id_doc = d.doc_id ");
				sql.append(" where vd.id_vpisvane = :idObj ");
			} else {
				idObj = docId;
				sql.append(" where d.doc_id = :idObj ");
			}
			sql.append(" and d.status in (:statWait, :statObraboten) and d.pay_type is not null and d.pay_type <> :nopay ");
			sql.append(" and d.pay_amount is not null and d.pay_amount != 0 ");
			sql.append(" and (d.pay_amount_real is null or d.pay_amount > d.pay_amount_real) ");

			list = createNativeQuery(sql.toString()).setParameter("idObj", idObj) //
					.setParameter("statWait", BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT) //
					.setParameter("statObraboten", BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN) //
					.setParameter("nopay", BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY) //
					.getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на неплатени суми за id_vpisvane=" + vpisvaneId + " или doc_id=" + docId, e);
		}
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * Извлича слуижителите с достъп до вписването
	 *
	 * @param vpisvaneId
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findVpisvaneAccess(Integer vpisvaneId) throws DbErrorException {
		List<Integer> dbList;
		try {
			dbList = createQuery("select x.codeRef from VpisvaneAccess x where x.vpisvaneId = ?1 order by x.id") //
					.setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на служители с достъп до вписване", e);
		}
		return dbList;
	}

	/**
	 * Документи за конкретно вписване. Сортира ги по дата на документа. </br>
	 * [0]-doc_id</br>
	 * [1]-rn_doc</br>
	 * [2]-doc_date</br>
	 * [3]-doc_vid</br>
	 * [4]-otnosno</br>
	 * [5]-vpisvane_doc.id</br>
	 * [6]-podpisal_doc</br>
	 * [7]-doc_type</br>
	 * [8]-prev_ud_id</br>
	 * [9]-curr_zaiav_id</br>
	 *
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findVpisvaneDocList(Integer vpisvaneId) throws DbErrorException {
		if (vpisvaneId == null) {
			return new ArrayList<>();
		}
		List<Object[]> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select d.doc_id, d.rn_doc, d.doc_date, d.doc_vid, d.otnosno, vd.id, d.podpisal_doc, d.doc_type ");
			sql.append(" , vd.prev_ud_id, vd.curr_zaiav_id ");
			sql.append(" from vpisvane_doc vd ");
			sql.append(" inner join doc d on d.doc_id = id_doc ");
			sql.append(" where vd.id_vpisvane = ?1 ");
			sql.append(" order by d.doc_date, d.doc_id ");

			list = createNativeQuery(sql.toString()).setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на документи за id_vpisvane=" + vpisvaneId, e);
		}
		return list;
	}

	/**
	 * Статуси за конкретно вписване. Сортира ги по дата на статуса.
	 *
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<VpisvaneStatus> findVpisvaneStatusList(Integer vpisvaneId) throws DbErrorException {
		if (vpisvaneId == null) {
			return new ArrayList<>();
		}
		List<VpisvaneStatus> list;
		try {
			list = createQuery("select x from VpisvaneStatus x where x.idVpisvane = ?1 order by x.id") //
					.setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на статуси за id_vpisvane=" + vpisvaneId, e);
		}
		return list;
	}

	/**
	 * Проверява дали усера, който е в ДАО-то има достъп до вписването
	 *
	 * @param vpisvane
	 * @param editMode
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 */
	public boolean hasVpisvaneAccess(Vpisvane vpisvane, boolean editMode, BaseSystemData sd) throws DbErrorException {
		UserData ud = (UserData) getUser();

		if (!ud.isLimitedAccess() // разширен и може да има достъп през области на контрол
				&& ud.hasAccess(BabhConstants.CODE_CLASSIF_REGISTRATURI, vpisvane.getRegistraturaId())) { // има до регистратуратата

			SystemClassif register = sd.decodeItemLite(BabhConstants.CODE_CLASSIF_VID_REGISTRI, vpisvane.getIdRegister(), getUserLang(), null, false);

			if (register != null // намерен е и усера има достъп до тази област
					&& ud.hasAccess(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL, register.getCodeParent())) {
				return true;
			}
		}

		Number count;
		try { // ще се пуска селект
			String sql = "select count (*) from vpisvane_access where vpisvane_id = ?1 and code_ref = ?2";

			Query query = createNativeQuery(sql) //
					.setParameter(1, vpisvane.getId()).setParameter(2, ud.getUserAccess());
			count = (Number) query.getResultList().get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при проверка за достъп до вписване.", e);
		}
		return count.intValue() > 0;
	}

	/**
	 * Премахва заявление за промяна/заличване от вписването
	 *
	 * @param docId
	 * @param vpisvaneId
	 * @param systemData
	 * @throws InvalidParameterException
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	@SuppressWarnings("unchecked")
	public void removeZaqavlenie(Integer docId, Integer vpisvaneId, BaseSystemData systemData) throws InvalidParameterException, DbErrorException, ObjectInUseException {
		if (docId == null || vpisvaneId == null) {
			throw new InvalidParameterException("Непълни данни: docId=" + docId + "; vpisvaneId=" + vpisvaneId);
		}
		DocDAO docDao = new DocDAO(getUser());

		Doc doc = docDao.findById(docId);
		if (doc == null) {
			throw new InvalidParameterException("Не е намерен документ с ИД=" + docId);
		}
		if (doc.getDocType() != null && !doc.getDocType().equals(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN)) {
			throw new InvalidParameterException("Избраният документ с ИД=" + docId + " не е входящ.");
		}

		docDao.resetZaqavlenie(doc, vpisvaneId);

		List<VpisvaneDoc> vpDocList; // може да е добавян няколко пъти заради греши и така ще се разкарат всички
		try {
			vpDocList = createQuery("select x from VpisvaneDoc x where x.idVpisvane = ?1 and x.idDoc = ?2") //
					.setParameter(1, vpisvaneId).setParameter(2, docId).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на връзка документ-вписване!", e);
		}
		for (VpisvaneDoc vpDoc : vpDocList) {
			new VpisvaneDocDAO(getUser()).delete(vpDoc);
		}
	}

	/**
	 * Запис на вписване заедно с данни за заявление + лица, обекти, дейности и т.н.</br>
	 * <b>Ако във allMpsList има нещо, значи ще се направят толкова отделни вписвания - само за новите!!!</b>
	 *
	 * @param vpisvane
	 * @param doc
	 * @param referent1
	 * @param referent2
	 * @param systemData
	 * @param allMpsList [0]-{@link Mps}; [1]-vp_id; [2]-vp_status; [1]и[2] ще ги има след запис
	 * @return последното вписване
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public Vpisvane save(Vpisvane vpisvane, Doc doc, Referent referent1, Referent referent2, BaseSystemData systemData, List<Object[]> allMpsList) throws DbErrorException, ObjectInUseException {
		Vpisvane result = vpisvane;

		if (vpisvane.getId() != null) { // корекция и правим корекция на сетнатото/текущо мпс
			result = save(vpisvane, doc, referent1, referent2, null, systemData);
		}

		if (allMpsList != null && !allMpsList.isEmpty()) { // има новодобавени МПС-та и трябва да се случат нови вписвания
			for (Object[] mpsData : allMpsList) {
				if (mpsData[1] != null) {
					continue; // това МПС е към вписване вече и няма как да напави ново писване
				}

				if (vpisvane.getId() != null && JPA.getUtil().getEntityManager().contains(vpisvane)) {
					JPA.getUtil().getEntityManager().detach(vpisvane);
				}
				vpisvane.setId(null);
				vpisvane.setMps((Mps) mpsData[0]);

				result = save(vpisvane, doc, referent1, referent2, null, systemData);

				// след записа актуализирам с данни от вписването
				mpsData[1] = vpisvane.getId();
				mpsData[2] = vpisvane.getStatus();
			}
		}

		return result;
	}

	/**
	 * Запис на вписване заедно с данни за заявление + лица, обекти, дейности и т.н.
	 *
	 * @param vpisvane
	 * @param doc
	 * @param referent1
	 * @param referent2
	 * @param referent3
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public Vpisvane save(Vpisvane vpisvane, Doc doc, Referent referent1, Referent referent2, Referent referent3, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		List<Referent> allReferents = new ArrayList<>();

		ReferentDAO referentDao = new ReferentDAO(getUser());
		DocDAO docDao = new DocDAO(getUser());

		//
		// --------------------------- ПРЕДСТАВЛЯВАЩО ЛИЦЕ / ЗАЯВИТЕЛ НАЧАЛО --------------------------- //
		if (referent3 != null && referent3.getRefType() != null) {
			referentDao.save(referent3, allReferents, false); // запис/корекция
		}

		if (referent2 != null && referent2.getRefType() != null) {
			referentDao.save(referent2, allReferents, false); // запис/корекция
		}

		if (referent1 != null) {
			if (referent1.getRefType() == null) {
				referent1.setRefType(CODE_ZNACHENIE_REF_TYPE_FZL);
			}
			referentDao.save(referent1, allReferents, false); // запис/корекция
		}

		// --------------------------- ПРЕДСТАВЛЯВАЩО ЛИЦЕ / ЗАЯВИТЕЛ КРАЙ --------------------------- //
		//

		//
		// -------------------------------- ЛИЦЕНЗИАНТ НАЧАЛО -------------------------------- //

		// определям обекта на лицензиране
		Integer idLicenziant = null;
		String licenziant = null; // това е специалния текст, който ще влезне във вписването

		if (Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ) //
				|| Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI) //
				|| Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP) //
				|| Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {

			if (referent3 != null && referent3.getCode() != null) {
				idLicenziant = referent3.getCode();
				licenziant = referent3.getIdentInfo();

			} else if (referent2 != null) {
				// този вече е записан
				idLicenziant = referent2.getCode(); // във всички тези случаи обектът на лицензиране е заявителя
				licenziant = referent2.getIdentInfo();
			} else {
				LOGGER.error("NULL Referent2 for Vpisvane.getCodePage()={}", vpisvane.getCodePage());
			}

		} else if (Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());

			if (vpisvane.getObektDeinost() != null) {
				vpisvane.getObektDeinost().setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ);
				vpisvane.setObektDeinost(obektDeinostDao.save(vpisvane.getObektDeinost(), true, allReferents));

				idLicenziant = vpisvane.getObektDeinost().getId();
				licenziant = vpisvane.getObektDeinost().getIdentInfo();
			} else {
				LOGGER.error("NULL ObektDeinost for Vpisvane.getCodePage()={}", vpisvane.getCodePage());
			}

		} else if (Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS) //
				|| Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
			MpsDAO mpsDao = new MpsDAO(getUser());

			if (vpisvane.getMps() != null) {
				vpisvane.setMps(mpsDao.save(vpisvane.getMps(), true, allReferents));

				idLicenziant = vpisvane.getMps().getId();
				licenziant = vpisvane.getMps().getIdentInfo();
			} else {
				LOGGER.error("NULL Mps for Vpisvane.getCodePage()={}", vpisvane.getCodePage());
			}

		} else if (Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
			OezRegDAO oezRegDao = new OezRegDAO(getUser());

			if (vpisvane.getOezReg() != null) {
				vpisvane.setOezReg(oezRegDao.save(vpisvane.getOezReg(), allReferents));

				idLicenziant = vpisvane.getOezReg().getId();
				licenziant = vpisvane.getOezReg().getIdentInfo();
			} else {
				LOGGER.error("NULL OezReg for Vpisvane.getCodePage()={}", vpisvane.getCodePage());
			}

		} else {
			LOGGER.error("UNKNOWN Vpisvane.getCodePage()={}", vpisvane.getCodePage());
		}

		// -------------------------------- ЛИЦЕНЗИАНТ КРАЙ -------------------------------- //
		//

		//
		// -------------------------------- ВПИСВАНЕ и ЗАЯВЛЕНИЕ НАЧАЛО -------------------------------- //

		vpisvane.setIdLicenziant(idLicenziant);
		vpisvane.setLicenziant(licenziant);

		boolean newVpisvane = vpisvane.getId() == null;

		if (vpisvane.getDateStatus() == null) {
			vpisvane.setDateStatus(new Date());
		}
		if (referent2 != null) {
			vpisvane.setCodeRefVpisvane(referent2.getCode());
		}

		Vpisvane saved = save(vpisvane);

		if (doc != null) {
			if (referent1 != null) {
				doc.setCodeRefCorresp(referent1.getCode());
			}
			doc.setCodeRefZaiavitel(vpisvane.getCodeRefVpisvane());

			if (!Objects.equals(doc.getDbStatus(), doc.getStatus())) {
				doc.setStatusDate(new Date()); // датата се сетва само при смяна на статус
			}

			doc.setLicenziantType(vpisvane.getLicenziantType()); // тук ще остане истинския тип на лицезианта, а не кода на страницата
			doc.setIdLicenziant(idLicenziant);

			doc.setVpisvaneId(vpisvane.getId());
			docDao.save(doc); // корекция
		}

		if (newVpisvane) {
			// при първи запис на вписване трябва да се направи запис и във вписване_статус.
			VpisvaneStatus status = new VpisvaneStatus();

			status.setIdVpisvane(vpisvane.getId());
			status.setStatus(vpisvane.getStatus());
			status.setDateStatus(vpisvane.getDateStatus());
			new VpisvaneStatusDAO(getUser()).save(status);

			// при първи запис на вписване трябва да се направи запис и в вписване_док.
			if (doc != null) {
				VpisvaneDoc vpDoc = new VpisvaneDoc();
				vpDoc.setIdDoc(doc.getId());
				vpDoc.setIdVpisvane(vpisvane.getId());

				new VpisvaneDocDAO(getUser()).save(vpDoc, doc, systemData);

				syncAccessFromDoc(vpisvane.getId(), vpisvane.getLicenziant(), docDao.findDocAccess(doc.getId())); // и малко достъп пращам към вписването
			}
		}

		// -------------------------------- ВПИСВАНЕ и ЗАЯВЛЕНИЕ КРАЙ -------------------------------- //
		//

		//
		// -------------------------------- ДЕЙНОСТ НАЧАЛО -------------------------------- //

		if (vpisvane.getEventDeinJiv() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {

			EventDeinJivDAO deinDao = new EventDeinJivDAO(getUser());

			if (vpisvane.getEventDeinJiv().getId() == null) {
				vpisvane.getEventDeinJiv().setIdVpisvane(vpisvane.getId());
			}
			EventDeinJiv dein = deinDao.save(vpisvane.getEventDeinJiv(), allReferents);
			saved.setEventDeinJiv(dein); // JPA MERGE

		} else if (vpisvane.getEventDeinostFuraji() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {

			EventDeinostFurajiDAO deinDao = new EventDeinostFurajiDAO(getUser());

			if (vpisvane.getEventDeinostFuraji().getId() == null) {
				vpisvane.getEventDeinostFuraji().setIdVpisvane(vpisvane.getId());
			}
			EventDeinostFuraji dein = deinDao.save(vpisvane.getEventDeinostFuraji(), allReferents);
			saved.setEventDeinostFuraji(dein); // JPA MERGE

		} else if (vpisvane.getEventDeinostVlp() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {

			EventDeinostVlpDAO deinDao = new EventDeinostVlpDAO(getUser());

			if (vpisvane.getEventDeinostVlp().getId() == null) {
				vpisvane.getEventDeinostVlp().setIdVpisvane(vpisvane.getId());
			}
			EventDeinostVlp dein = deinDao.save(vpisvane.getEventDeinostVlp(), allReferents);
			saved.setEventDeinostVlp(dein); // JPA MERGE

		} else if (vpisvane.getVlp() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {

			VlpDAO vlpDao = new VlpDAO(getUser());

			if (vpisvane.getVlp().getId() == null) {
				vpisvane.getVlp().setIdVpisvane(vpisvane.getId());
			}
			Vlp vlp = vlpDao.save(vpisvane.getVlp(), allReferents);
			saved.setVlp(vlp);
		}

		// -------------------------------- ДЕЙНОСТ КРАЙ -------------------------------- //
		//

		// заради JPA MERGE, защотo тези се записват преди да се запише вписването. различни са от дейностите
		saved.setMps(vpisvane.getMps());
		saved.setObektDeinost(vpisvane.getObektDeinost());
		saved.setOezReg(vpisvane.getOezReg());

		return saved;
	}

	/**
	 * Запис на нов УД към вписване.
	 *
	 * @param vpisvane
	 * @param opts
	 * @param udost
	 * @param systemData
	 * @return Връща рег.номер на обект, ако е генериран по време на запис на новия документ
	 * @throws ObjectInUseException
	 * @throws DbErrorException
	 */
	public String saveNewUdostDoc(Vpisvane vpisvane, RegisterOptionsDocsOut opts, Doc udost, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		String newRegNomer = null;

		if (Objects.equals(vpisvane.getStatus(), BabhConstants.STATUS_VP_VPISAN) // вписан
				&& opts != null) { // + да има и настройки, за да хване временен->постоянен
			// TODO тука ако са някакви миграции или ръчни въвеждания, не е много ясно дали трябва да се вика изобщо
			newRegNomer = new RegNomGenerator(getUser()).genRegNomer(vpisvane, opts, systemData, null);
		}

		if (udost.getDocType() == null) {
			udost.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN);
		}
		udost.setLicenziantType(vpisvane.getLicenziantType());
		udost.setIdLicenziant(vpisvane.getIdLicenziant());

		if (udost.getRegistraturaId() == null) {
			udost.setRegistraturaId(vpisvane.getRegistraturaId());
		}

		if (udost.getValid() == null) {
			udost.setValid(SysConstants.CODE_ZNACHENIE_DA);
		}
		udost.setValidDate(udost.getDocDate());

		if (opts != null && opts.getPeriodValid() != null && opts.getTypePeriodValid() != null) {
			udost.setDateValidAkt(DateUtils.calculateDate(udost.getDocDate(), opts.getTypePeriodValid(), opts.getPeriodValid()));
		}
//		Integer oldUdostId = vpisvane.getIdResult();

		DocDAO docDao = new DocDAO(getUser());

		boolean result = true; // дали документа влиза като резултатаен във вписването
		Object[] setting = docDao.findDocSettings(vpisvane.getRegistraturaId(), udost.getDocVid(), systemData);
		if (setting != null && Objects.equals(setting[8], BabhConstants.CODE_ZNACHENIE_HAR_IZMENENIE_UD)) {
			result = false;
		}

		// вътре в този запис ще се актуализира и вписването
		docDao.saveDelovoden(udost, vpisvane, true, result, systemData);

//		if (result && oldUdostId != null && !oldUdostId.equals(udost.getId())) {
//			// този трябва да стане невалиден
//			Doc oldUdost = docDao.findById(oldUdostId);
//			if (oldUdost != null) {
//				oldUdost.setVpisvaneId(vpisvane.getId());
//
//				oldUdost.setValid(null); // какво значение трябва да се използва
//				oldUdost.setValidDate(udost.getValidDate());
//
//				docDao.save(oldUdost);
//			}
//		}

		return newRegNomer;
	}

	/**
	 * Съхранява това като актуален спиък на хора с изричен достъп до вписването
	 *
	 * @param vpisvaneId
	 * @param vpisvaneLicenziant нужно е за журналиренето
	 * @param accessList
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	@SuppressWarnings("unchecked")
	public void saveVpisvaneAccess(Integer vpisvaneId, String vpisvaneLicenziant, List<Integer> accessList) throws DbErrorException, ObjectInUseException {
		List<VpisvaneAccess> dbList;
		try {
			dbList = createQuery("select x from VpisvaneAccess x where x.vpisvaneId = ?1") //
					.setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на служители с достъп до вписване", e);
		}
		Set<Integer> toSave = new HashSet<>(accessList);

		VpisvaneAccessDAO vpisvaneAccessDao = new VpisvaneAccessDAO(getUser());

		for (VpisvaneAccess va : dbList) {
			if (!toSave.remove(va.getCodeRef())) {
				va.setIdentInfo(vpisvaneLicenziant + " (vpisvane_id=" + vpisvaneId + ")");

				vpisvaneAccessDao.delete(va);
			}
		}
		for (Integer codeRef : toSave) {
			String ident = vpisvaneLicenziant + " (vpisvane_id=" + vpisvaneId + ")";
			vpisvaneAccessDao.save(new VpisvaneAccess(vpisvaneId, codeRef, ident));
		}
	}

	/**
	 * Запис/корекция на статус.<br>
	 * Ако lastStatus=true, тогава се прави корекция и на Vpisvane-то в случай, че има разлика в кода или датата на статуса.<br>
	 * При смяна на статус към вписан и ако е подаден opts се генерира УД.
	 *
	 * @param vpisvane
	 * @param status
	 * @param lastStatus
	 * @param opts
	 * @param udRn
	 * @param systemData
	 * @param migRegNomer номер при ръчно въвеждане на данни от миграция
	 * @return Връща рег.номер на обект, ако е генериран по време на смяна на статуса
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public String saveVpisvaneStatus(Vpisvane vpisvane, VpisvaneStatus status, boolean lastStatus, RegisterOptionsDocsOut opts //
			, String udRn, BaseSystemData systemData, String migRegNomer) throws DbErrorException, ObjectInUseException {

		new VpisvaneStatusDAO(getUser()).save(status);

		if (!lastStatus) { // ако не е последен не може да влияе на вписването
			return null;
		}
		// надолу е последният статус и ако има промени се актуализира вписването

		boolean statusChange = !Objects.equals(vpisvane.getStatus(), status.getStatus());
		boolean dateStatusChange = !Objects.equals(DateUtils.printDateFull(status.getDateStatus()) //
				, DateUtils.printDateFull(vpisvane.getDateStatus()));

		boolean vpisan = Objects.equals(status.getStatus(), BabhConstants.STATUS_VP_VPISAN);
		boolean udCreate = opts != null && opts.getVidDoc() != null && statusChange && vpisan;

		Integer registerId = BabhConstants.CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER;
		String udostDocVidText = null;
		if (udCreate) { // валидирам дали настройките са ОК
			udostDocVidText = systemData.decodeItem(BabhConstants.CODE_CLASSIF_DOC_VID, opts.getVidDoc(), getUserLang(), null);

			if (vpisvane.getFromМigr() != null && vpisvane.getFromМigr().intValue() == 0) {
				registerId = BabhConstants.CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER;
			} else {
				Object[] settings = new DocDAO(getUser()).findDocSettings(vpisvane.getRegistraturaId(), opts.getVidDoc(), systemData);

				if (settings == null || settings[1] == null) {
					throw new ObjectInUseException("За вид документ " + udostDocVidText + " няма дефиниран деловоден регистър.");
				}
				registerId = (Integer) settings[1];
			}
		}

		String newRegNomer = null;
		if (statusChange && vpisan) { // ако трябва ще се генерира и номер !само при смяна на статус към вписан!

			if (vpisvane.getFromМigr() != null && vpisvane.getFromМigr().intValue() == 0) { // ръчно въвеждане
				migRegNomer = SearchUtils.trimToNULL(migRegNomer);

				if (migRegNomer != null) { // ако е подадено нещо смислено се използва то
					newRegNomer = new RegNomGenerator(getUser()).genRegNomer(vpisvane, opts, systemData, migRegNomer);

				} // ако не е подадено, просто не се генерира номер

			} else {
				newRegNomer = new RegNomGenerator(getUser()).genRegNomer(vpisvane, opts, systemData, null);
			}
		}

		if (udCreate) { // трябва да се прави УД при промяна на статус към вписан
			Integer udostDocVid = opts.getVidDoc();

			vpisvane.setStatus(status.getStatus());
			vpisvane.setDateStatus(status.getDateStatus());

			Doc udost = new Doc();
			udost.setRnDoc(udRn);
			udost.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN);
			udost.setDocVid(udostDocVid);
			udost.setLicenziantType(vpisvane.getLicenziantType());
			udost.setIdLicenziant(vpisvane.getIdLicenziant());
			udost.setRegisterId(registerId);
			udost.setRegistraturaId(vpisvane.getRegistraturaId());
			udost.setDocDate(vpisvane.getDateStatus());
			udost.setOtnosno(udostDocVidText + " за " + vpisvane.getLicenziant());

			udost.setValid(SysConstants.CODE_ZNACHENIE_DA);
			udost.setValidDate(udost.getDocDate());

			if (opts.getPeriodValid() != null && opts.getTypePeriodValid() != null) {
				udost.setDateValidAkt(DateUtils.calculateDate(udost.getDocDate(), opts.getTypePeriodValid(), opts.getPeriodValid()));
			}

			// вътре в този запис ще се актуализира и вписването
			new DocDAO(getUser()).saveDelovoden(udost, vpisvane, true, true, systemData);

		} else if (statusChange || dateStatusChange) { // нормална смяна

			vpisvane.setStatus(status.getStatus());
			vpisvane.setDateStatus(status.getDateStatus());

			save(vpisvane); // обикновена корекция
		}

		return newRegNomer;
	}

	/**
	 * Запис на вписване заедно с данни за заявление + лица, обекти, дейности и т.н. </br>
	 * Спефично за случай, в който лицензианта е в някакви списъци и цялата каша от данни.</br>
	 * Идеята на този метод е да го открие и да го определи като референт3.
	 *
	 * @param vpisvane
	 * @param doc
	 * @param referent1
	 * @param referent2
	 * @param referent3
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public Vpisvane saveWithVlpLiceLicenziant(Vpisvane vpisvane, Doc doc, Referent referent1, Referent referent2, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		Referent referent3 = null;
		if (vpisvane.getEventDeinostVlp() != null && vpisvane.getEventDeinostVlp().getEventDeinostVlpLice() != null) {
			for (EventDeinostVlpLice vlpLice : vpisvane.getEventDeinostVlp().getEventDeinostVlpLice()) {
				if (vlpLice != null && Objects.equals(vlpLice.getTipVraz(), BabhConstants.CODE_ZNACHENIE_PRITEJATE_LICENZ_LICA_OTN)) {
					referent3 = vlpLice.getReferent();
					break; // TODO какво става ако не се намери или пък е сменено !?!?!
				}
			}
		}
		return save(vpisvane, doc, referent1, referent2, referent3, systemData);
	}

	/**
	 * Преди да се изтрие вписването трябва да се изтрият и други данни, които не са мапнати дирекно през JPA
	 */
	@Override
	protected void remove(Vpisvane entity) throws DbErrorException, ObjectInUseException {
		try {
			// VPISVANE_ACCESS
			int deleted = createNativeQuery("delete from VPISVANE_ACCESS where VPISVANE_ID = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} VPISVANE_ACCESS за вписване с VPISVANE_ID={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на вписване!", e);
		}
		super.remove(entity);
	}

	/**
	 * Синхронизира достъпа на вписването спрямо данните от достъпа за заявлението
	 *
	 * @param vpisvaneId
	 * @param vpisvaneLicenziant нужно е за журналиренето
	 * @param docAccessList
	 * @throws DbErrorException
	 */
	void syncAccessFromDoc(Integer vpisvaneId, String vpisvaneLicenziant, List<Integer> docAccessList) throws DbErrorException {
		// тук при ново вписване, може да не се гледа със селект какво е имало, но за сега нека е пълна логиката

		if (docAccessList == null || docAccessList.isEmpty()) {
			return;
		}
		VpisvaneAccessDAO vpisvaneAccessDao = new VpisvaneAccessDAO(getUser());

		List<Integer> vpisvaneAccessList = findVpisvaneAccess(vpisvaneId);

		for (Integer codeRef : docAccessList) {
			if (!vpisvaneAccessList.contains(codeRef)) {
				String ident = vpisvaneLicenziant + " (vpisvane_id=" + vpisvaneId + ")";
				vpisvaneAccessDao.save(new VpisvaneAccess(vpisvaneId, codeRef, ident));
			}
		}
	}

	/** @return да/не в зависимост от това дали лицензианта има друго вписване */
	private boolean findLicenziantVpisvaneOther(Integer licenziantId, Integer licenziantType, Integer vpId) throws DbErrorException {
		try {
			Query query = createNativeQuery( //
					"select id from vpisvane where id_licenziant = ?1 and licenziant_type = ?2 and id <> ?3") //
					.setParameter(1, licenziantId).setParameter(2, licenziantType).setParameter(3, vpId);
			return !query.getResultList().isEmpty();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на вписвания за Лицензиант (ид=" + licenziantId + ", тип=" + licenziantType + ")", e);
		}
	}

	/** @return текст за грешка и не се позволява да се изтрие. ако е нулл сме ок и трием */
	private String findMpsUsed(Integer mpsId) throws DbErrorException {
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> checkList = createNativeQuery( //
					"select id, tabl_event_deinost, deinost_id from mps_deinost where mps_id = ?1") //
					.setParameter(1, mpsId).getResultList();
			if (checkList.isEmpty()) {
				return null; // не се използва и може да се трие
			}

			int mpsDeinostId = ((Number) checkList.get(0)[0]).intValue();
			Integer tablEventDeinost = SearchUtils.asInteger(checkList.get(0)[1]);
			Integer deinostId = SearchUtils.asInteger(checkList.get(0)[2]);

			if (tablEventDeinost == null || deinostId == null) {
				LOGGER.error("!!! NULL mps_deinost data: id={}; tabl_event_deinost={}; deinost_id={}; !!!" //
						, mpsDeinostId, tablEventDeinost, deinostId);
				return "Невалидни данни за таблица Дейност –МПС (ИД=" + mpsDeinostId + ")";
			}

			String deinostTable;
			String deinostIme;
			if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV) {
				deinostTable = "event_deinost_jiv";
				deinostIme = "Дейности с животни";
			} else if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI) {
				deinostTable = "event_deinost_furaji";
				deinostIme = "Дейности с фуражи";
			} else if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP) {
				deinostTable = "event_deinost_vlp";
				deinostIme = "Дейности с ВЛП";
			} else {
				LOGGER.error("!!! UNKNOWN mps_deinost.tabl_event_deinost={} for id={} !!!", tablEventDeinost, mpsDeinostId);
				return "Невалидни данни за таблица Дейност –МПС (ИД=" + mpsDeinostId + ")";
			}

			StringBuilder sql = new StringBuilder();
			sql.append(" select v.id vpisvane_id, v.licenziant ");
			sql.append(" from vpisvane v ");
			sql.append(" inner join " + deinostTable + " e on e.id_vpisvane = v.id ");
			sql.append(" where e.id = ?1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> errorList = createNativeQuery(sql.toString()) //
					.setParameter(1, deinostId).getResultList();

			String error = null;
			if (!errorList.isEmpty()) { // ако няма вписване няма и какво да го спира
				error = "Регистрираното ТС се използва в " + deinostIme + ", за лицензиант " + errorList.get(0)[1];
			}
			return error;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на дейности за ТС с ид=" + mpsId, e);
		}
	}

	/** @return текст за грешка и не се позволява да се изтрие. ако е нулл сме ок и трием */
	private String findObektDeinostUsed(Integer obektDeinostId) throws DbErrorException {
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> checkList = createNativeQuery( //
					"select id, tabl_event_deinost, deinost_id from obekt_deinost_deinost where obekt_deinost_id = ?1") //
					.setParameter(1, obektDeinostId).getResultList();
			if (checkList.isEmpty()) {
				return null; // не се използва и може да се трие
			}

			int oddId = ((Number) checkList.get(0)[0]).intValue();
			Integer tablEventDeinost = SearchUtils.asInteger(checkList.get(0)[1]);
			Integer deinostId = SearchUtils.asInteger(checkList.get(0)[2]);

			if (tablEventDeinost == null || deinostId == null) {
				LOGGER.error("!!! NULL obekt_deinost_deinost data: id={}; tabl_event_deinost={}; deinost_id={}; !!!" //
						, oddId, tablEventDeinost, deinostId);
				return "Невалидни данни за таблица Обект на дейност -Дейност (ИД=" + oddId + ")";
			}

			String deinostTable;
			String deinostIme;
			if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV) {
				deinostTable = "event_deinost_jiv";
				deinostIme = "Дейности с животни";
			} else if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI) {
				deinostTable = "event_deinost_furaji";
				deinostIme = "Дейности с фуражи";
			} else if (tablEventDeinost == BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP) {
				deinostTable = "event_deinost_vlp";
				deinostIme = "Дейности с ВЛП";
			} else {
				LOGGER.error("!!! UNKNOWN obekt_deinost_deinost.tabl_event_deinost={} for id={} !!!", tablEventDeinost, oddId);
				return "Невалидни данни за таблица Обект на дейност -Дейност (ИД=" + oddId + ")";
			}

			StringBuilder sql = new StringBuilder();
			sql.append(" select v.id vpisvane_id, v.licenziant ");
			sql.append(" from vpisvane v ");
			sql.append(" inner join " + deinostTable + " e on e.id_vpisvane = v.id ");
			sql.append(" where e.id = ?1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> errorList = createNativeQuery(sql.toString()) //
					.setParameter(1, deinostId).getResultList();

			String error = null;
			if (!errorList.isEmpty()) { // ако няма вписване няма и какво да го спира
				error = "Регистрираният обект на дейност се използва в " + deinostIme + ", за лицензиант " + errorList.get(0)[1];
			}
			return error;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на дейности за обект на дейност с ид=" + obektDeinostId, e);
		}
	}

	/**
	 * Връзката вписване-док като обекти за конкретно вписване. Сортира ги по ИД.
	 *
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	private List<VpisvaneDoc> findVpisvaneDocObjList(Integer vpisvaneId) throws DbErrorException {
		if (vpisvaneId == null) {
			return new ArrayList<>();
		}
		List<VpisvaneDoc> list;
		try {
			list = createQuery("select x from VpisvaneDoc x where x.idVpisvane = ?1 order by x.id") //
					.setParameter(1, vpisvaneId).getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на вписване-док за id_vpisvane=" + vpisvaneId, e);
		}
		return list;
	}

	/** @return да/не в зависимост от това дали заявлението има друго вписване */
	private boolean findZaqavlenieVpisvaneOther(Integer docId, Integer vpId) throws DbErrorException {
		try {
			Query query = createNativeQuery( //
					"select id from vpisvane_doc where id_doc = ?1 and id_vpisvane <> ?2") //
					.setParameter(1, docId).setParameter(2, vpId);
			return !query.getResultList().isEmpty();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на вписвания за Заявление с ид=" + docId, e);
		}
	}
}
