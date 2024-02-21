package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_DOC_VID_SETTINGS;
import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_REGISTRI;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ALG_FREE;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ALG_INDEX_STEP;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ALG_VID_DOC;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL;
import static com.ib.system.SysConstants.CODE_DEFAULT_LANG;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.VpisvaneDAO.VpisvaneDocDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.DocAccess;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.system.BabhClassifAdapter;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.DialectConstructor;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link Doc}
 */
public class DocDAO extends AbstractDAO<Doc> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(DocDAO.class);

	/** DAO for {@link DocAccess} */
	static class DocAccessDAO extends AbstractDAO<DocAccess> {

		/** @param user */
		protected DocAccessDAO(ActiveUser user) {
			super(DocAccess.class, user);
		}
	}

	/** Това ще даде възможност да се генерира номер в отделна транзакция 
	 * @author belev */
	private class GenTransact extends Thread {
		private Doc doc;
		private int alg;
		Exception ex; // и като е в отделна нишка и гръмне няма как да знам и за това тука ще се пази грешката ако е има
		/**
		 * @param doc
		 * @param alg
		 */
		GenTransact(Doc doc, int alg) {
			this.doc = doc;
			this.alg = alg;
		}

		@Override
		public void run() {
			try {
				JPA.getUtil().begin();
				
				if (this.alg == BabhConstants.CODE_ZNACHENIE_ALG_VID_DOC) {
					genRnDocByVidDoc(this.doc);
				} else if (alg == BabhConstants.CODE_ZNACHENIE_ALG_RAZR_PREV_JIV) {
					genRnDocByRegisterAlg4(doc);
				} else {
					genRnDocByRegister(this.doc);
				}
				
				JPA.getUtil().commit();
				
			} catch (Exception e) {
				JPA.getUtil().rollback();
				this.ex = e;

			} finally {
				JPA.getUtil().closeConnection(); // това си е в отделна нишка и задължително трябва да си се затвори само
			}
		}
	}
	/** @param user */
	public DocDAO(ActiveUser user) {
		super(Doc.class, user);
	}

	/**
	 * + разните му не JPA каши и jpa-lazy<br>
	 * За да се изтеглят данни за referent1 трябва да се използва {@link ReferentDAO#findByCodeRef(Object)}, където се подава
	 * {@link Doc#getCodeRefCorresp()}
	 */
	@Override
	public Doc findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		Doc entity = super.findById(id);
		if (entity == null) {
			return entity;
		}

		entity.setDbStatus(entity.getStatus());

		return entity;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public Doc findByIdFromDeloWeb(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		
		try {
			ArrayList<Doc> result = (ArrayList<Doc>) JPA.getUtil().getEntityManager().createQuery("from Doc where workOffId = :idd ").setParameter("idd", id).getResultList();
			if (result.size() > 0) {
				Doc doc = result.get(0); 
				doc.setDbStatus(doc.getStatus());
				return doc;
			}else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("Error in findByIdFromDeloWeb while loading doc wuth id = " + id, e);
			throw new DbErrorException("Error in findByIdFromDeloWeb while loading doc wuth id = " + id, e);
		}
		
		
	}
	
	
	
	

	/** */
	@Override
	public Doc save(Doc entity) throws DbErrorException {
		Doc saved = super.save(entity);

		saved.setDbStatus(entity.getStatus()); // заради JPA мерге
		entity.setDbStatus(entity.getStatus()); // заради JPA мерге - ако не се използва резултата от save-a

		return saved;
	}

	/** 
	 * Запис на деловоден документ + генериране/ валидиране на номера<br>
	 * <b>При нов запис ако документът е генериран по някое вписване трябва да се сетнат {@link Doc#setLicenziantType(Integer)} и
	 * {@link Doc#setIdLicenziant(Integer)}, като се вземат от съответните полета на {@link Vpisvane}</b>
	 * 
	 * @param doc
	 * @param vpisvane има смисъл да се подава, когато за нов запис на док трябва да се направи връзка с таблица vpisvane_doc
	 * @param ud удост.док с различни вариянти за характер
	 * @param result <code>true</code> значи, че докуемента е резултатен и трябва да се сетнат данни в таблица vpisvane
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public Doc saveDelovoden(Doc doc, Vpisvane vpisvane, boolean ud, boolean result, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		SystemData sd = (SystemData) systemData;

		Integer alg = (Integer) sd.getItemSpecific(CODE_CLASSIF_REGISTRI, doc.getRegisterId(), CODE_DEFAULT_LANG, doc.getDocDate(), BabhClassifAdapter.REGISTRI_INDEX_ALG);
		boolean free = alg != null && alg.equals(CODE_ZNACHENIE_ALG_FREE);
				
		if (doc.getId() == null && doc.getRnDoc() != null) { // при нов запис ако има въведен номер трябва да се валидира
			if (doc.getRnDoc().indexOf('#') != -1) {
				throw new ObjectInUseException("Въведен е недопустим символ за номер.");
			}

			if (free) {
				LOGGER.debug("SKIP new doc validate CODE_ZNACHENIE_ALG_FREE");
			} else {
				String errorRnDoc = validateRnDoc(doc, sd, false);
				if (errorRnDoc != null) {
					throw new ObjectInUseException(errorRnDoc);
				}
			}
		}
		
		if (SearchUtils.isEmpty(doc.getGuid())) { // Градим гуид
			doc.setGuid("{" + UUID.randomUUID().toString().toUpperCase() + "}");
		}

		if (!free && SearchUtils.isEmpty(doc.getRnDoc())) { // Градим номер на документа, само ако не е с произволен номер
			generateRnDoc(doc, sd);
			
			String errorRnDoc = validateRnDoc(doc, systemData, true);
			if (errorRnDoc != null) {
				
				doc.setRnDoc(null);
				doc.setRnPrefix(null);
				doc.setRnPored(null);
				
				throw new ObjectInUseException(errorRnDoc 
					+ " Моля, обърнете се към администратор, който да провери стойността на 'Първи свободен №' в избрания регистър.");
			}
		}

		boolean newDoc = doc.getId() == null;

		if (doc.getFreeAccess() == null) {
			doc.setFreeAccess(SysConstants.CODE_ZNACHENIE_DA);
		}

		if (vpisvane != null) {
			doc.setVpisvaneId(vpisvane.getId());
		}

		Doc saved = super.save(doc);

		if (newDoc && vpisvane != null && vpisvane.getId() != null) {
			VpisvaneDoc vpDoc = new VpisvaneDoc();
			vpDoc.setIdDoc(doc.getId());
			vpDoc.setIdVpisvane(vpisvane.getId());

			VpisvaneDAO vpisvaneDao = new VpisvaneDAO(getUser());
			
			if (ud) {
				vpDoc.setPrevUdId(vpisvane.getIdResult());
				
				Object[] last = vpisvaneDao.findLastIncomeDoc(vpisvane.getId(), false);
				if (last != null) {
					vpDoc.setCurrZaiavId(SearchUtils.asInteger(last[0]));
				}
			}

			new VpisvaneDocDAO(getUser()).save(vpDoc, saved, systemData);

			if (result) { // ако е такъв винаги припокрива каквото имаме до сега във вписването
				vpisvane.setIdResult(doc.getId());
				vpisvane.setRegNomResult(doc.getRnDoc());
				vpisvane.setDateResult(doc.getDocDate());
				
				vpisvaneDao.save(vpisvane);
			}
		}
		
		return saved;
	}

	
	/**
	 * Запис на заявление + представляващо лице и заявител. Не би трябвало да се вика ако вече има вписване.
	 * 
	 * @param doc
	 * @param referent1
	 * @param referent2
	 * @return
	 * @throws DbErrorException
	 */
	public Doc saveZaiavlenie(Doc doc, Referent referent1, Referent referent2) throws DbErrorException {
		List<Referent> allReferents = new ArrayList<>();

		ReferentDAO referentDao = new ReferentDAO(getUser());
		
		if (referent2 != null && referent2.getRefType() != null) {
			referentDao.save(referent2, allReferents, false); // запис/корекция
			doc.setCodeRefZaiavitel(referent2.getCode());
		}

		if (referent1 != null) {
			if (referent1.getRefType() == null) {
				referent1.setRefType(CODE_ZNACHENIE_REF_TYPE_FZL);
			}
			referentDao.save(referent1, allReferents, false); // запис/корекция
			doc.setCodeRefCorresp(referent1.getCode());
		}

		if (!Objects.equals(doc.getDbStatus(), doc.getStatus())) {
			doc.setStatusDate(new Date()); // датата се сетва само при смяна на статус
		}

		Doc saved = save(doc); // корекция
		return saved;
	}
	
	/**
	 * Съхранява това като актуален спиък на хора с изричен достъп до документа
	 * 
	 * @param docId
	 * @param docIndentInfo нужно е за журнралиренето
	 * @param accessList
	 * @throws DbErrorException
	 * @throws ObjectInUseException 
	 */
	@SuppressWarnings("unchecked")
	public void saveDocAccess(Integer docId, String docIndentInfo, List<Integer> accessList) throws DbErrorException, ObjectInUseException {
		// тук теоретично ако документа е нов няма нужда се търси дали има достъп в БД и да се гледа после за вписвания,
		// но за сега нека е пълна логиката винаги. Като вземе да се вика от Васко ще се доуточним. 
		
		List<DocAccess> dbList;
		try {
			dbList = createQuery("select x from DocAccess x where x.docId = ?1")
					.setParameter(1, docId).getResultList();
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на служители с достъп до заявление", e);
		}
		Set<Integer> toSave = new HashSet<>(accessList);
		
		DocAccessDAO docAccessDao = new DocAccessDAO(getUser());
		
		for (DocAccess da : dbList) {
			if (!toSave.remove(da.getCodeRef())) {
				da.setIdentInfo(docIndentInfo + " (doc_id="+docId+")");

				docAccessDao.delete(da);
			}
		}
		for (Integer codeRef : toSave) {
			String ident = docIndentInfo + " (doc_id="+docId+")";
			docAccessDao.save(new DocAccess(docId, codeRef, ident));
		}
		
		if (accessList.isEmpty()) {
			return; // няма какво повече да се прави, защото няма какво да се синхронизира
		}
		
		// каквото е дошло към документа без значение ново/старо трябва да се добави към вписването
		// само ако документа има връзка с вписване и вече не са добавени служителите
		List<Object[]> vpisvaneList;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select v.id, v.licenziant ");
			sql.append(" from VpisvaneDoc vd ");
			sql.append(" inner join Vpisvane v on v.id = vd.idVpisvane ");
			sql.append(" where vd.idDoc = ?1 ");
			vpisvaneList = createQuery(sql.toString()).setParameter(1, docId).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на вписване по заявление с ИД=" + docId, e);
		}
		if (!vpisvaneList.isEmpty()) {
			VpisvaneDAO vpisvaneDao = new VpisvaneDAO(getUser());
			for (Object[] vpisvane : vpisvaneList) {
				vpisvaneDao.syncAccessFromDoc((Integer)vpisvane[0], (String)vpisvane[1], accessList);
			}
		}
	}

	/**
	 * Извлича слуижителите с достъп до документа
	 * 
	 * @param docId
	 * @throws DbErrorException
	 * @throws ObjectInUseException 
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findDocAccess(Integer docId) throws DbErrorException {
		List<Integer> dbList;
		try {
			dbList = createQuery("select x.codeRef from DocAccess x where x.docId = ?1 order by x.id")
					.setParameter(1, docId).getResultList();
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на служители с достъп до заявление", e);
		}
		return dbList;
	}
	
	/**
	 * Проверява дали усера, който е в ДАО-то има достъп до документа
	 *
	 * @param doc
	 * @param editMode
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public boolean hasDocAccess(Doc doc, boolean editMode, BaseSystemData sd) throws DbErrorException {
		UserData ud = (UserData) getUser();

		if (!Objects.equals(doc.getDocType(), BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN)) { // не е входящ
			// за собствените документи никак не е ясно как се процедира с достъпа, но за тях няма и да се вика

			if (editMode) { // актуализация само в собстената регистратура
				return Objects.equals(ud.getRegistratura(), doc.getRegistraturaId());

			} else { // преглед, до които имам достъп
				return ud.hasAccess(BabhConstants.CODE_CLASSIF_REGISTRATURI, doc.getRegistraturaId());
			}
		}
		// надолу трябва да са само заявленията
		
		if (!ud.isLimitedAccess() // разширен и може да има достъп през области на контрол
				&& ud.hasAccess(BabhConstants.CODE_CLASSIF_REGISTRATURI, doc.getRegistraturaId())) { // има до регистратуратата

			SystemClassif register = sd.decodeItemLite(BabhConstants.CODE_CLASSIF_VID_REGISTRI, doc.getRegisterId(), getUserLang(), null, false);

			if (register != null // намерен е и усера има достъп до тази област
					&& ud.hasAccess(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL, register.getCodeParent())) {
				return true;
			}
		}

		Number count;
		try { // ще се пуска селект
			String sql = "select count (*) from doc_access where doc_id = ?1 and code_ref = ?2";

			Query query = createNativeQuery(sql) //
					.setParameter(1, doc.getId()).setParameter(2, ud.getUserAccess());
			count = (Number) query.getResultList().get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при проверка за достъп до документ.", e);
		}
		if (count.intValue() > 0) {
			return true; // има достъп
		}

		// обаче ако няма трябва да се провери дали няма достъп до вписването, към което е това заявление
		// до тука ако няма злоупотреби се надявам да се стига много рядко
		List<Object[]> vpisvaneList; // v.id, v.registratura_id, v.id_register
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select v.id, v.registratura_id, v.id_register ");
			sql.append(" from vpisvane_doc vd ");
			sql.append(" inner join vpisvane v on v.id = vd.id_vpisvane ");
			sql.append(" where vd.id_doc = ?1 ");
			vpisvaneList = createNativeQuery(sql.toString()).setParameter(1, doc.getId()).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на вписване по заявление с ИД=" + doc.getId(), e);
		}
		if (!vpisvaneList.isEmpty()) {
			VpisvaneDAO vpisvaneDao = new VpisvaneDAO(getUser());
			
			for (Object[] row : vpisvaneList) {
				Vpisvane vpisvane = new Vpisvane();
			
				// тези трите са нужни за проверка през вписване
				vpisvane.setId(SearchUtils.asInteger(row[0]));
				vpisvane.setRegistraturaId(SearchUtils.asInteger(row[1]));
				vpisvane.setIdRegister(SearchUtils.asInteger(row[2]));

				if (vpisvaneDao.hasVpisvaneAccess(vpisvane, editMode, sd)) {
					// не е много вярно, защото не се знае през кое вписване е тръгнал
					return true; // има достъп през вписване
				}
			}
		}
		return false; 
	}
	
	/**
	 * Актуализира Идентификатор плащане за документа
	 * 
	 * @param docId
	 * @param paymentId
	 * @return
	 * @throws DbErrorException
	 */
	public boolean updatePaymentId(Integer docId, String paymentId) throws DbErrorException {
		try {
			int cnt = createNativeQuery("update doc set payment_id = ?1 where doc_id = ?2")
				.setParameter(1, paymentId).setParameter(2, docId).executeUpdate();
			
			String ident = "Идентификатор плащане: " + paymentId;
			SystemJournal journal = new SystemJournal(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC, docId, ident);

			journal.setCodeAction(SysConstants.CODE_DEIN_SYS_OKA);
			journal.setDateAction(new Date());
			journal.setIdUser(getUserId());
			
			saveAudit(journal);

			return cnt > 0;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при актуализиране на Идентификатор плащане за docId=" + docId, e);
		}
	}
	
	/**
	 * Изтриване на деловоден документ. Ако е по вписване се изтрива и връзката му с вписванто. Към момента трием само собствени.
	 * 
	 * @param doc
	 * @param vpisvane
	 * @param systemData
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public void deleteDelovoden(Doc doc, Vpisvane vpisvane, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		if (doc.getDocType() != null && doc.getDocType().equals(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN)) {
			throw new ObjectInUseException("Не се допуска изтриване на входящи документи.");
		}

		Object[] settings = findDocSettings(doc.getRegistraturaId(), doc.getDocVid(), systemData);
		if (settings != null && Objects.equals(settings[8], BabhConstants.CODE_ZNACHENIE_HAR_DOC_UDOST)) { // [8]-DOC_HAR
			throw new ObjectInUseException("Не се допуска изтриване на удостоверителни документи.");
		}
		
		if (vpisvane != null && vpisvane.getId() != null) {
			doc.setVpisvaneId(vpisvane.getId());

			@SuppressWarnings("unchecked")
			List<VpisvaneDoc> rows = createQuery(
					"select x from VpisvaneDoc x where x.idVpisvane = ?1 and x.idDoc = ?2")
					.setParameter(1, vpisvane.getId()).setParameter(2, doc.getId()).getResultList();

			VpisvaneDocDAO vpisvaneDocDao = new VpisvaneDocDAO(getUser());
			for (VpisvaneDoc vd : rows) {
				vpisvaneDocDao.delete(vd);
			}
			
			if (vpisvane.getIdResult() != null && vpisvane.getIdResult().equals(doc.getId())) {
				throw new ObjectInUseException("Не се допуска изтриване на удостоверителни документи.");
				
				// за момента долния код отпада, докато не се реши как ще се трият УД
				
//				// изтрива се резултатен документ и трябва или да се нулира IdResult(+номер/дата) или да се хване кой е бил последният и той да се изпозлва
//				Integer idResult = null;
//				String regNomResult = null;
//				Date dateResult = null;
//				try {
//					StringBuilder sql = new StringBuilder();
//					sql.append(" select d.doc_id, d.rn_doc, d.doc_date from vpisvane_doc vd inner join doc d on d.doc_id = vd.id_doc ");
//					sql.append(" where vd.id_vpisvane = ?1 "); // по това вписване
//					sql.append(" and d.doc_id <> ?2 "); // различен от този, които трием
//					sql.append(" and d.doc_vid = ?3 "); // но със същия вид
//					sql.append(" order by d.doc_date desc ");
//					
//					@SuppressWarnings("unchecked")
//					List<Object[]> udList = createNativeQuery(sql.toString()).setMaxResults(1)
//						.setParameter(1, vpisvane.getId()).setParameter(2, doc.getId()).setParameter(3, doc.getDocVid())
//						.getResultList();
//					if (!udList.isEmpty()) {
//						idResult = SearchUtils.asInteger(udList.get(0)[0]);
//						regNomResult = (String) udList.get(0)[1];
//						dateResult = (Date) udList.get(0)[2];
//					}
//				} catch (Exception e) {
//					throw new DbErrorException("Грешка при търсене на резултатен документ по вписване.", e);
//				}
//				vpisvane.setIdResult(idResult);
//				vpisvane.setRegNomResult(regNomResult);
//				vpisvane.setDateResult(dateResult);
//				
//				new VpisvaneDAO(getUser()).save(vpisvane);
			}
		}

		delete(doc);
	}

	/** 
	 * Нулира връзката на заявлението с вписванто.
	 * 
	 * @param doc
	 * @param vpisvaneId
	 * @throws DbErrorException
	 * @throws ObjectInUseException 
	 */
	void resetZaqavlenie(Doc doc, Integer vpisvaneId) throws DbErrorException, ObjectInUseException {
		doc.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT);
		doc.setStatusDate(new Date());

		doc.setLicenziantType(null);
		doc.setIdLicenziant(null);
		doc.setKachestvoLice(null);
		doc.setDocPodVid(null);

		doc.setVpisvaneId(vpisvaneId);
		
		if (doc.getProcedura() != null) { // трябва да се разкара и процедурата
			new ProcExeDAO(getUser()).deleteProcByDoc(doc.getId());
			doc.setProcedura(null); // и така после ще си се стартира друга както е дефинирано
		}
		
		save(doc);
	}

	/** за да го има за журнала след мерге */
	@Override
	protected Doc merge(Doc entity) throws DbErrorException {
		Doc merged = super.merge(entity);
		merged.setVpisvaneId(entity.getVpisvaneId());
		return merged;
	}

	/**
	 * Преди да се изтрие документа трябва да се изтрият и други данни, които не са мапнати дирекно през JPA
	 */
	@Override
	protected void remove(Doc entity) throws DbErrorException, ObjectInUseException {
		try {
			// DOC_ACCESS
			int deleted = createNativeQuery("delete from DOC_ACCESS where DOC_ID = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} DOC_ACCESS за документ с DOC_ID={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на документ!", e);
		}
		super.remove(entity);
	}

	/**
	 * Валидира се номера. Ако номера е ОК връща NULL, иначе тескта на причината за невалидност, която може да се покаже на
	 * екрана.
	 *
	 * @param doc
	 * @param includeInDeloId
	 * @param sd
	 * @param generated true означава че номера е генериран през БД в този момент
	 * @return
	 * @throws DbErrorException
	 */
	public String validateRnDoc(Doc doc, BaseSystemData sd, boolean generated) throws DbErrorException {
		String rnDoc = (doc.getRnDoc() != null ? doc.getRnDoc().trim() : null);
		doc.setRnDoc(rnDoc); // важно е да няма интервали в началото и края
		if (doc.getRnDoc() == null || doc.getRnDoc().length() == 0) {
			return null; // няма въведен номер, значи ще се генерира според регистъра
		}

		try {
			String notIdQuery = "";

			if (doc.getId() == null) { // пуска се валидация на номер при определени промени и в корекция
										// и долните проверки са само за нов запис

				if (!generated) { // само ако не е е генериран
					
					// валидиране спрямо регистъра и вид документ
					String errorRnDoc = validateRnDocRegister(doc, sd);
					if (errorRnDoc != null) {
						return errorRnDoc;
					}
				}

			} else {
				notIdQuery = " and DOC_ID != ?5 "; // за корекция да изключи себе си

			}

			// не трябва да има такъв документ в БД по регистратура, номер и година
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(doc.getDocDate());

			int yyyy = gc.get(Calendar.YEAR);

			Query query = createNativeQuery( //
				"select count (*) cnt from DOC where REGISTER_ID = ?1 and upper(RN_DOC) = ?2 and DOC_DATE >= ?3 and DOC_DATE <= ?4" + notIdQuery) //
					.setParameter(1, doc.getRegisterId()).setParameter(2, doc.getRnDoc().toUpperCase());

			int docuMonth = SystemData.getDocuMonth();
			int docuDay = SystemData.getDocuDay();
			
			if (docuMonth == 1 && docuDay == 1) { // отчетния период е календарна година

				gc.set(yyyy, Calendar.JANUARY, 1, 0, 0, 0);
				query.setParameter(3, gc.getTime());

				gc.set(yyyy, Calendar.DECEMBER, 31, 23, 59, 59);
				query.setParameter(4, gc.getTime());
				
				if (doc.getId() != null) {
					query.setParameter(5, doc.getId());
				}
				
				Number count = (Number) query.getResultList().get(0);

				if (count.intValue() > 0) {
					return "Невалиден регистров номер. Вече съществува документ с номер " + formRnDoc(doc.getRnDoc(), null) + " за година " + yyyy + ".";
				}

			} else { // периода е от-до произволна дата
				gc.set(yyyy, docuMonth-1, docuDay, 0, 0, 0); // началото на периода
		
				if (gc.getTimeInMillis() > doc.getDocDate().getTime()) {
					yyyy-=1; // периода е започнал през миналата година
					gc.set(Calendar.YEAR, yyyy);
				}
				query.setParameter(3, gc.getTime());
				Date from = gc.getTime(); // ако има грешка да се изолзва
				
				gc.set(yyyy+1, docuMonth-1, docuDay-1, 23, 59, 59); // предходния ден на следващата година
				query.setParameter(4, gc.getTime());

				if (doc.getId() != null) {
					query.setParameter(5, doc.getId());
				}

				Number count = (Number) query.getResultList().get(0);

				if (count.intValue() > 0) {
					return "Невалиден регистров номер. Вече съществува документ с номер " + formRnDoc(doc.getRnDoc(), null) 
						+ " за период " + DateUtils.printDate(from) + "-" + DateUtils.printDate(gc.getTime()) + ".";
				}
			}

		} catch (Exception e) {
			throw new DbErrorException("Грешка при валидация на номер на документ!", e);
		}

		return null;
	}
	
	/**
	 * Валидиране на въведен номер на документ спрямо регистъра. При нов запис и пререгистриране, когато се дава нов номер на
	 * документ.
	 *
	 * @param doc
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	String validateRnDocRegister(Doc doc, BaseSystemData sd) throws DbErrorException {
		Integer alg = (Integer) sd.getItemSpecific(CODE_CLASSIF_REGISTRI, doc.getRegisterId(), CODE_DEFAULT_LANG, doc.getDocDate(), BabhClassifAdapter.REGISTRI_INDEX_ALG);
		if (alg == null) {
			return "Грешка при валидиране на регистров номер на документ. Липсва алгоритъм!";
		}
		if (alg.equals(CODE_ZNACHENIE_ALG_FREE)) {
			return null;
		}

		int delimiterIndex = doc.getRnDoc().lastIndexOf('-');

		String dbPrefix;
		int dbActNomer;

		try { // трябва да се проверява какво е в БД, за да се провери коректен ли е номера

			if (alg.equals(CODE_ZNACHENIE_ALG_INDEX_STEP)) {
				List<Object[]> rows = createNativeQuery("select PREFIX, ACT_NOMER from REGISTRI where REGISTER_ID = ?1") //
					.setParameter(1, doc.getRegisterId()) //
					.getResultList();
				if (rows.isEmpty()) {
					return "Невалиден регистров номер. За избрания регистър липсва информация в БД!";
				}
				dbPrefix = SearchUtils.trimToNULL((String) rows.get(0)[0]);
				dbActNomer = ((Number) rows.get(0)[1]).intValue();

			} else if (alg.equals(CODE_ZNACHENIE_ALG_VID_DOC)) {
				List<Object[]> rows = createNativeQuery("select PREFIX, ACT_NOMER from DOC_VID_SETTINGS where REGISTER_ID = ?1 and DOC_VID = ?2") //
					.setParameter(1, doc.getRegisterId()).setParameter(2, doc.getDocVid()) //
					.getResultList();
				if (rows.isEmpty()) {
					return "Невалиден регистров номер. За избрания вид документ липсва информация в БД!";
				}
				dbPrefix = SearchUtils.trimToNULL((String) rows.get(0)[0]);
				dbActNomer = ((Number) rows.get(0)[1]).intValue();

			} else {
				return "Грешка при валидиране на регистров номер на документ. Неразпознат алгоритъм: " + alg;
			}

		} catch (Exception e) {
			throw new DbErrorException("Системна грешка при валидиране на регистров номер на документ!", e);
		}

		String rnPrefix = delimiterIndex == -1 ? null : doc.getRnDoc().substring(0, delimiterIndex); // може и да няма въведен префикс
		if (!Objects.equals(dbPrefix, rnPrefix)) {
			String tmp = alg.equals(CODE_ZNACHENIE_ALG_INDEX_STEP) ? "регистър" : "вид документ";
			String tmpPrefix = dbPrefix == null ? " не се допуска префикс" : (" трябва да въведете префикс: " + dbPrefix);
			return "Невалиден регистров номер. За избрания " + tmp + tmpPrefix + ".";
		}

		int rnPored;
		try {
			rnPored = Integer.parseInt(doc.getRnDoc().substring(delimiterIndex + 1, doc.getRnDoc().length()));
		} catch (Exception e) {
			return "Невалиден регистров номер. За пореден номер трябва да въведете цяло число.";
		}
		if (rnPored == dbActNomer) {
			return "Невалиден регистров номер. Моля, включете автоматичното генериране на номер.";
		}
		if (rnPored > dbActNomer) { // ?? ако документа е от минала година трябва ли да се гледа пак спрямо брояча?
			return "Невалиден регистров номер. За пореден номер трябва да въведете число по-малко от " + dbActNomer + ", което да е свободно.";
		}

		// всичко е точно
		doc.setRnPrefix(rnPrefix);
		doc.setRnPored(rnPored);

		return null;
	}
	/**
	 * Генериране на регистров номер на документ
	 *
	 * @param doc
	 * @param sd
	 * @throws DbErrorException
	 * @throws ObjectInUseException 
	 */
	void generateRnDoc(Doc doc, BaseSystemData sd) throws DbErrorException, ObjectInUseException {
		if (doc.getRegisterId() == null) {
			throw new DbErrorException("Грешка при генериране на регистров номер на документ. Липсва регистър!");
		}

		Integer alg = (Integer) sd.getItemSpecific(BabhConstants.CODE_CLASSIF_REGISTRI, doc.getRegisterId(), SysConstants.CODE_DEFAULT_LANG, doc.getDocDate(), BabhClassifAdapter.REGISTRI_INDEX_ALG);
		if (alg == null) {
			throw new DbErrorException("Грешка при генериране на регистров номер на документ. Липсва алгоритъм!");
		}

		if (JPA.getUtil().getDbVendorName().indexOf("POSTGRESQL") != -1) {
			GenTransact gt;
			try { // ще се генерира в отделна нишка защото в процедурите на постгрето няма вътрешно управление на транзакции
				gt = new GenTransact(doc, alg);
				gt.start();
				gt.join();	

			} catch (Exception e) {
				throw new DbErrorException("Системна грешка при генериране на регистров номер на документ ! POSTGRESQL !", e);
			}
			if (gt.ex != null) {
				if (gt.ex instanceof ObjectInUseException) {
					throw (ObjectInUseException) gt.ex;
				} else if (gt.ex instanceof DbErrorException) {
					throw (DbErrorException) gt.ex;
				}
				throw new DbErrorException(gt.ex); // някакво друго чудо е
			}

		} else { // тука в БД си се прави бегин комит и т.н.
		
			if (alg.equals(BabhConstants.CODE_ZNACHENIE_ALG_VID_DOC)) {
				genRnDocByVidDoc(doc);
			} else if (alg.equals(BabhConstants.CODE_ZNACHENIE_ALG_RAZR_PREV_JIV)) {
				genRnDocByRegisterAlg4(doc);
			} else {
				genRnDocByRegister(doc);
			}
		}
	}
	
	/**
	 * Генериране на регистров номер на документ по регистър
	 *
	 * @param doc
	 * @throws DbErrorException
	 */
	private void genRnDocByRegister(Doc doc) throws DbErrorException {
		try {
			StoredProcedureQuery storedProcedure = getEntityManager().createStoredProcedureQuery("gen_nom_register") //
				.registerStoredProcedureParameter(0, Integer.class, ParameterMode.IN) //
				.registerStoredProcedureParameter(1, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(2, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);

			storedProcedure.setParameter(0, doc.getRegisterId());

			storedProcedure.execute();

			doc.setRnDoc((String) storedProcedure.getOutputParameterValue(1));
			doc.setRnPrefix((String) storedProcedure.getOutputParameterValue(2));
			doc.setRnPored((Integer) storedProcedure.getOutputParameterValue(3));

		} catch (Exception e) {
			throw new DbErrorException("Грешка при генериране на регистров номер на документ по регистър!", e);
		}
	}

	/**
	 * Генериране на регистров номер на документ по регистър с алг.=разрешение за превоз на животни
	 *
	 * @param doc
	 * @throws DbErrorException
	 */
	private void genRnDocByRegisterAlg4(Doc doc) throws DbErrorException {
		try {
			StoredProcedureQuery storedProcedure = getEntityManager().createStoredProcedureQuery("gen_nom_register") //
				.registerStoredProcedureParameter(0, Integer.class, ParameterMode.IN) //
				.registerStoredProcedureParameter(1, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(2, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);

			storedProcedure.setParameter(0, doc.getRegisterId());

			storedProcedure.execute();

			doc.setRnDoc((String) storedProcedure.getOutputParameterValue(1));
			doc.setRnPrefix((String) storedProcedure.getOutputParameterValue(2));
			doc.setRnPored((Integer) storedProcedure.getOutputParameterValue(3));

			// 15хххххх и 0хххххх
			doc.setRnDoc(doc.getRnPrefix() + String.format("%06d", doc.getRnPored()));

		} catch (Exception e) {
			throw new DbErrorException("Грешка при генериране на регистров номер на документ по регистър с алг.=разрешение за превоз на животни!", e);
		}
	}

	
	/**
	 * Генериране на регистров номер на документ по вид документ
	 *
	 * @param doc
	 * @throws ObjectInUseException 
	 */
	private void genRnDocByVidDoc(Doc doc) throws ObjectInUseException {
		try {
			StoredProcedureQuery storedProcedure = getEntityManager().createStoredProcedureQuery("gen_nom_vid_doc") //
				.registerStoredProcedureParameter(0, Integer.class, ParameterMode.IN) //
				.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN) //
				.registerStoredProcedureParameter(2, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(3, String.class, ParameterMode.OUT) //
				.registerStoredProcedureParameter(4, Integer.class, ParameterMode.OUT); //

			storedProcedure.setParameter(0, doc.getDocVid());
			storedProcedure.setParameter(1, doc.getRegistraturaId());

			storedProcedure.execute();

			doc.setRnDoc((String) storedProcedure.getOutputParameterValue(2));
			doc.setRnPrefix((String) storedProcedure.getOutputParameterValue(3));
			doc.setRnPored((Integer) storedProcedure.getOutputParameterValue(4));

		} catch (Exception e) {
			throw new ObjectInUseException("За избрания регистър и вид документ не може да бъде генериран регистрационен номер.");
		}
		
		if (doc.getRnDoc() == null || doc.getRnDoc().length() == 0) {
			throw new ObjectInUseException("За избрания регистър и вид документ не може да бъде генериран регистрационен номер.");
		}
	}
	
	/**
	 * Намира настройки по вид документ. Ако няма ред в таблицата по подадените аргументи връща <code>null</code>. Иначе: <br>
	 * [0]-SHEMA_ID<br>
	 * [1]-REGISTER_ID<br>
	 * [2]-CREATE_DELO<br>
	 * [3]-празно<br>
	 * [4]-FILE_OBJECTS.OBJECT_ID !ИД-то на настройката само ако има шаблони! <b>извлича се от регистратура БАБХ</b><br>
	 * [5]-PROC_DEF_IN <b>извлича се от регистратура БАБХ</b><br>
	 * [6]-PROC_DEF_OWN <b>извлича се от регистратура БАБХ</b><br>
	 * [7]-PROC_DEF_WORK <b>извлича се от регистратура БАБХ</b><br>
	 * [8]-DOC_HAR<br>
	 *
	 * @param registratura
	 * @param docVid
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 */
	public Object[] findDocSettings(Integer registratura, Integer docVid, BaseSystemData sd) throws DbErrorException {
		int code = (registratura + "_" + docVid).hashCode();
		Object[] result = (Object[]) sd.getItemSpecifics(CODE_CLASSIF_DOC_VID_SETTINGS, code, CODE_DEFAULT_LANG, null);

		if (registratura != null && registratura.intValue() != BabhConstants.CODE_REGISTRATURA_BABH) {
			// иска се за регистратура която не е бабх
			// но по последни искания в момента процедури и шаблони се дефинират само за бабх
			// и трябва да се дръпнат от там и да се наместат в резултата
			
			int codeBabh = (BabhConstants.CODE_REGISTRATURA_BABH + "_" + docVid).hashCode();
			Object[] resultBabh = (Object[]) sd.getItemSpecifics(CODE_CLASSIF_DOC_VID_SETTINGS, codeBabh, CODE_DEFAULT_LANG, null);
		
			if (resultBabh != null) { // копирам
				
				if (result == null) { // все пак трябва да има и къде да се вкарат
					result = new Object[10];
				}

				if (result[1] == null) { // само ако няма на локално ниво
					result[1] = resultBabh[1];
				}
				result[4] = resultBabh[4];
				result[5] = resultBabh[5];
				result[6] = resultBabh[6];
				result[7] = resultBabh[7];
				result[8] = resultBabh[8];
			}
		}
		return result;
	}

	/**Търсене на информация за плащане на документi
	 * @return Връща готов JSON генериран от базата във формат [] . ТОДО: да се помисли дали да не връща обекти , които да се транфоримрат в йсон за да се ползва метода и от JSF-a
	 */
	public String findDocPayment() throws DbErrorException {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT  ");
		sql.append("    COUNT(myRes),  ");
//		sql.append("    json_agg(myRes) as myJson    ");
		sql.append("    CAST(json_agg(myRes) as TEXT) myJson    ");
		sql.append("FROM  ");
		sql.append("    (   SELECT ");
		sql.append("            d.DOC_ID, ");
		sql.append("            d.doc_vid, ");
		sql.append("            d.doc_date, ");
		sql.append("            v_sc_doc_vid.tekst as doc_vid_text, ");
		sql.append("            d.plateno, ");
		sql.append("            d.pay_type, ");
		sql.append("            v_sc_type_pay.tekst AS pay_type_text, ");
		sql.append("            d.pay_amount, ");
		sql.append("            d.pay_amount_real, ");
		sql.append("            d.pay_date, ");
		sql.append("            d.payment_id, ");
		sql.append("            d.status AS doc_status, ");
		sql.append("            d.register_id, ");
		sql.append("            v_sc.tekst AS register_id_text, ");
		sql.append("            d.registratura_id, ");
		sql.append("            r.registratura                           AS registratura_id_text , ");
		sql.append("            ar.ref_type                         AS predstav_type, ");// -- 3- lice,4-eik
		sql.append("            COALESCE(ar.fzl_egn, ar.nfl_eik)               AS predstav_egn_eik, ");
		sql.append("            d.code_ref_corresp||' '||ar.ref_name           AS predstav, ");
		sql.append("            d.code_usluga                                  AS code_usliga, ");
		sql.append("            zaiavitel.ref_type                             AS zaiavitel_type, ");
		sql.append("            COALESCE(zaiavitel.fzl_egn, zaiavitel.nfl_eik) AS zaiavitel_egn_eik, ");
		sql.append("            zaiavitel.ref_name                             AS zaiavitel, ");
		sql.append("            v_sc_obl_c_text.code                           AS obl_controll, ");
		sql.append("            v_sc_obl_c_text.tekst                          AS obl_controll_text ");

		sql.append("        FROM ");
		sql.append("            DOC                          d ");

		sql.append("            LEFT OUTER JOIN LOCK_OBJECTS z ON z.OBJECT_TIP = 51 AND z.OBJECT_ID = d.DOC_ID ");
		/*           -- разкодиране на регистъра " */
		sql.append("            LEFT JOIN v_system_classif v_sc ON d.register_id= v_sc.code AND v_sc.code_classif=  ");
		sql.append("                520 ");
		/*            --разкодиране на областта на контрол "*/
		sql.append("            LEFT JOIN system_classif sc_obl_c ON d.register_id = sc_obl_c.code AND  ");
		sql.append("                sc_obl_c.code_classif=678 ");
		sql.append("            LEFT JOIN v_system_classif v_sc_obl_c_text ON v_sc_obl_c_text.code=  ");
		sql.append("                sc_obl_c.code_parent AND v_sc_obl_c_text.code_classif=520 ");
		/* 				predstavliavasht*/
		sql.append("            LEFT JOIN adm_referents ar ON ar.code = d.code_ref_corresp ");
		sql.append("            LEFT JOIN registraturi  r  ON r.registratura_id = d.registratura_id ");
		/*            --razkodirane tipa na pla]ane---- " */
		sql.append("            LEFT JOIN v_system_classif v_sc_type_pay ON v_sc_type_pay.code=d.pay_type AND  ");
		sql.append("                v_sc_type_pay.code_classif=524 ");
		/* 				-- zaiavitel */
		sql.append("            left join adm_referents zaiavitel on zaiavitel.code = d.code_ref_zaiavitel ");
		/* 			   -- vid na dokument */
		sql.append("            left join v_system_classif v_sc_doc_vid on v_sc_doc_vid.code=d.doc_vid and v_sc_doc_vid.code_classif=104 ");

		sql.append("        WHERE ");
		sql.append("            d.pay_type <> 1 ");       			//-- Махаме всички които са "без такса"
		sql.append("        AND d.pay_type IS NOT NULL ");			//-- ако е нулл значи е нещо, кито изобщо не се плаща
		sql.append("        AND d.pay_amount > d.pay_amount_real ");//-- значи не е платено
		sql.append("        AND d.pay_amount !=0  ");				//-- ако е 0 значи не е определена още таксата
		sql.append("        AND d.status IN ( 14,15)  ");			//-- "итересуват ни само тези със статус "чака обработка","обработен "
		sql.append("    ) AS myRes");
		Object[] singleResult=null;
		try {
			Query nativeQuery = createNativeQuery(sql.toString());
			//nativeQuery.unwrap(org.hibernate.query.NativeQuery.class).addScalar("myJson", JsonNodeBinaryType.INSTANCE);
			singleResult = (Object[]) nativeQuery.getSingleResult();
			System.out.println("res[0]:"+singleResult[0]);
			System.out.println("res[1]:"+(String)singleResult[1]);
		}catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на основни данни за документ!", e);
		}
		return singleResult!=null? (String) singleResult[1] :"";

	}

	/**
	 * като се подаде елементите от резултата на търсенето, ще се формира правилен номер за визуализиране
	 * 
	 * @param rnDoc
	 * @param docDate
	 * @param pored
	 * @return
	 */
	public static String formRnDocDate(Object rnDoc, Object docDate, Object pored) {
		if (SystemData.isDocPoredDeloGen() && pored != null) {
			return rnDoc + "#" + pored + "/" + DateUtils.printDate((Date) docDate);
		}
		return rnDoc + "/" + DateUtils.printDate((Date) docDate);
	}

	/**
	 * Формира част от селекът за да се слепи поредния номер. Гледа се и настройката.
	 * 
	 * @param alias   трябва да се подава заедно с точката
	 * @param dialect
	 * @return
	 */
	public static String formRnDocSelect(String alias, String dialect) {

		if (SystemData.isDocPoredDeloGen()) {
			if ("SQLSERVER".equals(dialect)) {
				return "case when " + alias + "PORED_DELO is null then " + alias + "RN_DOC else concat(" + alias + "RN_DOC ,'#', " + alias + "PORED_DELO) end";
			}
			return "case when " + alias + "PORED_DELO is null then " + alias + "RN_DOC else " + alias + "RN_DOC || '#' || " + alias + "PORED_DELO end";
		}
		return alias + "RN_DOC";
	}
	
	/**
	 * като се подаде елементите от резултата на търсенето, ще се формира правилен номер за визуализиране
	 * 
	 * @param rnDoc
	 * @param pored
	 * @return
	 */
	public static String formRnDoc(Object rnDoc, Object pored) {
		if (SystemData.isDocPoredDeloGen() && pored != null) {
			return rnDoc + "#" + pored;
		}
		return (String) rnDoc;
	}
	
	/**
	 * Намира основни данни за документа. Резултата е от вида:<br>
	 * [0]-OTNOSNO<br>
	 * [1]-DOC_VID <br>
	 * [2]-GUID<br>
	 * [3]-RN_DOC<br>
	 * [4]-DOC_DATE<br>
	 * [5]-CODE_REF_CORRESP <br>
	 * [6]-GUID_SEOS <br>
	 * [7]-GUID_SSEV <br> 
	 * [8]-ORG_EIK <br> 
	 * [9]-ORG_NAME <br>
	
	
	 * @param id
	 * @return намерения док или NULL ако за това ИД няма данни
	 * @throws DbErrorException
	 */
	public Object[] findDocDataForSeos(Integer id) throws DbErrorException {
		try {
			String dialect = JPA.getUtil().getDbVendorName();
			
			String otnosno = " OTNOSNO ";
			if (dialect.indexOf("ORACLE") != -1) {
				 otnosno = " TO_CHAR(OTNOSNO) ";
			}

			@SuppressWarnings("unchecked")
			List<Object[]> result = createNativeQuery("select "+otnosno+", DOC_VID, GUID, RN_DOC, DOC_DATE, CODE_REF_CORRESP, REGISTRATURI.GUID_SEOS, REGISTRATURI.GUID_SSEV, REGISTRATURI.ORG_EIK, REGISTRATURI.ORG_NAME from doc join REGISTRATURI on doc.REGISTRATURA_ID  =  REGISTRATURI.REGISTRATURA_ID where DOC_ID = :docId").setParameter("docId", id).getResultList();
			if (result.size() == 1) {
				return result.get(0);
			}

			LOGGER.error("findDocDataForSeos(docId={}) NOT FOUND!", id);
			return null;

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на основни данни за документ!", e);
		}
	}
	
	/**
	 * Намира основни данни за документа. Резултата е от вида:<br>
	 * [0]-DOC_ID<br>
	 * [1]-RN_DOC<br>
	 * [2]-DOC_DATE<br>
	 * [3]-DOC_VID<br>
	 * [4]-OTNOSNO (String)<br>
	 * [5]-PORED_DELO<br>
	 *
	 * @param id
	 * @return намерения док или NULL ако за това ИД няма данни
	 * @throws DbErrorException
	 */
	public Object[] findDocData(Integer id) throws DbErrorException {
		try {
			String dialect = JPA.getUtil().getDbVendorName();

			StringBuilder sql = new StringBuilder();
			sql.append(" select DOC_ID, RN_DOC, DOC_DATE, DOC_VID, ");
			sql.append(DialectConstructor.limitBigString(dialect, "OTNOSNO", 300) + " OTNOSNO, PORED_DELO "); // max 300!
			sql.append(" from DOC where DOC_ID = :docId ");

			@SuppressWarnings("unchecked")
			List<Object[]> result = createNativeQuery(sql.toString()).setParameter("docId", id).getResultList();
			if (result.size() == 1) {
				return result.get(0);
			}

			LOGGER.error("findDocData(docId={}) NOT FOUND!", id);
			return null;

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на основни данни за документ!", e);
		}
	}
	
	/**
	 * @param idDoc
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getDocIgnoreList(Integer idDoc) throws DbErrorException {
		return new ArrayList<>();
	}
	
	/**
	 * Актуализира брой прикачени файлове за документа
	 *
	 * @param doc
	 * @param countFiles
	 * @throws DbErrorException
	 */
	public void updateCountFiles(Doc doc, Integer countFiles) throws DbErrorException {
		try {
			createNativeQuery("update DOC set COUNT_FILES = ?1 where DOC_ID = ?2") //
				.setParameter(1, countFiles).setParameter(2, doc.getId()) //
				.executeUpdate();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при актуализиране на брой прикачени файлове за документ.", e);
		}

		doc.setCountFiles(countFiles); // за да се ОК при последващ запис
	}

	
	public void updatePaidTax(Object[] docData, BigDecimal paidAmount, Date payDate) throws DbErrorException {
		try {
			Integer docId = SearchUtils.asInteger(docData[0]);
			
			createNativeQuery(" update DOC set pay_amount_real = ?1 , pay_date = ?2 , plateno = 1 where doc_id = ?3 ")
				.setParameter(1, paidAmount)
				.setParameter(2, payDate)
				.setParameter(3, docId)
				.executeUpdate();

			// трябва да се журналира и това действие
			String ident = "Плащане на дължима такса за заявление: " 
						+ docData[1] + "/" +  docData[4] + "."
						+ " Сума: " + paidAmount + " лв.";
			SystemJournal journal = new SystemJournal(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC, docId, ident);

			journal.setCodeAction(SysConstants.CODE_DEIN_SYS_OKA);
			journal.setDateAction(new Date());
			journal.setIdUser(getUserId());
			
			saveAudit(journal);

		} catch(Exception e) {
			throw new DbErrorException("Грешка при актуализиране на платена такса и дата на плащане за документ.", e);
		}
	}
	public void updateEditTax(Object[] docData, BigDecimal paidAmount, Date payDate) throws DbErrorException {
		try {
			Integer docId = SearchUtils.asInteger(docData[0]);
			LOGGER.info("docId:{}, payAmount:{},oayDate:{}",docId,paidAmount,payDate);
			//createNativeQuery(" update DOC set pay_amount = ?1, pay_amount_real = ?2 , pay_date = ?3 , plateno = 1 where doc_id = ?4 ")
			createNativeQuery(" update DOC1 set  pay_amount_real = :payAmaount , pay_date = :payDate , plateno = 1 where doc_id = :docId ")
			//.setParameter(1, new BigDecimal( (String)docData[9]))
			.setParameter("payAmaount", paidAmount)
			.setParameter("payDate", payDate)
			.setParameter("docId", docId)
			.executeUpdate();
			
			// трябва да се журналира и това действие
			String ident = "Редакция на такса за заявление. Платена сума:"+paidAmount;
//					": "+ docData[1] + "/" +  docData[4] + "."
//					+ "Такса: " + (String)docData[9] + " Сума: " + paidAmount + " лв." + "Дата на плащане:" + payDate;
			SystemJournal journal = new SystemJournal(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC, docId, ident);
			
			journal.setCodeAction(SysConstants.CODE_DEIN_SYS_OKA);
			journal.setDateAction(new Date());
			journal.setIdUser(getUserId());
			
			saveAudit(journal);
			
		} catch(Exception e) {
			throw new DbErrorException("Грешка при актуализиране на платена такса и дата на плащане за документ.", e);
		}
	}
	
}
