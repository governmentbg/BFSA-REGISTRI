package com.ib.babhregs.beans;

import com.ib.system.db.JPA;
import org.bouncycastle.crypto.agreement.jpake.JPAKEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class TestReg {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestReg.class);
    List<Object[]> resultList=new ArrayList<>();
    @PostConstruct
    private void init(){
        LOGGER.info("inti");
        try {
            JPA jpa = JPA.getUtil();

            resultList = JPA.getUtil().getEntityManager().createNativeQuery("" +
                    "select id_vpisvane," +
                    "       oblast_text," +
                    "       obshtina_text," +
                    "       addr_text," +
                    "       licenziant_name," +
                    "       licenziant_egn," +
                    "       nomer_licenz," +
                    "       date_licenz," +
                    "       identInfo from v_register_12" +
                    " WHERE edjv_vid=8").getResultList();
        }catch(Exception e){
            LOGGER.error("",e);
        }
    }

    public List<Object[]> getResultList() {
        LOGGER.info("getResult.size={}",resultList.size());
        return resultList;
    }

   /* public static void main(String[] args) {
//        try {
//            JPA jpa = JPA.getUtil();
//
//            List<Object[]> resultList = JPA.getUtil().getEntityManager().createNativeQuery("" +
//                            "select id_vpisvane, json_identif"+
//                    " from v_register_12" +
//                            "WHERE where edjv_vid=8").getResultList();
//        } catch (Exception e) {
//            LOGGER.error("", e);
//        }
        System.out.println("aaaaaaaaa");
    }*/
}
