package com.ib.babhregs;

import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocinActivity;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.system.db.JPA;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;


public class SimpleSaveEntity {

//    public static void main(String[] args) {
//
//        RegisterOptions ro = new RegisterOptions();
//        ro.setRegisterId(-1);
//        ro.setObjectType(-2);
//
//
//        try {
//            JPA jpa = JPA.getUtil();
//            EntityManager eManager = jpa.getEntityManager();
//            EntityTransaction tr = eManager.getTransaction();
//            tr.begin();
//            eManager.persist(ro);
//            tr.commit();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

        public static void main(String[] args) {

            RegisterOptionsDocsIn doc1=new RegisterOptionsDocsIn();
            //doc1.setVidService(-1);
            doc1.setVidDoc(-2);
            doc1.setPurpose(-3);
            doc1.setPayCharacteristic(-4);
            doc1.setPayAmount(-5.1f);

            RegisterOptionsDocinActivity docActivity=new RegisterOptionsDocinActivity();
            docActivity.setActivityId(-9);

            doc1.getDocsInActivity().add(docActivity);

        try {
            JPA jpa = JPA.getUtil();
            EntityManager eManager = jpa.getEntityManager();
            EntityTransaction tr = eManager.getTransaction();
            tr.begin();
            eManager.persist(doc1);
            tr.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
