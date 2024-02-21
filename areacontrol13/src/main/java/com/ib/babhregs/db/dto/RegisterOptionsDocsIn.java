package com.ib.babhregs.db.dto;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "register_options_docs_in")
public class RegisterOptionsDocsIn implements Serializable {
    private static final long serialVersionUID = -854352370115619349L;
    @SequenceGenerator(name = "RegisterOptions", sequenceName = "seq_register_options", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "RegisterOptions")
    @Column(name = "id", nullable = false)
    @JournalAttr(label="id",defaultText = "Системен идентификатор", isId = "true")
    private Integer id;
//    @Basic
//    @Column(name = "register_options_id", nullable = false)


    @ManyToOne
    @JoinColumn(name = "register_options_id",nullable = false)
    @XmlTransient 
    private RegisterOptions registerOptions;

//    @Basic
//    @Column(name = "vid_service", nullable = false)
//    @JournalAttr(label="vidService",defaultText = "Вид на услугата",classifID = ""+BabhConstants.CODE_CLASSIF_VIDOVE_USLUGI )
//    private Integer vidService;
    
    
    @Basic
    @Column(name = "meu_nimber", nullable = true, length = 20)
    @JournalAttr(label="meuNimber",defaultText = "Номер на услугата")
    private String meuNimber;
    
    @Basic
    @Column(name = "vid_doc", nullable = false)
    @JournalAttr(label="vidDoc",defaultText = "Вид на документа",classifID = ""+BabhConstants.CODE_CLASSIF_DOC_VID  )
    private Integer vidDoc;
    
    @Basic
    @Column(name = "purpose", nullable = false)
    @JournalAttr(label="purpose",defaultText = "Предназначение",classifID = ""+BabhConstants.CODE_CLASSIF_ZAIAV_PREDNAZNACHENIE )
    private Integer purpose;
    
    @Basic    
    @JournalAttr(label="payCharacteristic",defaultText = "Плащане на услугата",classifID = ""+BabhConstants.CODE_CLASSIF_METOD_PLASHTANE )
    @Column(name = "pay_characteristic", nullable = false)
    private Integer payCharacteristic;
    
    
    @Basic
    @Column(name = "pay_amount", nullable = false)
    @JournalAttr(label="payAmount",defaultText = "Размер на таксата")
    private Float payAmount=0F;
    
    @Transient
    private Integer idRegister;
    

//    @OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
//    @Fetch(value = FetchMode.SUBSELECT)
//    @JoinColumn(name="register_options_docs_in_id")
    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER,mappedBy = "docIn",orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JournalAttr(label="docsInActivity",defaultText = "Дейности към входните документи")
    private List<RegisterOptionsDocinActivity> docsInActivity = new ArrayList<RegisterOptionsDocinActivity>();

    @Basic
    @Column(name = "only_babh")
    private Integer onlyBabh=2;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


//    public Integer getVidService() {
//        return vidService;
//    }
//
//    public void setVidService(Integer vidService) {
//        this.vidService = vidService;
//    }

    public String getMeuNimber() {
        return meuNimber;
    }

    public void setMeuNimber(String meuNimber) {
        this.meuNimber = meuNimber;
    }

    public Integer getVidDoc() {
        return vidDoc;
    }

    public void setVidDoc(Integer vidDoc) {
        this.vidDoc = vidDoc;
    }

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public Integer getPayCharacteristic() {
        return payCharacteristic;
    }

    public void setPayCharacteristic(Integer payCharacteristic) {
        this.payCharacteristic = payCharacteristic;
    }

    public Float getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Float payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getOnlyBabh() {
        return onlyBabh;
    }

    public void setOnlyBabh(Integer onlyBabh) {
        this.onlyBabh = onlyBabh;
    }

    public List<RegisterOptionsDocinActivity> getDocsInActivity() {
        return docsInActivity;
    }

    public void setDocsInActivity(List<RegisterOptionsDocinActivity> docsInActivity) {
        this.docsInActivity = docsInActivity;
    }

    @Override
    public String toString() {
        return "RegisterOptionsDocsIn{" +
                "id=" + id +
             //   "registerOptionsId=" + registerOptionsId +
             //   ", vidService=" + vidService +
                ", meuNimber='" + meuNimber + '\'' +
                ", vidDoc=" + vidDoc +
                ", purpose=" + purpose +
                ", payCharacteristic=" + payCharacteristic +
                ", payAmount=" + payAmount +
                ", docsInActivity=" + docsInActivity +
                ", onlyBabh=" + onlyBabh +
                '}';
    }

    @XmlTransient 
    public RegisterOptions getRegisterOptions() {
        return registerOptions;
    }

    public void setRegisterOptions(RegisterOptions docsIn) {
        this.registerOptions = docsIn;
    }

	public Integer getIdRegister() {
		return idRegister;
	}

	public void setIdRegister(Integer idRegister) {
		this.idRegister = idRegister;
	}
}
