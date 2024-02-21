package com.ib.babhregs.db.dao;

import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import org.junit.Test;

import static org.junit.Assert.*;

public class DocDAOTest {
    DocDAO dao=new DocDAO(ActiveUser.DEFAULT);
    @Test
    public void findDocPayment() {
        try {
            String docPayment = dao.findDocPayment();
            System.out.println("---------------------------------");
            System.out.println(docPayment);
        }catch (Exception e){
            e.printStackTrace();
            fail();

        }finally {
            JPA.getUtil().closeConnection();
        }

    }
}