package com.ib.babhregs.components;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.faces.component.FacesComponent;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.DocAccess;
import com.ib.babhregs.db.dto.DocVidSetting;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.OezReg;
import com.ib.babhregs.db.dto.Praznici;
import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.db.dto.ProcExe;
import com.ib.babhregs.db.dto.ProcExeEtap;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.Register;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.Registratura;
import com.ib.babhregs.db.dto.RegistraturaGroup;
import com.ib.babhregs.db.dto.RegistraturaMailBox;
import com.ib.babhregs.db.dto.RegistraturaReferent;
import com.ib.babhregs.db.dto.RegistraturaSetting;
import com.ib.babhregs.db.dto.ShablonLogic;
import com.ib.babhregs.db.dto.UserNotifications;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.experimental.ObjectComparator;
import com.ib.babhregs.search.DocSearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.CompObjAuditSys;
import com.ib.indexui.db.dto.AdmGroup;
import com.ib.indexui.db.dto.AdmUser;
import com.ib.indexui.db.dto.StatTable;
import com.ib.indexui.db.dto.UniversalReport;
import com.ib.indexui.report.uni.SprObject;
import com.ib.indexui.system.Constants;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.ObjectsDifference;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SyslogicListEntity;
import com.ib.system.db.dto.SyslogicListOpisEntity;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemClassifOpis;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.db.dto.SystemOption;
import com.ib.system.utils.JAXBHelper;

