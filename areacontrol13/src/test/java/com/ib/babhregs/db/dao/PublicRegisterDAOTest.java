package com.ib.babhregs.db.dao;

import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import org.junit.Test;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PublicRegisterDAOTest {
    PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
    @Test
    public void testTragovciNaJivotni(){
        SelectMetadata smd = publicRegisterDAO.getRegisterAsList(5);
        String sql=smd.getSql();
        Map<String, Object> sqlParameters = smd.getSqlParameters();
        System.out.println(sql);
        System.out.println("parameters count:"+sqlParameters.size());
        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);


        for (Map.Entry<String,Object> entry : sqlParameters.entrySet()){
            query.setParameter(entry.getKey(), entry.getValue());
        }
        List resultList = null;
        try {

            resultList = query.getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }


//        return query.getResultList();
        assertTrue(resultList.size()>0);
        System.out.println(resultList.size());

    }
    @Test
    public void testTragovciNaJZarodishniProdikti(){
        SelectMetadata smd = publicRegisterDAO.getRegisterAsList(5);
        String sql=smd.getSql();
        System.out.println(sql);
        System.out.println(sql.toUpperCase().lastIndexOf("FROM"));
        System.out.println("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
    }
    @Test
    public void testTragovciVlp(){
        SelectMetadata smd = publicRegisterDAO.getRegisterAsList(23);
        String sql=smd.getSql();
        Map<String, Object> sqlParameters = smd.getSqlParameters();
        System.out.println(sql);
        System.out.println("parameters count:"+sqlParameters.size());
        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);



        for (Map.Entry<String,Object> entry : sqlParameters.entrySet()){
            System.out.println(entry.getKey() +" "+entry.getValue());
            query.setParameter(entry.getKey(), entry.getValue());
        }
        List resultList = null;
        try {

            resultList = query.getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }


//        return query.getResultList();
        assertTrue(resultList.size()>0);
        System.out.println(resultList.size());

    }
    
    
    @Test
    public void testFurajniDobavki(){
        SelectMetadata smd = publicRegisterDAO.getRegisterAsList(31);
        String sql=smd.getSql();
        Map<String, Object> sqlParameters = smd.getSqlParameters();
        System.out.println(sql);
        System.out.println("parameters count:"+sqlParameters.size());
        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);


        for (Map.Entry<String,Object> entry : sqlParameters.entrySet()){
            query.setParameter(entry.getKey(), entry.getValue());
        }
        List resultList = null;
        try {

            resultList = query.getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }


//        return query.getResultList();
        assertTrue(resultList.size()>0);
        System.out.println(resultList.size());

    }
    
    
}
