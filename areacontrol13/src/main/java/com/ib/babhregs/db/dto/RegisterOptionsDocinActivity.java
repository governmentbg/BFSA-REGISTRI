package com.ib.babhregs.db.dto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "register_options_docin_activity")
public class RegisterOptionsDocinActivity implements Serializable {
    private static final long serialVersionUID = 4038646556091157809L;
    @SequenceGenerator(name = "RegisterOptions", sequenceName = "seq_register_options", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "RegisterOptions")
    @Column(name = "id", nullable = false)
    private Integer id;

//    @Basic
//    @Column(name = "register_options_docs_in_id", nullable = false)
//    private Integer registerOptionsDocsInId;
    @ManyToOne
    @JoinColumn(name = "register_options_docs_in_id",nullable = false)
    @XmlTransient 
    private RegisterOptionsDocsIn docIn;


    @Basic
    @Column(name = "activity_id", nullable = false)
    @JournalAttr(label="activityId",defaultText = "Дейност",classifID = ""+BabhConstants.CODE_CLASSIF_VID_DEINOST  )
    private Integer activityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getRegisterOptionsDocsInId() {
//        return registerOptionsDocsInId;
//    }
//
//    public void setRegisterOptionsDocsInId(Integer registerOptionsDocsInId) {
//        this.registerOptionsDocsInId = registerOptionsDocsInId;
//    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    @Override
    public String toString() {
        return "RegisterOptionsDocinActivity{" +
                "id=" + id +
                ", activityId=" + activityId +
                '}';
    }

    @XmlTransient 
    public RegisterOptionsDocsIn getDocIn() {
        return docIn;
    }

    public void setDocIn(RegisterOptionsDocsIn docIn) {
        this.docIn = docIn;
    }
}
