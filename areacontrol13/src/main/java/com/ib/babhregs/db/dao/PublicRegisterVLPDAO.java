package com.ib.babhregs.db.dao;


import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.SelectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Map;


/**
 * Основно дао, в което да се блъскат всички методи за генериране на публичните регистри
 */
public class PublicRegisterVLPDAO {
private static final Logger LOGGER = LoggerFactory.getLogger(PublicRegisterDAO.class);

//параметри за регистри 1-23 животни
private String [] [] params=  {
		//01 idRegister,typeObjectEpizodichnoZnch,508(видЖ), 502(предн.) 518(техн.)
		{"47","2,4","37","30","19,20,1,21"},
		//02
		{"48","2,4","6,39,157,74","31,5,6,3",null},
		//03
		{"49","2,4","43,73,75,74",null,null},
		//04
		{"50","2,4","36",null,null},
		//05
		{"51","2,4","38,39,40,67,68,71,72,42,147,37","34",null},
		//06
		{"52","43",null,null,null},
		//07
		{"53","5,6",null,null,null},
		//08
		{"54","12,13",null,null,null},
		//09
		{"55","31",null,null,null},
		//10
		{"56","32","32",null,null},
		//11
		{"57","33",null,null,null},
		//12
		{"58","15",null,null,null},
		//13
		{"59",null,"1,17,18,19",null,null},
		//14
		{"60",null,"2,20,21,22",null,null},
		//15
		{"61",null,"4,24,25,26,27",null,null},
		//16
		{"62","1,3,8",null,null,null},
		//17
		{"63","18,35,47,48,49,50,46,41,51,20",null,null,null},
		//18
		{"64","7",null,null,null},
		//19
		{"65","23",null,null,null},
		//20
		{"66","40",null,null,null},
		//21
		{"67","39",null,null,null},
		//22
		{"68","42",null,"35,36",null},
		//23
		{"69","37,34,29,20",null,null,null},
		//24
		//{"6",null,null,null,null},
		//25
		//{"6",null,null,null,null}
		};

    /**
     * Основен метода за генериране на табличен резултат от регистър
     * в него ще има безброй суичове
     *
     * @param idRegister - id-to на регистъра от класификацията
     * @return връща списък от лицензирани неща
     */
    public SelectMetadata getRegisterAsList(Integer idRegister) {
        LOGGER.debug("genereate register for:{}",idRegister);
        SelectMetadata result = new SelectMetadata();

        switch (idRegister) {
            case 5: //Регистър на търговците на животни
                result = registerAnimalTraders(1);
                break;
                
            case -8: //Регистър на транспортните средства, с които се превозват животни
                result = registerMPSAnimals();
                break;
                
            case 9: //Регистър на вет. лекари и обектите, в които осъществяват ветеринарно медицинска практика
                result = registerListVetsVLZ();
                break;
                
            case 11: //Регистър на издадените разрешителни за провеждане на опити с животни
 //               result = registerAnimalTrials();
                break;
                
            case 13: //Регистър на кипите за добив и съхранение на ембриони и яйцеклетки
                result = registerEmbrionTeams();
                break;
                
//            case 15: //Регистър на мероприятия с животни
//                result = registerAnimalEvents(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV);
//                break;
                
//            case 17: //Регистър на организациите, получили одобрително становище от БАБХ да организират курсове 
//                result = registerOrganizationsCources();
//                break;
                
            case 18: //Списък на издадените удостоверения за правоспособност на водачи на транспортни средства, в които се превозват животни, и на придружители на животни при транспортиране 
                result = registerTransportSvidetelstvo();
                break;
                
            case 19: //Регистър на заявленията за изплащане на обезщетения на собствениците на убити или унищожени животни
                result = registerPayDeadAnimals();
                break;
                
            case 20: //Регистър на издадените разрешения за износ на животни и зародишни продукти
                result = registerSellAnimalsAbroad();
                break;
                
            case 47://Регистър на ферми за стокови кокошки носачки, отглеждани в алтернативни системи и уголемени клетки
            	result = registerAnimalDefault(params[0] ) ;
                break;
                
            case 48://Регистър на ферми за развъдни стада птици
            	result = registerAnimalDefault(params[1] ) ;
                break;
                
            case 49://Регистър на ферми за водоплаващи птици 
            	result = registerAnimalDefault(params[2] ) ;
                break;
                
            case 50://Регистър на ферми за бройлери
            	result = registerAnimalDefault(params[3] ) ;
                break;
            case 51://Регистър на ферми за други птици (пуйки, подрастващи носачки и др.)
            	result = registerAnimalDefault(params[4] ) ;
                break;
                
            case 52://Регистър на люпилните 
            	result = registerAnimalDefault(params[5] ) ;
                break;
            case 53://Регистър на пчелините в България 
            	result = registerAnimalDefault(params[6] ) ;
                break;
                
            case 54://Регистър на ферми за аквакултури
            	result = registerAnimalDefault(params[7] ) ;
                break;
                
            case 55://Регистър на приютите за животни
            	result = registerAnimalDefault(params[8] ) ;
                break;
                
            case 56://Регистър на развъдниците за кучета 
            	result = registerAnimalDefault(params[9] ) ;
                break;
            case 57://Регистър на хотели и пансиони за домашни любимци 
            	result = registerAnimalDefault(params[10] ) ;
                break;
                
            case 58://Регистър на животновъдни обекти за космат и пернат дивеч 
            	result = registerAnimalDefault(params[11] ) ;
                break;
                
            case 59://Регистър на животновъдни обекти за ЕПЖ (едри преживни животни)
            	result = registerAnimalDefault(params[12] ) ;
                break;
                
            case 60://Регистър на животновъдни обекти за ДПЖ (дребни преживни животни)
            	result = registerAnimalDefault(params[13] ) ;
                break;
                
            case 61://Регистър на животновъдни обекти за ЕКЖ (еднокопитни животни) 
            	result = registerAnimalDefault(params[14] ) ;
                break;
                
            case 62://Регистър на животновъдни обекти за свине
            	result = registerAnimalDefault(params[15] ) ;
                break;
            case 63://Регистър на животновъдни обекти за други видове животни (за други видове животни, 
            	//различни от упоменатите по - горе - за зайци, буби, охлюви, калифорнийски червей и др., животни за атракция, животни за опити)
            	result = registerAnimalDefault(params[16] ) ;
                break;
                
            case 64://Регистър на животновъдни обекти – „Лично стопанство“
            	result = registerAnimalDefault(params[17] ) ;
                break;
            case 65://Регистър на одобрени пазари за живи животни
            	result = registerAnimalDefault(params[18] ) ;
                break;
                
            case 66://Регистър на обектите за събиране или карантиниране на животни 
            	result = registerAnimalDefault(params[19] ) ;
                break;
                
            case 67://Регистър на местата за почивка на животни по време на транспортиране
            	result = registerAnimalDefault(params[20] ) ;
                break;
                
            case 68://Регистър на центровете за трансплантация на ембриони, центровете за изкуствено осеменяване и центровете за съхранение на сперма 
            	result = registerAnimalDefault(params[21] ) ;
                break;
                
            case 69://Регистър на зоопаркове, терариуми, циркове, волиери, вивариуми
            	result = registerAnimalDefault(params[22] ) ;
                break;
            	
            case 72://Регистър на производителите на средства за идентификация на животните
                result = registerAnimalIdentifiers(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT);//6
                break;
                
            case 73://Регистър на търговците на средства за идентификация на животните
                result = registerAnimalIdentifiers(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT);//7
                break;
                
            case 74://Регистър на търговците на зародишни продукти
                result = registerAnimalTraders(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD);//2
                break;
                
//            case 23://Регистър на издадените лицензи за търговия на едро с ВЛП
//                result = registerVlpTraders();//Staro
//                break;
            case 31:// Регистър на одобрените обекти за търговия на фуражни добавки
                result = registerFurajniDobavki();
                break;
 
            case 32:// Регистър на регистрираните обекти в сектор Фуражи съгласно чл. 17, ал. 3 от ЗФ
                result = registerRegObecti17();
                break;
                
            case 44:// Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
                result = registerRegOOP();
                break;
            case 24://Регистър на издадените разрешения за търговия на дребно с ВЛП 
            case 23://Регистър на издадените разрешения за търговия на едро с ВЛП/drдребно с ВЛП
                result = registriVLP24(idRegister);
                break;
   
            case 121:// Регистър на издадените разрешения за внос на ВЛП    
            case 120://Регистър на издадените разрешения за производство на ВЛП 
                result = registriVLP(idRegister);
                break;
            case 29://Списък на търговците с разрешение за продажба на ВЛП от разстояние 
            case 122://Списък на търговците с разрешение за паралелна търговия с ВЛП
//              result = registerVlpTradersDistance(idRegister);
                result = registerVlpTradersDistanceParalel(idRegister);//след обединение
                break;

            case 28://Регистър на одобрените реклами 
                result = registerVlpPublicity(idRegister);
                break;
            case 30://Списък на регистрираните производители, вносители и разпространители на активни вещества 
                result = registerVlpАctiveSubstances(idRegister);
                break;
            case 27://Регистър на инвитро диагностичните ВЛП 
                result = registerVlpInVitro(idRegister);
                break;
            case 26://Регистър на издадените разрешения за дейности с наркотични вещества за ветеринарномедицински цели 
                result = registerVlpNarkoVMC(idRegister);
                break;
            case 25://Регистър на издадените разрешения за търговия с ВЛП 
                result = registerVlpTraders(idRegister);
                break;

                
                
                
        	}
        return result;
    }

 

