package com.ib.babhregs.beans;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import com.ib.babhregs.db.dao.PublicRegisterVLPDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.model.DataModelListener;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.Query;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class Reg25 extends IndexUIbean   {

	/**
	 *Опит за много Регистри  
	 */
	private static final long serialVersionUID = 1574880756078348004L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsSellersEmbrionBean.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterVLPDAO publicRegisterVLPDAO = new PublicRegisterVLPDAO();
	private String titleReg;
	private Integer idReg;
	private SystemData sd;
	private UserData ud;
	private transient VpisvaneDAO	daoVp;

	
	@PostConstruct
	void initData() throws DbErrorException {
		String idRegString = JSFUtils.getRequestParameter("idReg");
		titleReg="/pages/reg"+idRegString+".xhtml";
		idReg = Integer.parseInt(idRegString);
		setSd((SystemData) getSystemData());
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		System.out.println();
		try { 
			
			String sql = " select vpisvane.id from vpisvane " + 
						 " WHERE id_register =:registerId  and status=:status";
								
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			
			q.setParameter("registerId", 27);
			q.setParameter("status", 2);
			
			 @SuppressWarnings("unchecked")
			 List<BigInteger> rows = q.getResultList();
			 List<Vpisvane> listVpisvane=new ArrayList<Vpisvane>();
			 List<regList> regListAll=new ArrayList<Reg25.regList>();
			for (BigInteger row : rows) {
				Integer id = row.intValue();//SearchUtils.asInteger(row[0]);
				Vpisvane vpis = daoVp.findById(id);
				EventDeinostVlp edv = vpis.getEventDeinostVlp();
				
				evDeinostVlp ed=new evDeinostVlp();
//				List<bolestVlp> lb=new ArrayList<Reg27.eventDeinostVlp.bolestVlp>();
//				List<Integer> aa = edv.getBolestList();
//				bolestVlp bolV=new 
//				ed.setIdVpisvane(edv.getIdVpisvane());
//				ed.setDanniKontragent(edv.getDanniKontragent());
//				ed.setOpisanie(ed.getOpisanie());
//				ObektDeinost od = vpis.getObektDeinost();
//				regList rl =new regList();
//				rl.setId(vpis.getId());
//				rl.setIdRegister(vpis.getIdRegister());
//				rl.setLicenziant(vpis.getLicenziant());
//				rl.setRegNomResult(vpis.getRegNomResult());
//				rl.setEventDeinostVlp(ed);
				listVpisvane.add(vpis);
				System.out.println();
				
			}
			 System.out.println();
		} catch (Exception e) {			
			LOGGER.error("Грешка при търсене дали се използва регистъра!",e);	
			throw new DbErrorException("Грешка при търсене дали се използва регистъра!");
		}	

		
		System.out.println();
		
		System.out.println();
		try {
			
			SelectMetadata a = publicRegisterVLPDAO.getRegisterAsList(idReg);
			regsList=new LazyDataModelSQL2Array(publicRegisterVLPDAO.getRegisterAsList(idReg),"date_licenz desc");
			System.out.println();
		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
		
		String title = getMessageResourceString(LABELS, "regsSellersFodder.title");		  
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{
			
			String title = getMessageResourceString(LABELS, "regsSellersFodder.title");		
			new CustomExpPreProcess().preProcessPDF(document, title,  null, null, null);		
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(),e);			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);			
		} 
	}
	
	

	/**
	 * за експорт в pdf 
	 * @return
	 */
	public PDFOptions pdfOptions() {
		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
        return pdfOpt;
	}
		
	public LazyDataModelSQL2Array getRegsList() {
		return regsList;
	}

	public void setRegsList(LazyDataModelSQL2Array regsList) {
		this.regsList = regsList;
	}

	public String getTitleReg() {
		return titleReg;
	}

	public void setTitleReg(String titleReg) {
		this.titleReg = titleReg;
	}

	public Integer getIdReg() {
		return idReg;
	}

	public void setIdReg(Integer idReg) {
		this.idReg = idReg;
	}
	
	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}

	public VpisvaneDAO getDaoVp() {
		return daoVp;
	}

	public void setDaoVp(VpisvaneDAO daoVp) {
		this.daoVp = daoVp;
	}

	public SystemData getSd() {
		return sd;
	}

	public void setSd(SystemData sd) {
		this.sd = sd;
	}
	public class regList{
		public regList(){
			
		}
		private Integer id;
		private Integer idRegister;
		private Date dateZaqvlVpis;
		private String regNomResult;
		private Integer licenziantType;
		private String licenziant;
//		private List<evDeinostVlp> eventDeinostVlp;
	
		
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getIdRegister() {
			return idRegister;
		}
		public void setIdRegister(Integer idRegister) {
			this.idRegister = idRegister;
		}
		public Date getDateZaqvlVpis() {
			return dateZaqvlVpis;
		}
		public void setDateZaqvlVpis(Date dateZaqvlVpis) {
			this.dateZaqvlVpis = dateZaqvlVpis;
		}
		public String getRegNomResult() {
			return regNomResult;
		}
		public void setRegNomResult(String regNomResult) {
			this.regNomResult = regNomResult;
		}
		public Integer getLicenziantType() {
			return licenziantType;
		}
		public void setLicenziantType(Integer licenziantType) {
			this.licenziantType = licenziantType;
		}
		public String getLicenziant() {
			return licenziant;
		}
		public void setLicenziant(String licenziant) {
			this.licenziant = licenziant;
		}
//		public List<evDeinostVlp> getEventDeinostVlp() {
//			return eventDeinostVlp;
//		}
//		public void setEventDeinostVlp(List<evDeinostVlp> eventDeinostVlp) {
//			this.eventDeinostVlp = eventDeinostVlp;
//		}
			
	}
	public class evDeinostVlp{
		private Integer id;
		private String opisanie; // (varchar(1000))
		private String danniKontragent; // (varchar(1000))
		private String dopInfo; // (varchar(1000))
		private Integer idVpisvane; // (int8)
		public evDeinostVlp(){
			
		}
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getOpisanie() {
			return opisanie;
		}
		public void setOpisanie(String opisanie) {
			this.opisanie = opisanie;
		}
		public String getDanniKontragent() {
			return danniKontragent;
		}
		public void setDanniKontragent(String danniKontragent) {
			this.danniKontragent = danniKontragent;
		}
		public String getDopInfo() {
			return dopInfo;
		}
		public void setDopInfo(String dopInfo) {
			this.dopInfo = dopInfo;
		}
		public Integer getIdVpisvane() {
			return idVpisvane;
		}
		public void setIdVpisvane(Integer idVpisvane) {
			this.idVpisvane = idVpisvane;
		}
		
		public class bolestiVlp{
			private Integer idVpisvane; // (int8)
			private Integer idBolest; // (int8)
			
			public bolestiVlp(){
				
			}
			
			public Integer getIdBolest() {
				return idBolest;
			}
			public void setIdBolest(Integer idBolest) {
				this.idBolest = idBolest;
			}
			public Integer getIdVpisvane() {
				return idVpisvane;
			}
			public void setIdVpisvane(Integer idVpisvane) {
				this.idVpisvane = idVpisvane;
			}
			
		}
	}
}