/** */
@FacesComponent(value = "compObjAudit", createTag = true)
public class CompObjAudit extends CompObjAuditSys {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompObjAudit.class);

	
	/**
	 * Тука се правят едни врътки за да може в основният екран на вписването да се покажат през преглед на журнала
	 * навързаните обекти към вписването- документи, лица, дейности, обекти на лицензиране и т.н.
	 */
	@Override
	public void initObjAudit() {
		super.initObjAudit();

		Integer codeObj = (Integer) getAttributes().get("codeObj");
		if (!Objects.equals(codeObj, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)) {
			return; // само за вписване има такива дивотии
		}
		Integer idObj = (Integer) getAttributes().get("idObj");
		
		Date fromDate = null; // свързаните ще се теглят от тази датам защото може да са възникнали преди вписването
		Set<Entry<Integer, Integer>> othersIdent = new HashSet<>(); // <ид, код> на тези свързани обекти

		try {
			for (SystemJournal j : getDocHistory()) {
				if (fromDate == null) { // трябва ми най ранната, когато се появява вписването
					fromDate = j.getDateAction();
				}

				if (Objects.equals(j.getCodeObject(), codeObj) && Objects.equals(j.getIdObject(), idObj)) {
					// ако е на реда на вписването се добавят 1 и 2 само ако ги има
					
					if (j.getJoinedIdObject1() != null && j.getJoinedCodeObject1() != null) {
						othersIdent.add(new AbstractMap.SimpleEntry<>(j.getJoinedIdObject1(), j.getJoinedCodeObject1()));
					}
					if (j.getJoinedIdObject2() != null && j.getJoinedCodeObject2() != null) {
						othersIdent.add(new AbstractMap.SimpleEntry<>(j.getJoinedIdObject2(), j.getJoinedCodeObject2()));
					}
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при определяне на свързани данни от журнала", e);
		} // горе няма работа с БД и няма клосе


		try {
			if (fromDate != null && !othersIdent.isEmpty()) {
				getDocHistory().addAll(findOthersRows(othersIdent, fromDate));

				Collections.sort(getDocHistory(), Comparator.comparing(SystemJournal::getId));
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при извличане на данни от журнала", e);
		} finally {
			JPA.getUtil().closeConnection();
		}
	}

	@SuppressWarnings("unchecked")
	private List<SystemJournal> findOthersRows(Set<Entry<Integer, Integer>> othersIdent, Date fromDate) {
		Map<String, Integer> params = new HashMap<>();
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select x from SystemJournal x where x.dateAction >= :fromDate ");
		
		int index = 0;
		for (Entry<Integer, Integer> entry : othersIdent) {
			if (index == 0) {
				sql.append(" and ( ");
			} else {
				sql.append(" or ");
			}
			
			String idArg = "idArg_" + index;
			String codeArg = "codeArg_" + index;
			
			sql.append(" (x.idObject = :"+idArg+" and x.codeObject = :"+codeArg+") ");

			params.put(idArg, entry.getKey());
			params.put(codeArg, entry.getValue());
			
			index++;
		}
		sql.append(" ) order by x.id asc ");
		
		Query query = JPA.getUtil().getEntityManager().createQuery(sql.toString());

		// тъй като в един запис вписването най вероятно не  първото по време в журнала давам някакво измислено време преди
		query.setParameter("fromDate", new Date(fromDate.getTime() - 30 * 1000));
		
		for (Entry<String, Integer> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();

	}
	/**
	 * Зарежда текущите разликите - сегашно и предишно състояние
	 * @param selectedEvent
	 */
	@Override
	public List<ObjectsDifference> loadCurrentDiff(SystemJournal currentEventTmp,SystemJournal previousEventTmp) {
		List<ObjectsDifference> compareResult=new ArrayList<ObjectsDifference>(); 
		
		
		LOGGER.info("LoadCurrentDiff between {} and {}",currentEventTmp!=null?currentEventTmp.getId():null,previousEventTmp!=null?previousEventTmp.getId():null);
		
		try {
						
//			Object xmlToObject2 = JAXBHelper.xmlToObject2(getSelectedEvent().getObjectXml());
//			System.out.println("==========================="+xmlToObject2.getClass());
			
			Object currentObj=null,prevObj=null;
			Integer codeObject=currentEventTmp!=null?currentEventTmp.getCodeObject():previousEventTmp.getCodeObject();
			Integer codeAction=currentEventTmp!=null?currentEventTmp.getCodeAction():previousEventTmp.getCodeAction();
			
			if (codeAction != null && codeAction.equals(BabhConstants.CODE_DEIN_UNISEARCH)) {
				if (previousEventTmp.getObjectXml() != null) {
					SprObject spr = JAXBHelper.xmlToObject(SprObject.class, previousEventTmp.getObjectXml());
					
					return convertSprObjectToDifferences(spr, getSystemData(), previousEventTmp.getDateAction()) ;
				}
			}
			
			
			switch (codeObject) {
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC:
					if (codeAction != null && codeAction.equals(BabhConstants.CODE_DEIN_SEARCH)) {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, currentEventTmp.getObjectXml()):new DocSearch();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocSearch.class, previousEventTmp.getObjectXml()):new DocSearch();
					}else {
						currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Doc.class, currentEventTmp.getObjectXml()):new Doc();
						prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Doc.class, previousEventTmp.getObjectXml()):new Doc();
					}
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_FILE:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Files.class, currentEventTmp.getObjectXml()):new Files();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Files.class, previousEventTmp.getObjectXml()):new Files();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_IZR_DOST:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocAccess.class, currentEventTmp.getObjectXml()):new DocAccess();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocAccess.class, previousEventTmp.getObjectXml()):new DocAccess();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_IZR_DOST_VP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneAccess.class, currentEventTmp.getObjectXml()):new VpisvaneAccess();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneAccess.class, previousEventTmp.getObjectXml()):new VpisvaneAccess();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REISTRATURA:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Registratura.class, currentEventTmp.getObjectXml()):new Registratura();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Registratura.class, previousEventTmp.getObjectXml()):new Registratura();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REGISTER:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Register.class, currentEventTmp.getObjectXml()):new Register();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Register.class, previousEventTmp.getObjectXml()):new Register();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REISTRATURA_SETT:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaSetting.class, currentEventTmp.getObjectXml()):new RegistraturaSetting();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaSetting.class, previousEventTmp.getObjectXml()):new RegistraturaSetting();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC_VID_SETT:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(DocVidSetting.class, currentEventTmp.getObjectXml()):new DocVidSetting();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(DocVidSetting.class, previousEventTmp.getObjectXml()):new DocVidSetting();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_MAILBOX:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaMailBox.class, currentEventTmp.getObjectXml()):new RegistraturaMailBox();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaMailBox.class, previousEventTmp.getObjectXml()):new RegistraturaMailBox();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REISTRATURA_GROUP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaGroup.class, currentEventTmp.getObjectXml()):new RegistraturaGroup();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaGroup.class, previousEventTmp.getObjectXml()):new RegistraturaGroup();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REG_GROUP_CORRESP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaReferent.class, currentEventTmp.getObjectXml()):new RegistraturaReferent();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(RegistraturaReferent.class, previousEventTmp.getObjectXml()):new RegistraturaReferent();
					break;
					
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_USER:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(AdmUser.class, currentEventTmp.getObjectXml()):new AdmUser();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(AdmUser.class, previousEventTmp.getObjectXml()):new AdmUser();
					break;
					
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_DEF:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ProcDef.class, currentEventTmp.getObjectXml()):new ProcDef();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ProcDef.class, previousEventTmp.getObjectXml()):new ProcDef();
					break;
				
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_DEF_ETAP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ProcDefEtap.class, currentEventTmp.getObjectXml()):new ProcDefEtap();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ProcDefEtap.class, previousEventTmp.getObjectXml()):new ProcDefEtap();
					break;
	
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Referent.class, currentEventTmp.getObjectXml()):new Referent();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Referent.class, previousEventTmp.getObjectXml()):new Referent();
					break;
	
				case SysConstants.CODE_ZNACHENIE_JOURNAL_OPTION:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(SystemOption.class, currentEventTmp.getObjectXml()):new SystemOption();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(SystemOption.class, previousEventTmp.getObjectXml()):new SystemOption();
					break;
			
				case Constants.CODE_ZNACHENIE_JOURNAL_STAT_TABLE:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(StatTable.class, currentEventTmp.getObjectXml()):new StatTable();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(StatTable.class, previousEventTmp.getObjectXml()):new StatTable();
					break;
				case Constants.CODE_ZNACHENIE_JOURNAL_UNI_REPORT:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(UniversalReport.class, currentEventTmp.getObjectXml()):new UniversalReport();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(UniversalReport.class, previousEventTmp.getObjectXml()):new UniversalReport();
					break;
	
				case SysConstants.CODE_ZNACHENIE_JOURNAL_OPIS:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(SystemClassifOpis.class, currentEventTmp.getObjectXml()):new SystemClassifOpis();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(SystemClassifOpis.class, previousEventTmp.getObjectXml()):new SystemClassifOpis();
					break;
				case SysConstants.CODE_ZNACHENIE_JOURNAL_CLASSIF:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(SystemClassif.class, currentEventTmp.getObjectXml()):new SystemClassif();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(SystemClassif.class, previousEventTmp.getObjectXml()):new SystemClassif();
					break;
				case SysConstants.CODE_ZNACHENIE_JOURNAL_LIST:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(SyslogicListOpisEntity.class, currentEventTmp.getObjectXml()):new SyslogicListOpisEntity();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(SyslogicListOpisEntity.class, previousEventTmp.getObjectXml()):new SyslogicListOpisEntity();
					break;
				case SysConstants.CODE_ZNACHENIE_JOURNAL_LISTROW:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(SyslogicListEntity.class, currentEventTmp.getObjectXml()):new SyslogicListEntity();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(SyslogicListEntity.class, previousEventTmp.getObjectXml()):new SyslogicListEntity();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_EXE:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ProcExe.class, currentEventTmp.getObjectXml()):new ProcExe();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ProcExe.class, previousEventTmp.getObjectXml()):new ProcExe();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_EXE_ETAP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ProcExeEtap.class, currentEventTmp.getObjectXml()):new ProcExeEtap();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ProcExeEtap.class, previousEventTmp.getObjectXml()):new ProcExeEtap();
					break;
	
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_PRAZNIK:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Praznici.class, currentEventTmp.getObjectXml()):new Praznici();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Praznici.class, previousEventTmp.getObjectXml()):new Praznici();
					break;
					
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_GROUPUSER:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(AdmGroup.class, currentEventTmp.getObjectXml()):new AdmGroup();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(AdmGroup.class, previousEventTmp.getObjectXml()):new AdmGroup();
					break;
	
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_NOTIF: // журналира се само изтриването на нотификация
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(UserNotifications.class, currentEventTmp.getObjectXml()):new UserNotifications();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(UserNotifications.class, previousEventTmp.getObjectXml()):new UserNotifications();
					break;
					
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_REGISTER_OPTIONS: // журналира се само изтриването на нотификация
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(RegisterOptions.class, currentEventTmp.getObjectXml()):new RegisterOptions();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(RegisterOptions.class, previousEventTmp.getObjectXml()):new RegisterOptions();
					break;
	
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE: 
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Vpisvane.class, currentEventTmp.getObjectXml()):new Vpisvane();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Vpisvane.class, previousEventTmp.getObjectXml()):new Vpisvane();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE_STATUS: 
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneStatus.class, currentEventTmp.getObjectXml()):new VpisvaneStatus();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneStatus.class, previousEventTmp.getObjectXml()):new VpisvaneStatus();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE_DOC: 
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneDoc.class, currentEventTmp.getObjectXml()):new VpisvaneDoc();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(VpisvaneDoc.class, previousEventTmp.getObjectXml()):new VpisvaneDoc();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(EventDeinJiv.class, currentEventTmp.getObjectXml()):new EventDeinJiv();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(EventDeinJiv.class, previousEventTmp.getObjectXml()):new EventDeinJiv();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(EventDeinostFuraji.class, currentEventTmp.getObjectXml()):new EventDeinostFuraji();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(EventDeinostFuraji.class, previousEventTmp.getObjectXml()):new EventDeinostFuraji();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(EventDeinostVlp.class, currentEventTmp.getObjectXml()):new EventDeinostVlp();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(EventDeinostVlp.class, previousEventTmp.getObjectXml()):new EventDeinostVlp();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_OBEKT_DEINOST:
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_VLZ:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ObektDeinost.class, currentEventTmp.getObjectXml()):new ObektDeinost();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ObektDeinost.class, previousEventTmp.getObjectXml()):new ObektDeinost();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_OEZ:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(OezReg.class, currentEventTmp.getObjectXml()):new OezReg();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(OezReg.class, previousEventTmp.getObjectXml()):new OezReg();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Mps.class, currentEventTmp.getObjectXml()):new Mps();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Mps.class, previousEventTmp.getObjectXml()):new Mps();
					break;
				case BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS_FURAJI:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(MpsFuraji.class, currentEventTmp.getObjectXml()):new MpsFuraji();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(MpsFuraji.class, previousEventTmp.getObjectXml()):new MpsFuraji();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_VLP:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(Vlp.class, currentEventTmp.getObjectXml()):new Vlp();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(Vlp.class, previousEventTmp.getObjectXml()):new Vlp();
					break;

				case BabhConstants.CODE_ZNACHENIE_JOURNAL_SHABLON_LOGIC:
					currentObj=currentEventTmp!=null?JAXBHelper.xmlToObject(ShablonLogic.class, currentEventTmp.getObjectXml()):new ShablonLogic();
					prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(ShablonLogic.class, previousEventTmp.getObjectXml()):new ShablonLogic();
					break;

	//			case BabhConstants.CODE_ZNACHENIE_JOURNAL_FILE_OBJECT:
	//				currentObj=JAXBHelper.xmlToObject(FileObject.class, getSelectedEvent().getObjectXml());
	//				prevObj=previousEventTmp!=null?JAXBHelper.xmlToObject(FileObject.class, previousEventTmp.getObjectXml()):new FileObject();
	//				break;
					
				default:
					LOGGER.error("Object code="+currentEventTmp.getCodeObject()+" not implemented");
					break;
			}
//			Doc currentDoc = JAXBHelper.xmlToObject(Doc.class, getSelectedEvent().getObjectXml());
//			Doc prevDoc = previousEventTmp!=null?JAXBHelper.xmlToObject(Doc.class, previousEventTmp.getObjectXml()):new Doc();
//			
			 compareResult = new ObjectComparator(
					previousEventTmp!=null?previousEventTmp.getDateAction():new Date(),
					currentEventTmp!=null?currentEventTmp.getDateAction():new Date(),
							(SystemData) JSFUtils.getManagedBean("systemData"), 
							null).compare( prevObj,currentObj);
			 
			 
			
			 
			
		} catch (Exception e1) {
			LOGGER.error("",e1);
			e1.printStackTrace();
		} 
		return compareResult;

	}
	


	
	
}