    /**
     * СК520-26
     * Регистър на издадените разрешения за дейности с наркотични вещества за ветеринарномедицински цели 
     * @return
     */
	private SelectMetadata registerVlpNarkoVMC(Integer idRegister) {

        LOGGER.debug("registerVlpNarkoVMC()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
        		+ "                    v.id AS id_vpisvane ,  "        		
        		+ "                    v.reg_nom_result AS licenz,"
        		+ "                    array_to_string (ARRAY  "
        		+ "                   (   SELECT  "
        		+ "                            vsc.tekst  "
        		+ "                        FROM  "
        		+ "                            event_deinost_vlp_vid      vid  "
        		+ "                            JOIN v_system_classif vsc ON (  "
        		+ "                                    vsc.code_classif=506"
        		+ "                                AND vsc.code=vid.vid)  "
        		+ "                        WHERE  "
        		+ "                             vid.id_event_deinost_vlp = edv.id),'<br />') AS vid_dein,"        		
        		+ "                     od.naimenovanie         as naim,"
        		+ "                     od.nas_mesto            AS OEZ_NAS_MESTO ,"
        		+ "                     od.obsht                AS OEZ_OBSHT,"
        		+ "                     od.obl                  AS OEZ_OBL,"
        		+ "                     od.address              AS OEZ_ADDRESS,"
        		+ "                      array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                            vsc1.tekst  "
        		+ "                        FROM  "
        		+ "                             event_deinost_vlp_vid      vidf  " // TODO заменя се от event_deinost_furaji_predmet
        		+ "                            JOIN v_system_classif vsc1 ON (  "
        		+ "                                    vsc1.code_classif=vidf.vid" // TODO BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ
        		+ "                                AND vsc1.code=vidf.vid)  "
        		+ "                        WHERE  "
        		+ "                            vidf.id_event_deinost_vlp = edv.id),'<br />') AS vid_f, "
        		+ "                   v.licenziant as licenziant,"
        		+ "                   v.date_result as date_licenz, "        		
         		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
            	+ "                   v.date_zaqvl_vpis AS date_zaqvl,  "        		
        		+ "                   array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                            ar.ref_name ||', '||vsc1.tekst||', '|| TO_CHAR(edvl.date_beg ,'dd.mm.yyyy')  "
        		+ "                        FROM  "
        		+ "                             event_deinost_vlp_lice edvl  " 
        		+ "                            JOIN adm_referents ar "
        		+ "								 ON   "
        		+ "                                 ar.code=edvl.code_ref " 
        		
        		+ "                            JOIN v_system_classif vsc1 ON (  "
        		+ "                                    vsc1.code_classif= "+ BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT+" " 
        		+ "                                                          )  "
        		+ "       				 	WHERE vsc1.code=edvl.tip_vraz "
        		+ "                     	 AND    edvl.event_deinost_vlp_id =edv.id),'<br />') AS liceObekt, "
        		+ "                   ara.ekatte as ekatte, "        		
           		+ "                   ara.addr_text as addr_text,  "        		
           		+ "                   concat_ws(' ',att.tvm , att.ime) as grLizenziant,  "        		
           		+ "                   concat_ws(' ','Обл.',att.oblast_ime) as oblLizenziant,  "        		
           		+ "                   concat_ws(' ','Общ.',att.obstina_ime) as obstinaLizenziant,  "        		

        		+ "                   v.info as zabelejka, "        		
           		+ "                   array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                           sc1.tekst  "
        		+ "                        FROM  "
        		+ "                             event_deinost_vlp_predmet edvp  " 
        		+ "                            JOIN v_system_classif sc1 ON (  "
        		+ "                                    sc1.code_classif= "+ BabhConstants.CODE_CLASSIF_NARK_VESHTESTVA+" " 
        		+ "                                    and sc1.code=edvp.predmet  AND sc1.lang=1                     )  "
        		+ "       				 	WHERE  "
        		+ "                     	 edvp.id_event_deinost_vlp=edv.id),'<br />') AS vechtestva "

        		+ "FROM  "
        		+ "                    vpisvane v "
        		+ "                    JOIN event_deinost_vlp edv ON  v.id=edv.id_vpisvane  "
        		+ "                    JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein"
        		+ "                    JOIN obekt_deinost od   ON  odd.obekt_deinost_id = od.id  "
        		+ "                    LEFT OUTER JOIN vlp vlp  ON  vlp.id_vpisvane=v.id  "
        		+ "                    LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = 2) "
        		+ "                    LEFT OUTER JOIN  ekatte_att att ON att.ekatte=ara.ekatte "

        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }




	/** Регистър на производителите и търговци на средства за идентификация на животните
     *
     * @param type =7-търговци,8 производители
     * @return
     */
    private SelectMetadata registerAnimalIdentifiers(int type) {
        LOGGER.debug("registerAnimalIdentifiers(type={})",type);
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =   "select id_vpisvane," +
                "       oblast_text," +
                "       obshtina_text," +
                "       addr_text," +
                "       licenziant_name," +
                "       licenziant_egn," +
                "       nomer_licenz," +
                "       date_licenz," +
                "       identInfo from v_register_12" +
                " WHERE edjv_vid=:type";

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        smd.setSql(sql);
        smd.setSqlCount("select count(id_vpisvane) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("type", type);


        smd.setSqlParameters(sqlParameters);

        return smd;
    }

    /**
     * Starooo
     * СК520-23
     * Регистър на издадените лицензи за търговия на едро с ВЛП
     * @return
     */
//    private SelectMetadata registerVlpTraders() {
//
//        LOGGER.debug("registerVlpTraders()");
//        SelectMetadata smd=new SelectMetadata();
//        //noinspection SpellCheckingInspection
//        String sql =  "SELECT " +
//                "    v.id, " +
//                "    v.reg_nom_result                                             AS nomer_licenz , " +
//                "    v.date_result                                                AS date_licenz , " +
//                "    ar.ref_name || ' '|| COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') AS NAME, " +
//                "    od.naimenovanie                                              AS obekt_naim, " +
//                "    od.nas_mesto                                                 AS OEZ_NAS_MESTO , " +
//                "    od.obsht                                                     AS OEZ_OBSHT, " +
//                "    od.obl                                                       AS OEZ_OBL, " +
//                "    od.address                                                   AS OEZ_ADDRESS, " +
//                //"    odl.imena , " +
//                "    odl.code_ref imena , " + // TODO трябва да се разкодира през рефентите
//                "    array_to_string (ARRAY " +
//                "    (   SELECT " +
//                "            vsc.tekst " +
//                "        FROM " +
//                "            event_deinost_vlp_predmet      predmet " +
//                "            JOIN v_system_classif vsc ON ( " +
//                "                    vsc.code_classif=predmet.code_classif " +
//                "                AND vsc.code=predmet.predmet) " +
//                "        WHERE " +
//                "            predmet.id_event_deinost_vlp=edv.id),', ') AS predmet " +
//                "FROM " +
//                "    vpisvane                         v "+//-- основни данни за вписването " +
//                "    JOIN adm_referents      ar  ON ar.code=v.id_licenziant "+//-- licenziant " +
//                "    JOIN event_deinost_vlp           edv ON v.id=edv.id_vpisvane " +
//                "    JOIN obekt_deinost_deinost       odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " +   //100 " +
//                "    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id " +
//                "    JOIN obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
//                "WHERE " +
//                "    v.id_register=:idRegister " +
//                "AND v.status=2";//-- интересуват ни само тези със статус - 2 - вписан";
//
////        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
////        query.setParameter("type", type);
////        return query.getResultList();
//        smd.setSql(sql);
//        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
//        Map<String, Object> sqlParameters=new HashMap<>();
//        sqlParameters.put("idRegister", 23);
//        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);
//        
//        smd.setSqlParameters(sqlParameters);
//
//        return smd;
//    }

    /**
     * Генерира регистъра на търговзи на животни или зародишни продукти
     *
     * @param type 1- търговци на животни, 2 търговци на зародишни продукти
     * @return List<Object[]> където колоните са :
     *  ид | номер на лицензиа | дата на лцензия | име+егн (ако е фрима е ЕИК)| нас.място | обл  | obsht | adress | видове животно (разделени със ,) | забележка
     */
    private SelectMetadata registerAnimalTraders(int type) {
        LOGGER.debug("registerAnimalTraders(type={})",type);
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql = "SELECT " +
                "    v.id, " +
                "    v.reg_nom_result                                                AS nomer_licenz , " +
                "    v.date_result                                                   AS date_licenz , " +
                "    ar.ref_name || ' '|| COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') as    NAME, " +
                "    od.nas_mesto                                                    AS OEZ_NAS_MESTO , " +
                "    od.obsht                                                        AS OEZ_OBSHT, " +
                "    od.obl                                                          AS OEZ_OBL, " +
                "    od.address                                                      AS OEZ_ADDRESS, " +
                "    array_to_string (ARRAY " +
                "    (   SELECT " +
                "            vsc.tekst " +
                "        FROM " +
                "            oez_harakt                     oh " +
                "            JOIN v_system_classif vsc ON ( " +
                "                    vsc.code_classif=508 " +
                "                AND vsc.code=oh.vid_jivotno) " +
                "        WHERE " +
                "            oh.id_OEZ=od.id),', ') AS vid_animals, " +
                "    edj.dop_info " +
                "FROM " +
                "    vpisvane                    v " + //-- основни данни за вписването  +
                "    JOIN adm_referents ar   ON ar.code=v.id_licenziant " + //licenziant
                "    JOIN event_deinost_jiv         edj  ON v.id=edj.id_vpisvane " +
                "    JOIN event_deinost_jiv_vid     edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= :type" +// -- 1-  -- животни -- , 2-зародишни продукти (класс 506 " +
                "    JOIN obekt_deinost_deinost odd ON odd.deinost_id=edj.id AND odd.tabl_event_deinost = :tabl_dein " +   //98
                "    JOIN obekt_deinost         od  ON odd.obekt_deinost_id = od.id " + //-- тук е оез-то
                " WHERE " +
                "    v.id_register=:idRegister " +
                "AND v.status=2 ";//-- интересуват ни само тези със статус - 2 - вписан";

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", 6);
        sqlParameters.put("type", type);
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    
    /**
     * СК520-31
     * Регистър на одобрените обекти, съгласно чл.20, ал. 3 от Закона за фуражите
     * @return
     */
    private SelectMetadata registerFurajniDobavki() {

        LOGGER.debug("registerAnimalTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
        		+ "                    v.id AS id_vpisvane  ,  "        		
        		+ "                    od.reg_nom   as ident ,"
        		+ "                    array_to_string (ARRAY  "
        		+ "                   (   SELECT  "
        		+ "                            vsc.tekst  "
        		+ "                        FROM  "
        		+ "                            event_deinost_furaji_vid      vid  "
        		+ "                            JOIN v_system_classif vsc ON (  "
        		+ "                                    vsc.code_classif=506"
        		+ "                                AND vsc.code=vid.vid)  "
        		+ "                        WHERE  "
        		+ "                            vid.event_deinost_furaji_id =  edf.id),', ') AS vid_dein,"        		
        		+ "                     od.naimenovanie  as operatorObect,"
        		+ "                     od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
        		+ "                     od.obsht                                                     AS OEZ_OBSHT,"
        		+ "                     od.obl                                                       AS OEZ_OBL,"
        		+ "                     od.address                                                   AS OEZ_ADDRESS,"
        		+ "                      array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                            vsc1.tekst  "
        		+ "                        FROM  "
        		+ "                            event_deinost_furaji_vid      vidf  " // TODO заменя се от event_deinost_furaji_predmet
        		+ "                            JOIN v_system_classif vsc1 ON (  "
        		+ "                                    vsc1.code_classif=vidf.vid" // TODO BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ
        		+ "                                AND vsc1.code=vidf.vid)  "
        		+ "                        WHERE  "
        		+ "                            vidf.event_deinost_furaji_id =  edf.id),', ') AS vid_f, "
        		+ "                      v.licenziant as licenziant  ,"
        		+ "                      v.date_result as date_licenz "        		
        		+ "FROM  "
        		+ "                    vpisvane v "
        		+ "                    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane  "
        		+ "                    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein"
        		+ "                    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id  "
        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", 31);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    /**
     * @param type
     * @return
     * yoni
     */
//    private SelectMetadata registerAnimalEvents(int type) {
//        LOGGER.debug("registerAnimalEvents(type={})",type);
//        SelectMetadata smd=new SelectMetadata();
//        //noinspection SpellCheckingInspection
//        int idRegister=15;
//        int status= BabhConstants.STATUS_VP_VPISAN;
//        StringBuilder sql = new StringBuilder("SELECT ")
//        		.append("    v.id                                         							AS id_vpisvane , --00 Номер по ред")
//        		.append("  	 COALESCE(vsc2.tekst,'Undefined code:'||541||':'||edj.meroptiatie)				as   vid_meropr,--01 Вид на мероприятието")
//        		.append("  	edj.date_beg_meropriatie												AS   date_beg_meropr,--02 Начална дата")
//        		.append("  	edj.date_end_meropriatie 												AS   date_end_meropr,--03 Крайна дата")
//        		.append("  	edj.adres 																AS   mesto_meropr,--04 Място на мероприятието")
//        		.append("   v.reg_nom_result                                        				AS nomer_licenz ,--05 Рег. номер на заповедта")
//        		.append("   v.date_result                                             				AS date_licenz, --06 Дата на заповедта")
//        		.append("   v.registratura_id AS registratura_id , --07")
//				.append("   v.reg_nom_zaqvl_vpisvane   AS reg_nom_zaqvl_vpisvane, --08")
//				.append("   v.date_zaqvl_vpis AS date_zaqvl_vpis,  --09")
//        		.append("    array_to_string(ARRAY")
//        		.append("    (   SELECT")
//        		.append("            COALESCE(vsc.tekst,'Undefined code:'||508||':'||edjp.predmet)")
//        		.append("        FROM")
//        		.append("            event_deinost_jiv_predmet edjp ")
//        		.append("            left JOIN v_system_classif     vsc ON (")
//        		.append("                    vsc.code_classif=508")
//        		.append("                AND vsc.date_do IS NULL")
//        		.append("                AND vsc.code=edjp.predmet )")
//        		.append("        WHERE")
//        		.append("            edjv.event_deinost_jiv_id=edj.id),'=') AS animalInfo --10 Видове животни")
//        		.append(" FROM    vpisvane                   v ")
//        		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
//        		.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= :type ")
//        		.append(" LEFT JOIN v_system_classif vsc2 ON ( vsc2.code_classif=541 AND vsc2.date_do IS NULL AND vsc2.code=edj.meroptiatie) ")
//        		.append(" WHERE    v.id_register= :idRegister AND v.status= :status ");//-- интересуват ни само тези със статус - 2 - вписан";
//
//        String sqlString= sql.toString();
//        smd.setSql(sqlString);
//        
//        /* Формиране на заявката за броя резултати */
//        StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
//        .append(" FROM    vpisvane                   v  ")
//		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
//		.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid=:type ")
//		.append(" LEFT JOIN v_system_classif vsc2 ON ( vsc2.code_classif=541 AND vsc2.date_do IS NULL AND vsc2.code=edj.meroptiatie) ")
//		.append(" WHERE    v.id_register= :idRegister AND v.status= :status ");//-- интересуват ни само тези със статус - 2 - вписан";
//        String sqlCountString= sqlCountSB.toString();
//        smd.setSqlCount(sqlCountString);
//        
//        /* Слагаме параметрите */
//        Map<String, Object> sqlParameters=new HashMap<>();
//        sqlParameters.put("idRegister", idRegister);
//        sqlParameters.put("status",status );
//        sqlParameters.put("type", type);//11 мероприятия с животни 
//        smd.setSqlParameters(sqlParameters);
//        return smd;
//    }
//    
    
    /**
     * @param type
     * @return
     * yoni
     */
//    private SelectMetadata registerOrganizationsCources( ) {
//        LOGGER.debug("registerOrganizationsCources()");
//        SelectMetadata smd=new SelectMetadata();
//        int idRegister=17;
//        int status=BabhConstants.STATUS_VP_VPISAN;
//        //noinspection SpellCheckingInspection
//		StringBuilder sql = new StringBuilder("SELECT ")
//				.append(" v.id                                         AS id_vpisvane ,-- 00 ")
//				.append(" ara.ekatte                                                        AS addr_ekatte,--01 Номер по ред; ")
//				.append(" ea.oblast_ime                                                     AS oblast_text,--02 Област (лицензиант); ")
//				.append(" ea.obstina_ime                                                    AS obshtina_text,--03 Община (лицензиант); ")
//				.append(" ea.ime                                                            AS ekatte_text,--04 Населено място (лицензиант); ")
//				.append(" ara.addr_text,                                                                     --05 Улица/сграда (лицензиант); ")
//				.append(" ar.ref_type                             AS licenziant_type,--06 3 -firma,4-Lice; ")
//				.append(" ar.ref_name                                           AS licenziant_name, -- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице); ")
//				.append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                           AS licenziant_egn,--08 ЕИК (за ЮЛ); ")
//				.append(" edj.opisanie_cyr                                                                  as dein_opisanie,--09 Описание на дейността; ")
//				.append(" v.reg_nom_result                                        AS nomer_licenz ,--10 Рег. номер на становище; ")
//				.append(" v.date_result                                           AS date_licenz, --11 Дата на регистрация; ")
//				.append(" v.registratura_id AS registratura_id,--12 ")
//				.append("  v.reg_nom_zaqvl_vpisvane  AS reg_nom_zaqvl_vpisvane,--13 ")
//				.append("  v.date_zaqvl_vpis  AS date_zaqvl_vpis --14 ")
//				.append(" FROM vpisvane                   v ")
//				.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
//				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
//				.append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen")
//				.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
//				.append(" LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22 ")
//				.append(" AND vsc3.date_do IS NULL  AND vsc3.code=ara.addr_country) ")
//				.append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
//																			// вписан";
//		String sqlString= sql.toString();
//		smd.setSql(sqlString);
//		
//		/* Формиране на заявката за броя резултати */
//		StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
//		.append(" FROM vpisvane                   v ")
//		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
//		.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
//		.append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen")
//		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
//		.append(" LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22 ")
//		.append(" AND vsc3.date_do IS NULL  AND vsc3.code=ara.addr_country) ")
//		.append(" WHERE id_register= :idRegister AND v.status= :status ");
//		
//		String sqlCountString= sqlCountSB.toString();
//		smd.setSqlCount(sqlCountString);
//
//		/* Слагаме параметрите */
//        Map<String, Object> sqlParameters=new HashMap<>();
//        sqlParameters.put("idRegister", idRegister);
//        sqlParameters.put("status", status);
//        smd.setSqlParameters(sqlParameters);
//        return smd;
//    }
    
    
    /**
     * @return
     * yoni
     * ВАЖНО за всички удостоверителни документи вместо да join-ваме таблица doc  ползваме reg_nom_result, date_result пренесени от там
     */
    private SelectMetadata registerTransportSvidetelstvo( ) {
        LOGGER.debug("registerTransportSvidetelstvo()");
        SelectMetadata smd=new SelectMetadata();
        int idRegister=18;
        int status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				 .append(" v.id                                         AS id_vpisvane , -- 00 ")
			     .append(" ara.ekatte                                                        AS addr_ekatte,--01 Номер по ред; ")
			     .append(" ea.oblast_ime                                                     AS oblast_text,--02 Област (лицензиант); ")
			      .append(" ea.obstina_ime                                                    AS obshtina_text,--03 Община (лицензиант);    ")   
			      .append(" ea.ime                                                            AS ekatte_text,--04 Населено място (лицензиант);   ")                       
			      .append(" ara.addr_text,                                                                     --05 Улица/сграда (лицензиант); ")
			      .append(" ar.ref_type                             AS licenziant_type,--06 3 -firma,4-Lice ")
			      .append(" ar.ref_name                                           AS licenziant_name, --07 Лицензиант(имена на физическо лице или наименование на юридическо лице); ")
			      .append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                           AS licenziant_egn,--08 ЕИК (за ЮЛ); ")
			   	  .append(" edj.lice_kachestvo                                                                as lice_kachestvo,--09 Качество на лицето – водач, придружител; ")
			   	.append(" v.reg_nom_result                             AS nomer_licenz ,--10 Рег. номер на удостоверение; ")
	   			.append(" v.date_result                                              AS date_licenz,-- 11 Дата на издаване. ")
				.append(" v.registratura_id AS registratura_id,-- 12 ")
				.append(" v.reg_nom_zaqvl_vpisvane AS reg_nom_zaqvl_vpisvane,-- 13 ")
				.append(" v.date_zaqvl_vpis  AS date_zaqvl_vpis-- 14 ")
			  .append(" FROM vpisvane                   v ")
			     .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane and edj.lice_kachestvo in (1,2,3) --водач, придружител, водач и придружител ")
			      .append("JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= 12 --(кл. 506 дейност с регистри, издаване на лиценз правоспособност) ")
			     .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			     .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
			     .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			     .append("	LEFT JOIN v_system_classif vsc3 ON ( ")
			      .append("        vsc3.code_classif=22 ")
			      .append("    AND vsc3.date_do IS NULL ")
			       .append("   AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
		String sqlString= sql.toString();
		smd.setSql(sqlString);

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
		 .append(" FROM vpisvane                   v ")
	     .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane and edj.lice_kachestvo in (1,2,3) --водач, придружител, водач и придружител")
	     .append("JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= 12 --(кл. 506 дейност с регистри, издаване на лиценз правоспособност) ")
	     .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
	     .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
	     .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
	     .append("	LEFT JOIN v_system_classif vsc3 ON ( ")
	     .append("        vsc3.code_classif=22 ")
	     .append("    AND vsc3.date_do IS NULL ")
	     .append("   AND vsc3.code=ara.addr_country) ")
	    .append(" WHERE id_register= :idRegister AND v.status= :status ");

		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    
    /**
     * @return
     * yoni
     * Регистър на екипите, получили одобрение за добив и съхранение на ембриони и яйцеклетки
     */
    private SelectMetadata registerEmbrionTeams( ) {
        LOGGER.debug("registerEmbrionTeams()");
        SelectMetadata smd=new SelectMetadata();
        int idRegister=13;
        int status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" min(v.id)                                       AS id_vpisvane ,-- 00 ")
				.append(" min(v.reg_nom_result  )                           AS nomer_licenz ,--01 Рег. номер на удостоверение; ")
				.append(" min(v.date_result )                                              AS date_licenz,--02 Дата на издаване. ")
				.append("   (select TT.ekip from ( select max(edjl.event_deinost_jiv_id)as ttid , ")
				.append(" STRING_AGG (ar.ref_name || '(' ||COALESCE(vsc.tekst,'Undefined code:'||622||':'||edjl.tip_vraz)||')',';') as ekip  ")
				.append(" from event_deinost_jiv_lice edjl ")
				.append(" JOIN adm_referents         ar   ON edjl.code_ref =ar.code ")
				.append(" left JOIN v_system_classif     vsc ")
				.append(" ON ( vsc.code_classif=622 AND vsc.date_do IS NULL AND vsc.code=edjl.tip_vraz ) ")
				.append(" where edj.id=edjl.event_deinost_jiv_id group by edjl.event_deinost_jiv_id ) as TT ), --03 персонал ")
				.append(" min( v.registratura_id) as registratura_id,--04 ")
				.append(" min(v.reg_nom_zaqvl_vpisvane) as reg_nom_zaqvl_vpisvane,--05 ")
				.append(" min(v.date_zaqvl_vpis) as date_zaqvl_vpis--06 ")
				.append(" FROM vpisvane                   v ")
				.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane --and v.id_register=13 and v.status=1 ")
				.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
				.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";


		String sqlString= sql.toString();
		smd.setSql(sqlString);

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder(" select count(edj.id) ")
		.append(" FROM vpisvane                   v ")
		.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane ")
		.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
		.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
		.append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
//    private SelectMetadata registerAnimalTrials( ) {
//        LOGGER.debug("registerAnimalTrials()");
//        SelectMetadata smd=new SelectMetadata();
//        int idRegister=11;
//        int status=BabhConstants.STATUS_VP_VPISAN;
//        //noinspection SpellCheckingInspection
//		StringBuilder sql = new StringBuilder("SELECT ")
//				.append(" min(v.id)                                       AS id_vpisvane , --00 ")
//				.append(" min(v.reg_nom_result  )                           AS nomer_licenz ,-- 01 Рег. номер на удостоверение; ")
//				.append(" min(v.date_result )                                              AS date_licenz, --02 Дата на издаване. ")
//				.append(" min(ar.ref_name  )                                         AS licenziant_name,--03 Имена/наименование на лицензианта ")
//				.append(" min(ar.ref_type  )                           AS licenziant_type,--04 3 -firma,4-Lice ")
//				.append(" min(ara.addr_text )                           as licenziant_sedalishte,--05 Адрес/седалище на лицензианта; ")
//				.append(" min(edjv.vid 	)								as vid_deinost_obekt,--06 Вид на обекта на дейност; ")
//				.append(" min(edj.adres 	)                             as mesto, -- 07 Местонахождение на обекта; ")
//				.append(" min(edj.cel )                                as cel, --08 Цел на опитите; ")
//				.append("   (select TT.ekip from ( select max(edjl.event_deinost_jiv_id)as ttid , ")
//				.append(" STRING_AGG (ar.ref_name || '(' ||COALESCE(vsc.tekst,'Undefined code:'||622||':'||edjl.tip_vraz)||')',';') as ekip  ")
//				.append(" from event_deinost_jiv_lice edjl ")
//				.append(" JOIN adm_referents         ar   ON edjl.code_ref =ar.code ")
//				.append(" left JOIN v_system_classif     vsc ")
//				.append(" ON ( vsc.code_classif=622 AND vsc.date_do IS NULL AND vsc.code=edjl.tip_vraz ) ")
//				.append(" where edj.id=edjl.event_deinost_jiv_id group by edjl.event_deinost_jiv_id ) as TT ), -- 09 персонал ")
//				.append(" min( v.registratura_id) as registratura_id,-- 10")
//				.append(" min(v.reg_nom_zaqvl_vpisvane) as reg_nom_zaqvl_vpisvane,-- 11 ")
//				.append(" min(v.date_zaqvl_vpis) as date_zaqvl_vpis -- 12 ")
//				.append(" FROM vpisvane                   v ")
//				.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane --and v.id_register=13 and v.status=1 ")
//				.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
//				.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
//			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
//																			// вписан"
//
//		String sqlString= sql.toString();
//		smd.setSql(sqlString);
//		
//		/* Формиране на заявката за броя резултати */
//		StringBuilder sqlCountSB= new StringBuilder("select count(edj.id) ")
//			.append(" FROM vpisvane                   v ")
//			.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane ")
//			.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id")
//			.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id")
//			.append(" WHERE id_register= :idRegister AND v.status= :status ");
//        
//        
//        /* Слагаме параметрите */
//        Map<String, Object> sqlParameters=new HashMap<>();
//        sqlParameters.put("idRegister", idRegister);
//        sqlParameters.put("status", status);
//        smd.setSqlParameters(sqlParameters);
//        return smd;
//    }
//    
    /**
     * Регистър на регистрираните обекти в сектор Фуражи съгласно чл. 17, ал. 3 от ЗФ
     * @return
     */
    private SelectMetadata  registerRegObecti17() {

        LOGGER.debug("registerAnimalTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
        		+ "                    v.id,  "        		
        		+ "                    od.reg_nom,"
        		+ "                    array_to_string (ARRAY  "
        		+ "                   (   SELECT  "
        		+ "                            vsc.tekst  "
        		+ "                        FROM  "
        		+ "                            event_deinost_furaji_vid      vid  "
        		+ "                            JOIN v_system_classif vsc ON (  "
        		+ "                                    vsc.code_classif=506"
        		+ "                                AND vsc.code=vid.vid)  "
        		+ "                        WHERE  "
        		+ "                            vid.event_deinost_furaji_id =  edf.id),', ') AS vid_dein,"        		
        		+ "                     od.naimenovanie,"
        		+ "                     od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
        		+ "                     od.obsht                                                     AS OEZ_OBSHT,"
        		+ "                     od.obl                                                       AS OEZ_OBL,"
        		+ "                     od.address                                                   AS OEZ_ADDRESS,"
        		+ "                      array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                            vsc1.tekst  "
        		+ "                        FROM  "
        		+ "                            event_deinost_furaji_vid      vidf  " // TODO заменя се от event_deinost_furaji_predmet
        		+ "                            JOIN v_system_classif vsc1 ON (  "
        		+ "                                    vsc1.code_classif=vidf.vid" // TODO BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ
        		+ "                                AND vsc1.code=vidf.vid)  "
        		+ "                        WHERE  "
        		+ "                            vidf.event_deinost_furaji_id =  edf.id),', ') AS vid_f, "
        		+ "                      v.licenziant,v.date_result as date_licenz "        		
        		+ "FROM  "
        		+ "                    vpisvane v "
        		+ "                    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane  "
        		+ "                    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein"
        		+ "                    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id  "
        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", 32);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
   
    /**
     * СК520-44
     * Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
     * @return
     */
    private SelectMetadata  registerRegOOP() {

        LOGGER.debug("registerAnimalTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
         		+ "         v.id 			AS id_vpisvane  ,  "        		
            	+ "         v.licenziant 	as licenziant,"
         		+ "         v.reg_nom_result AS licenz,"
         		+ "         v.date_result AS date_licenz ,"
        		+ "         od.naimenovanie  AS naimObect,"
        		+ "         od.reg_nom       as reg_nom,"
        		+ "         od.obl           as obl ,"
        		+ "         od.obsht         as obsht  ,"
        		+ "         od.nas_mesto     as nas_mesto ,"
         		+ "         od.address       as address , "
        		+ "         array_to_string (ARRAY  "
        		+ "            ( SELECT  "
        		+ "                 vsc.tekst  "
        		+ "              FROM  "
        		+ "                 event_deinost_furaji_vid vid "
        		+ "              JOIN v_system_classif vsc ON (  "
        		+ "                 vsc.code_classif="+BabhConstants.CODE_CLASSIF_VID_DEINOST+ " "
        		+ "                   AND vsc.code=vid.vid)  "
        		+ "                        WHERE  "
        		+ "                            vid.event_deinost_furaji_id = edf.id),', ') AS vid_dein"        		
//        		+ "           od.naimenovanie,"
//        		+ "           od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
//        		+ "           od.obsht                                                     AS OEZ_OBSHT,"
//        		+ "           od.obl                                                       AS OEZ_OBL,"
//        		+ "           od.address                                                   AS OEZ_ADDRESS,"
//        		+ "                      array_to_string (ARRAY  "
//        		+ "                       (   SELECT  "
//        		+ "                            vsc1.tekst  "
//        		+ "                        FROM  "
//        		+ "                            event_deinost_furaji_vid      vidf  " // TODO заменя се от event_deinost_furaji_predmet
//        		+ "                            JOIN v_system_classif vsc1 ON (  "
//        		+ "                                    vsc1.code_classif=vidf.vid" // TODO BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ
//        		+ "                                AND vsc1.code=vidf.vid)  "
//        		+ "                        WHERE  "
//        		+ "                            vidf.event_deinost_furaji_id =  edf.id),', ') AS vid_f, "
//        		+ "                      v.licenziant,v.date_result as date_licenz "        		
        		+ " FROM  "
        		+ "           vpisvane v "
        		+ " JOIN event_deinost_furaji edf ON  v.id=edf.id_vpisvane  "
        		+ " LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein"
        		+ " LEFT OUTER JOIN obekt_deinost od  ON odd.obekt_deinost_id = od.id  "
        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", 44);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }

    

    
    private SelectMetadata registerAnimalDefault(String[] par ) {
        //idRegister,typeObjectEpizodichnoZnch,508(видЖ), 502(предн.) 518(техн.)
    	//String idRegister=null;
        String  epizodichnoZnch=null, vidAnimals=null, prednaznachenie=null, tehnologia=null;
        //00. регистър
//        if(par[0]!=null) {
//            idRegister=par[0];
//        }

        //01 обект епизодично значение
        if(par[1]!=null) {
            epizodichnoZnch=par[1];
        }
        //02. вид животно използва се общата класификация 508
        if(par[2]!=null) {
            vidAnimals=par[2];
        }
        //03 предназначение използва се класификация 502
        if(par[3]!=null) {
            prednaznachenie=par[4];
        }
        //04 техника на отглеждане, използва се класификация 518
        if(par[4]!=null) {
            tehnologia=par[4];
        }
        int idRegister=4;
        int status=BabhConstants.STATUS_VP_VPISAN;
        LOGGER.debug("registerAnimalDefault()");
        SelectMetadata smd=new SelectMetadata();
        StringBuilder sql = new StringBuilder("SELECT ")
        	    .append("     min(v.id   )                                                              AS id_vpisvane ,--00 ")
        	    .append("     min(v.reg_nom_result  )                                                   AS nomer_licenz ,--01 Рег. номер на удостоверение за регистрация; ")
        	    .append("     min(v.date_result     )                                                   AS date_licenz,-- 02 Дата на регистрация; ")
        	    .append("     min(v.registratura_id    )                                                as registratura_id,--03 ОЕЗ регистририла лицензията ")
        	    .append("     min(v.reg_nom_zaqvl_vpisvane     )                                        as reg_nom_zaqvl_vpisvane,-- 04 рег. номер първоначално  вписване ")
        	    .append("     min(v.date_zaqvl_vpis   )                                                 as date_zaqvl_vpis, --05 дата първоначално вписване ")
        	    .append("     min(ar.ref_type    )                                                      AS licenziant_type,--06 3 -firma,4-Lice ")
        	    .append("     min(od.naimenovanie     )                                                 AS OEZ_name,--07 име на ОЕЗ ")
        	    .append("     min(ar.ref_name      )                                                    AS licenziant_name,--08 оператор ")
        	    .append("     min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn,--09 ЕИК (за ЮЛ); ")
        	    .append("     min(ea.oblast_ime   )                                                     AS oblast_text,--10 Област (ОЕЗ); ")
        	    .append("     min(ea.obstina_ime   )                                                    AS obshtina_text,--11 Община (ОЕЗ);     ")   
        	    .append("     min(ea.ime    )                                                           AS ekatte_text,--12 Населено място (ОЕЗ);    ")   
        	    .append("     min(od.address    )                                                       as ulica,--13 Улица/сграда (ОЕЗ); ")
        	    .append("     min(od.gps_lat ||' '||od.gps_lon   )                                      as gps_coord,--14 GPS - координати; ")
        	    .append("     min(od.reg_nom           )                                                as vet_num,--15 Ветеринарен рег. номер на ОЕЗ; ")
        	    .append("     min(COALESCE(vsc3.tekst,'Undefined code:'||519||':'||od.vid)  )           as oez_vid,--16 Вид на ОЕЗ; ")
        	    .append("     (select  TT.harakTXT from ")
        	    .append("     (select max(oh. id_oez) as OEZid, ")
        	    .append("           STRING_AGG ('Вид жив:'||COALESCE(vsc0.tekst,'Undefined code:'||508||':'||oh.vid_jivotno)||';' ")
        	    .append("           || 'Предн:'||COALESCE(vsc1.tekst,'Undefined code:'||502||':'||oh.prednaznachenie)||';' ")
        	    .append("           || 'Техн:'||COALESCE(vsc2.tekst,'Undefined code:'||518||':'||oh.tehnologia)||';Кап:'||oh.kapacitet_text,'=END =') as harakTXT ")
        	    .append("            from oez_harakt oh ")
        	    .append("            left JOIN v_system_classif     vsc0 ON ( ")
        	    .append("                    vsc0.code_classif=508--classif 508 Вид на животно ")
        	    .append("                AND vsc0.date_do IS NULL ")
        	    .append("                AND vsc0.code=oh.vid_jivotno ) ")
        	    .append("        left JOIN v_system_classif vsc1 ON ( ")
        	    .append("                vsc1.code_classif=502 -- classif 502 Предназначение на животните ")
        	    .append("            AND vsc1.date_do IS NULL ")
        	    .append("           AND vsc1.code = oh.prednaznachenie)  ")
        	    .append("        left JOIN v_system_classif vsc2 ON ( ")
        	    .append("                vsc2.code_classif=518 -- classif 518 Технология на отглеждане ")
        	    .append("            AND vsc2.date_do IS NULL ")
        	    .append("            AND vsc2.code = oh.tehnologia)  ")
        	    .append("            where oh.id_oez=od.id ");
        	    if(vidAnimals!=null) {
        	        sql.append("    and oh.vid_jivotno in( "+ vidAnimals+" ) --classif 508 Вид на животно ");
        	    }
        	    if(prednaznachenie!=null) {
        	      sql.append("    and oh.prednaznachenie in( "+ prednaznachenie+" ) --classif 502 Предназначение на животните ");
        	    }
        	    if(tehnologia!=null) {
        	      sql.append("    and oh.tehnologia in( "+ tehnologia+" )-- classif 518 Технология на отглеждане ");
        	    }
        	     sql.append("   group by oh. id_oez ) TT)   as harakTXT --17 Характеристики на обекта (много): ")
        	    .append("FROM vpisvane                   v ")
        	    .append("join obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  ")
        	    .append("and od.vid =1 --classif 519 Обект на дейност  ");
        	     if(epizodichnoZnch!=null) {
        	        sql.append("    and od.vid_oez  in("+ epizodichnoZnch+")--classif 500 Вид на обекта с епизоотично значение ");
        	    }
        	    sql.append("left join oez_harakt oh              on oh.id_oez=od.id ")
        	    .append("join obekt_deinost_lica odl on odl.obekt_deinost_id=od.id ")
        	    .append("JOIN adm_referents         ar   ON ar.code=odl.code_ref  ")
        	    .append("LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto ")
        	    .append("LEFT JOIN v_system_classif vsc4 ON ( ")
        	    .append("        vsc4.code_classif=22 --classif 22 ekatte ")
        	    .append("    AND vsc4.date_do IS NULL ")
        	    .append("    AND vsc4.code=37 --bulgaria (kod na strana) ")
        	    .append("    ) ")
        	    .append("LEFT JOIN v_system_classif vsc3 ON ( ")
        	    .append("    vsc3.code_classif=519 --classif 519 Вид на ОЕЗ ")
        	    .append("AND vsc3.date_do IS NULL ")
        	    .append("AND vsc3.code=od.vid ) ")
        	.append("WHERE id_register= :idRegister AND v.status= :status --classif 516 Статус на вписване ")
        	.append("group by od.id --  записите се събират(групират) по ид ОЕЗ  ");

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        String sqlString= sql.toString();
        smd.setSql(sqlString);
        
        /* Формиране на заявката за броя резултати */
        StringBuilder sqlCountSB= new StringBuilder("select count(od.id )  ")
        .append("FROM vpisvane                   v ")
	    .append("join obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  ")
	    .append("and od.vid =1 --classif 519 Обект на дейност  ");
	     if(epizodichnoZnch!=null) {
	    	 sqlCountSB.append("    and od.vid_oez  in("+ epizodichnoZnch+")--classif 500 Вид на обекта с епизоотично значение ");
	    }
	     sqlCountSB.append("left join oez_harakt oh              on oh.id_oez=od.id ");
	    
	    if(vidAnimals!=null) {
	    	sqlCountSB.append("    and oh.vid_jivotno in( "+ vidAnimals+" ) --classif 508 Вид на животно ");
	    }
	    if(prednaznachenie!=null) {
	    	sqlCountSB.append("    and oh.prednaznachenie in( "+ prednaznachenie+" ) --classif 502 Предназначение на животните ");
	    }
	    if(tehnologia!=null) {
	    	sqlCountSB.append("    and oh.tehnologia in( "+ tehnologia+" )-- classif 518 Технология на отглеждане ");
	    }
	    
	    sqlCountSB.append("join obekt_deinost_lica odl on odl.obekt_deinost_id=od.id ")
	    .append("JOIN adm_referents         ar   ON ar.code=odl.code_ref  ")
	    .append("LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto ")
	    .append("LEFT JOIN v_system_classif vsc4 ON ( ")
	    .append("        vsc4.code_classif=22 --classif 22 ekatte ")
	    .append("    AND vsc4.date_do IS NULL ")
	    .append("    AND vsc4.code=37 --bulgaria (kod na strana) ")
	    .append("    ) ")
	    .append("LEFT JOIN v_system_classif vsc3 ON ( ")
	    .append("    vsc3.code_classif=519 --classif 519 Вид на ОЕЗ ")
	    .append("AND vsc3.date_do IS NULL ")
	    .append("AND vsc3.code=od.vid ) ")
	    .append("WHERE id_register= :idRegister AND v.status= :status --classif 516 Статус на вписване ");
	    String sqlCountString= sqlCountSB.toString();
        smd.setSqlCount(sqlCountString);
        
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);  
        
        if(vidAnimals!=null) {
            sqlParameters.put("vidAnimals", vidAnimals);
        }
        if(vidAnimals!=null) {
            sqlParameters.put("vidAnimals", vidAnimals);
        }
      if(prednaznachenie!=null) {
          sqlParameters.put("prednaznachenie", prednaznachenie);
      }
      if(tehnologia!=null) {
          sqlParameters.put("tehnologia", tehnologia);
      }
        
        smd.setSqlParameters(sqlParameters);
        
        System.out.println(smd.toString());
        return smd;
    }
    
    
    /**
     * Регистър на заявленията за изплащане на обезщетения на собствениците на убити или унищожени животни
     * yoni
     */
    private SelectMetadata registerPayDeadAnimals( ) {
        LOGGER.debug("registerPayDeadAnimals()");
        SelectMetadata smd=new SelectMetadata();
        int idRegister=19;
        int status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				 .append(" v.id                                                              AS id_vpisvane,-- 00 id на вписване  ")
				    .append(" ara.ekatte                                                        AS addr_ekatte,-- 01 Населено място(лицензиант); ")
				    .append(" ea.oblast_ime                                                     AS oblast_text,-- 02 Област (лицензиант); ")
				    .append(" ea.obstina_ime                                                    AS obshtina_text,-- 03 Община (лицензиант);    ")    
				    .append(" ea.ime                                                            AS ekatte_text,-- 04 Населено място (лицензиант);                           ")
				    .append(" ara.addr_text                                                     AS licenziant_address,-- 05 Улица/сграда (лицензиант); ")
				    .append(" ar.ref_type                                                       AS licenziant_type,-- 06 3 -firma,4-Lice ")
				    .append(" ar.ref_name                                                       AS licenziant_name, -- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице); ")
				    .append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                           AS licenziant_egn,-- 08 ЕИК (за ЮЛ); ")
				   	.append(" od.naimenovanie                                                   AS OEZ_name,  -- 09 Наименование на ОЕЗ; ")
				    .append(" od.reg_nom                                                        AS OEZ_rn, -- 10 Рег. номер на ОЕЗ; ")
				    .append(" edjp.broi                                                         AS broi, -- 11 Брой животни; ")
				    .append(" v.reg_nom_result                                                  AS nomer_licenz, --12 Рег. номер на акта; ")
				    .append(" v.date_result                                                     AS date_licenz,-- 13 Дата на регистрация. ")
				    .append(" v.registratura_id                                                 AS registratura_id,-- 14 ОЕЗ регистририла лицензията     ")        
				    .append(" v.reg_nom_zaqvl_vpisvane                                          AS reg_nom_zaqvl_vpisvane,-- 15 рег. номер първоначално вписване  ")           
				    .append(" v.date_zaqvl_vpis                                                 AS date_zaqvl_vpis --16 дата първоначално вписване    ")         

				.append(" FROM vpisvane                   v ")
				    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
					.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
					.append(" LEFT join event_deinost_jiv_predmet edjp  on edjp.event_deinost_jiv_id =edj.id  ")
					.append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
					.append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
				    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
				    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
				    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
				   	.append(" LEFT JOIN v_system_classif vsc3 ON ( ")
				    .append("         vsc3.code_classif=22 ")
				    .append("     AND vsc3.date_do IS NULL ")
				    .append("     AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
				.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
				.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
				.append(" LEFT join event_deinost_jiv_predmet edjp  on edjp.event_deinost_jiv_id =edj.id  ")
				.append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
				.append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			   	.append(" LEFT JOIN v_system_classif vsc3 ON ( ")
			    .append("         vsc3.code_classif=22 ")
			    .append("     AND vsc3.date_do IS NULL ")
			    .append("     AND vsc3.code=ara.addr_country) ")
		  .append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);


		 /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    /**
     * Регистър на издадените разрешения за износ на животни и зародишни продукти
     * @author yoni
     */
    private SelectMetadata registerSellAnimalsAbroad( ) {
        LOGGER.debug("registerSellAnimalsAbroad()");
        SelectMetadata smd=new SelectMetadata();
        int idRegister=20;
        int status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
			     .append(" min(v.id  )                                                              AS id_vpisvane,-- 00 id на вписване  ")
			     .append(" min(ara.ekatte   )                                                       AS addr_ekatte,-- 01 Населено място(лицензиант); ")
			     .append(" min(ea.oblast_ime   )                                                    AS oblast_text,-- 02 Област (лицензиант); ")
			     .append(" min(ea.obstina_ime   )                                                   AS obshtina_text,-- 03 Община (лицензиант);   ")     
			     .append(" min(ea.ime    )                                                          AS ekatte_text,-- 04 Населено място (лицензиант);     ")                      
			     .append(" min(ara.addr_text   )                                                    AS licenziant_address,-- 05 Улица/сграда (лицензиант); ")
			     .append(" min(ar.ref_type   )                                                      AS licenziant_type,-- 06 3 -firma,4-Lice ")
			     .append(" min(ar.ref_name   )                                                      AS licenziant_name, -- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице); ")
			     .append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')    )                         AS licenziant_egn,-- 08 ЕИК (за ЮЛ); ")
			     .append(" min(od.naimenovanie  )                                                   AS OEZ_name,  -- 09 Наименование на ОЕЗ; ")
			     .append(" min(od.reg_nom   )                                                       AS OEZ_rn, -- 10 Рег. номер на ОЕЗ; ")
			     .append(" min(edj.opisanie_cyr     )                                               AS dein_description, -- 11 Описание на дейността; ")
			    .append(" (select TT.countries from (select  max(edjd.event_deinost_jiv_id) as deinID, STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||22||':'||edjd.darj),',') as countries  ")
			.append(" from event_deinost_jiv_darj edjd  ")
			.append(" left JOIN v_system_classif     vsc0 ON ( ")
			 .append("                    vsc0.code_classif=22--classif 22 Държави ")
			 .append("                AND vsc0.date_do IS NULL ")
			 .append("                AND vsc0.code=edjd.darj ) ")
			.append(" where 1=1  group by edjd.event_deinost_jiv_id) TT where deinID= edj.id )    AS countries, -- 12 Страни, за които ще се изнася; ")
			 .append("     min(v.reg_nom_result )                                                 AS nomer_licenz, --13 Рег. номер на акта; ")
			  .append("    min(v.date_result )                                                    AS date_licenz,-- 14 Дата на регистрация. ")
			  .append("    min(v.registratura_id)                                                 AS registratura_id,-- 15 ОЕЗ регистририла лицензията    ")         
			   .append("   min(v.reg_nom_zaqvl_vpisvane)                                          AS reg_nom_zaqvl_vpisvane,-- 16 рег. номер първоначално вписване     ")        
			    .append("  min(v.date_zaqvl_vpis)                                                 AS date_zaqvl_vpis --17 дата първоначално вписване    ")         
			.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
			    .append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
			    .append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
			    .append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
			    .append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    .append(" LEFT JOIN v_system_classif vsc3 ON ( ")
			     .append("        vsc3.code_classif=22 ")
			     .append("    AND vsc3.date_do IS NULL ")
			     .append("    AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(edj.id) ")
				.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
			    .append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
			    .append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
			    .append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
			    .append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 --1-za  korespondenciq,2-postoqnen ")
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    .append(" LEFT JOIN v_system_classif vsc3 ON ( ")
			     .append("        vsc3.code_classif=22 ")
			     .append("    AND vsc3.date_do IS NULL ")
			     .append("    AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		
		 /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    /**
     * Регистър на вет. лекари и обектите, в които осъществяват ветеринарно медицинска практика
     * @author yoni
     */
    private SelectMetadata registerListVetsVLZ() {
        LOGGER.debug("registerListVetsVLZ()");
        SelectMetadata smd=new SelectMetadata();
        int idRegister=9;
        int status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
			   	 .append(" v.id                                                                AS id_vpisvane,-- 00 id на вписване  ")
			     .append(" ar.ref_type                                                         AS licenziant_type,-- 01 3 -firma,4-Lice ")
			     .append(" ar.ref_name                                                         AS licenziant_name, -- 02 Лицензиант(имена на физическо лице или наименование на юридическо лице); ")
			     .append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                             AS licenziant_egn,-- 03 ЕИК (за ЮЛ); ")
			     .append(" v.reg_nom_result                                                    AS nomer_licenz, --04 Рег. номер на сертификата; ")
			     .append(" v.date_result                                                       AS date_licenz,-- 05 Дата на регистрация; ")
			     .append(" COALESCE(vsc4.tekst,'Undefined code:'||505||':'||od.vid_vlz )       AS vlz_vid,-- 06 Вид на ВЛЗ; ")
			     .append(" od.naimenovanie                                                     AS vlz_name,--07    Наименование на ВЛЗ ")
			     .append(" od.zemliste                                                         AS vlz_mesto,-- 08 Местонахождение ; ")
			     .append(" od.reg_nom                                                          AS vlz_urn,-- 09 УРИ на ВЛЗ ; ")
			     .append(" v.registratura_id                                                   AS registratura_id,-- 10 ОЕЗ регистририла лицензията   ")          
			     .append(" v.reg_nom_zaqvl_vpisvane                                            AS reg_nom_zaqvl_vpisvane,-- 11 рег. номер първоначално вписване ")
			     .append(" v.date_zaqvl_vpis                                                   AS date_zaqvl_vpis --12 дата първоначално вписване     ")        
			.append(" FROM vpisvane                   v  ")
//			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
//				.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
//				.append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
//				.append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
				.append(" JOIN obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  and od.vid = 4 --calssif 519, значение VLZ")
				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" LEFT JOIN v_system_classif vsc4 ON ( ")
			    .append(" vsc4.code_classif=505 --classif 505 Вид на ВЛЗ ")
			        .append(" AND vsc4.date_do IS NULL ")
			        .append(" AND vsc4.code=od.vid_vlz) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
		.append(" FROM vpisvane                   v  ")
		.append(" JOIN obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  and od.vid = 4 --calssif 519, значение VLZ")
		.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
	    .append(" LEFT JOIN v_system_classif vsc4 ON ( ")
	    .append(" vsc4.code_classif=505 --classif 505 Вид на ВЛЗ ")
	    .append(" AND vsc4.date_do IS NULL ")
	    .append(" AND vsc4.code=od.vid_vlz) ")
	    .append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }


    
    /**
     * Регистър на транспортните средства, с които се превозват животни
     * @author yoni
     */
    private SelectMetadata registerMPSAnimals() {
        LOGGER.debug("registerMPSAnimals()");
        
        int idRegister=8;
        int status=BabhConstants.STATUS_VP_VPISAN;
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
		   	.append("  min( v.id  )                                                             AS id_vpisvane,-- 00 id на вписване  ")
		   	.append("  min(COALESCE(vsc1.tekst,'Undefined code:'||509||':'|| m.vid  ))          AS mps_vid,-- 01 Вид на ТС: ")
		   	.append("  min(COALESCE(vsc2.tekst,'Undefined code:'||583||':'|| m.marka ))         AS mps_marka,-- 02 Марка и модел; ")
		   	.append("  min(m.model  )                                                           AS mps_model,-- 03 Марка и модел; ")
		   	.append("  min(m.plosht  )                                                          AS mps_plosht,-- 04 Площ:   ")
		    .append("  min( m.nom_dat_sert   )                                                  AS mps_talon,-- 05 Рег. номер и дата на талона; ")
		   	.append("  min(v.registratura_id )                                                  AS registratura_id,-- 06 ОДБХ, издало лиценза;  ")
		    .append("  min(ar.ref_type )                                                        AS licenziant_type,-- 07 3 -firma,4-Lice ")
		    .append("  min(ar.ref_name  )                                                       AS licenziant_name, -- 08 Заявител(имена на физическо лице или наименование на юридическо лице); ")
		    .append("  min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') )                            AS licenziant_egn,-- 09 ЕИК (за ЮЛ); ")
		    .append("  min(v.reg_nom_result )                                                   AS nomer_licenz, --10 Рег. номер на сертификата; ")
		    .append("  min(v.date_result  )                                                     AS date_licenz,-- 11 Дата на регистрация; ")
		    .append("  min(v.reg_nom_zaqvl_vpisvane  )                                          AS reg_nom_zaqvl_vpisvane,-- 12 рег. номер първоначално вписване            , ")
			.append("  min(v.date_zaqvl_vpis   )                                                AS date_zaqvl_vpis, --13 дата първоначално вписване    ")         
			.append(" (select TT.kapacitet from (select  max(mkj.mps_id) as mpsID,  STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||581||':'||mkj.vid_jiv) ||':'|| mkj.broi_jiv,',') as kapacitet --14 Капацитет (много): ")
			.append(" from mps_kapacitet_jiv mkj  ")
			.append("  left JOIN v_system_classif     vsc0 ON ( ")
			.append("                     vsc0.code_classif=581--classif 581 Вид животно за транспорт ")
			.append("                 AND vsc0.date_do IS NULL ")
			.append("                 AND vsc0.code=mkj.vid_jiv ) ")
			.append(" where 1=1    group by mkj.mps_id) TT where mpsID= mkj.mps_id )    AS kapacitet ")
			.append(" FROM vpisvane                   v ")
			.append("     JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			.append("     JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
			.append(" 	join mps_deinost md    ON md.deinost_id=edj.id and tabl_event_deinost=98 and md.tip_vraz=3 ")
			.append(" 	join mps m           on m.id=md.mps_id   ")
			.append(" 	left join mps_kapacitet_jiv mkj on mkj.mps_id=m.id  ")
			.append(" 	LEFT JOIN v_system_classif vsc1 ON ( ")
			.append("             vsc1.code_classif=509 --classif 509 Вид на МПС ")
			.append("         AND vsc1.date_do IS NULL ")
			.append("         AND vsc1.code=m.vid ) ")
			.append("     LEFT JOIN v_system_classif vsc2 ON ( ")
			.append("             vsc2.code_classif=583 --classif 583 Марка на МПС ")
			.append("         AND vsc2.date_do IS NULL ")
			.append("         AND vsc2.code=m.marka )	 ")
			.append(" WHERE id_register= :idRegister AND v.status= :status group by mkj.mps_id");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(mkj.mps_id) ")
		.append(" FROM vpisvane                   v ")
		.append("     JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
		.append("     JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
		.append(" 	join mps_deinost md    ON md.deinost_id=edj.id and tabl_event_deinost=98 and md.tip_vraz=3 ")
		.append(" 	join mps m           on m.id=md.mps_id   ")
		.append(" 	left join mps_kapacitet_jiv mkj on mkj.mps_id=m.id  ")
		.append(" 	LEFT JOIN v_system_classif vsc1 ON ( ")
		.append("             vsc1.code_classif=509 --classif 509 Вид на МПС ")
		.append("         AND vsc1.date_do IS NULL ")
		.append("         AND vsc1.code=m.vid ) ")
		.append("     LEFT JOIN v_system_classif vsc2 ON ( ")
		.append("             vsc2.code_classif=583 --classif 583 Марка на МПС ")
		.append("         AND vsc2.date_do IS NULL ")
		.append("         AND vsc2.code=m.marka )	 ")
		.append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    /**
    * СК520=120
    * Регистър на издадените разрешения за производство или внос на ВЛП
    * Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
    * @param idRegister 
    * @return
    */
      private SelectMetadata  registriVLP(Integer idRegister) {

        LOGGER.debug("registerAnimalTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
        		+ "                    v.id             AS id_vpisvane,  "        		
        		+ "                    v.reg_nom_result as licenz, "        		
           		+ "                    v.date_result AS date_licenz , "        		
           		+ "                    v.licenziant as licenziant , "        		
//        		+ "                    od.reg_nom,"
//				+ "                    od.naimenovanie as naimObect,"
        		+ "                    array_to_string (ARRAY  "
        		+ "                   (   SELECT  DISTINCT "
        		+ "                            concat_ws(' ',case when od.naimenovanie =  '' then 'Обект без име' else  od.naimenovanie end  , ea.tvm , ea.ime ,'Обл.',ea.oblast_ime,'Общ.',ea.obstina_ime,od.address,  "
        		+ "                        array_to_string (ARRAY  "
        		+ "                           (   SELECT  DISTINCT "
        		+ "                              vsc.tekst "

        		+ "                            FROM  "
        		+ "                               obekt_deinost_prednaznachenie odp  "
        		+ "                            JOIN  v_system_classif vsc ON (  "
        		+ "                                    vsc.code_classif="+ BabhConstants.CODE_CLASSIF_PREDN_OBEKT+" "
        		+ "                                AND vsc.code=odp.prednaznachenie)  "
        		+ "                            WHERE  "
        		+ "                               odp.obekt_deinost_id = od.id),', ') "
        		+ "                              , 'От:', TO_CHAR(odd.date_beg, 'dd.mm.yyyy') ,TO_CHAR(odd.date_end, 'dd.mm.yyyy'),' ')  "        		
        		+ "                        FROM  "
        		+ "                            obekt_deinost od  "
        		+ "                            LEFT OUTER JOIN obekt_deinost_deinost odd ON (  "
        		+ "                                    odd.tabl_event_deinost="+ BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP +" "
        		+ "                                AND odd.deinost_id=edv.id )  "

        		+ "                            LEFT OUTER JOIN ekatte_att ea ON  ea.ekatte=od.nas_mesto "
        		+ "                        WHERE  "
        		+ "                             odd.obekt_deinost_id = od.id),'<br />') AS obect, "        		
        		+ "                      array_to_string (ARRAY  "
        		+ "                       (SELECT  concat_ws(' ','За ',case when od.naimenovanie =  '' then 'Обект без име' else  od.naimenovanie end ,': ', "
        		+ "                       array_to_string (ARRAY  "
        		+ "                       (   SELECT  "
        		+ "                            concat_ws(' ',ar.ref_name,vsc1.tekst,' ',TO_CHAR(odd.date_beg,'dd.mm.yyyy'),TO_CHAR(odd.date_end, 'dd.mm.yyyy'),' <br />')  "
//        		+ "                            concat_ws(' ',ar.ref_name,vsc1.tekst,'От: ',TO_CHAR(odd.date_beg,'dd.mm.yyyy'),TO_CHAR(odd.date_end, 'dd.mm.yyyy'))  "
        		+ "                        FROM  "
        		+ "                           obekt_deinost_lica odl  " 
        		+ "                           JOIN adm_referents ar ON ar.code=odl.code_ref  "
        		+ "                           JOIN v_system_classif vsc1 ON (  "
        		+ "                                    vsc1.code_classif= " + BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT +" "
        		+ "                                AND odl.code_ref=ar.code)  "
        		+ "                        WHERE  "
        		+ "                             vsc1.code=odl.role AND odl.obekt_deinost_id =odd.obekt_deinost_id ),'<br />')) "
        		+ "                        FROM  "
        		+ "                            obekt_deinost od  "
        		+ "                            LEFT OUTER JOIN obekt_deinost_deinost odd ON (  "
        		+ "                                    odd.tabl_event_deinost="+ BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP +" "
        		+ "                                AND odd.deinost_id=edv.id )  "

        		+ "                            LEFT OUTER JOIN ekatte_att ea ON  ea.ekatte=od.nas_mesto "
        		+ "                        WHERE  "
        		+ "                              odd.obekt_deinost_id = od.id     ),'<br />') AS liceobect, "        		

        		
        		
        		+ "                   d.doc_pod_vid,  "        		
        		+ "                   case "        		
        		+ "                   when d.doc_pod_vid=20 then 'Производство' "        		
        		+ "                   when d.doc_pod_vid=225 then 'Внос' "        		
        		+ "                   else ' Null '  "        		
        		+ "                    end as deinost, "        		
        		+ "                   ara.ekatte as ekatte, "        		
           		+ "                   ara.addr_text as addr_text,  "        		
           		+ "                   concat_ws(' ',att.tvm , att.ime) as grLizenziant,  "        		
           		+ "                   concat_ws(' ','Обл.',att.oblast_ime) as oblLizenziant,  "        		
           		+ "                    concat_ws(' ','Общ.',att.obstina_ime) as obstinaLizenziant,  "        		

        		+ "                   v.info as zabelejka, "        		
        		
        		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
           		+ "                   v.date_zaqvl_vpis AS date_zaqvl  "        		
     
        		+ "FROM  "
        		+ "                    vpisvane v "
        		+ "                    LEFT OUTER JOIN vlp vlp ON vlp.id_vpisvane=v.id  "
        		+ "                    LEFT OUTER JOIN event_deinost_vlp edv ON edv.id_vpisvane=v.id   "
//        		+ "                    LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein"
//        		+ "                    LEFT OUTER JOIN obekt_deinost         od  ON odd.obekt_deinost_id = od.id  "
        		+ "                    LEFT OUTER JOIN event_deinost_vlp_lice edvl  ON edv.id=edvl.event_deinost_vlp_id  "
        		+ "                    LEFT OUTER JOIN adm_referents ar  ON ar.code=edvl.code_ref  "
           		+ "                    LEFT OUTER JOIN doc d  ON d.doc_id=v.id_zaqavlenie  "
        		+ "                    LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = 2) "
        		+ "                    LEFT OUTER JOIN  ekatte_att att ON att.ekatte=ara.ekatte "

        		+ " WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);        
//        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    /**
     * СК520-29 
     * Списък на търговците с разрешение за продажба на ВЛП от разстояние *  
    	 * @param idRegister 
     * @return
     */
//    private SelectMetadata registerVlpTradersDistance(Integer idRegister) {
//
//            LOGGER.debug("registerVlpTradersDistance()");
//            SelectMetadata smd=new SelectMetadata();
//            //noinspection SpellCheckingInspection
//            String sql =  "SELECT " +
//                    "    v.id  AS id_vpisvane   , " +
//               		"    v.licenziant as licenziant, " +       		
//                    "    v.reg_nom_result   AS nomer_licenz , " +
//                    "    v.date_result      AS date_licenz , " +
//                    "    od.naimenovanie 	as naimObect, " +
//                    "    od.nas_mesto 		AS OEZ_NAS_MESTO , " +
//                    "    od.obsht           AS OEZ_OBSHT, " +
//                    "    od.obl             AS OEZ_OBL, " +
//                    "    od.address         AS OEZ_ADDRESS, " +
//                    "    edv.danni_kontragent as danni_kontragent, " +
//                    "    edv.nom_dat_lizenz as nom_dat_lizenz, "  //
//               		+ "                   d.doc_pod_vid,  "        		
//            		+ "                   case "        		
//            		+ "                   when d.doc_pod_vid=96 then 'Търговия от разстояние' "        		
//            		+ "                   when d.doc_pod_vid=235 then 'Паралелна търговия' "        		
//            		+ "                   else ' Null '  "        		
//            		+ "                    end as deinost, "+        		
// 
//                    "    edv.email          AS edvEmail, " +
//                    "    edv.site           AS edvSite, " +
//                    "    od.email           AS odMail, " +
//                    "    edv.adres          AS edvAdres, " +
//                    "    v.date_status      AS dateStatus, " + 
//                    "    v.reg_nom_zaqvl_vpisvane  AS nom_zaqvl, " +
//                    "    v.date_zaqvl_vpis         AS date_zaqvl " + 
//                    " FROM " +
//                    "    vpisvane                         v "+//-- основни данни за вписването " +
//                    "    JOIN  event_deinost_vlp edv    ON v.id=edv.id_vpisvane "+//-- licenziant " +
//                    "    JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " +   //100 " +
//                    "    LEFT OUTER JOIN  obekt_deinost od  ON odd.obekt_deinost_id = od.id " +
////                    "    LEFT OUTER JOIN  obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
//                    "WHERE " +
//                    "    v.id_register=:idRegister " +
//                    "AND v.status=2";//-- интересуват ни само тези със статус - 2 - вписан";
//
////            Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
////            query.setParameter("type", type);
////            return query.getResultList();
//            smd.setSql(sql);
//            smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
//            Map<String, Object> sqlParameters=new HashMap<>();
//            sqlParameters.put("idRegister", idRegister);
//            sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);
//            
//            smd.setSqlParameters(sqlParameters);
//
//            return smd;
//        }
 
    /**
     * СК520-29  i 122
     * Списък на търговците с разрешения за продажба от разстояние или паралелна търговия с ВЛП *  
     * @param idRegister 
     * @return
     */
    private SelectMetadata registerVlpTradersDistanceParalel(Integer idRegister) {

            LOGGER.debug("registerVlpTradersDistance()");
            SelectMetadata smd=new SelectMetadata();
            //noinspection SpellCheckingInspection
            String sql =  "SELECT " +
                    "    v.id  AS id_vpisvane   , " +
               		"    v.licenziant as licenziant, " +       		
                    "    v.reg_nom_result   AS nomer_licenz , " +
                    "    v.date_result      AS date_licenz , " +
                    "    od.naimenovanie 	as naimObect, " +
                    "    od.nas_mesto 		AS OEZ_NAS_MESTO , " +
                    "    od.obsht           AS OEZ_OBSHT, " +
                    "    od.obl             AS OEZ_OBL, " +
                    "    od.address         AS OEZ_ADDRESS, " +
                    "    edv.danni_kontragent as danni_kontragent, " +
                    "    edv.nom_dat_lizenz as nom_dat_lizenz, "  //
               		+ "                   d.doc_pod_vid,  "        		
            		+ "                   case "        		
            		+ "                   when d.doc_pod_vid=96 then 'Търговия от разстояние' "        		
            		+ "                   when d.doc_pod_vid=235 then 'Паралелна търговия' "        		
            		+ "                   else ' Null '  "        		
            		+ "                    end as deinost, "+        		

                    "    edv.email          AS edvEmail, " +
                    "    edv.site           AS edvSite, " +
                    "    od.email           AS odMail, " +
                    "    edv.adres          AS edvAdres, " +
            		"    v.info as zabelejka, " +       		
            		"    ara.ekatte as ekatte, "        		
               		+ "  ara.addr_text as addr_text,  "        		
               		+ "  concat_ws(' ',att.tvm , att.ime) as grLizenziant,  "        		
               		+ "  concat_ws(' ','Обл.',att.oblast_ime) as oblLizenziant,  "        		
               		+ "  concat_ws(' ','Общ.',att.obstina_ime) as obstinaLizenziant,  "   +     		

                    "    v.date_status      AS dateStatus, " + 
                    "    v.reg_nom_zaqvl_vpisvane  AS nom_zaqvl, " +
                    "    v.date_zaqvl_vpis         AS date_zaqvl " + 
                    " FROM " +
                    "    vpisvane                         v "+//-- основни данни за вписването " +
                    "    JOIN  event_deinost_vlp edv    ON v.id=edv.id_vpisvane "+//-- licenziant " +
                    "    JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " +   //100 " +
                    "    LEFT OUTER JOIN  obekt_deinost od  ON odd.obekt_deinost_id = od.id " +
             		"    LEFT OUTER JOIN doc d  ON d.doc_id=v.id_zaqavlenie  " +
            		"    LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = 2) "
            		+ "  LEFT OUTER JOIN  ekatte_att att ON att.ekatte=ara.ekatte " +
            	      
                    //                    "    LEFT OUTER JOIN  obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
                    " WHERE " +
                    "    v.id_register=:idRegister " +
                    "AND v.status=2";//-- интересуват ни само тези със статус - 2 - вписан";

//            Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//            query.setParameter("type", type);
//            return query.getResultList();
            smd.setSql(sql);
            smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
            Map<String, Object> sqlParameters=new HashMap<>();
            sqlParameters.put("idRegister", idRegister);
            sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);
            
            smd.setSqlParameters(sqlParameters);

            return smd;
        }

    
    /**
     * СК520-28 
     * Регистър на одобрените реклами  
    	 * @param idRegister 
     * @return
     */
	private SelectMetadata registerVlpPublicity(Integer idRegister) {
        LOGGER.debug("registerVlpPublicity()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT " +
                "    v.id AS id_vpisvane  , " +
                "    edv.vlp_reklama                                             AS vlp_reklama , " +
                "    edv.prednazn_reklama                                        AS prednazn_reklama_cod , " +
                "    v.date_result                                                AS date_licenz , " +
//                "    ar.ref_name || ' '|| COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') AS NAME, " +
                "    edv.nom_dat_lizenz                                          AS nom_dat_lizenz, " +
                "    v.licenziant_type                                           AS licenziant_type , " +
                "    v.licenziant                                                AS licenziant, " +
//                "    od.obl                                                       AS OEZ_OBL, " +
//                "    od.address                                                   AS OEZ_ADDRESS, " +
//                //"    odl.imena , " +
//                "    odl.code_ref imena , " + // TODO трябва да се разкодира през рефентите
                "    array_to_string (ARRAY " +
                "    (   SELECT " +
                "            vsc.tekst " +
                "        FROM " +
                "            event_deinost_vlp edv2 " +
                "            JOIN v_system_classif vsc  ON ( " +
                "                  v.id=edv2.id_vpisvane and  vsc.code_classif= 612 ) " +
//                "                     vsc.code_classif= :classiff ) " +
                "        WHERE " +
                "        vsc.code=edv2.prednazn_reklama),', ') AS prednazn_reklama_text, " 
				+ " v.info as zabelejka, "        		
	     		+ "                   ara.ekatte as ekatte, "        		        		+ ""
	 			+ "                   ara.addr_text as addr_text  "        		

				+ " FROM " +
                "    vpisvane                         v "+//-- основни данни за вписването " +
                "    LEFT OUTER JOIN event_deinost_vlp edv  ON v.id=edv.id_vpisvane "+//-- licenziant " +
//                "    LEFT OUTER JOIN obekt_deinost_deinost odd odd.deinost_id=edv.id " +
                "    LEFT OUTER JOIN obekt_deinost_deinost       odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " //100 " +
         		+ "  LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = 1) "

//                "    LEFT OUTER JOIN  obekt_deinost               od  ON odd.obekt_deinost_id = od.id " +
//                "    LEFT OUTER JOIN  obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
                + " WHERE " +
                "    v.id_register=:idRegister " +
                "AND v.status=2";//-- интересуват ни само тези със статус - 2 - вписан";

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        smd.setSql(sql);
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);
//        sqlParameters.put("classiff", BabhConstants.CODE_CLASSIF_PREDN_REKLAMA);

        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        
        smd.setSqlParameters(sqlParameters);

        return smd;
	}
  /**
 * СК520-30 
  * Списък на регистрираните производители, вносители и разпространители на активни вещества 
	 * @param idRegister 
  * @return
  */
 private SelectMetadata  registerVlpАctiveSubstances(Integer idRegister) {

     LOGGER.debug("registerVlpАctiveSubstances()");
     SelectMetadata smd=new SelectMetadata();
     //noinspection SpellCheckingInspection
     String sql =  "SELECT  "
     		+ "                    v.id AS id_vpisvane  ,  "        		
        	+ "                    v.licenziant as licenziant,"
     		+ "                    v.date_result AS date_licenz,"
//     		+ "                    edvp.naimenovanie as naimenovanie ,"
//     		+ "                    edvp.naimenovanie_lat as naimenovanie_lat,"
//     		+ "                    ar.ref_name as ref_name"
     		+ "                    array_to_string (ARRAY  "
     		+ "                   (   SELECT  "
     		+ "                            vsc.tekst "
     		+ "                        FROM  "
     		+ "                            event_deinost_vlp_vid      vid  "
     		+ "                            JOIN v_system_classif vsc ON (  "
     		+ "                                    vsc.code_classif="+ BabhConstants.CODE_CLASSIF_VID_DEINOST+" "
     		+ "                                AND vsc.code=vid.vid)  "
     		+ "                        WHERE  "
     		+ "                            vid.id_event_deinost_vlp =  edv.id),'<br />') AS vid_dein,"        		
     		+ "                     od.naimenovanie        AS obectDeinost  ,"
     		+ "                     od.nas_mesto           AS OEZ_NAS_MESTO ,"
     		+ "                     od.obsht               AS OEZ_OBSHT,"
     		+ "                     od.obl                 AS OEZ_OBL,"
     		+ "                     od.address             AS OEZ_ADDRESS, "
//     		+ "                     edvpr.predmet          AS codActVestestvo, "
//     		+ "                      edvpr.code_classif    AS code_classif, "
//     		+ "                      edvpr.dop_info        AS dop_info "
     		+ "                      array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                            sc.tekst ||', '||p.dop_info  "
     		+ "                        FROM  "
     		+ "                            event_deinost_vlp_predmet p  "  
     		+ "                            JOIN babhregs.v_system_classif sc ON (  "
     		+ "                                 sc.code_classif = p.code_classif " 
     		+ "                                AND sc.code=p.predmet and sc.lang=1)  "
     		+ "                        WHERE  "
     		+ "                              p.id_event_deinost_vlp=edv.id),'<br />') as predmet , "
    		+ "                   v.info as zabelejka, "        		
  		    + "                   v.reg_nom_zaqvl_vpisvane      AS nom_zaqvl,"
     		+ "                   v.date_zaqvl_vpis             AS date_zaqvl "
     		+ " FROM  "
     		+ "                    vpisvane v "
     		+ "                    JOIN event_deinost_vlp edv ON v.id=edv.id_vpisvane  "
     		+ "                    JOIN obekt_deinost_deinost odd  ON odd.deinost_id=edv.id AND odd.tabl_event_deinost= "+BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP
     		+ "                    JOIN obekt_deinost od    ON odd.obekt_deinost_id = od.id   "
//     		+ "                    JOIN adm_referents ar ON ar.code=edvl.code_ref  "
     		+ " WHERE  v.id_register= :idRegister AND v.status=2";
     System.out.println("sql:"+sql);
     smd.setSql(sql);
     System.out.println("sql:"+sql);
     smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
     Map<String, Object> sqlParameters=new HashMap<>();
     sqlParameters.put("idRegister", idRegister);        
//     sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

     smd.setSqlParameters(sqlParameters);
     return smd;
 }

 /**
 * СК520=27 
 * Регистър на инвитро диагностичните ВЛП   
	 * @param idRegister 
 * @return
 */
 private SelectMetadata  registerVlpInVitro(Integer idRegister) {

     LOGGER.debug("registerVlpАctiveSubstances()");
     SelectMetadata smd=new SelectMetadata();
     //noinspection SpellCheckingInspection
     String sql =  "SELECT  DISTINCT "
     		+ "         v.id 			AS id_vpisvane  ,  "        		
        	+ "         v.licenziant 	as licenziant,"
     		+ "         v.reg_nom_result AS licenz,"
     		+ "         v.date_result AS date_licenz ,"
     		+ "         edvp.naimenovanie as naim,"
     		+ "         edvp.naimenovanie_lat as naimlat,"
     		+ "         array_to_string (ARRAY  "
     		+ "           (   SELECT DISTINCT "
     		+ "                    vsc.tekst "
     		+ "               FROM  "
     		+ "                     event_deinost_vlp_bolesti edvb  "
     		+ "               JOIN v_system_classif vsc ON (  "
     		+ "                    vsc.code_classif="+ BabhConstants.CODE_CLASSIF_BOLESTI+" "
     		+ "                           AND edvb.event_dejnost_vlp_id=edv.id)  "
     		+ "               WHERE  "
     		+ "                   vsc.code=edvb.bolest),'<br />') AS bolesti,"        		
//     		+ "                     od.naimenovanie        AS obectDeinost  ,"
//     		+ "                     od.nas_mesto           AS OEZ_NAS_MESTO ,"
//     		+ "                     od.obsht               AS OEZ_OBSHT,"
//     		+ "                     od.obl                 AS OEZ_OBL,"
//     		+ "                     od.address             AS OEZ_ADDRESS, "
////     		+ "                     edvpr.predmet          AS codActVestestvo, "
////     		+ "                      edvpr.code_classif    AS code_classif, "
////     		+ "                      edvpr.dop_info        AS dop_info "
     		+ "                      array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                             concat_ws(' ',ar.ref_name,vsc.tekst,', ',ara.addr_text,', ',ea.tvm,' ',ea.ime,', ',ea.obstina_ime,', ',ea.oblast_ime) "
     		+ "                        FROM  "
     		+ "                            event_deinost_vlp_lice edvl2  " // 
     		+ "                        JOIN adm_referents ar ON (  "
     		+ "                                ar.code=edvl2.code_ref)  "

     		+ "                        JOIN v_system_classif vsc ON (  "
     		+ "                                    vsc.code_classif ="+ BabhConstants.CODE_CLASSIF_VRAZ_SYBITIE_OBEKT+" " // TODO BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ
     		+ "                                AND edvl2.code_ref=ar.code)  "
     		
 
     		+ "                        JOIN adm_ref_addrs ara ON (  "
     		+ "                                    ara.code_ref=v.code_ref_vpisvane "
     		+ "                                AND addr_type = 1)  "

     		
     		+ "                        JOIN ekatte_att ea ON (  "
     		+ "                                ea.ekatte=ara.ekatte )  "
     		
     		
     		
     		+ "                        WHERE  "
     		+ "                               vsc.code=edvl2.tip_vraz and edvl2.event_deinost_vlp_id =edv.id),'<br />') AS vrazka, "
     		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
        	+ "                   v.date_zaqvl_vpis AS date_zaqvl,  "        		
        	+ "                   ara.ekatte        as ekatte,  "        		
        	+ "                   ara.addr_text    as addr_text,  "        		
    		+ "                   v.info as zabelejka "        		

     		+ " FROM  "
     		+ "          vpisvane v "
     		+ "          JOIN event_deinost_vlp edv            ON v.id=edv.id_vpisvane  "
     		+ "          JOIN event_deinost_vlp_predmet edvp   ON edv.id=edvp.id_event_deinost_vlp "
     		+ "          JOIN adm_referents ar     ON ar.code=v.id_licenziant "
     		+ "          JOIN adm_ref_addrs ara    ON ara.code_ref=v.id_licenziant AND ara.addr_type=1  "
     		+ " WHERE  v.id_register= :idRegister AND v.status=2";
     System.out.println("sql:"+sql);
     smd.setSql(sql);
     smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
     Map<String, Object> sqlParameters=new HashMap<>();
     sqlParameters.put("idRegister", idRegister);        
//     sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

     smd.setSqlParameters(sqlParameters);
     return smd;
 }
	
 /**
 * СК520=25 
 * Регистър на издадените разрешения за търговия с ВЛП   
	 * @param idRegister 
 * @return
 */
 private SelectMetadata  registerVlpTraders(Integer idRegister) {

     LOGGER.debug("registerVlpTraders()");
     SelectMetadata smd=new SelectMetadata();
     //noinspection SpellCheckingInspection
     String sql =  "SELECT  "
     		+ "                    v.id             AS id_vpisvane,  "        		
     		+ "                    v.reg_nom_result as licenz, "        		
        	+ "                    v.date_result AS date_licenz , "        		
        	+ "                    v.licenziant as licenziant , "        		
//     		+ "                    od.reg_nom,"
//			+ "                    od.naimenovanie as naimObect,"
     		+ "                    array_to_string (ARRAY  "
     		+ "                   (   SELECT  "
     		+ "                             vsc.tekst ||', '||sub.name  ||', '||quantity ||', '||measurement||', '||standart  "
     		+ "                        FROM  "
     		+ "                            vlp_veshtva vlpv  "
     		+ "                            JOIN v_system_classif vsc  ON (  "
     		+ "                                    vsc.code_classif="+ BabhConstants.CODE_CLASSIF_VID_VESHT_ZA_VLP+" "
     		+ "                                AND vsc.code=vlpv.type)  "
     		+ "                            JOIN substances sub  ON (sub.identifier=vlpv.vid_identifier)  "

     		
     		+ "                        WHERE  "
     		+ "                            vlpv.vlp_id = vlp.id),'\r\n') AS vestestva,"        		
//     		+ "                     od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
//     		+ "                     od.obsht                                                     AS OEZ_OBSHT,"
//     		+ "                     od.obl                                                       AS OEZ_OBL,"
//     		+ "                     od.address                                                   AS OEZ_ADDRESS,"
     		+ "                      array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                            vsc1.tekst  "
     		+ "                        FROM  "
     		+ "                           vlp_vid_jiv vlpj  " 
     		+ "                          JOIN v_system_classif vsc1 ON (  "
     		+ "                                    vsc1.code_classif= " + BabhConstants.CODE_CLASSIF_VID_JIV_ES +" "
     		+ "                                                         )  "
     		+ "                        WHERE  "
     		+ "                             vsc1.code=vlpj.vid_jiv  AND vlpj.vlp_id =vlp.id ),'<br />') AS jivotni, "
     		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
        	+ "                   v.date_zaqvl_vpis AS date_zaqvl,  "        		
     		+ "                   vlp.naimenovanie_cyr     AS naim, "        		
        	+ "                   vlp.naimenovanie_lat AS naimlat,  "        		

        	+ "                      array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                            vsc3.tekst  "
     		+ "                        FROM  "
     		+ "                           vlp_opakovka vlpo  " 
     		+ "                          JOIN v_system_classif vsc3 ON (  "
     		+ "                                    vsc3.code_classif= " + BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA +" "
     		+ "                                                         )  "
     		+ "                        WHERE  "
     		+ "                              vsc3.code=vlpo.opakovka  AND vlpo.vlp_id =vlp.id ),'<br />') AS opakovka, "

     		+ "                      array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                             vsc2.tekst  "
     		+ "                        FROM  "
     		+ "                            vlp_farm_form vff  " 
     		+ "                          JOIN v_system_classif vsc2 ON (  "
     		+ "                                    vsc2.code_classif= " + BabhConstants.CODE_CLASSIF_PHARM_FORMI +" "
     		+ "                                                         )  "
     		+ "                        WHERE  "
     		+ "                              vsc2.code=vff.farm_form  AND vff.vlp_id =vlp.id ),'<br />') AS farmform, "
        	+ "                   vlp.rejim_otpuskane AS rejim_otpuskane , "        		
        	+ "                   vlp.karenten_srok AS karenten_srok , "        		

     		+ "                    array_to_string (ARRAY  "
     		+ "                   (   SELECT  "
     		+ "                             vsc4.tekst ||', '||ar.ref_name||', '||to_char(vlpl.date_beg,'dd.mm.yyyy' )  "
     		+ "                        FROM  "
     		+ "                            vlp_lice vlpl  "
     		+ "                            JOIN  v_system_classif vsc4  ON (  "
     		+ "                                    vsc4.code_classif="+ BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT+" "
     		+ "                                AND vsc4.code=vlpl.tip)  "
     		+ "                            JOIN adm_referents ar  ON (ar.code=vlpl.code_ref)  "

     		
     		+ "                        WHERE  "
     		+ "                            vlpl.vlp_id =vlp.id),'<br />') AS kvlica,"        		
    		+ "                   v.info as zabelejka "        		

        	
     		
     		+ " FROM  "
     		+ "                    vpisvane v "
//     		+ "                    LEFT OUTER JOIN event_deinost_vlp edv ON v.id=edv.id_vpisvane  "
//     		+ "                    LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein"
//     		+ "                    LEFT OUTER JOIN obekt_deinost         od  ON odd.obekt_deinost_id = od.id  "
//     		+ "                    LEFT OUTER JOIN event_deinost_vlp_lice edvl  ON edv.id=edvl.event_deinost_vlp_id  "
     		+ "                    LEFT OUTER JOIN vlp vlp   ON vlp.id_vpisvane=v.id  "
     		+ "WHERE  v.id_register= :idRegister AND v.status=2";
     
     smd.setSql(sql);
     smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
     Map<String, Object> sqlParameters=new HashMap<>();
     sqlParameters.put("idRegister", idRegister);        
//     sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

     smd.setSqlParameters(sqlParameters);
     return smd;
 }

 /**
  * СК520=24  Регистър на издадените разрешения за търговия на дребно с ВЛП 
  * СК520=23 Регистър на издадените разрешения за търговия на едро с ВЛП с ВЛП
  * Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
  * @param idRegister 
  * @return
  */
 private SelectMetadata  registriVLP24(Integer idRegister) {

     LOGGER.debug("registerAnimalTraders()");
     SelectMetadata smd=new SelectMetadata();
     //noinspection SpellCheckingInspection
     String sql =  "SELECT  "
     		+ "                    v.id             AS id_vpisvane,  "        		
     		+ "                    v.reg_nom_result as licenz, "        		
        		+ "                    v.date_result AS date_licenz , "        		
        		+ "                    v.licenziant as licenziant , "        		
				+ "                    od.naimenovanie as naimObect,"
     		+ "                    array_to_string (ARRAY  "
     		+ "                   (   SELECT  "
     		+ "                            vsc.tekst  "
     		+ "                        FROM  "
     		+ "                            obekt_deinost_prednaznachenie odp  "
     		+ "                            JOIN v_system_classif vsc ON (  "
     		+ "                                    vsc.code_classif="+ BabhConstants.CODE_CLASSIF_PREDN_OBEKT+" "
     		+ "                                AND vsc.code=odp.prednaznachenie)  "
     		+ "                        WHERE  "
     		+ "                            odp.obekt_deinost_id = od.id),'<br />') AS prednaznachenie,"        		
     		+ "                     od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
     		+ "                     od.obsht                                                     AS OEZ_OBSHT,"
     		+ "                     od.obl                                                       AS OEZ_OBL,"
     		+ "                     od.address                                                   AS OEZ_ADDRESS,"
     		+ "                     array_to_string (ARRAY  "
     		+ "                       (   SELECT  "
     		+ "                            ar.ref_name ||', '||vsc1.tekst  "
     		+ "                        FROM  "
     		+ "                           obekt_deinost_lica odl  " 

     		+ "                            JOIN adm_referents ar ON (  "
     		+ "                                     ar.code=odl.code_ref ) "

     		+ "                            JOIN v_system_classif vsc1 ON (  "
     		+ "                                    vsc1.code_classif= " + BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT +" ) "
     		+ "                        WHERE  "
     		+ "                             vsc1.code=odl.role  AND odl.obekt_deinost_id =od.id ),'<br />') AS liceObekt, "
     		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
        		+ "                   v.date_zaqvl_vpis AS date_zaqvl,  "        		

     		+ "                     array_to_string (ARRAY  "
     		+ "                       (   SELECT DISTINCT "
     		+ "                            vsc.tekst  "
     		+ "                        FROM  "
     		+ "                           event_deinost_vlp_predmet edvp  " 
     		+ "                        JOIN  v_system_classif vsc ON (vsc.code=edvp.predmet "
     		+ "                                AND    vsc.code_classif= " + BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP +" ) "
     		+ "                        WHERE  "
     		+ "                             edvp.id_event_deinost_vlp=edv.id ),'<br />') AS predmet, "
     		+ "                   ara.ekatte as ekatte, "        		        		+ ""
 			+ "                   ara.addr_text as addr_text,  "        		
       		+ "                   v.info as zabelejka,  "        		
       		+ "                   edv.dop_info  "        		

     		+ " FROM  "
     		+ "                    vpisvane v "
     		+ "                    LEFT OUTER JOIN event_deinost_vlp edv ON v.id=edv.id_vpisvane  "
     		+ "                    LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = "+BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP
     		+ "                    LEFT OUTER JOIN obekt_deinost         od  ON odd.obekt_deinost_id = od.id  "
     		+ "                    LEFT OUTER JOIN event_deinost_vlp_lice edvl  ON edv.id=edvl.event_deinost_vlp_id  "
     		+ "                    LEFT OUTER JOIN adm_referents ar  ON ar.code=edvl.code_ref  "
     		+ "                    LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = 1) "
     		+ "WHERE  v.id_register= :idRegister AND v.status=2";
     
     smd.setSql(sql);
     smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
     Map<String, Object> sqlParameters=new HashMap<>();
     sqlParameters.put("idRegister", idRegister);        
//     sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

     smd.setSqlParameters(sqlParameters);
     return smd;
 }

    
}
