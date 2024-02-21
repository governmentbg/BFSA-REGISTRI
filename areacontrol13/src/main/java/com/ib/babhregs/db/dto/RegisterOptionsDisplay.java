package com.ib.babhregs.db.dto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "register_options_display")
public class RegisterOptionsDisplay implements Serializable {
    private static final long serialVersionUID = -7765211140899450999L;
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

    @Transient
    private Integer registerId;
    
    @Basic
    @Column(name = "object_classif_id", nullable = false)
    @JournalAttr(label="objectClassifId",defaultText = "Регистър",classifID = ""+BabhConstants.CODE_CLASSIF_ATTR_EKRANI)
    private Integer objectClassifId;
    
    @Basic
    @Column(name = "label")
    @JournalAttr(label="label",defaultText = "Етикет")
    private String label;

    @Override
    public String toString() {
        return "RegisterOptionsDisplay{" +
                "id=" + id +
                ", objectClassifId=" + objectClassifId +
                ", label='" + label + '\'' +
                '}';
    }

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

   
    public Integer getObjectClassifId() {
        return objectClassifId;
    }

    public void setObjectClassifId(Integer objectClassifId) {
        this.objectClassifId = objectClassifId;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	 @XmlTransient
    public RegisterOptions getRegisterOptions() {
        return registerOptions;
    }

    public void setRegisterOptions(RegisterOptions registerOptions) {
        this.registerOptions = registerOptions;
    }

    public Integer getRegisterId() {
        return (registerOptions!=null?registerOptions.getRegisterId():null);
    }
}
