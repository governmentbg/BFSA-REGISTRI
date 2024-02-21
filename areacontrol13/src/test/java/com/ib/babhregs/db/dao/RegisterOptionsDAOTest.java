package com.ib.babhregs.db.dao;

import com.ib.babhregs.db.dto.*;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class RegisterOptionsDAOTest {

    RegisterOptions ro = new RegisterOptions();
    RegisterOptionsDAO dao;

    @Before
    public void setUp() throws Exception {
        System.out.println("==========start init data ===");
        ro = new RegisterOptions();
        ro.setRegisterId(-1);
        ro.setObjectType(-2);

        dao = new RegisterOptionsDAO(ActiveUser.DEFAULT);
        System.out.println("==========end init data ===");
    }

    /**
     * Запис само на основния обект
     */
    @Test
    public void testSaveOnlyMain() {
//        ro = new RegisterOptions();
//        ro.setRegisterId(-1);
//        ro.setObjectType(-2);


        try {
            JPA.getUtil().begin();
            dao.save(ro);
            JPA.getUtil().commit();
            assertNotNull(ro.getId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Error ehen save");
        }
    }

    //    @Test
//    public void testSaveMain(){
//
//        //Основен обект
//        ro = new RegisterOptions();
//        ro.setRegisterId(-1);
//        ro.setObjectType(-2);
//
//        //Входящи документи
//        RegisterOptionsDocsIn doc1=new RegisterOptionsDocsIn();
//        doc1.setVidService(-1);
//        doc1.setVidDoc(-2);
//        doc1.setPurpose(-3);
//        doc1.setPayCharacteristic(-4);
//        doc1.setPayAmount(-5.1f);
//
//        RegisterOptionsDocinActivity docActivity=new RegisterOptionsDocinActivity();
//        docActivity.setActivityId(-9);
//
//        doc1.getDocsInActivity().add(docActivity);
//        ro.getDocsIn().add(doc1);
//
//        //Изходящи документи
//        RegisterOptionsDocsOut docsOut1=new RegisterOptionsDocsOut();
//        docsOut1.setNomGenerator("alabala");
//        docsOut1.setSaveNomReissue(-1);
//        docsOut1.setSaveNomReissue(-2);
//        docsOut1.setVidDoc(-3);
//
//        ro.getDocsOut().add(docsOut1);
//
//        //Komponenti na ekrana
//        RegisterOptionsDisplay dsp=new RegisterOptionsDisplay();
//        dsp.setObjectClassifId(-1);
//
//        ro.getDisplay().add(dsp);
//        try {
//            JPA.getUtil().begin();
//
//
//
//            dao.save(ro);
//            JPA.getUtil().commit();
//            assertNotNull(ro.getId());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Error ehen save");
//        }
//    }
    @Test
    public void testSaveMainWithDocsIn() {


        //List<RegisterOptionsDocsIn> docs=new ArrayList<RegisterOptionsDocsIn>();
        RegisterOptionsDocsIn doc1 = new RegisterOptionsDocsIn();
       // doc1.setVidService(-1);
        doc1.setVidDoc(-2);
        doc1.setPurpose(-3);
        doc1.setPayCharacteristic(-4);
        doc1.setPayAmount(-5.1f);
        doc1.setRegisterOptions(ro);

        RegisterOptionsDocinActivity docActivity = new RegisterOptionsDocinActivity();
        docActivity.setActivityId(-9);
        docActivity.setDocIn(doc1);

        doc1.getDocsInActivity().add(docActivity);

        RegisterOptionsDocsIn doc2 = new RegisterOptionsDocsIn();
    //    doc2.setVidService(-11); // не се ползва
        doc2.setVidDoc(-22);
        doc2.setPurpose(-33);
        doc2.setPayCharacteristic(-44);
        doc2.setPayAmount(-55.2f);
        doc2.setRegisterOptions(ro);
        RegisterOptionsDocinActivity docActivity2 = new RegisterOptionsDocinActivity();
        docActivity2.setActivityId(-99);
        docActivity2.setDocIn(doc2);
        doc2.getDocsInActivity().add(docActivity2);


        ro.getDocsIn().add(doc1);
        ro.getDocsIn().add(doc2);
        try {
            JPA.getUtil().begin();

            dao.save(ro);
            JPA.getUtil().commit();

            assertNotNull(ro.getId());
            assertEquals(2, ro.getDocsIn().size());
            System.out.println("SavedId=" + ro.getId());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error ehen save");
        }
    }

    @Test
    public void testRemoveDocIn(){
        System.out.println("========== save start ==========");
        testSaveMainWithDocsIn();
        System.out.println("========== save end ==========");
        try {
            RegisterOptions roTmp = dao.findById(ro.getId());

            System.out.println("========== remove start ==========");
            JPA.getUtil().begin();

            roTmp.getDocsIn().remove(0);
            dao.save(roTmp);
            JPA.getUtil().commit();
            System.out.println("========== remove end ==========");

            RegisterOptions roTmpDeleted = dao.findById(ro.getId());
            assertEquals(1, roTmpDeleted.getDocsIn().size());
        }catch(Exception e){
            e.printStackTrace();
            fail("Error removing document in");
        }

    }
    @Test
    public void testSaveMainWithDocsOut() {


        //List<RegisterOptionsDocsIn> docs=new ArrayList<RegisterOptionsDocsIn>();
        RegisterOptionsDocsOut doc1 = new RegisterOptionsDocsOut();
        doc1.setNomGenerator("alabala");
        doc1.setSaveNomReissue(-1);
        doc1.setSaveNomReissue(-2);
        doc1.setVidDoc(-3);
        doc1.setRegisterOptions(ro);
        doc1.setTypePeriodValid(0);
        doc1.setPeriodValid(10);

        ro.getDocsOut().add(doc1);
        try {
            JPA.getUtil().begin();

            dao.save(ro);
            JPA.getUtil().commit();

            assertNotNull(ro.getId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Error ehen save");
        }
    }

    @Test
    public void testSaveMainWithDisplay() {
        //List<RegisterOptionsDocsIn> docs=new ArrayList<RegisterOptionsDocsIn>();
        RegisterOptionsDisplay dsp = new RegisterOptionsDisplay();
        dsp.setObjectClassifId(-1);
        dsp.setRegisterOptions(ro);
        ro.getDisplay().add(dsp);
        try {
            JPA.getUtil().begin();

            dao.save(ro);
            JPA.getUtil().commit();

            assertNotNull(ro.getId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Error ehen save");
        }
    }


    /**
     * Запис и извличане на цял обект
     * 1. записваме 1 обект в РегистерОптионс
     * 2.записаме 2 входящи документа всеки с по един обект за показване
     * <p>
     * !!! Ползва другите тестое за зинсърт.Ако там се промени - този метод ще се скапе
     */
    @Test
    public void testGet() {
        try {
            testSaveMainWithDocsIn();
            testSaveMainWithDocsOut();
            testSaveMainWithDisplay();
            assertNotNull(ro.getId());

            System.out.println("generated Id:" + ro.getId());

            //        Query q = JPA.getUtil().getEntityManager().createNativeQuery("select * from register_options where id=:regOptionsId");
            //        q.setParameter("regOptionsId",ro.getId());
            //        List res = q.getResultList();
            //        assertNotNull(res);
            //        assertTrue(1<=res.size());
            System.out.println("========== end save ==========");
            //Това е само за да лоадне от базата е не от паметта
            org.hibernate.Session session = (org.hibernate.Session) JPA.getUtil().getEntityManager().getDelegate();
            session.evict(ro);
            System.out.println("========== start load id " + ro.getId() + "==========");

            //Chek main object
            RegisterOptions roDB = JPA.getUtil().getEntityManager().find(RegisterOptions.class, ro.getId());
            assertNotNull(roDB);

            //Chek DocsIn
            assertNotNull(roDB.getDocsIn());
            System.out.println("docsIn.size="+roDB.getDocsIn().size());
            assertEquals("Docs_In size is:" + roDB.getDocsIn().size(), 2, roDB.getDocsIn().size());
            //Check docs in activity
            for (RegisterOptionsDocsIn docsIn : roDB.getDocsIn()) {
                assertNotNull(docsIn.getDocsInActivity());
            }
            //Chek docs out
            assertNotNull(roDB.getDocsOut());
            assertEquals(1, roDB.getDocsOut().size());
            assertEquals(0, roDB.getDocsOut().get(0).getTypePeriodValid().intValue());
            assertEquals(10, roDB.getDocsOut().get(0).getPeriodValid().intValue());

            //Check dosplay
            assertNotNull(roDB.getDisplay());
            assertEquals(1, roDB.getDisplay().size());
            System.out.println("========== end load ==========");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }
@Test
    public void testGet2(){
        try {

            //Chek main object
            RegisterOptions roDB = JPA.getUtil().getEntityManager().find(RegisterOptions.class, 824);
            assertNotNull(roDB);

            //Chek DocsIn
            assertNotNull(roDB.getDocsIn());
            System.out.println("docsIn.size="+roDB.getDocsIn().size());
            assertEquals("Docs_In size is:" + roDB.getDocsIn().size(), 1, roDB.getDocsIn().size());
            //Check docs in activity

            System.out.println("========== end load ==========");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void testLoadROptions(){


//        EntityGraph graph = JPA.getUtil().getEntityManager().getEntityGraph("test1");
//        Map hints = new HashMap();
//        hints.put("javax.persistence.fetchgraph", graph);

        Stream fromRegisterOptions = JPA.getUtil().getEntityManager().createQuery("from RegisterOptions order by id").getResultList().stream();
        Map<Integer,RegisterOptions> mapR = (HashMap)fromRegisterOptions.collect(Collectors.toMap(RegisterOptions::getRegisterId, Function.identity(), (oldValue, newValue) -> oldValue));
        System.out.println(mapR.size());
        assertTrue("Броя на регистрите трябва да е !=0",mapR.size()>0);
        List<RegisterOptionsDocsIn> docsIn=null;

        int docsCount = 0;
        //vid document- viz komponenti
        Map<Integer,Map<Integer,RegisterOptionsDisplay>> regDisplay=new HashMap<>();
        //Съответствие м/у вид док-регистър
        Map<Integer,Integer> vidDoc2Register=new HashMap();
        //Действия за документ
        Map<Integer,Map<Integer, RegisterOptionsDocinActivity>>  actionsByVidDoc = new HashMap<>();

        for (Map.Entry<Integer, RegisterOptions> entry : mapR.entrySet()) {
            RegisterOptions register = entry.getValue();
            List<RegisterOptionsDisplay> display = register.getDisplay();
             docsIn = register.getDocsIn();
            System.out.println("aa:"+docsIn.size());
             docsCount=docsCount+docsIn.size();
            System.out.println("docsCount="+docsCount);
            for (RegisterOptionsDocsIn currentDoc : docsIn) {
                vidDoc2Register.put(currentDoc.getVidDoc(), register.getRegisterId());

                Stream<RegisterOptionsDisplay> streamDisplay = display.stream();
                regDisplay.put(currentDoc.getVidDoc(), streamDisplay.collect(Collectors.toMap(RegisterOptionsDisplay::getObjectClassifId, Function.identity(), (oldValue, newValue) -> oldValue)));

                Stream<RegisterOptionsDocinActivity> streamActins = currentDoc.getDocsInActivity().stream();
                actionsByVidDoc.put(currentDoc.getVidDoc(),streamActins.collect(Collectors.toMap(RegisterOptionsDocinActivity::getActivityId, Function.identity(), (oldValue, newValue) -> oldValue)));
            }
        }

        System.out.println("Docs count="+docsIn.size());
        System.out.println("NAstr per doc count="+regDisplay.size());
        assertEquals("броя на входящите документи не съвпада!!!",regDisplay.size(), docsCount);
        assertEquals("Count of vidDoc2Register is incorrect",vidDoc2Register.size(),docsCount);





    }
    @Test
    public void testLoadAttrByDoc(){
        Stream fromRegisterOptions = JPA.getUtil().getEntityManager().createQuery("from RegisterOptions order by id").getResultList().stream();
        Map<Integer,RegisterOptions> mapR = (HashMap)fromRegisterOptions.collect(Collectors.toMap(RegisterOptions::getRegisterId, Function.identity(), (oldValue, newValue) -> oldValue));
        System.out.println(mapR.size());
        assertNotNull(mapR);
        assertTrue(mapR.size()>0);
//        mapR.entrySet().stream().filter(e -> "Effective Java".equals(e.getValue().getDocsIn().))


    }

    @Test
    public void testLoadDoc2Register(){
//        Stream fromRegisterOptionsDocsIn = JPA.getUtil().getEntityManager().createQuery("from RegisterOptionsDocsIn order by id").getResultList().stream();
        Stream fromRegisterOptionsDocsIn = JPA.getUtil().getEntityManager().createQuery("from RegisterOptionsDocsIn order by id").getResultList().stream();
        Map<Integer,RegisterOptionsDocsIn> mapROD = (HashMap)fromRegisterOptionsDocsIn.collect(Collectors.toMap(RegisterOptionsDocsIn::getVidDoc, Function.identity(), (oldValue, newValue) -> oldValue));
        System.out.println(mapROD);
        assertNotNull(mapROD);
        assertTrue(mapROD.size()>0);
    }
    @After
    public void clean() throws Exception {
        System.out.println("========== start to delete all test data ==========");
        System.out.println("============== delete id:" + ro.getId());
        if (ro.getId()!=null) {
            try {
                JPA.getUtil().begin();


                dao.deleteById(ro.getId());
                JPA.getUtil().commit();
                org.hibernate.Session session = (org.hibernate.Session) JPA.getUtil().getEntityManager().getDelegate();
                session.evict(ro);

            } catch (Exception e) {
                e.printStackTrace();
                //  fail("Error when delete");
            }
        }
        System.out.println("========== end to delete all test data ==========");
    }
}