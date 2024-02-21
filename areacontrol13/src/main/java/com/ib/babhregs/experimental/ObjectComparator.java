package com.ib.babhregs.experimental;

import java.util.Date;
import java.util.List;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.OezReg;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.db.dto.AdmGroup;
import com.ib.indexui.system.Constants;
import com.ib.system.BaseObjectComparator;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemJournal;

public class ObjectComparator extends BaseObjectComparator {

	public ObjectComparator(Date oldDate, Date newDate, BaseSystemData sd) {
		super(oldDate, newDate, sd);
		// TODO Auto-generated constructor stub
	}
	
	public ObjectComparator(Date oldDate, Date newDate, BaseSystemData sd, Integer lang) {
		super(oldDate, newDate, sd, lang);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String formatVal(Object o, String codeClassif, int codeObject, Date dat) {
		//System.out.println("CC=" + codeClassif);
		
		if (codeObject > 0) {
			String ident = null;
			try {				
				Integer id = Integer.parseInt(""+o);
				
				switch(codeObject) {
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC:
					    Doc doc = JPA.getUtil().getEntityManager().find(Doc.class, id);
					    if (doc != null) {
					    	ident =  doc.getIdentInfo();			    	
					    }
					    break;
				  case Constants.CODE_ZNACHENIE_JOURNAL_GROUPUSER:
					    AdmGroup group = JPA.getUtil().getEntityManager().find(AdmGroup.class, id);
					    if (group != null) {
					    	ident =  group.getIdentInfo();			    	
					    }
					    break;
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_REGISTER_OPTIONS:
					  RegisterOptions ro = JPA.getUtil().getEntityManager().find(RegisterOptions.class, id);
					    if (ro != null) {
					    	ident =  ro.getIdentInfo();			    	
					    }
					    break;
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT:
					    @SuppressWarnings("unchecked")
					    List<Referent> list = JPA.getUtil().getEntityManager().createQuery(
					    		"select r from Referent r where r.code = ?1 order by r.id desc").setParameter(1, id).getResultList();
					    if (list != null && !list.isEmpty()) {
					    	ident = list.get(0).getIdentInfo();			    	
					    }
					    break;    
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE:
					    Vpisvane vpisvane = JPA.getUtil().getEntityManager().find(Vpisvane.class, id);
					    if (vpisvane != null) {
					    	ident = vpisvane.getIdentInfo();			    	
					    }
					    break;    
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_OBEKT_DEINOST:
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_VLZ:
					    ObektDeinost obektDeinost = JPA.getUtil().getEntityManager().find(ObektDeinost.class, id);
					    if (obektDeinost != null) {
					    	ident = obektDeinost.getIdentInfo();			    	
					    }
					    break;    
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_OEZ:
					    OezReg oezReg = JPA.getUtil().getEntityManager().find(OezReg.class, id);
					    if (oezReg != null) {
					    	ident = oezReg.getIdentInfo();			    	
					    }
					    break;    
					    
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS:
					    Mps mps = JPA.getUtil().getEntityManager().find(Mps.class, id);
					    if (mps != null) {
					    	ident = mps.getIdentInfo();			    	
					    }
					    break;  
				  case BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS_FURAJI:
					    MpsFuraji mpsFuraji = JPA.getUtil().getEntityManager().find(MpsFuraji.class, id);
					    if (mpsFuraji != null) {
					    	ident = mpsFuraji.getIdentInfo();			    	
					    }
					    break;  
					    
				  default:
					  break;
				}
				
				if (ident == null) {
					//Правим още един опит през журнала
					SystemJournal j = (SystemJournal) JPA.getUtil().getEntityManager().createQuery("from SystemJournal where codeObject =:co and idObject = :io and codeAction = :ca")
							.setParameter("co", codeObject)
							.setParameter("io", id)
							.setParameter("ca", SysConstants.CODE_DEIN_IZTRIVANE)
							.getSingleResult();
					if (j != null) {
						ident = j.getIdentObject();
					}
				}
				
				if (ident == null) {
					return "Id= " + id;
				}else {
					return ident + "(Id= " +id + ")";
				}
				
			} catch (Exception e) {
				return ident + " (Грешка при идентификация)";
			}

			
		}else {
			if (codeClassif == null || codeClassif.equalsIgnoreCase("none")) {
				return fromatSimpleVal(o);
			}else {
				return decodeVal(o,codeClassif, dat, lang);
			}
		}
		
		
	}
	
	
	
	
}
	


