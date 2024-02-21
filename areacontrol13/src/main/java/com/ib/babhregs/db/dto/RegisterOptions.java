package com.ib.babhregs.db.dto;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.PersistentEntity;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

import javax.faces.application.FacesMessage;
import javax.persistence.*;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "register_options")

public class RegisterOptions extends TrackableEntity implements AuditExt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -241834101307772650L;

	@SequenceGenerator(name = "RegisterOptions", sequenceName = "seq_register_options", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "RegisterOptions")
	@Column(name = "id")
	@JournalAttr(label="id",defaultText = "Системен идентификатор", isId = "true")
	private Integer id;
	@Basic
	@Column(name = "register_id")
	@JournalAttr(label="registerId",defaultText = "Регистър",classifID = ""+BabhConstants.CODE_CLASSIF_VID_REGISTRI)
	private Integer registerId;
	
	@Basic
	@Column(name = "object_type")
	@JournalAttr(label="objectType",defaultText = "Тип на обекта",classifID = ""+BabhConstants.CODE_CLASSIF_TIP_OBEKT_LICENZ)
	private Integer objectType;
//	@Basic
//	@Column(name = "required_lat")
//	private Integer requiredLat = 2;
//	@Basic
//	@Column(name = "only_person")
//	private Integer onlyPerson = 2;
//	@Basic
//	@Column(name = "only_firma")
//	private Integer onlyFirma = 2;
//	@Basic
//	@Column(name = "person_foreigner")
//	private Integer personForeigner = 2;
//	@Basic
//	@Column(name = "firma_foreigner")
//	private Integer firmaForeigner = 2;
	@Basic
	@Column(name = "register_sql")
	@JournalAttr(label="registerSql",defaultText = "SQL за регистъра")
	private String registerSql;

	/**
	 * Входни документи за регистъра
	 */
	@JournalAttr(label="docsIn",defaultText = "Входни документи за регистъра")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "registerOptions", orphanRemoval = true)
	private List<RegisterOptionsDocsIn> docsIn = new ArrayList<RegisterOptionsDocsIn>();

	/**
	 * Изходни документи
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "registerOptions", orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	@JournalAttr(label="docsOut",defaultText = "Изходни документи за регистъра")
	private List<RegisterOptionsDocsOut> docsOut = new ArrayList<RegisterOptionsDocsOut>();

	/**
	 * Структура на екрана
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "registerOptions", orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	@JournalAttr(label="display",defaultText = "Структура на екрана")
	private List<RegisterOptionsDisplay> display = new ArrayList<RegisterOptionsDisplay>();

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_REGISTER_OPTIONS;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}

	public Integer getObjectType() {
		return objectType;
	}

	public void setObjectType(Integer objectType) {
		this.objectType = objectType;
	}

//	public Integer getRequiredLat() {
//		return requiredLat;
//	}
//
//	public void setRequiredLat(Integer requiredLat) {
//		this.requiredLat = requiredLat;
//	}
//
//	public Integer getOnlyPerson() {
//		return onlyPerson;
//	}
//
//	public void setOnlyPerson(Integer onlyPerson) {
//		this.onlyPerson = onlyPerson;
//	}
//
//	public Integer getOnlyFirma() {
//		return onlyFirma;
//	}
//
//	public void setOnlyFirma(Integer onlyFirma) {
//		this.onlyFirma = onlyFirma;
//	}
//
//	public Integer getPersonForeigner() {
//		return personForeigner;
//	}
//
//	public void setPersonForeigner(Integer personForeigner) {
//		this.personForeigner = personForeigner;
//	}
//
//	public Integer getFirmaForeigner() {
//		return firmaForeigner;
//	}
//
//	public void setFirmaForeigner(Integer firmaForeigner) {
//		this.firmaForeigner = firmaForeigner;
//	}

	public String getRegisterSql() {
		return registerSql;
	}

	public void setRegisterSql(String registerSql) {
		this.registerSql = registerSql;
	}

	public List<RegisterOptionsDocsIn> getDocsIn() {
		return docsIn;
	}

	public void setDocsIn(List<RegisterOptionsDocsIn> docsIn) {
		this.docsIn = docsIn;
	}

	public List<RegisterOptionsDocsOut> getDocsOut() {
		return docsOut;
	}

	public void setDocsOut(List<RegisterOptionsDocsOut> docsOut) {
		this.docsOut = docsOut;
	}

	public List<RegisterOptionsDisplay> getDisplay() {
		return display;
	}

	public void setDisplay(List<RegisterOptionsDisplay> display) {
		this.display = display;
	}

	@Override
	public String toString() {
		return "RegisterOptions{" + "id=" + id + ", registerId=" + registerId + ", objectType=" + objectType
//				+ ", requiredLat=" + requiredLat + ", onlyPerson=" + onlyPerson + ", onlyFirma=" + onlyFirma
//				+ ", personForeigner=" + personForeigner + ", firmaForeigner=" + firmaForeigner
				+ ", registerSql='"
				+ registerSql + '\'' + ", docsIn=" + docsIn + ", docsOut=" + docsOut + ", display=" + display + '}';
	}

	/***
	 * ИЗПОЛЗВА СЕ ОТ ЕКРАНА ЗА ВИЗУАЛИЗИРАНЕ НА СИСТЕМ-ДАТАТА
	 * (systemDataBrowser.xhtl) за визуализиране на детайлите за настройките
	 ***/
	public String toStringSystem() {

		StringBuilder sb = new StringBuilder();
		try {
			/*** ОСНОВНИ ДАННИ НА ОБЕКТА ***/
			sb.append("<b> Основни данни </b>  <br/>");
			sb.append("id=" + id);
			sb.append(", registerId=" + registerId);
			sb.append(", objectType=" + objectType);
//			sb.append(", requiredLat=" + requiredLat);
//			sb.append(", onlyPerson=" + onlyPerson);
//			sb.append(", onlyFirma=" + onlyFirma);
//			sb.append(", personForeigner=" + personForeigner);
//			sb.append(", firmaForeigner=" + firmaForeigner);
			sb.append(", registerSql=" + registerSql);

			sb.append("<br/>");

			/*** ВХОДНИ ДОКУМЕНТИ ***/
			sb.append("<b> Входни документи </b>  <br/>");
			if (this.docsIn != null && this.docsIn.size() > 0) {				
				for (RegisterOptionsDocsIn tmpDocIn : this.docsIn) {
					sb.append("{ ");
					sb.append("id=" + tmpDocIn.getId());
				//	sb.append(", vidService=" + tmpDocIn.getVidService()); // ne se polzva
					sb.append(", meuNimber=" + tmpDocIn.getMeuNimber());
					sb.append(", vidDoc=" + tmpDocIn.getVidDoc());
					sb.append(", purpose=" + tmpDocIn.getPurpose());
					sb.append(", payCharacteristic" + tmpDocIn.getPayCharacteristic());
					sb.append(", payAmount=" + tmpDocIn.getPayAmount());
					sb.append(", onlyBabh=" + tmpDocIn.getOnlyBabh());

					
					/*** ДЕЙНОСТИ ***/
					if (tmpDocIn.getDocsInActivity() != null && tmpDocIn.getDocsInActivity().size() > 0) {
						int countAct = 1;
						for (RegisterOptionsDocinActivity tmpActivity : tmpDocIn.getDocsInActivity()) {							
							sb.append("id=" + tmpActivity.getId());
							sb.append(", , activityId=" + tmpActivity.getActivityId());

						}
					}
					sb.append(" }" );
				}
			}
			
			sb.append("<br/>");

			/*** ИЗХОДНИ ДОКУМЕНТИ ***/
			sb.append("<b> Изходни документи </b>  <br/>");
			if (this.docsOut != null && this.docsOut.size() > 0) {				
				for (RegisterOptionsDocsOut tmpDocOut : this.docsOut) {
					sb.append("{ ");
					sb.append("id=" + tmpDocOut.getId());
					sb.append(", vidDoc=" + tmpDocOut.getVidDoc());
					sb.append(", nomGenerator=" + tmpDocOut.getNomGenerator());
					sb.append(", saveNomReissue==" + tmpDocOut.getSaveNomReissue());
					sb.append(" }" );
				}
				
			}

			sb.append("<br/>");

			/*** АТТРИБУТИ НА ЕКРАНА ***/
			sb.append("<b> Атрибути на екрана </b>  <br/>");
			if (this.display != null && this.display.size() > 0) {				
				for (RegisterOptionsDisplay tmpDisplay : this.display) {
					sb.append("{ ");
					sb.append("id=" + tmpDisplay.getId());
					sb.append(", objectClassifId=" + tmpDisplay.getObjectClassifId());
					sb.append(", label=" + tmpDisplay.getLabel());
					sb.append(" }" );
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при извличане на детайли за настройка на регистър! ");
		}

		return sb.toString();
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal j = new  SystemJournal();				
		j.setCodeObject(getCodeMainObject());
		j.setIdObject(getId());
		j.setIdentObject(getIdentInfo());
		j.setDateAction(new Date());		
		return j;
	}
}
