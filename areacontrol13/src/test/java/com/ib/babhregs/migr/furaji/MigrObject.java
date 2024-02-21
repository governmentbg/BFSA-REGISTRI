package com.ib.babhregs.migr.furaji;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostFurajiVid;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.SysConstants;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * @author belev
 */
public class MigrObject {

	private Referent zaiavitel;

	private Doc				zaiav;
	private Vpisvane		vpisvane;
	private VpisvaneStatus	status;

	private Doc udost;

	private EventDeinostFuraji	furaji;
	private ObektDeinost		obekt;

	private List<Doc> otherZaiavList;

	/**
	 * @param register
	 */
	public MigrObject(Integer register) {
		this.zaiavitel = new Referent();
		this.zaiavitel.setAddressKoresp(new ReferentAddress());
		this.zaiavitel.getAddressKoresp().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);

		this.zaiav = new Doc();
		this.zaiav.setRegisterId(register);
		this.zaiav.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN);
		this.zaiav.setFreeAccess(SysConstants.CODE_ZNACHENIE_DA);
		this.zaiav.setMethodReg(0);

		this.udost = new Doc();
		this.udost.setRegisterId(BabhConstants.CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER);
		this.udost.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN);
		this.udost.setFreeAccess(SysConstants.CODE_ZNACHENIE_DA);
		this.udost.setMethodReg(0);

		this.vpisvane = new Vpisvane();
		this.vpisvane.setIdRegister(register);
		this.vpisvane.setFromМigr(BabhConstants.CODE_ZNACHENIE_MIGR_END);

		this.status = new VpisvaneStatus();

		this.furaji = new EventDeinostFuraji();
		this.obekt = new ObektDeinost();

		this.otherZaiavList = new ArrayList<>();
	}

	/** @return the furaji */
	public EventDeinostFuraji getFuraji() {
		return this.furaji;
	}

	/** @return the obekt */
	public ObektDeinost getObekt() {
		return this.obekt;
	}

	/** @return the otherZaiavList */
	public List<Doc> getOtherZaiavList() {
		return this.otherZaiavList;
	}

	/** @return the status */
	public VpisvaneStatus getStatus() {
		return this.status;
	}

	/** @return the udost */
	public Doc getUdost() {
		return this.udost;
	}

	/** @return the vpisvane */
	public Vpisvane getVpisvane() {
		return this.vpisvane;
	}

	/** @return the zaiav */
	public Doc getZaiav() {
		return this.zaiav;
	}

	/** @return the zaiavitel */
	public Referent getZaiavitel() {
		return this.zaiavitel;
	}

	/**
	 * @param em
	 * @return ид на записаното вписване
	 * @throws DbErrorException
	 */
	public Integer load(EntityManager em) throws DbErrorException {
		// TODO в момента е само за фуражи с коде_паге=BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI

		// ВАЖНО. Записва се само това, на което е сетнато USER_REG,
		// защото иначе няма критерии кое има данни и кое няма

		Date now = new Date();

		//
		if (this.zaiavitel.getUserReg() != null) { // запис на заявител
			this.zaiavitel.setDateReg(now);

			Referent referent = new ReferentDAO(ActiveUser.DEFAULT) //
					.findByIdent(this.zaiavitel.getNflEik(), this.zaiavitel.getFzlEgn(), null, null);

			if (referent == null) { // няма го и нов запис
				this.zaiavitel.setCode(nextVal(em, "SEQ_ADM_REFERENTS_CODE"));
				this.zaiavitel.setCodePrev(0);
				this.zaiavitel.setCodeParent(0);
				this.zaiavitel.setDateOt(DateUtils.systemMinDate());
				this.zaiavitel.setDateDo(DateUtils.systemMaxDate());

				em.persist(this.zaiavitel);

				if (this.zaiavitel.getAddressKoresp().getUserReg() != null) {
					this.zaiavitel.getAddressKoresp().setDateReg(now);

					this.zaiavitel.getAddressKoresp().setCodeRef(this.zaiavitel.getCode());

					em.persist(this.zaiavitel.getAddressKoresp());
				}
			} else { // използваме този от БД
				this.zaiavitel = referent;
				// TODO какво правим с останалите данни и ако има разлика?!?!
			}
		}

		//
		if (this.zaiav.getUserReg() != null) { // запис на заявление
			this.zaiav.setDateReg(now);

			this.zaiav.setGuid("{" + UUID.randomUUID().toString().toUpperCase() + "}");
			this.zaiav.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE);
			this.zaiav.setIdLicenziant(this.zaiavitel.getCode());
			this.zaiav.setCodeRefZaiavitel(this.zaiavitel.getCode());

			this.zaiav.setCodeRefCorresp(BabhConstants.CODE_ZNACHENIE_REF_SLUJEBEN);
			this.zaiav.setKachestvoLice(4); // Пълномощник

			em.persist(this.zaiav);
		}

		//
		if (this.udost.getUserReg() != null) { // запис на УД
			this.udost.setDateReg(now);

			this.udost.setGuid("{" + UUID.randomUUID().toString().toUpperCase() + "}");

			em.persist(this.udost);
		}

		//
		// запис на вписване - без това няма как
		this.vpisvane.setDateReg(now);

		this.vpisvane.setIdZaqavlenie(this.zaiav.getId());
		this.vpisvane.setRegNomZaqvlVpisvane(this.zaiav.getRnDoc());
		this.vpisvane.setDateZaqvlVpis(this.zaiav.getDocDate());

		this.vpisvane.setIdResult(this.udost.getId());
		this.vpisvane.setRegNomResult(this.udost.getRnDoc());
		this.vpisvane.setDateResult(this.udost.getDocDate());

		this.vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE);
		this.vpisvane.setIdLicenziant(this.zaiavitel.getCode());
		this.vpisvane.setCodeRefVpisvane(this.zaiavitel.getCode());
		this.vpisvane.setLicenziant(this.zaiavitel.getIdentInfo());

		em.persist(this.vpisvane);

		//
		if (this.status.getUserReg() != null) { // вписване статус
			this.status.setDateReg(now);

			this.status.setIdVpisvane(this.vpisvane.getId());

			em.persist(this.status);
		}

		//
		if (this.zaiav.getUserReg() != null) { // вписване док заявление
			VpisvaneDoc vd = new VpisvaneDoc();

			vd.setUserReg(this.zaiav.getUserReg());
			vd.setDateReg(now);

			vd.setIdVpisvane(this.vpisvane.getId());
			vd.setIdDoc(this.zaiav.getId());

			em.persist(vd);
		}

		//
		if (this.udost.getUserReg() != null) { // вписване уд
			VpisvaneDoc vd = new VpisvaneDoc();

			vd.setUserReg(this.udost.getUserReg());
			vd.setDateReg(now);

			vd.setIdVpisvane(this.vpisvane.getId());
			vd.setIdDoc(this.udost.getId());
			vd.setCurrZaiavId(this.zaiav.getId());

			em.persist(vd);
		}

		if (this.furaji.getUserReg() != null) { // фуражи
			this.furaji.setDateReg(now);

			this.furaji.setIdVpisvane(this.vpisvane.getId());

			if (this.obekt.getUserReg() != null) { // обект на дейност
				this.obekt.setDateReg(now);

				this.obekt.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_FURAJI);

				em.persist(this.obekt);

				ObektDeinostDeinost odd = new ObektDeinostDeinost();
				odd.setObektDeinostId(this.obekt.getId());
				odd.setTablEventDeinost(BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

				this.furaji.setObektDeinostDeinost(new ArrayList<>());
				this.furaji.getObektDeinostDeinost().add(odd);

				this.furaji.setSklad(SysConstants.CODE_ZNACHENIE_DA);
				this.furaji.setRegNom(this.obekt.getRegNom()); // TODO тука не е много ясно какво и защо
			}
			em.persist(this.furaji);

			if (this.furaji.getVidList() != null) {
				for (Integer vid : this.furaji.getVidList()) {
					EventDeinostFurajiVid dein = new EventDeinostFurajiVid(this.furaji.getId());
					dein.setVid(vid);

					em.persist(dein);
				}
			}
		}

		for (Doc doc : this.otherZaiavList) {
			if (doc.getUserReg() != null) { // има някакви заявления за промяна
				doc.setDateReg(now);

				doc.setRegisterId(this.zaiav.getRegisterId());
				doc.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN);
				doc.setFreeAccess(SysConstants.CODE_ZNACHENIE_DA);
				doc.setMethodReg(0);

				doc.setGuid("{" + UUID.randomUUID().toString().toUpperCase() + "}");
				doc.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE);
				doc.setIdLicenziant(this.zaiavitel.getCode());
				doc.setCodeRefZaiavitel(this.zaiavitel.getCode());

				doc.setCodeRefCorresp(BabhConstants.CODE_ZNACHENIE_REF_SLUJEBEN);
				doc.setKachestvoLice(4); // Пълномощник

				em.persist(doc);

				VpisvaneDoc vd = new VpisvaneDoc();

				vd.setUserReg(doc.getUserReg());
				vd.setDateReg(now);

				vd.setIdVpisvane(this.vpisvane.getId());
				vd.setIdDoc(doc.getId());

				em.persist(vd);
			}
		}

		return this.vpisvane.getId();
	}

	/**
	 * Дава следващ ключ като се използва sequence
	 *
	 * @param seqName
	 * @return
	 * @throws DbErrorException
	 */
	protected final int nextVal(EntityManager em, String seqName) throws DbErrorException {
		String sql = "SELECT nextval('" + seqName + "') ";
		try {
			Query query = em.createNativeQuery(sql);

			return SearchUtils.asInteger(query.getSingleResult());
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
	}
}
