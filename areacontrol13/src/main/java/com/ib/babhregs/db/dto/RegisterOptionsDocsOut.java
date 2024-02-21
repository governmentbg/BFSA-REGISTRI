package com.ib.babhregs.db.dto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "register_options_docs_out")
public class RegisterOptionsDocsOut implements Serializable {
    private static final long serialVersionUID = -3444927480396291613L;
    @SequenceGenerator(name = "RegisterOptions", sequenceName = "seq_register_options", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "RegisterOptions")
    @Column(name = "id", nullable = false)
    @JournalAttr(label="id",defaultText = "Системен идентификатор", isId = "true")
    private Integer id;
//    @Basic
//    @Column(name = "register_options_id", nullable = false)
//    private Integer registerOptionsId;

    @ManyToOne
    @JoinColumn(name = "register_options_id",nullable = false)
    @XmlTransient 
    private RegisterOptions registerOptions;

    @Basic
    @Column(name = "vid_doc", nullable = false)
    @JournalAttr(label="vidDoc",defaultText = "Вид на документа",classifID = ""+BabhConstants.CODE_CLASSIF_DOC_VID  )
    private Integer vidDoc;
    
    @Basic
    @JournalAttr(label="nomGenerator",defaultText = "Метод за генериране на номера")
    @Column(name = "nom_generator", nullable = false, length = 20)
    private String nomGenerator;
    
    @Basic
    @Column(name = "save_nom_reissue", nullable = false)
    @JournalAttr(label="saveNomReissue",defaultText = "Запазва ли се рег. номера при преиздаване",classifID = ""+BabhConstants.CODE_CLASSIF_DANE  )
    private Integer saveNomReissue;
/////////////////////////////////////////////////////
    @Basic
    @Column(name = "type_period_valid", nullable = true)
 //   @JournalAttr(label="saveNomReissue",defaultText = "Запазва ли се рег. номера при преиздаване",classifID = ""+BabhConstants.CODE_CLASSIF_DANE  )
    private Integer typePeriodValid;

    @Basic
    @Column(name = "period_valid", nullable = true)
    //   @JournalAttr(label="saveNomReissue",defaultText = "Запазва ли се рег. номера при преиздаване",classifID = ""+BabhConstants.CODE_CLASSIF_DANE  )
    private Integer periodValid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getRegisterOptionsId() {
//        return registerOptionsId;
//    }
//
//    public void setRegisterOptionsId(Integer registerOptionsId) {
//        this.registerOptionsId = registerOptionsId;
//    }

    public Integer getVidDoc() {
        return vidDoc;
    }

    public void setVidDoc(Integer vidDoc) {
        this.vidDoc = vidDoc;
    }

    public String getNomGenerator() {
        return nomGenerator;
    }

    public void setNomGenerator(String nomGenerator) {
        this.nomGenerator = nomGenerator;
    }

    public Integer getSaveNomReissue() {
        return saveNomReissue;
    }

    public void setSaveNomReissue(Integer saveNomReissue) {
        this.saveNomReissue = saveNomReissue;
    }

    public Integer getTypePeriodValid() {
        return typePeriodValid;
    }

    public void setTypePeriodValid(Integer typePeriodValid) {
        this.typePeriodValid = typePeriodValid;
    }

    public Integer getPeriodValid() {
        return periodValid;
    }

    public void setPeriodValid(Integer periodValid) {
        this.periodValid = periodValid;
    }

    @Override
    public String toString() {
        return "RegisterOptionsDocsOut{" +
                "id=" + id +
                ", vidDoc=" + vidDoc +
                ", nomGenerator='" + nomGenerator + '\'' +
                ", saveNomReissue=" + saveNomReissue +
                '}';
    }

    @XmlTransient 
    public RegisterOptions getRegisterOptions() {
        return registerOptions;
    }

    public void setRegisterOptions(RegisterOptions docsIn22) {
        this.registerOptions = docsIn22;
    }
}
