package com.ib.babhregs.db.dao;


import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.SelectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Основно дао, в което да се блъскат всички методи за генериране на публичните регистри
 */
public class PublicRegisterDAO {
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
		{"56","32","33",null,null}, //куче е 33, а не 32
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
        System.out.println("genereate register for:{}"+idRegister);
        SelectMetadata result = new SelectMetadata();

        switch (idRegister) {
            case 5: //Регистър на търговците на животних
                result = registerAnimalTraders(1);
                break;
                
            case 7: //Регистър на издадените разрешителни за превоз на животни 
                result = registerApprovedMPStransport();
                break;
            case -8: //Регистър на транспортните средства, с които се превозват животни
                result = registerMPSAnimals();
                break;
                
            case 9: //Регистър на вет. лекари и обектите, в които осъществяват ветеринарно медицинска практика
                result = registerListVetsVLZ();
                break;
                
            case 11: //Регистър на издадените разрешителни за провеждане на опити с животни
                result = registerAnimalTrials();
                break;
                
            case 13: //Регистър на кипите за добив и съхранение на ембриони и яйцеклетки
                result = registerEmbrionTeams();
                break;
                
            case 15: //Регистър на мероприятия с животни
                result = registerAnimalEvents(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV);
                break;
                //16. Регистър на фирмите и центровете, предоставящи обучение в сферата на животновъдството
            case 132:
                result = registerOrganizationsCources(1);
                break;
            case 133:
                result = registerOrganizationsCources(2);
                break;
                
            case 18: //Списък на издадените удостоверения за правоспособност на водачи на транспортни средства, в които се превозват животни, и на придружители на животни при транспортиране 
                result = registerTransportSvidetelstvo();
                break;
                
            case 19: //Регистър на заявленията за изплащане на обезщетения на собствениците на убити или унищожени животни
                result = registerPayDeadAnimals();
                break;
                
            case 20: //Регистър на издадените разрешения за износ на животни и зародишни продукти
                result = registerSellAnimalsAbroad();
                break;
                
            case 33: //Регистър на операторите, транспортиращи фуражи съгласно чл. чл. 17е, ал. 2 от Закона за фуражите 
                result = registerFurajiOperatorTransportChlen_17();
                break;
                
            case 43: //Регистър на транспортните средства за превоз на странични животински продукти
                result = registerListMpsSJP();
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
//                result = registerVlpTraders();
//                break;
            case 31:// Регистър на одобрените обекти за търговия на фуражни добавки
                result = registerFurajniDobavki1();
                break;
 
            case 32:// Регистър на регистрираните обекти в сектор Фуражи съгласно чл. 17, ал. 3 от ЗФ
                //result = registerRegObecti17(idRegister);
                result = registerRegObecti17_3();
                break;
                
            case 35://  Регистър на оператори, внасящи фуражи от трети държави по чл. 9 от Закона за фуражите (type=39)
                result = registerImportThirdCountries(39);
                break;
                
                case 36://  Регистър на оператори, внасящи фуражи от трети държави по чл. 10 от Закона за фуражите (type=81)
                result = registerImportThirdCountries(81);
                break;

                  case 37://  Списък на предприятия, одобрени за износ на царевица за Китай
                  result = registerThirdCountries(63,169);
                  break;
                  
                  case 38://   Списък на предприятия, одобрени за износ на люцерна за Китай
                  result = registerThirdCountries(63,170);
                  break;
                  
                  case 39://   Списък на предприятия, одобрени за износ на комбинирани фуражи за Китай
                  result = registerThirdCountries(63,73);
                  break;
                  
                  case 40:// Списък на обектите за производства на фуражни добавки, одобрени за износ за Китай
                  result = registerThirdCountries(63,34);
                  break;
                  case 41:// Списък на предприятия за преработка, одобрени за износ на слънчогледов шрот за Китай
                  result = registerThirdCountries(63,171);
                  break;
                  case 42:// Списък на предприятия за преработка, одобрени за износ на български сух зърнен спиртоварен остатък с извлеци за Китай
                  result = registerThirdCountries(63,172);
                  break;
//                  case 42:// Списък на предприятия за преработка, одобрени за износ на фуражи с растителен произход за Китай
//                  result = registerThirdCountries(63,66);
//                  break;
                  case 126://  Регистър на операторите с издаден ветеринарен сертификат за износ на фуражи за трети държави по чл. 53з, ал. 1 от ЗФ
                  result = registerThirdCountries(102,null);
                  break;
              
                
            case 44:// Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
//                result = registerRegOOP();
//                break;
                
                result = registerObjectsSJP();
                break;
                
            case 24://Регистър на издадените разрешения за търговия на дребно с ВЛП 
            case 23://Регистър на издадените разрешения за търговия на едро с ВЛП с ВЛП
                result = registriVLP24(idRegister);
                break;

//            case 121:// Регистър на издадените разрешения за внос на ВЛП    
//            case 120://Регистър на издадените разрешения за производство на ВЛП 
//                result = registriVLP(idRegister);
//                break;
            case 29://Списък на търговците с разрешение за продажба на ВЛП от разстояние 
            case 122://Списък на търговците с разрешение за паралелна търговия с ВЛП
                result = registerVlpTradersDistance(idRegister);
                break;
                
            case 127:// Регистър на издадените становища за прилагане на подмярка 4.1 от ПРСР
                result = registerProizvoditeliMiarka(1);
                break;
            case 128:// Регистър на издадените становища за прилагане на подмярка 4.2 от ПРСР
                result = registerProizvoditeliMiarka(2);
                break;
            case 129:// Регистър на издадените становища за прилагане на подмярка 5.1 от ПРСР
                result = registerProizvoditeliMiarka(5);
                break;
            case 130:// Регистър на издадените становища за прилагане на подмярка 6.1 от ПРСР
                result = registerProizvoditeliMiarka(3);
                break;
            case 131:// Регистър на издадените становища за прилагане на подмярка 6.3 от ПРСР
                result = registerProizvoditeliMiarka(4);
                break;
                
              case 34:// Регистър на одобрените обекти  медикаментозни фуражи 
              result = registerMedFuraji();
              break;
                
//            case 79:// Регистър на одобрените обекти за производство с медикаментозни фуражи 
//                result = registerMedFuraji(79);
//                break;
//                
//            case 80:// Регистър на одобрените обекти за търговия с медикаментозни фуражи
//                result = registerMedFuraji(80);
//                break;
                
            case 124:// Регистър на издадените сертификати за добра практика
                result = registerGoodPractices();
                break;

            case 28://Регистър на одобрените реклами 
                result = registerVlpPublicity(idRegister);
                break;

        	}
        return result;
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
                "       identInfo, info, licenziant_type from v_register_12" +
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
     * Регистър на издадените лицензи за търговия на едро с ВЛП
     * @return
     */
    private SelectMetadata registerVlpTraders() {

        LOGGER.debug("registerVlpTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT " +
                "    v.id, " +
                "    v.reg_nom_result                                             AS nomer_licenz , " +
                "    v.date_result                                                AS date_licenz , " +
                "    ar.ref_name || ' '|| COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') AS NAME, " +
                "    od.naimenovanie                                              AS obekt_naim, " +
                "    od.nas_mesto                                                 AS OEZ_NAS_MESTO , " +
                "    od.obsht                                                     AS OEZ_OBSHT, " +
                "    od.obl                                                       AS OEZ_OBL, " +
                "    od.address                                                   AS OEZ_ADDRESS, " +
                //"    odl.imena , " +
                "    odl.code_ref imena , " + // TODO трябва да се разкодира през рефентите
                "    array_to_string (ARRAY " +
                "    (   SELECT " +
                "            vsc.tekst " +
                "        FROM " +
                "            event_deinost_vlp_predmet      predmet " +
                "            JOIN v_system_classif vsc ON ( " +
                "                    vsc.code_classif=predmet.code_classif " +
                "                AND vsc.code=predmet.predmet) " +
                "        WHERE " +
                "            predmet.id_event_deinost_vlp=edv.id),', ') AS predmet " +
                "FROM " +
                "    vpisvane                         v "+//-- основни данни за вписването " +
                "    JOIN adm_referents      ar  ON ar.code=v.id_licenziant "+//-- licenziant " +
                "    JOIN event_deinost_vlp           edv ON v.id=edv.id_vpisvane " +
                "    JOIN obekt_deinost_deinost       odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " +   //100 " +
                "    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id " +
                "    JOIN obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
                "WHERE " +
                "    v.id_register=:idRegister " +
                "AND v.status=2";//-- интересуват ни само тези със статус - 2 - вписан";

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", 23);
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);
        
        smd.setSqlParameters(sqlParameters);

        return smd;
    }

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
     * Регистър на одобрените обекти, съгласно чл.20, ал. 3 от Закона за фуражите
     * @return
     */
    private SelectMetadata registerFurajniDobavki() {

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
        		+ "                            vidf.event_deinost_furaji_id =  edf.id),'<br />') AS vid_f, "
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
        sqlParameters.put("idRegister", 31);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    
    
    private SelectMetadata registerFurajniDobavki1() {
        LOGGER.debug("registerFurajniDobavki1");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        Integer idRegister=31;
        Integer status= BabhConstants.STATUS_VP_VPISAN;
        StringBuilder sql = new StringBuilder("SELECT ")
        		.append("    min(v.id   )                                                       AS id_vpisvane , ")//--00
        		.append("    min(od.reg_nom)                                                           as vet_num, ")//--01 Ветеринарен рег. номер на ОЕЗ;
        		.append("    min(od.naimenovanie     )                                                 AS OEZ_name, ")//--02 име на ОЕЗ
        		.append("    min(v.reg_nom_result  )                                                   AS nomer_licenz , ")//--03 Рег. номер на удостоверение за регистрация; 
        		.append("    min(v.date_result     )                                                   AS date_licenz, ")//-- 04 Дата на регистрация;
        		.append("    min(v.registratura_id    )                                                as registratura_id, ")//--05 ОЕЗ регистририла лицензията
        		.append("    min(ar.ref_type    )                                                      AS licenziant_type, ")//--06 3 -firma,4-Lice 
        		.append("    min(ar.ref_name      )                                                    AS licenziant_name, ")//--07 оператор
        		.append("    min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
        		.append("    min(ea.oblast_ime   )                                                     AS oblast_text, ")//--09 Област (ОЕЗ);
        		.append("    min(ea.obstina_ime   )                                                    AS obshtina_text, ")//       --10 Община (ОЕЗ);
        		.append("    min(ea.ime    )                                                           AS ekatte_text, ")//      --11 Населено място (ОЕЗ);
        		.append("    min(od.address    )                                                       as ulica, ")//--12 Улица/сграда (ОЕЗ);                     
        		.append("    min(ara.ekatte  )                                                         AS ref_addr_ekatte, ")//--13 ekkate лицензиант;
        		.append("    min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")//--14 Област (лицензиант);
        		.append("    min(ea1.obstina_ime  )                                                    AS ref_obshtina_text, ")//--15 Община (лицензиант);       
        		.append("    min(ea1.ime   )                                                           AS ref_ekatte_text, ")//-- 16 Населено място (лицензиант);                          
        		.append("    min(ara.addr_text )                                                       AS ref_ulica, ")// --17 Улица/сграда (лицензиант);
        		.append("    min(edf.broi_tehn_linii )                                                 as br_linia, ")// --18 Брой технологични линии;
        		.append("    min(v.info)                                                               as info, ")// --19 забележка
        		.append("    (select TT.deinInfo from ( select max(edfv.event_deinost_furaji_id)as ttid , ") 
        		.append("    STRING_AGG(COALESCE(vsc0.tekst,'Undefined code:'||506||':'||edfv.vid),';') as deinInfo ")
        		.append("    from event_deinost_furaji_vid      edfv    ")
        		.append("    left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=506 AND vsc0.date_do IS NULL AND vsc0.code=edfv.vid  ) ")
        		.append("    where  edfv.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT ) ")//  -- 20 видове дейност
        		.append("    from vpisvane v ")
        		.append("    JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
        		.append("    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
        		.append("    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein ")
        		.append("    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id ")
        		.append("    LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
        		//.append("    LEFT JOIN v_system_classif vsc4 ON (  vsc4.code_classif=22 AND vsc4.date_do IS NULL and vsc4.lang=1 AND vsc4.code=37 )  ")
        		.append("    left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1 ")// --1-za  korespondenciq,2-postoqnen
        		.append("    LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
        		//.append("    LEFT JOIN v_system_classif vsc5 ON (  vsc5.code_classif=22  AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=37 )  ")
        		.append("    WHERE  v.id_register= :idRegister AND v.status= :status group by edf.id ");//-- интересуват ни само тези със статус - 2 - вписан";

        String sqlString= sql.toString();
        smd.setSql(sqlString);
        
        /* Формиране на заявката за броя резултати */
        StringBuilder sqlCountSB= new StringBuilder("select count(DISTINCT v.id) ")
        		.append("    from vpisvane v ")
        		.append("    JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
        		.append("    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
        		.append("    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein ")
        		.append("    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id ")
		.append(" WHERE    v.id_register= :idRegister AND v.status= :status ");//-- интересуват ни само тези със статус - 2 - вписан";
        String sqlCountString= sqlCountSB.toString();
        smd.setSqlCount(sqlCountString);
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status",status );
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);
 
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerFurajniDobavki1: "+smd.toString());
        return smd;
    }
    
    
    private SelectMetadata registerRegObecti17_3() {
        LOGGER.debug("registerRegObecti17_3");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        Integer idRegister=32;
        Integer status= BabhConstants.STATUS_VP_VPISAN;
        StringBuilder sql = new StringBuilder("SELECT ")
        		.append("    min(v.id   )                                                       AS id_vpisvane , ")//--00
        		.append("    min(od.reg_nom)                                                           as vet_num, ")//--01 Ветеринарен рег. номер на ОЕЗ;
        		.append("    min(od.naimenovanie     )                                                 AS OEZ_name, ")//--02 име на ОЕЗ
        		.append("    min(v.reg_nom_result  )                                                   AS nomer_licenz , ")//--03 Рег. номер на удостоверение за регистрация; 
        		.append("    min(v.date_result     )                                                   AS date_licenz, ")//-- 04 Дата на регистрация;
        		.append("    min(v.registratura_id    )                                                as registratura_id, ")//--05 ОЕЗ регистририла лицензията
        		.append("    min(ar.ref_type    )                                                      AS licenziant_type, ")//--06 3 -firma,4-Lice 
        		.append("    min(ar.ref_name      )                                                    AS licenziant_name, ")//--07 оператор
        		.append("    min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
        		.append("    min(ea.oblast_ime   )                                                     AS oblast_text, ")//--09 Област (ОЕЗ);
        		.append("    min(ea.obstina_ime   )                                                    AS obshtina_text, ")//       --10 Община (ОЕЗ);
        		.append("    min(ea.ime    )                                                           AS ekatte_text, ")//      --11 Населено място (ОЕЗ);
        		.append("    min(od.address    )                                                       as ulica, ")//--12 Улица/сграда (ОЕЗ);                     
        		.append("    min(ara.ekatte  )                                                         AS ref_addr_ekatte, ")//--13 ekkate лицензиант;
        		.append("    min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")//--14 Област (лицензиант);
        		.append("    min(ea1.obstina_ime  )                                                    AS ref_obshtina_text, ")//--15 Община (лицензиант);       
        		.append("    min(ea1.ime   )                                                           AS ref_ekatte_text, ")//-- 16 Населено място (лицензиант);                          
        		.append("    min(ara.addr_text )                                                       AS ref_ulica, ")// --17 Улица/сграда (лицензиант);
        		.append("    min(edf.broi_tehn_linii )                                                 as br_linia, ")// --18 Брой технологични линии;
        		.append("    min(v.info)                                                               as info, ")// --19 забележка
        		.append("    (select TT.deinInfo from ( select max(edfp.event_deinost_furaji_id)as ttid , ") 
        		.append("    STRING_AGG(COALESCE(vsc0.tekst,'Undefined code:'||531||':'||edfp.vid)||'-'||COALESCE(vsc1.tekst,'Undefined code:'||552||':'||edfp.prednaznachenie)  ,';') as deinInfo ")
        		.append("    from event_deinost_furaji_predmet edfp     ")
        		.append("    left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=531 AND vsc0.date_do IS NULL AND vsc0.code=edfp.vid  ) ")
        		.append("    left JOIN v_system_classif     vsc1 ON (vsc1.code_classif=552 AND vsc1.date_do IS NULL AND vsc1.code=edfp.prednaznachenie  ) ")
        		.append("    where  edfp.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT ) ")//  -- 20 видове дейност
        		.append("    from vpisvane v ")
        		.append("    JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
        		.append("    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
        		.append("    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein ")
        		.append("    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id ")
        		.append("    LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
        		//.append("    LEFT JOIN v_system_classif vsc4 ON (  vsc4.code_classif=22 AND vsc4.date_do IS NULL and vsc4.lang=1 AND vsc4.code=37 )  ")
        		.append("    left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1 ")// --1-za  korespondenciq,2-postoqnen
        		.append("    LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
        		//.append("    LEFT JOIN v_system_classif vsc5 ON (  vsc5.code_classif=22  AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=37 )  ")
        		.append("    WHERE  v.id_register= :idRegister AND v.status= :status group by edf.id ");//-- интересуват ни само тези със статус - 2 - вписан";

        String sqlString= sql.toString();
        smd.setSql(sqlString);
        
        /* Формиране на заявката за броя резултати */
        StringBuilder sqlCountSB= new StringBuilder("select count(DISTINCT v.id) ")
        		.append("    from vpisvane v ")
        		.append("    JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
        		.append("    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
        		.append("    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein ")
        		.append("    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id ")
		.append(" WHERE    v.id_register= :idRegister AND v.status= :status ");//-- интересуват ни само тези със статус - 2 - вписан";
        String sqlCountString= sqlCountSB.toString();
        smd.setSqlCount(sqlCountString);
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status",status );
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);
 
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerFurajniDobavki1: "+smd.toString());
        return smd;
    }
    
    
    
    /**
     * @param type
     * @return
     * yoni
     */
    private SelectMetadata registerAnimalEvents(Integer type) {
        LOGGER.debug("registerAnimalEvents(type={})",type);
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        Integer idRegister=15;
        Integer status= BabhConstants.STATUS_VP_VPISAN;
        StringBuilder sql = new StringBuilder("SELECT ")
        		.append("    min(v.id  )                                        						AS id_vpisvane , ")//--00 Номер по ред
        		.append("  	 min(COALESCE(vsc2.tekst,'Undefined code:'||541||':'||edj.meroptiatie) )	AS   vid_meropr, ")//--01 Вид на мероприятието
        		.append("  	 min(edj.date_beg_meropriatie )												AS   date_beg_meropr, ")//--02 Начална дата
        		.append("  	 min(edj.date_end_meropriatie  )										    AS   date_end_meropr, ")//--03 Крайна дата
        		.append("  	 min(edj.adres 	 )															AS   mesto_meropr, ")//--04 Място на мероприятието
        		.append("    min(v.reg_nom_result  )                                       				AS nomer_licenz , ")//--05 Рег. номер на заповедта
        		.append("    min(v.date_result  )                                            			AS date_licenz, ")//--06 Дата на заповедта
        		.append("    min(v.registratura_id  )                                                   AS registratura_id , ")//--07
				.append("    min(v.reg_nom_zaqvl_vpisvane)                                              AS reg_nom_zaqvl_vpisvane, ")// --08
				.append("    min(v.date_zaqvl_vpis)                                                     AS date_zaqvl_vpis, ")// --09
        		.append("  	 (select TT.animalInfo from ( select max(edjp.event_deinost_jiv_id)as ttid , ")
        		.append("  	 STRING_AGG(COALESCE(vsc0.tekst,'Undefined code:'||508||':'||edjp.predmet),';') as animalInfo ")
        		.append("  	 from event_deinost_jiv_predmet edjp   ")
        		.append("  	 left JOIN v_system_classif     vsc0 ON ( ")
        		.append("  	                     vsc0.code_classif=508 ")
        		.append("  	                 AND vsc0.date_do IS NULL ")
        		.append("  	                 AND vsc0.code=edjp.predmet  ) ")
        		.append("  	 where edj.id=edjp.event_deinost_jiv_id  group by edjp.event_deinost_jiv_id ) as TT ) as animalInfo, ")//--10 Видове животни
        		.append("    min(v.info)                                                               as info ")// --11 забележка
        		.append(" FROM    vpisvane                   v ")
        		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
        		.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= :type ")
        		.append(" JOIN event_deinost_jiv_predmet edjp  ON edjp.event_deinost_jiv_id=edj.id ")
        		.append(" LEFT JOIN v_system_classif vsc2 ON ( vsc2.code_classif=541 AND vsc2.date_do IS NULL AND vsc2.code=edj.meroptiatie) ")
        		.append(" WHERE    v.id_register= :idRegister AND v.status= :status group by edj.id ");//-- интересуват ни само тези със статус - 2 - вписан";

        String sqlString= sql.toString();
        smd.setSql(sqlString);
        
        /* Формиране на заявката за броя резултати */
        StringBuilder sqlCountSB= new StringBuilder("select count(DISTINCT v.id) ")
        .append(" FROM    vpisvane                   v  ")
		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
		.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= :type ")
		.append(" LEFT JOIN v_system_classif vsc2 ON ( vsc2.code_classif=541 AND vsc2.date_do IS NULL AND vsc2.code=edj.meroptiatie) ")
		.append(" WHERE    v.id_register= :idRegister AND v.status= :status ");//-- интересуват ни само тези със статус - 2 - вписан";
        String sqlCountString= sqlCountSB.toString();
        smd.setSqlCount(sqlCountString);
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status",status );
        sqlParameters.put("type", type);//11 мероприятия с животни 
 
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerAnimalEvents: "+smd.toString());
        return smd;
    }
    
    
    /**
     * @param type
     * @return
     * yoni
     */
    private SelectMetadata registerOrganizationsCources(int type ) {
    	String types=(type==1?"1,2":"3");
        LOGGER.debug("registerOrganizationsCources()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=16;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" v.id                                         AS id_vpisvane , ")//-- 00 
				.append(" ara.ekatte                                                        AS addr_ekatte, ")//--01 Номер по ред; 
				.append(" ea.oblast_ime                                                     AS oblast_text,  ")//--02 Област (лицензиант);
				.append(" ea.obstina_ime                                                    AS obshtina_text, ")//--03 Община (лицензиант); 
				.append(" ea.ime                                                            AS ekatte_text, ")//--04 Населено място (лицензиант);
				.append(" ara.addr_text,                                                                      ")//--05 Улица/сграда (лицензиант);
				.append(" ar.ref_type                             AS licenziant_type,  ")//--06 3 -firma,4-Lice;
				.append(" ar.ref_name                                           AS licenziant_name, ")//-- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице); 
				.append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                           AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
				.append(" edj.opisanie_cyr                                                                  as dein_opisanie, ")//--09 Описание на дейността; 
				.append(" v.reg_nom_result                                        AS nomer_licenz , ")//--10 Рег. номер на становище;
				.append(" v.date_result                                           AS date_licenz,  ")//--11 Дата на регистрация;
				.append(" v.registratura_id AS registratura_id, ")//--12
				.append("  v.reg_nom_zaqvl_vpisvane  AS reg_nom_zaqvl_vpisvane, ")//--13
				.append("  v.date_zaqvl_vpis  AS date_zaqvl_vpis,   ")//--14
				.append("  v.info                                                           as info ")//-15 забележка
				.append(" FROM vpisvane                   v ")
				.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
				.append(" JOIN event_deinost_jiv_obuchenie edjo on edj.id=edjo.event_deinost_jiv_id and sfera_obucenie in (:types) ")
				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
				.append(" LEFT JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 ")// --1-za  korespondenciq,2-postoqnen
				.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
				//.append(" LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22  AND vsc3.date_do IS NULL  and vsc3.lang=1 AND vsc3.code=ara.addr_country) ")
				.append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";
		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( DISTINCT v.id) ")
		.append(" FROM vpisvane                   v ")
		.append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane ")
		.append(" JOIN event_deinost_jiv_obuchenie edjo on edj.id=edjo.event_deinost_jiv_id and sfera_obucenie in (:types) ")
		.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
		.append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 ")//--1-za  korespondenciq,2-postoqnen
		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
		//.append(" LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22  AND vsc3.date_do IS NULL  and vsc3.lang=1 AND vsc3.code=ara.addr_country) ")
		.append(" WHERE id_register= :idRegister AND v.status= :status ");
		
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        sqlParameters.put("types", castStringParamList(types));
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerOrganizationsCources: "+smd.toString());
        return smd;
    }
    
    
    /**
     * @return
     * yoni
     * ВАЖНО за всички удостоверителни документи вместо да join-ваме таблица doc  ползваме reg_nom_result, date_result пренесени от там
     */
    private SelectMetadata registerTransportSvidetelstvo( ) {
        LOGGER.debug("registerTransportSvidetelstvo()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=18;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				 .append(" v.id                                         AS id_vpisvane , ")//-- 00 
			     .append(" ara.ekatte                                                        AS addr_ekatte, ")//--01 Номер по ред;
			     .append(" ea.oblast_ime                                                     AS oblast_text, ")//--02 Област (лицензиант);
			      .append(" ea.obstina_ime                                                    AS obshtina_text,    ")  //--03 Община (лицензиант);
			      .append(" ea.ime                                                            AS ekatte_text,   ") //--04 Населено място (лицензиант);                      
			      .append(" ara.addr_text,                                                                      ")//--05 Улица/сграда (лицензиант);
			      .append(" ar.ref_type                             AS licenziant_type, ")//--06 3 -firma,4-Lice 
			      .append(" ar.ref_name                                           AS licenziant_name, ")// --07 Лицензиант(имена на физическо лице или наименование на юридическо лице);
			      .append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                           AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
			   	  .append(" edj.lice_kachestvo                                                                as lice_kachestvo, ")//--09 Качество на лицето – водач, придружител;
			   	.append(" v.reg_nom_result                             AS nomer_licenz , ")//--10 Рег. номер на удостоверение; 
	   			.append(" v.date_result                                              AS date_licenz, ")//-- 11 Дата на издаване.
				.append(" v.registratura_id AS registratura_id, ")//-- 12 
				.append(" v.reg_nom_zaqvl_vpisvane AS reg_nom_zaqvl_vpisvane, ")//-- 13
				.append(" v.date_zaqvl_vpis  AS date_zaqvl_vpis, ")//-- 14
				.append("  v.info                                                           as info ")//-15 забележка
			  .append(" FROM vpisvane                   v ")
			     .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane and edj.lice_kachestvo in (1,2,3)  ")//--водач, придружител, водач и придружител
			      .append("JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= 12 ")//--(кл. 506 дейност с регистри, издаване на лиценз правоспособност) 
			     .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			     .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
			     .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    // .append("	LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22  AND vsc3.date_do IS NULL and vsc3.lang=1 AND vsc3.code=ara.addr_country) ")
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
	     .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane and edj.lice_kachestvo in (1,2,3) ")//--водач, придружител, водач и придружител
	     .append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id AND edjv.vid= 12  ")//--(кл. 506 дейност с регистри, издаване на лиценз правоспособност)
	     .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
	     .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
	     .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
	     // .append("	LEFT JOIN v_system_classif vsc3 ON ( vsc3.code_classif=22  AND vsc3.date_do IS NULL and vsc3.lang=1 AND vsc3.code=ara.addr_country ) ")
	    .append(" WHERE id_register= :idRegister AND v.status= :status ");

		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerTransportSvidetelstvo: "+smd.toString());
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
        Integer idRegister=13;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder(" SELECT ")
				.append(" min(v.id)                                       AS id_vpisvane , ")//-- 00
				.append(" min(v.reg_nom_result  )                           AS nomer_licenz , ")//--01 Рег. номер на удостоверение;
				.append(" min(v.date_result )                                              AS date_licenz, ")//--02 Дата на издаване.
				.append("   (select TT.ekip from ( select max(edjl.event_deinost_jiv_id)as ttid , ")
				.append(" STRING_AGG (ar.ref_name || '(' ||COALESCE(vsc.tekst,'Undefined code:'||622||':'||edjl.tip_vraz)||')',';') as ekip  ")
				.append(" from event_deinost_jiv_lice edjl ")
				.append(" JOIN adm_referents         ar   ON edjl.code_ref =ar.code ")
				.append(" left JOIN v_system_classif     vsc ")
				.append(" ON ( vsc.code_classif=622 AND vsc.date_do IS NULL AND vsc.code=edjl.tip_vraz ) ")
				.append(" where edj.id=edjl.event_deinost_jiv_id group by edjl.event_deinost_jiv_id ) as TT ),  ")//--03 персонал
				.append(" min( v.registratura_id) as registratura_id, ")//--04
				.append(" min(v.reg_nom_zaqvl_vpisvane) as reg_nom_zaqvl_vpisvane, ")//--05
				.append(" min(v.date_zaqvl_vpis) as date_zaqvl_vpis, ")//--06
				.append(" min(v.info)                         as info ")//--07 забележка
				.append(" FROM vpisvane                   v  ")
				.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane  ")//--and v.id_register=13 and v.status=1
				.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
				.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";


		String sqlString= sql.toString();
		 System.out.println("sqlString "+sqlString);
		smd.setSql(sqlString);

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder(" select count(DISTINCT edj.id) ")
		.append(" FROM vpisvane                   v ")
		.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane ")
		.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
		.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
		.append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		 System.out.println("sqlCountString "+sqlCountString);
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerEmbrionTeams: "+smd.toString());
        return smd;
    }
    
    
    private SelectMetadata registerAnimalTrials( ) {
        LOGGER.debug("registerAnimalTrials()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=11;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" min(v.id)                                       AS id_vpisvane ,  ")//--00
				.append(" min(v.reg_nom_result  )                           AS nomer_licenz ,")//-- 01 Рег. номер на удостоверение; 
				.append(" min(v.date_result )                                              AS date_licenz,  ")//--02 Дата на издаване.
				.append(" min(ar.ref_name  )                                         AS licenziant_name, ")//--03 Имена/наименование на лицензианта
				.append(" min(ar.ref_type  )                           AS licenziant_type, ")//--04 3 -firma,4-Lice
				.append(" min(ara.addr_text )                           as licenziant_sedalishte, ")//--05 Адрес/седалище на лицензианта; 
				.append(" min(edjv.vid 	)								as vid_deinost_obekt, ")//--06 Вид на обекта на дейност;
				.append(" min(edj.adres 	)                             as mesto,  ")//-- 07 Местонахождение на обекта;
				.append(" min(edj.cel )                                as cel,  ")//--08 Цел на опитите;
				.append("   (select TT.ekip from ( select max(edjl.event_deinost_jiv_id)as ttid , ")
				.append(" STRING_AGG (ar.ref_name || '(' ||COALESCE(vsc.tekst,'Undefined code:'||622||':'||edjl.tip_vraz)||')',';') as ekip  ")
				.append(" from event_deinost_jiv_lice edjl ")
				.append(" JOIN adm_referents         ar   ON edjl.code_ref =ar.code ")
				.append(" left JOIN v_system_classif     vsc ")
				.append(" ON ( vsc.code_classif=622 AND vsc.date_do IS NULL AND vsc.code=edjl.tip_vraz ) ")
				.append(" where edj.id=edjl.event_deinost_jiv_id group by edjl.event_deinost_jiv_id ) as TT ), ")//-- 09 персонал 
				.append(" min( v.registratura_id) as registratura_id, ")//-- 10
				.append(" min(v.reg_nom_zaqvl_vpisvane) as reg_nom_zaqvl_vpisvane, ")//-- 11
				.append(" min(v.date_zaqvl_vpis) as date_zaqvl_vpis,  ")//-- 12
				.append(" min(v.info)                         as info, ")//--13 забележка
                .append("  min(ara.ekatte  )                                  AS ref_addr_ekatte, ")//--14 ekkate лицензиант;
                .append("  min(ea1.oblast_ime )                               AS ref_oblast_text, ")//--15 Област (лицензиант);
                .append("  min(ea1.obstina_ime  )                             AS ref_obshtina_text, ")//--16 Община (лицензиант);       
                .append("  min(ea1.ime   )                                    AS ref_ekatte_text, ")//-- 17 Населено място (лицензиант);                          
                .append("  min(ara.addr_text )                                AS ref_ulica, ")//--18 Улица/сграда (лицензиант);
				.append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,''))                           AS licenziant_egn ")//--19 ЕИК (за ЮЛ);
				.append(" FROM vpisvane                   v ")
				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")  
				.append(" LEFT OUTER JOIN  adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=2 ")
				.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane  ")
				.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id ")
				.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан"

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(DISTINCT edj.id) ")
			.append(" FROM vpisvane                   v ")
			.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")  
			.append(" LEFT OUTER JOIN  adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=2 ")
			.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
			.append(" JOIN event_deinost_jiv     edj   ON v.id=edj.id_vpisvane ")
			.append(" JOIN event_deinost_jiv_vid edjv  ON edjv.event_deinost_jiv_id=edj.id")
			.append(" JOIN event_deinost_jiv_lice edjl ON edjl.event_deinost_jiv_id=edj.id")
			.append(" WHERE id_register= :idRegister AND v.status= :status ");
        
		
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerAnimalTrials: "+smd.toString());
        return smd;
    }
    
    /**
     * СК520-32
     * Регистър на регистрираните обекти в сектор Фуражи съгласно чл. 17, ал. 3 от ЗФ
     * @param idRegister 
     * @return
     */
    private SelectMetadata  registerRegObecti17(Integer idRegister) {

        LOGGER.debug("registerAnimalTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT  "
        		+ "                    v.id as id_vpisvane,  "  //00     		
        		+ "                    od.reg_nom  as regNom,"  //0 
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
        sqlParameters.put("idRegister", idRegister);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
   
//    /**
//     * Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
//     * @return
//     */
//    private SelectMetadata  registerRegOOP() {
//
//        LOGGER.debug("registerAnimalTraders()");
//        SelectMetadata smd=new SelectMetadata();
//        //noinspection SpellCheckingInspection
//        String sql =  "SELECT  "
//        		+ "                    v.id,  "        		
//        		+ "                    od.reg_nom,"
//        		+ "                    array_to_string (ARRAY  "
//        		+ "                   (   SELECT  "
//        		+ "                            vsc.tekst  "
//        		+ "                        FROM  "
//        		+ "                            event_deinost_furaji_vid      vid  "
//        		+ "                            JOIN v_system_classif vsc ON (  "
//        		+ "                                    vsc.code_classif=506"
//        		+ "                                AND vsc.code=vid.vid)  "
//        		+ "                        WHERE  "
//        		+ "                            vid.event_deinost_furaji_id =  edf.id),', ') AS vid_dein,"        		
//        		+ "                     od.naimenovanie,"
//        		+ "                     od.nas_mesto                                                 AS OEZ_NAS_MESTO ,"
//        		+ "                     od.obsht                                                     AS OEZ_OBSHT,"
//        		+ "                     od.obl                                                       AS OEZ_OBL,"
//        		+ "                     od.address                                                   AS OEZ_ADDRESS,"
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
//        		+ "FROM  "
//        		+ "                    vpisvane v "
//        		+ "                    JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane  "
//        		+ "                    JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = :tabl_dein"
//        		+ "                    JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id  "
//        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
//        
//        smd.setSql(sql);
//        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
//        Map<String, Object> sqlParameters=new HashMap<>();
//        sqlParameters.put("idRegister", 44);        
//        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);
//
//        smd.setSqlParameters(sqlParameters);
//        return smd;
//    }

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
        		+ "                            vid.event_deinost_furaji_id = edf.id),'<br />') AS vid_dein,"        		
        		+ "         array_to_string (ARRAY  "
        		+ "            ( SELECT  "
        		+ "                 vsc1.tekst  "
        		+ "              FROM  "
        		+ "                 event_deinost_furaji_kategoria edfk "
        		+ "              JOIN v_system_classif vsc1 ON (  "
        		+ "                 vsc1.code_classif="+BabhConstants.CODE_CLASSIF_CATEGORY_SJP+ " "
        		+ "                   AND vsc1.code=edfk.kategoria)  "
        		+ "                        WHERE  "
        		+ "                            edfk.event_deinost_furaji_id = edf.id),'<br />') AS kategoria,"        		
        		+ "         array_to_string (ARRAY  "
        		+ "            ( SELECT  "
        		+ "                 vsc2.tekst  "
        		+ "              FROM  "
        		+ "                  event_deinost_furaji_predmet edfp "
        		+ "              JOIN v_system_classif vsc2 ON (  "
        		+ "                 vsc2.code_classif="+BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ+ " "
        		+ "                   AND vsc2.code=edfp.vid)  "
        		+ "                        WHERE  "
        		+ "                            edfp.event_deinost_furaji_id = edf.id),'<br />') AS predmet,"        		
        		+ "                   v.reg_nom_zaqvl_vpisvane as nom_zaqvl, "        		
           		+ "                   v.date_zaqvl_vpis AS date_zaqvl  "        		

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
            prednaznachenie=par[3];
        }
        //04 техника на отглеждане, използва се класификация 518
        if(par[4]!=null) {
            tehnologia=par[4];
        }
        Integer idRegister=4;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        LOGGER.debug("registerAnimalDefault()");
        SelectMetadata smd=new SelectMetadata();
        StringBuilder sql = new StringBuilder("SELECT ")
        	    .append("     min(v.id   )                                                              AS id_vpisvane , ")//--00
        	    .append("     min(v.reg_nom_result  )                                                   AS nomer_licenz , ")//--01 Рег. номер на удостоверение за регистрация; 
        	    .append("     min(v.date_result     )                                                   AS date_licenz, ")//-- 02 Дата на регистрация;
        	    .append("     min(v.registratura_id    )                                                as registratura_id, ")//--03 ОЕЗ регистририла лицензията
        	    .append("     min(v.reg_nom_zaqvl_vpisvane     )                                        as reg_nom_zaqvl_vpisvane, ")//-- 04 рег. номер първоначално  вписване
        	    .append("     min(v.date_zaqvl_vpis   )                                                 as date_zaqvl_vpis,  ")//--05 дата първоначално вписване
        	    .append("     min(ar.ref_type    )                                                      AS licenziant_type, ")//--06 3 -firma,4-Lice 
        	    .append("     min(od.naimenovanie     )                                                 AS OEZ_name, ")//--07 име на ОЕЗ
        	    .append("     min(ar.ref_name      )                                                    AS licenziant_name, ")//--08 оператор
        	    .append("     min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")//--09 ЕИК (за ЮЛ);
        	    .append("     min(ea.oblast_ime   )                                                     AS oblast_text, ")//--10 Област (ОЕЗ);
        	    .append("     min(ea.obstina_ime   )                                                    AS obshtina_text,     ")   //--11 Община (ОЕЗ);
        	    .append("     min(ea.ime    )                                                           AS ekatte_text,    ")   //--12 Населено място (ОЕЗ);
        	    .append("     min(od.address    )                                                       as ulica, ")//--13 Улица/сграда (ОЕЗ);
        	    .append("     min(SUBSTR(cast (od.gps_lat as text),1,5) || ' - ' || SUBSTR(cast (od.gps_lon as text),1,5) )  as gps_coord, ")//--14 GPS - координати;
        	    .append("     min(od.reg_nom           )                                                as vet_num, ")//--15 Ветеринарен рег. номер на ОЕЗ; 
        	    .append("     min(COALESCE(vsc3.tekst,'Undefined code:'||500||':'||od.vid_oez)  )           as oez_vid, ")//--16 Вид на ОЕЗ;
        	    .append("  min(ara.ekatte  )                                  AS ref_addr_ekatte, ")//--17 ekkate лицензиант;
        	    .append("  min(ea1.oblast_ime )                               AS ref_oblast_text, ")//--18 Област (лицензиант);
        	    .append("  min(ea1.obstina_ime  )                             AS ref_obshtina_text, ")//--19 Община (лицензиант);       
        	    .append("  min(ea1.ime   )                                    AS ref_ekatte_text, ")//-- 20 Населено място (лицензиант);                          
        	    .append("  min(ara.addr_text )                                AS ref_ulica, ")//--21 Улица/сграда (лицензиант);
        	    .append(" min(v.info)                                                           as info, ")//--22
        	    .append("     (select  TT.harakTXT from ")
        	    .append("     (select max(oh. id_oez) as OEZid, ")
        	    .append("           STRING_AGG ( COALESCE(COALESCE(vsc0.tekst,'Undefined code:'||508||':'||oh.vid_jivotno),'')||' - ' ")
        	    .append("           || COALESCE(COALESCE(vsc1.tekst,'Undefined code:'||502||':'||oh.prednaznachenie),'')||' - ' ")
        	    .append("           || COALESCE(COALESCE(vsc2.tekst,'Undefined code:'||518||':'||oh.tehnologia),'') || ' - ' || oh.kapacitet,'<br/>') as harakTXT ")
        	    .append("            from oez_harakt oh ")
        	    .append("            left JOIN v_system_classif     vsc0 ON ( ")
        	    .append("                    vsc0.code_classif=508 ")//--classif 508 Вид на животно 
        	    .append("                AND vsc0.date_do IS NULL ")
        	    .append("                AND vsc0.code=oh.vid_jivotno ) ")
        	    .append("        left JOIN v_system_classif vsc1 ON ( ")
        	    .append("                vsc1.code_classif=502  ")//-- classif 502 Предназначение на животните
        	    .append("            AND vsc1.date_do IS NULL ")
        	    .append("           AND vsc1.code = oh.prednaznachenie)  ")
        	    .append("        left JOIN v_system_classif vsc2 ON ( ")
        	    .append("                vsc2.code_classif=518 ")//-- classif 518 Технология на отглеждане 
        	    .append("            AND vsc2.date_do IS NULL ")
        	    .append("            AND vsc2.code = oh.tehnologia)  ")
        	    .append("            where oh.id_oez=od.id ");
        	    if(vidAnimals != null) {
        	        sql.append("    and oh.vid_jivotno   ").append(" in (:vidAnimals) ");//--classif 508 Вид на животно
        	    }
        	    if(prednaznachenie != null) {
        	      sql.append("    and oh.prednaznachenie  ").append(" in (:prednaznachenie) ");//--classif 502 Предназначение на животните
        	    }
        	    if(tehnologia != null) {
        	      sql.append("    and oh.tehnologia ").append(" in (:tehnologia) ");//-- classif 518 Технология на отглеждане
        	    }
        	     sql.append("   group by oh. id_oez ) TT)   as harakTXT  ")//--23 Характеристики на обекта (много):
        	    .append(" FROM vpisvane                   v ")
        	    .append(" join obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  ")
        	    .append(" and od.vid =1   ");//--classif 519 Обект на дейност
        	     if(epizodichnoZnch != null) {
        	        sql.append("    and od.vid_oez  ").append(" in (:epizodichnoZnch) ");//--classif 500 Вид на обекта с епизоотично значение
        	    }
        	    sql.append(" left join oez_harakt oh              on oh.id_oez=od.id ")
        	    .append(" LEFT join obekt_deinost_lica odl on odl.obekt_deinost_id=od.id ")
        	    .append(" LEFT JOIN adm_referents         ar   ON ar.code=odl.code_ref  ")
        	    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto ")
        	//    .append(" LEFT JOIN v_system_classif vsc4 ON (    vsc4.code_classif=22    AND vsc4.date_do IS NULL and vsc4.lang=1 AND vsc4.code=37  ) ")
        	    .append(" LEFT JOIN adm_ref_addrs ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
        	    .append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
        	 //   .append(" LEFT JOIN v_system_classif vsc5 ON (   vsc5.code_classif=22  AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=ara.addr_country )  ")
        	    .append(" LEFT JOIN v_system_classif vsc3 ON ( ")
        	    .append("    vsc3.code_classif=500  ")//--classif 519 Вид на ОЕЗ
        	    .append(" AND vsc3.date_do IS NULL ")
        	    .append(" AND vsc3.code=od.vid_oez ) ")
        	.append(" WHERE id_register= :idRegister AND v.status= :status  ");// --classif 516 Статус на вписване
        	 if(vidAnimals != null) {
     	        sql.append("    and oh.vid_jivotno   ").append(" in (:vidAnimals) ");//--classif 508 Вид на животно
     	    }
     	    if(prednaznachenie != null) {
     	      sql.append("    and oh.prednaznachenie  ").append(" in (:prednaznachenie) ");//--classif 502 Предназначение на животните
     	    }
     	    if(tehnologia != null) {
     	      sql.append("    and oh.tehnologia ").append(" in (:tehnologia) ");//-- classif 518 Технология на отглеждане
     	    }
     	   sql.append(" group by od.id   ");//--  записите се събират(групират) по ид ОЕЗ

//        Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
//        query.setParameter("type", type);
//        return query.getResultList();
        String sqlString= sql.toString();
        smd.setSql(sqlString);
        
        /* Формиране на заявката за броя резултати */
        StringBuilder sqlCountSB= new StringBuilder("select count(DISTINCT od.id )  ")
        .append(" FROM vpisvane                   v ")
	    .append(" join obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  ")
	    .append(" and od.vid =1   ");//--classif 519 Обект на дейност
	     if(epizodichnoZnch != null) {
	    	 sqlCountSB.append("    and od.vid_oez  ").append(" in (:epizodichnoZnch) ");//--classif 500 Вид на обекта с епизоотично значение 
	    }
	     sqlCountSB.append(" left join oez_harakt oh              on oh.id_oez=od.id ");
	    
	    if(vidAnimals != null) {
	    	sqlCountSB.append("    and oh.vid_jivotno ").append(" in (:vidAnimals) ");//--classif 508 Вид на животно
	    }
	    if(prednaznachenie != null) {
	    	sqlCountSB.append("    and oh.prednaznachenie  ").append(" in (:prednaznachenie) ");//--classif 502 Предназначение на животните
	    }
	    if(tehnologia != null) {
	    	sqlCountSB.append("    and oh.tehnologia  ").append(" in (:tehnologia) ");;
	    }
	    
	    sqlCountSB.append(" LEFT join obekt_deinost_lica odl on odl.obekt_deinost_id=od.id ")
	    .append(" LEFT JOIN adm_referents         ar   ON ar.code=odl.code_ref  ")
	    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto ")
//	    .append(" LEFT JOIN v_system_classif vsc4 ON (    vsc4.code_classif=22    AND vsc4.date_do IS NULL and vsc4.lang=1 AND vsc4.code=37  ) ")
	    .append(" LEFT JOIN adm_ref_addrs ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
	    .append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
	//   .append(" LEFT JOIN v_system_classif vsc5 ON (   vsc5.code_classif=22  AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=ara.addr_country )  ")
	    .append(" LEFT JOIN v_system_classif vsc3 ON ( ")
	    .append("    vsc3.code_classif=519  ")//--classif 519 Вид на ОЕЗ
	    .append(" AND vsc3.date_do IS NULL ")
	    .append(" AND vsc3.code=od.vid ) ")
	    .append(" WHERE id_register= :idRegister AND v.status= :status ");
		 if(vidAnimals != null) {
			 sqlCountSB.append("    and oh.vid_jivotno   ").append(" in (:vidAnimals) ");//--classif 508 Вид на животно
  	    }
  	    if(prednaznachenie != null) {
  	    	sqlCountSB.append("    and oh.prednaznachenie  ").append(" in (:prednaznachenie) ");//--classif 502 Предназначение на животните
  	    }
  	    if(tehnologia != null) {
  	    	sqlCountSB.append("    and oh.tehnologia ").append(" in (:tehnologia) ");//-- classif 518 Технология на отглеждане
  	    }
	    String sqlCountString= sqlCountSB.toString();
        smd.setSqlCount(sqlCountString);
        
        
        /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);  
        
		if (vidAnimals != null) {
			sqlParameters.put("vidAnimals", castStringParamList(vidAnimals));
		}
		if (epizodichnoZnch != null) {
			sqlParameters.put("epizodichnoZnch", castStringParamList(epizodichnoZnch));
		}
		if (prednaznachenie != null) {
			sqlParameters.put("prednaznachenie", castStringParamList(prednaznachenie));
		}
		if (tehnologia != null) {
			sqlParameters.put("tehnologia", castStringParamList(tehnologia));
		}
        
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerAnimalDefault--> "+par[0]+"; "+smd.toString());
        return smd;
    }
    
    
    List<Integer> castStringParamList(String param){
    	List<Integer> res= new ArrayList<Integer>();
    	String [] el= param.split(",");
    	for(String s:el) {
    		res.add(Integer.parseInt(s));
    	}
    	return res;
    }
    
//    public String parameterOneOrMany(String namedV, String namedP) {
//    	return (namedV.contains(","))?" in( :"+namedP+") ":" = :"+namedP+" ";
//    }
    
    /**
     * Регистър на заявленията за изплащане на обезщетения на собствениците на убити или унищожени животни
     * yoni
     */
    private SelectMetadata registerPayDeadAnimals( ) {
        LOGGER.debug("registerPayDeadAnimals()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=19;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				 .append(" min(v.id  )                                                              AS id_vpisvane,  ")//-- 00 id на вписване
				    .append(" min(ara.ekatte  )                                                        AS addr_ekatte, ")//-- 01 Населено място(обект);
				    .append(" min(ea.oblast_ime  )                                                     AS oblast_text, ")//-- 02 Област (обект);
				    .append(" min(ea.obstina_ime  )                                                    AS obshtina_text,    ") //-- 03 Община (обект);   
				    .append(" min(ea.ime  )                                                            AS ekatte_text,     ")//-- 04 Населено място (обект);                      
				    .append(" min(od.address  )                                                           as ulica, ")//-- 05 Улица/сграда (обект);
				    .append(" min(ar.ref_type  )                                                       AS licenziant_type, ")//-- 06 3 -firma,4-Lice
				    .append(" min(ar.ref_name  )                                                       AS licenziant_name,  ")//-- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице);
				    .append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')   )                          AS licenziant_egn, ")//-- 08 ЕИК (за ЮЛ);
				   	.append(" min(od.naimenovanie   )                                                  AS OEZ_name,  ")// -- 09 Наименование на ОЕЗ;
				    .append(" min(od.reg_nom  )                                                        AS OEZ_rn,  ")//-- 10 Рег. номер на ОЕЗ;
				    .append(" (select TT.broi from ( select max(edjp.event_deinost_jiv_id)as deinID , ") 
		    		.append(" STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||508||':'||edjp.predmet)||'('||edjp.broi||')',';') as broi ")
    				.append(" from event_deinost_jiv_predmet edjp ")
					.append(" left JOIN v_system_classif     vsc0 ON (  vsc0.code_classif=508   AND vsc0.date_do IS NULL  AND vsc0.code=edjp.predmet )  ")
					.append(" where 1=1 group by edjp.event_deinost_jiv_id ) as TT where deinID= edj.id ), ")//-- 11 Брой животни;
				    
				    .append(" min(v.reg_nom_result  )                                                  AS nomer_licenz,  ")//--12 Рег. номер на акта;
				    .append(" min(v.date_result     )                                                  AS date_licenz, ")//-- 13 Дата на регистрация. 
				    .append(" min(v.registratura_id  )                                                 AS registratura_id,  ")//-- 14 ОЕЗ регистририла лицензията           
				    .append(" min(v.reg_nom_zaqvl_vpisvane  )                                          AS reg_nom_zaqvl_vpisvane, ")//-- 15 рег. номер първоначално вписване            
				    .append(" min(v.date_zaqvl_vpis    )                                               AS date_zaqvl_vpis,    ") //--16 дата първоначално вписване         
				    
                    .append("  min(ara.ekatte  )                                    AS ref_addr_ekatte, ")//--17 ekkate лицензиант;
                    .append("  min(ea1.oblast_ime  )                                AS ref_oblast_text, ")//--18 Област (лицензиант);
                    .append("  min(ea1.obstina_ime  )                               AS ref_obshtina_text, ")//--19 Община (лицензиант);       
                    .append("  min(ea1.ime       )                                 AS ref_ekatte_text, ")//-- 20 Населено място (лицензиант);                          
                    .append("  min(ara.addr_text   )                                AS ref_ulica, ")//--21 Улица/сграда (лицензиант);
                    .append(" min(v.info     )                                                        as info ")//--22
				    
				    
				.append(" FROM vpisvane                   v ")
				    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
					.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
					.append(" LEFT join event_deinost_jiv_predmet edjp  on edjp.event_deinost_jiv_id =edj.id  ")
					.append(" LEFT join obekt_deinost_deinost odd  ON odd.deinost_id=edj.id and tabl_event_deinost=98  ")
					.append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
				    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
				    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
				    
				    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto ")
				    .append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				    
				   //	.append(" LEFT JOIN v_system_classif vsc3 ON (  vsc3.code_classif=22  AND vsc3.date_do IS NULL and vsc3.lang=1  AND vsc3.code=ara.addr_country ) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( DISTINCT v.id) ")
				.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
				.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
				.append(" LEFT join event_deinost_jiv_predmet edjp  on edjp.event_deinost_jiv_id =edj.id  ")
				.append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
				.append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    //	.append(" LEFT JOIN v_system_classif vsc3 ON (  vsc3.code_classif=22  AND vsc3.date_do IS NULL and vsc3.lang=1  AND vsc3.code=ara.addr_country ) ")
		  .append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);


		 /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerPayDeadAnimals: "+smd.toString());
        return smd;
    }
    
    
    /**
     * Регистър на издадените разрешения за износ на животни и зародишни продукти
     * @author yoni
     */
    private SelectMetadata registerSellAnimalsAbroad( ) {
        LOGGER.debug("registerSellAnimalsAbroad()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=20;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
			     .append(" min(v.id  )                                                              AS id_vpisvane,  ")//-- 00 id на вписване
			     .append(" min(ara.ekatte   )                                                       AS addr_ekatte, ")//-- 01 Населено място(лицензиант);
			     .append(" min(ea.oblast_ime   )                                                    AS oblast_text, ")//-- 02 Област (лицензиант);
			     .append(" min(ea.obstina_ime   )                                                   AS obshtina_text,   ")//-- 03 Община (лицензиант);     
			     .append(" min(ea.ime    )                                                          AS ekatte_text,    ") //-- 04 Населено място (лицензиант);                      
			     .append(" min(ara.addr_text   )                                                    AS licenziant_address, ")//-- 05 Улица/сграда (лицензиант);
			     .append(" min(ar.ref_type   )                                                      AS licenziant_type, ")//-- 06 3 -firma,4-Lice
			     .append(" min(ar.ref_name   )                                                      AS licenziant_name,  ")//-- 07 Лицензиант(имена на физическо лице или наименование на юридическо лице);
			     .append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')    )                         AS licenziant_egn, ")//-- 08 ЕИК (за ЮЛ);
			     .append(" min(od.naimenovanie  )                                                   AS OEZ_name,   ")//-- 09 Наименование на ОЕЗ;
			     .append(" min(od.reg_nom   )                                                       AS OEZ_rn,  ")//-- 10 Рег. номер на ОЕЗ;
			     .append(" min(edj.opisanie_cyr     )                                               AS dein_description,  ") //-- 11 Описание на дейността;
			    .append(" (select TT.countries from (select  max(edjd.event_deinost_jiv_id) as deinID, STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||22||':'||edjd.darj),',') as countries  ")
			.append(" from event_deinost_jiv_darj edjd  ")
			.append(" left JOIN v_system_classif     vsc0 ON ( ")
			 .append("                    vsc0.code_classif=22 and vsc0.lang=1 ")//--classif 22 Държави 
			 .append("                AND vsc0.date_do IS NULL ")
			 .append("                AND vsc0.code=edjd.darj ) ")
			.append(" where 1=1  group by edjd.event_deinost_jiv_id) TT where deinID= edj.id )    AS countries,  ")//-- 12 Страни, за които ще се изнася;
			 .append("     min(v.reg_nom_result )                                                 AS nomer_licenz, ")//--13 Рег. номер на акта; 
			  .append("    min(v.date_result )                                                    AS date_licenz, ")//-- 14 Дата на регистрация.
			  .append("    min(v.registratura_id)                                                 AS registratura_id,  ")//-- 15 ОЕЗ регистририла лицензията           
			   .append("   min(v.reg_nom_zaqvl_vpisvane)                                          AS reg_nom_zaqvl_vpisvane,    ") //-- 16 рег. номер първоначално вписване        
			    .append("  min(v.date_zaqvl_vpis)                                                 AS date_zaqvl_vpis,     ") //--17 дата първоначално вписване
			    .append(" min(v.info)                                                           as info ")//--18
			.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
			    .append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
			    .append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
			    .append(" LEFT join obekt_deinost_deinost odd  ON odd.deinost_id=edj.id and tabl_event_deinost=98  ")
			    .append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    //.append(" LEFT JOIN v_system_classif vsc3 ON (   vsc3.code_classif=22 and vsc3.lang=1  AND vsc3.date_do IS NULL   AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status group by edj.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( DISTINCT edj.id) ")
				.append(" FROM vpisvane                   v ")
			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
			    .append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
			    .append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
			    .append(" LEFT join obekt_deinost_deinost odd  ON odd.deinost_id=edj.id and tabl_event_deinost=98  ")
			    .append(" LEFT join obekt_deinost od           on od.id=odd.obekt_deinost_id   ")
			    .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
			    .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			    //.append(" LEFT JOIN v_system_classif vsc3 ON (   vsc3.code_classif=22 and vsc3.lang=1  AND vsc3.date_do IS NULL   AND vsc3.code=ara.addr_country) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		
		 /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters); 
        
        System.out.println("SMD registerSellAnimalsAbroad: "+smd.toString());
        return smd;
    }
    
    /**
     * Регистър на вет. лекари и обектите, в които осъществяват ветеринарно медицинска практика
     * @author yoni
     */
    private SelectMetadata registerListVetsVLZ() {
        LOGGER.debug("registerListVetsVLZ()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=9;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
			   	 .append(" v.id                                                                AS id_vpisvane, ")//-- 00 id на вписване 
			     .append(" ar.ref_type                                                         AS licenziant_type, ")//-- 01 3 -firma,4-Lice
			     .append(" ar.ref_name                                                         AS licenziant_name, ")//-- 02 Лицензиант(имена на физическо лице или наименование на юридическо лице); 
			     .append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                             AS licenziant_egn, ")//-- 03 ЕИК (за ЮЛ);
			     .append(" v.reg_nom_result                                                    AS nomer_licenz,  ")//--04 Рег. номер на сертификата;
			     .append(" v.date_result                                                       AS date_licenz, ")//-- 05 Дата на регистрация;
			     .append(" COALESCE(vsc4.tekst,'Undefined code:'||505||':'||od.vid_vlz )       AS vlz_vid, ")//-- 06 Вид на ВЛЗ;
			     .append(" od.naimenovanie                                                     AS vlz_name, ")//--07    Наименование на ВЛЗ 
			     .append(" od.zemliste                                                         AS vlz_mesto, ")//-- 08 Местонахождение ;
			     .append(" od.reg_nom                                                             AS vlz_urn, ")//-- 09 УРИ на ВЛЗ ;
			     .append(" v.registratura_id                                                   AS registratura_id,   ")//-- 10 ОЕЗ регистририла лицензията          
			     .append(" v.reg_nom_zaqvl_vpisvane                                            AS reg_nom_zaqvl_vpisvane, ")//-- 11 рег. номер първоначално вписване 
			     .append(" v.date_zaqvl_vpis                                                   AS date_zaqvl_vpis     ") //--12 дата първоначално вписване        
			.append(" FROM vpisvane                   v  ")
//			    .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane  ")
//				.append(" JOIN event_deinost_jiv_vid edjv ON edjv.event_deinost_jiv_id=edj.id  ")
//				.append(" join event_deinost_jiv_darj edjd on edjd.event_deinost_jiv_id =edj.id  ")
//				.append(" LEFT join obekt_deinost_deinost odd  ON odd.obekt_deinost_id=edj.id and tabl_event_deinost=98  ")
				.append(" JOIN obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  and od.vid = 4 ")//--calssif 519, значение VLZ
				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
			    .append(" LEFT JOIN v_system_classif vsc4 ON ( ")
			    .append(" vsc4.code_classif=505  ")//--classif 505 Вид на ВЛЗ
			        .append(" AND vsc4.date_do IS NULL ")
			        .append(" AND vsc4.code=od.vid_vlz) ")
			  .append(" WHERE id_register= :idRegister AND v.status= :status ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count(v.id) ")
		.append(" FROM vpisvane                   v  ")
		.append(" JOIN obekt_deinost od           on od.id=v.id_licenziant and v.licenziant_type = 1  and od.vid = 4 ")//--calssif 519, значение VLZ
		.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant ")
	    .append(" LEFT JOIN v_system_classif vsc4 ON ( ")
	    .append(" vsc4.code_classif=505 ")//--classif 505 Вид на ВЛЗ 
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
        
        System.out.println("SMD registerListVetsVLZ: "+smd.toString());
        return smd;
    }


    
    /**
     * Регистър на транспортните средства, с които се превозват животни
     * @author yoni
     */
    private SelectMetadata registerMPSAnimals() {
        LOGGER.debug("registerMPSAnimals()");
        
        Integer idRegister=8;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        SelectMetadata smd=new SelectMetadata();

		StringBuilder sql = new StringBuilder("SELECT ")
		   	.append("  min( v.id  )                                                             AS id_vpisvane,  ")//-- 00 id на вписване
		   	.append("  min(COALESCE(vsc1.tekst,'Undefined code:'||509||':'|| m.vid  ))          AS mps_vid, ")//-- 01 Вид на ТС:
		   	.append("  min(m.model  )                                                           AS mps_model, ")//-- 02 Марка и модел; 
		   	.append("  min(m.plosht  )                                                          AS mps_plosht, ")//-- 03 Площ:   
		    .append("  min( m.nom_dat_reg   )                                                  AS mps_talon, ")//-- 04 Рег. номер и дата на талона;
		   	.append("  min(v.registratura_id )                                                  AS registratura_id, ")//-- 05 ОДБХ, издало лиценза; 
		    .append("  min(ar.ref_type )                                                        AS licenziant_type, ")//-- 06 3 -firma,4-Lice 
		    .append("  min(ar.ref_name  )                                                       AS licenziant_name,  ")//-- 07 Заявител(имена на физическо лице или наименование на юридическо лице);
		    .append("  min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') )                            AS licenziant_egn,  ")//-- 08 ЕИК (за ЮЛ);
		    .append("  min(v.reg_nom_result )                                                   AS nomer_licenz,  ")//--09 Рег. номер на сертификата;
		    .append("  min(v.date_result  )                                                     AS date_licenz, ")//-- 10 Дата на регистрация; 
		    .append("  min(v.reg_nom_zaqvl_vpisvane  )                                          AS reg_nom_zaqvl_vpisvane, ")// -- 11 рег. номер първоначално вписване 
			.append("  min(v.date_zaqvl_vpis   )                                                AS date_zaqvl_vpis,    ")//--12 дата първоначално вписване          
			.append(" (select TT.kapacitet from (select  max(mkj.mps_id) as mpsID,  STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||581||':'||mkj.vid_jiv) ||':'|| mkj.broi_jiv,',') as kapacitet ")//--13 Капацитет (много): 
			.append(" from mps_kapacitet_jiv mkj  ")
			.append("  left JOIN v_system_classif     vsc0 ON ( ")
			.append("                     vsc0.code_classif=581 ")//--classif 581 Вид животно за транспорт 
			.append("                 AND vsc0.date_do IS NULL ")
			.append("                 AND vsc0.code=mkj.vid_jiv ) ")
			.append(" where 1=1    group by mkj.mps_id) TT where mpsID= mkj.mps_id )    AS kapacitet, ")
			.append(" min(v.info)                                                           as info ")
			.append(" FROM vpisvane                   v ")
			.append("     join mps m on m.id = v.id_licenziant ")
			.append("      left join mps_kapacitet_jiv mkj on mkj.mps_id = m.id ")
			.append("      left join mps_lice ml on ml.mps_id=m.id ")
			.append("      left join adm_referents  ar   ON ar.code=ml.code_ref ")
			.append(" 	LEFT JOIN v_system_classif vsc1 ON ( ")
			.append("             vsc1.code_classif=509  ")//--classif 509 Вид на МПС
			.append("         AND vsc1.date_do IS NULL ")
			.append("         AND vsc1.code=m.vid ) ")
			.append(" WHERE id_register= :idRegister AND v.status= :status group by mkj.mps_id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		
		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder(" select count(DISTINCT mkj.mps_id) ")
		.append(" FROM vpisvane                   v ")
		.append("     join mps m on m.id = v.id_licenziant ")
		.append("      left join mps_kapacitet_jiv mkj on mkj.mps_id = m.id ")
		.append("      left join mps_lice ml on ml.mps_id=m.id ")
		.append("      left join adm_referents  ar   ON ar.code=ml.code_ref ")
		.append(" 	LEFT JOIN v_system_classif vsc1 ON ( ")
		.append("             vsc1.code_classif=509  ")//--classif 509 Вид на МПС
		.append("         AND vsc1.date_do IS NULL ")
		.append("         AND vsc1.code=m.vid ) ")
		.append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerMPSAnimals: "+smd.toString());
        return smd;
    }
    
    /**
     * Регистър на издадените разрешителни за превоз на животни
     * @author yoni
     */
    private SelectMetadata registerApprovedMPStransport() {
        LOGGER.debug("registerApprovedMPStransport()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=7;
        Integer status=BabhConstants.STATUS_VP_VPISAN;

		StringBuilder sql = new StringBuilder(" SELECT ")
			   .append(" min( v.id  )                                                             AS id_vpisvane, ")//-- 00 id на вписване  
			   .append(" min(ea.oblast_ime    )                                                    AS oblast_text, ")//-- 01 Област (лицензиант);
			   .append(" min(ea.obstina_ime   )                                                   AS obshtina_text, ")//-- 02 Община (лицензиант);  
			   .append(" min(ea.ime  )                                                          AS ekatte_text, ")//-- 03 Населено място (лицензиант);                                        
			   .append(" min(ara.addr_text   )                                                    AS licenziant_address, ")//--04 Улица/сграда (лицензиант);
			   .append(" min(ar.ref_type )                                                        AS licenziant_type, ")//-- 05 3 -firma,4-Lice 
			   .append(" min(ar.ref_name  )                                                       AS licenziant_name, ")// -- 06 Заявител(имена на физическо лице или наименование на юридическо лице); 
			   .append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') )                            AS licenziant_egn, ")//-- 07 ЕИК (за ЮЛ); 
			   .append(" min(v.reg_nom_result )                                                   AS nomer_licenz, ")// --08 Рег. номер на сертификата; 
			   .append(" min(v.date_result  )                                                     AS date_licenz, ")//-- 09 Дата на регистрация; 
			   .append(" min(v.registratura_id )                                                  AS registratura_id, ")//-- 10 ОДБХ, издало лиценза;  
			   .append("  min(v.reg_nom_zaqvl_vpisvane  )                                          AS reg_nom_zaqvl_vpisvane, ")//-- 11 рег. номер първоначално вписване            , 
			   .append(" min(v.date_zaqvl_vpis   )                                                AS date_zaqvl_vpis, ")// --12 дата първоначално вписване             
			   .append(" (select min(TT2.kapacitet) from ( select  md.id as mdID,STRING_AGG ( COALESCE(vsc1.tekst,'Undefined code:'||509||':'|| TT1.vid )  || ' '|| ")
			   .append(" COALESCE(vsc2.tekst,'Undefined code:'||583||':'|| TT1.marka ) || ' '|| coalesce(TT1.model,'-') || ',с площ: '|| coalesce(TT1.plosht,'-') || ',капацитет:'|| coalesce(TT1.kapacitet,'-'),',') as kapacitet ")
			   .append(" from  ")
			   .append(" (select TT.mpsID as mpsID,m1.vid as vid, m1.marka as marka, m1.model as model, m1.plosht as plosht, TT.kapacitet from (select  max(mkj.mps_id) as mpsID,  STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||581||':'||mkj.vid_jiv) ||':'|| mkj.broi_jiv,',') as kapacitet  ")//--14 Капацитет (много): 
			   .append(" from mps_kapacitet_jiv mkj   ")
			   .append(" left JOIN v_system_classif     vsc0 ON (  vsc0.code_classif=581 AND vsc0.date_do IS NULL  AND vsc0.code=mkj.vid_jiv ) ")
			   .append(" where 1=1    group by mkj.mps_id ) TT ")
			   .append(" join mps m1 on m1.id=TT.mpsID ) TT1 ")
			   .append(" join mps_deinost md on md.mps_id=TT1.mpsID ")
			   .append(" LEFT JOIN v_system_classif vsc1 ON ( vsc1.code_classif=509 AND vsc1.date_do IS NULL AND vsc1.code=TT1.vid ) ")
			   .append(" LEFT JOIN v_system_classif vsc2 ON ( vsc2.code_classif=583  AND vsc2.date_do IS NULL  AND vsc2.code=TT1.marka ) group by md.id   ) TT2 where mdID=md.id)  AS kapacitet, ")
			   .append("  min(v.info)                                                           as info ")
			   .append(" FROM vpisvane                   v  ")
			   .append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant  ")
			   .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 ")// --1-za  korespondenciq,2-postoqnen
			   .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
			   .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
			   .append(" join mps_deinost md    ON md.deinost_id=edj.id and tabl_event_deinost=98  ")//--and md.tip_vraz=3 
			   .append(" join mps m           on m.id=md.mps_id    ")
			   .append(" left join mps_kapacitet_jiv mkj on mkj.mps_id=m.id    ")
			   .append(" where  v.id_register=:idRegister AND v.status=:status  group by md.id ");// -- интересуват ни само тези със статус - 2 -вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
				.append(" FROM vpisvane                   v ")
				.append(" JOIN adm_referents         ar   ON ar.code=v.id_licenziant  ")
				   .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1 ")// --1-za  korespondenciq,2-postoqnen
				   .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
				   .append(" JOIN event_deinost_jiv     edj  ON v.id=edj.id_vpisvane   ")
				   .append(" join mps_deinost md    ON md.deinost_id=edj.id and tabl_event_deinost=98  ")//--and md.tip_vraz=3 
				   .append(" join mps m           on m.id=md.mps_id    ")
				   .append(" left join mps_kapacitet_jiv mkj on mkj.mps_id=m.id    ")
				   .append(" where  v.id_register=:idRegister AND v.status=:status   ");// -- интересуват ни само тези със статус - 2 -вписан";

		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerApprovedMPStransport: "+smd.toString());
        return smd;
    }
    
    
    
    
    /**
     * Регистър на издадените разрешения за търговия на дребно с ВЛП 
     * Регистър на издадените разрешения за търговия на едро с ВЛП с ВЛП
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
        		+ "                   ara.ekatte as ekatte, "        		
           		+ "                   ara.addr_text as addr_text,  "        		
           		+ "                   v.info as zabelejka  "        		

        		+ " FROM  "
        		+ "                    vpisvane v "
        		+ "                    LEFT OUTER JOIN event_deinost_vlp edv ON v.id=edv.id_vpisvane  "
        		+ "                    LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein"
        		+ "                    LEFT OUTER JOIN obekt_deinost         od  ON odd.obekt_deinost_id = od.id  "
        		+ "                    LEFT OUTER JOIN event_deinost_vlp_lice edvl  ON edv.id=edvl.event_deinost_vlp_id  "
        		+ "                    LEFT OUTER JOIN adm_referents ar  ON ar.code=edvl.code_ref  "
        		+ "                    LEFT OUTER JOIN adm_ref_addrs ara  ON (ara.code_ref=v.code_ref_vpisvane and addr_type = v.licenziant_type) "
        		+ "WHERE  v.id_register= :idRegister AND v.status=2";
        
        smd.setSql(sql);
        smd.setSqlCount("select count(v.id) "+sql.substring(sql.toUpperCase().lastIndexOf("FROM")));
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);        
        sqlParameters.put("tabl_dein", BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP);

        smd.setSqlParameters(sqlParameters);
        return smd;
    }
    
    
    private SelectMetadata registerVlpTradersDistance(Integer idRegister) {

//            LOGGER.debug("registerVlpTraders()");
           SelectMetadata smd=new SelectMetadata();
//            //noinspection SpellCheckingInspection
//            String sql =  "SELECT " +
//                    "    v.id, " +
//               		"    v.licenziant, " +       		
//                    "    v.reg_nom_result   AS nomer_licenz , " +
//                    "    v.date_result      AS date_licenz , " +
//                    "    od.naimenovanie 	as naimObect, " +
//                    "    od.nas_mesto 		AS OEZ_NAS_MESTO , " +
//                    "    od.obsht           AS OEZ_OBSHT, " +
//                    "    od.obl             AS OEZ_OBL, " +
//                    "    od.address         AS OEZ_ADDRESS, " +
//                    "    edv.danni_kontragent, " +
//                    "    edv.nom_dat_lizenz, " + //
// 
//                    "    edv.email          AS edvEmail, " +
//                    "    edv.site           AS edvSite, " +
//                    "    od.email           AS odMail, " +
//                    "    edv.adres          AS edvAdres, " +
//                    "    v.date_status      AS dateStatus " + //
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

            return smd;
        }
    
    
	private SelectMetadata registerVlpPublicity(Integer idRegister) {
        LOGGER.debug("registerVlpTraders()");
        SelectMetadata smd=new SelectMetadata();
        //noinspection SpellCheckingInspection
        String sql =  "SELECT " +
                "    v.id, " +
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
                "                     vsc.code_classif= 612 ) " +
//                "                     vsc.code_classif= :classiff ) " +
                "        WHERE " +
                "            vsc.code=edv2.prednazn_reklama),', ') AS prednazn_reklama_text " +
                "FROM " +
                "    vpisvane                         v "+//-- основни данни за вписването " +
                "    LEFT OUTER JOIN event_deinost_vlp edv  ON v.id=edv.id_vpisvane "+//-- licenziant " +
//                "    LEFT OUTER JOIN obekt_deinost_deinost odd odd.deinost_id=edv.id " +
                "    LEFT OUTER JOIN obekt_deinost_deinost       odd ON odd.deinost_id=edv.id AND odd.tabl_event_deinost = :tabl_dein " +   //100 " +
//                "    LEFT OUTER JOIN  obekt_deinost               od  ON odd.obekt_deinost_id = od.id " +
//                "    LEFT OUTER JOIN  obekt_deinost_lica odl ON odl.obekt_deinost_id = od.id " +
                "WHERE " +
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
     * Регистър на транспортните средства за превоз на странични животински продукти
     * @author yoni
     */
    private SelectMetadata registerListMpsSJP() {
        LOGGER.debug("registerListMpsSJP()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=43;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" min( v.id )                         							        as id_vpisvane , ")// --00
				.append(" min( v.reg_nom_result      )                						 	as nomer_licenz ,  ")//--01 Рег. номер на разрешителното;
				.append(" min( v.date_result   )                      						 	as date_licenz, ")// --02 Дата на издаване;
				.append(" min( v.registratura_id   )                  						 	as registratura_id, ")// --03 ОДБХ, регистрирала ТС.
				.append(" min( v.reg_nom_zaqvl_vpisvane    )          						 	as reg_nom_zaqvl_vpisvane, ")//--04
				.append(" min( v.date_zaqvl_vpis  )                   						 	as date_zaqvl_vpis, ")// --05
				.append(" min( ar.ref_type  )                                    			 	    as licenziant_type, ")//-- 06
				.append(" min( ar.ref_name   )                                   			 	    as licenziant_name, ")//-- 07 Заявител (имена на ФЛ или наименование на ЮЛ)
				.append(" min( COALESCE(ar.nfl_eik, fzl_egn, fzl_lnc, '') )      			 	    as licenziant_egn, ")//--08 ЕИК (ако е ЮЛ)
				.append(" min( COALESCE(vsc0.tekst,'Undefined code:'||509||':'|| m.vid  ) )   	as mps_vid,  ")//--09 Вид на МПС; - classif  
				.append(" min( m.model )                                         			 	    as mps_markaModel, ")//--10 Марка и модел;
				.append(" min( m.nomer  )                                        			 	    as mps_nomer, ")// --11 Рег. номер;
				.append(" min( m.tovaropodemnost  )                              			 	    as mps_tovar,  ")//-- 12 Товароподемност;
				.append("  min( ard1.nom_doc  )                                 			 	    as mps_cert1,  ")//-- 13 Номер и дата на международния сертификат;
				.append("  min( ard2.nom_doc  )                                 			 	    as mps_cert2,  ")//-- 14 Номер и дата на вътрешен сертификат;
				.append("  min(v.info)                                                               as info,  ")// --15 забележка
				.append("  min(ara.ekatte  )                                                         AS ref_addr_ekatte,  ")// --16 ekkate лицензиант;
				.append("  min(ea1.oblast_ime )                                                      AS ref_oblast_text,  ")//--17 Област (лицензиант);
				.append("  min(ea1.obstina_ime  )                                                    AS ref_obshtina_text,  ")//--18 Община (лицензиант);  
				.append("  min(ea1.ime   )                                                           AS ref_ekatte_text,  ")//-- 19 Населено място (лицензиант);
				.append("  min(ara.addr_text )                                                       AS ref_ulica,  ")//--20 Улица/сграда (лицензиант);
				.append(" (select TT.category from (select  max( mc.mps_id ) as deinID, STRING_AGG (COALESCE(vsc1.tekst,'Undefined code:'||527||':'|| mc.category),',') as category  ")
				.append(" from mps_category mc ")
				.append(" left JOIN v_system_classif     vsc1 ON (  vsc1.code_classif=527 AND vsc1.date_do IS NULL  AND vsc1.code=mc.category   ) ")
				.append(" where 1=1  group by  mc.mps_id ) TT where  mc.mps_id =deinID )    as mps_spj_cat  ")//--21 cateogoria --classif 
				.append(" from vpisvane v ")
				.append(" inner join mps m on m.id = v.id_licenziant ")
				.append(" join mps_lice ml  on ml.mps_id=m.id ")
				.append(" left join adm_referents ar         on ar.code =ml.code_ref ")
				.append(" left join adm_ref_doc ard1 on ar.code = ard1.code_ref and ard1.vid_doc=6 ")//--classif  537 вид документ лице
				.append(" left join adm_ref_doc ard2 on ar.code = ard2.code_ref and ard2.vid_doc=7 ")//--classif  537 вид документ лице
				.append(" join mps_category mc          on mc.mps_id=m.id ")
				.append(" left JOIN v_system_classif     vsc0 ON (  vsc0.code_classif=509 AND vsc0.date_do IS NULL  AND vsc0.code=m.vid  ) ")
				.append("  left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1 ")//--1-za  korespondenciq,2-postoqnen
				.append("  LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				//.append(" LEFT JOIN v_system_classif vsc5 ON (  vsc5.code_classif=22 AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=ara.addr_country)   ")// --bulgaria (kod na strana)
				.append(" where id_register= :idRegister AND v.status= :status group by mc.mps_id  ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
		.append(" from vpisvane v  ")
		.append(" inner join mps m on m.id = v.id_licenziant ")//
		.append(" join mps_lice ml  on ml.mps_id=m.id ")
	    .append(" left join adm_referents ar         on ar.code =ml.code_ref ")
		.append(" left join adm_ref_doc ard1 on ar.code = ard1.code_ref and ard1.vid_doc=6 ")//--classif  537 вид документ лице
		.append(" left join adm_ref_doc ard2 on ar.code = ard2.code_ref and ard2.vid_doc=7 ")//--classif  537 вид документ лице
		.append("  left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1 ")//--1-za  korespondenciq,2-postoqnen
		.append("  LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
		//.append(" LEFT JOIN v_system_classif vsc5 ON (  vsc5.code_classif=22 AND vsc5.date_do IS NULL and vsc5.lang=1 AND vsc5.code=ara.addr_country)   ")// --bulgaria (kod na strana)
	    .append(" join mps_category mc          on mc.mps_id=m.id ")
		.append(" left JOIN v_system_classif     vsc0 ON (  vsc0.code_classif=509 AND vsc0.date_do IS NULL  AND vsc0.code=m.vid  ) ")
	    .append(" WHERE id_register= :idRegister AND v.status= :status ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerListMpsSJP: "+smd.toString());
        return smd;
    }
    
    /**
     * Регистър на издадените становища на земеделски производители, кандидатстващи по подмерки 4.1, 4.2, 5.1, 6.1, 6.3 от ПРСР в сектор „Фуражи“
     * Регистър на издадените становища за прилагане на подмярка 4.1 от ПРСР type=1 idRed=127
     * Регистър на издадените становища за прилагане на подмярка 4.2 от ПРСР type=2 idRed=128
     * Регистър на издадените становища за прилагане на подмярка 5.1 от ПРСР (animals) type=5 idRed=129
     * Регистър на издадените становища за прилагане на подмярка 6.1 от ПРСР type=3 idRed=130
     * Регистър на издадените становища за прилагане на подмярка 6.3 от ПРСР type=4 idRed= 131
     * @author yoni
     */
    private SelectMetadata registerProizvoditeliMiarka(int type) {
        LOGGER.debug("registerProizvoditeliMiarka()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=46;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" min(v.id)                          			as id_vpisvane , ")// --00
				.append(" min(v.reg_nom_result )                    			as nomer_licenz , ")// --01
				.append(" min(v.date_result)                        			as date_licenz, ")// --02
				.append(" min(v.registratura_id )                   			as registratura_id, ")// --03
				.append(" min(v.reg_nom_zaqvl_vpisvane )            			as reg_nom_zaqvl_vpisvane, ")//--04
				.append(" min(v.date_zaqvl_vpis )                   			as date_zaqvl_vpis, ")// --05
				.append(" min(od.reg_nom)                           			as nomer , ")// -- 06 Номер на обекта;
				.append(" (select TT.animals from (select  max(edfpj.event_deinost_furaji_id ) as deinID, STRING_AGG (COALESCE(vsc0.tekst,'Undefined code:'||672||':'||edfpj.vid_jiv ) ,';') as animals  ")
				.append(" from event_deinost_furaji_prednazn_jiv edfpj   ")
				.append(" left JOIN v_system_classif     vsc0 ON (  vsc0.code_classif=508 AND vsc0.date_do IS NULL  AND vsc0.code = edfpj.vid_jiv ) ")//
				.append(" where 1=1  group by edfpj.event_deinost_furaji_id ) TT where deinID= edf.id )    AS animals,  ")//-- 07 Животни, които ще се отглеждат – само за подмярка 5.1 - (много);
				 .append(" min(ar.ref_type )                                 as licenziant_type, ")//08
				 .append(" min(ar.ref_name )                        		 	as licenziant_name, ")// -- 09 Оператор на обекта (имена на ФЛ или наименование на ЮЛ);
				 .append(" min(coalesce(ar.nfl_eik, fzl_egn, fzl_lnc, ''))     as licenziant_egn, ")// -- 10 ЕИК (за ЮЛ);
				 .append(" min(od.naimenovanie )                               as obekt_name , ")// -- 11 Наименование на обекта;
				.append("  min(od.address )                                    as mesto, ")// -- 12  Местонахождение;
				 .append(" min(ea.oblast_ime )                                 as oblast_text, ")// -- 13 Област (на обекта)
				 .append(" min(ea.obstina_ime )                                as obshtina_text, ")// -- 14 Община (на обекта)
				 .append(" min(ea.ime   )                                      as ekatte_text, ")// -- 15 Населено място (на обекта).
				 .append(" min(v.info)                                                           as info ")//-16 забележка
				.append(" from vpisvane v ")//
				.append(" join event_deinost_furaji edf on edf.id_vpisvane =v.id and edf.miarka = :type ")//  --(1,2,5,3,4)  (127,128,129,130,131) classif Вид на подмярка от ПРСР (559)
				.append(" join obekt_deinost_deinost odd  on odd.deinost_id =edf.id and tabl_event_deinost =99  ")//--furaji
				.append(" join obekt_deinost od on od.id = odd.obekt_deinost_id  ")
				.append(" join adm_referents ar on  ar.code = v.code_ref_vpisvane  ")
				.append(" left join ekatte_att ea on ea.ekatte = od.nas_mesto ")
				//.append(" left join v_system_classif vsc4 on ( vsc4.code_classif = 22 and vsc4.lang=1 and vsc4.date_do is null and vsc4.code = 37 ) ")
				.append(" where id_register= :idRegister AND v.status= :status  group by edf.id  ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
		.append(" from vpisvane v  ")
		.append(" join event_deinost_furaji edf on edf.id_vpisvane =v.id and edf.miarka = :type ")//  --(1,2,5,3,4)  (127,128,129,130,131) classif Вид на подмярка от ПРСР (559)
		.append(" join obekt_deinost_deinost odd  on odd.deinost_id =edf.id and tabl_event_deinost =99  ")//--furaji
		.append(" join obekt_deinost od on od.id = odd.obekt_deinost_id  ")
		.append(" join adm_referents ar on  ar.code = v.code_ref_vpisvane  ")
		.append(" left join ekatte_att ea on ea.ekatte = od.nas_mesto ")
		//.append(" left join v_system_classif vsc4 on ( vsc4.code_classif = 22 and vsc4.lang=1 and vsc4.date_do is null and vsc4.code = 37 ) ")
		.append(" where id_register= :idRegister AND v.status= :status  ");// -- интересуват ни само тези със статус - 2 -
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        sqlParameters.put("type", type);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerProizvoditeliMiarka: "+smd.toString());
        return smd;
    }
    
    
    
    private SelectMetadata registerFurajiChlen_17_Al_3() {
        LOGGER.debug("registerFurajiChlen_17_Al_3()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=32;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" v.id                                           AS id_vpisvane , ")// --00 Номер по ред;
				.append(" od.reg_nom                                     AS regNom, ")// --01 Идентификационен номер на обекта;
				.append(" COALESCE(vsc0.tekst,'Undefined code:'||506||':'||edfv.vid), ")// --02 Вид дейност;
				.append(" ar.ref_type                                    AS licenziant_type, ")//--03 3 -firma,4-Lice
				.append(" ar.ref_name                                    AS licenziant_name, ")// --04 Оператор на обекта (имена на ФЛ или наименование на ЮЛ);
				.append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')        AS licenziant_egn, ")//-- 05 ЕИК (за ЮЛ);
				.append(" od.naimenovanie                                as obekt_name , ")// -- 06 Наименование на обекта;
				.append(" od.address                                     as mesto, ")// --07 Местонахождение;
				.append(" edf.broi_tehn_linii                            as broiLn, ")// --08 Брой технологични линии;
				.append(" v.reg_nom_result                               AS nomer_licenz , ")//-- 09Рег. номер на разрешителното;
				.append(" v.date_result                                  AS date_licenz, ")// --10 Дата на издаване;
				.append(" ea.oblast_ime                                  as oblast_text, ")// --11 Област (на обекта)
				.append(" ea.obstina_ime                                 as obshtina_text, ")// --12 Община (на обекта)
				.append(" ea.ime                                         as ekatte_text, ")//--13  Населено място (на обекта);
				.append(" v.registratura_id                              AS registratura_id, ")//-- 14 ОЕЗ регистририла лицензията            ,
				.append(" v.reg_nom_zaqvl_vpisvane                       AS reg_nom_zaqvl_vpisvane, ")//-- 15 рег. номер първоначално вписване            ,
				.append(" v.date_zaqvl_vpis                              AS date_zaqvl_vpis, ")// --16 дата първоначално вписване    
				.append(" v.info                                                           as info ")//-17 забележка
				.append(" FROM vpisvane                   v ")
				.append(" JOIN event_deinost_furaji edf        ON v.id = edf.id_vpisvane ")
				.append(" JOIN event_deinost_furaji_vid edfv   ON edfv.event_deinost_furaji_id = edf.id  ")
				.append(" LEFT JOIN obekt_deinost_deinost odd on odd.deinost_id=edf.id and odd.tabl_event_deinost=99   ")
				.append(" LEFT JOIN obekt_deinost od   on od.id=odd.obekt_deinost_id  ")
				.append(" LEFT JOIN adm_referents         ar   ON ar.code=v.code_ref_vpisvane ")
				.append(" LEFT JOIN ekatte_att ea on ea.ekatte = od.nas_mesto ")
				.append(" LEFT JOIN v_system_classif vsc0 ON ( vsc0.code_classif=506 AND vsc0.date_do IS null AND vsc0.code=edfv.vid) ")
				.append(" where id_register= :idRegister AND v.status= :status  ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
				.append(" FROM vpisvane                   v ")
				.append(" JOIN event_deinost_furaji edf        ON v.id = edf.id_vpisvane ")
				.append(" JOIN event_deinost_furaji_vid edfv   ON edfv.event_deinost_furaji_id = edf.id  ")
				.append(" LEFT JOIN obekt_deinost_deinost odd on odd.deinost_id=edf.id and odd.tabl_event_deinost=99   ")
				.append(" LEFT JOIN obekt_deinost od   on od.id=odd.obekt_deinost_id  ")
				.append(" LEFT JOIN adm_referents         ar   ON ar.code=v.code_ref_vpisvane ")
				.append(" LEFT JOIN ekatte_att ea on ea.ekatte = od.nas_mesto ")
				.append(" LEFT JOIN v_system_classif vsc0 ON ( vsc0.code_classif=506 AND vsc0.date_do IS null AND vsc0.code=edfv.vid) ")
				.append(" where id_register= :idRegister AND v.status= :status  ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerFurajiChlen_17_Al_3: "+smd.toString());
        return smd;
    }
    
    /**
     * Регистър на операторите, транспортиращи фуражи съгласно чл. чл. 17е, ал. 2 от Закона за фуражите 
     */
    private SelectMetadata registerFurajiOperatorTransportChlen_17() {
        LOGGER.debug("registerFurajiOperatorTransportChlen_17()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=33;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
                StringBuilder sql = new StringBuilder("SELECT ")
                                .append(" min(v.id )                                           AS id_vpisvane , ")//--00
                                .append(" min(v.reg_nom_result )                               AS nomer_licenz , ")//-- 01 Рег. номер на разрешителното;
                                .append(" min(v.date_result )                                  AS date_licenz, ")// --02 Дата на издаване;
                                .append(" min(v.registratura_id   )                            AS registratura_id, ")//-- 03 ОЕЗ регистририла лицензията            ,
                                .append(" min(v.reg_nom_zaqvl_vpisvane )                       AS reg_nom_zaqvl_vpisvane, ")//-- 04 рег. номер първоначално вписване            ,
                                .append(" min(v.date_zaqvl_vpis )                              AS date_zaqvl_vpis, ")// --05 дата първоначално вписване
                                .append(" min(ar.ref_type )                                    AS licenziant_type, ")//--06 3 -firma,4-Lice
                                .append(" min(ar.ref_name  )                                   AS licenziant_name, ")// --07 Оператор на обекта (имена на ФЛ или наименование на ЮЛ);
                                .append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'') )        AS licenziant_egn, ")//-- 08 ЕИК (за ЮЛ);
                                .append(" min(ea.oblast_ime )                                  as oblast_text, ")// --09 Област (на обекта)
                                .append(" min(ea.obstina_ime  )                                as obshtina_text, ")// --10 Община (на обекта)
                                .append(" min(ea.ime  )                                        as ekatte_text, ")//--11  Населено място (на обекта);
                                .append(" min(ara.addr_text )                                  as address, ")//--12 Улица/сграда (лицензиант);
                                .append(" (select TT.mpsInfo from ( select max(edf.id) as Fid ,  ")
                                .append(" STRING_AGG (m.model || ' '|| m.nomer || ' ,'  ||COALESCE(vsc.tekst,'Undefined code:'||550||':'||edfp.sastoianie ),';') as mpsInfo  ")
                                .append(" From event_deinost_furaji edf ")
                                .append(" left JOIN event_deinost_furaji_predmet edfp     ON edfp.event_deinost_furaji_id = edf.id ")
                                .append(" LEFT JOIN mps_deinost md on  md.deinost_id=edf.id  and   tabl_event_deinost=99 ")
                                .append(" join mps   m              on md.mps_id=m.id ")
                                .append(" left JOIN v_system_classif     vsc ON ( vsc.code_classif=550 AND vsc.date_do IS NULL AND vsc.code=edfp.sastoianie ) ")
                                .append(" where edf.id=edfp.event_deinost_furaji_id   group by edf.id ) as TT where Fid=edf.id) as mpsInfo,  ")//--13
                                .append(" min(v.info)                                                           as info ")//--14 забележка
                                .append(" from vpisvane v  ")
                                .append(" LEFT JOIN adm_referents         ar   ON ar.code=v.code_ref_vpisvane ")
                                .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
                                .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
                                .append(" JOIN event_deinost_furaji edf        ON v.id = edf.id_vpisvane ")
                                .append(" JOIN event_deinost_furaji_predmet edfp     ON edfp.event_deinost_furaji_id = edf.id ")
                                .append(" LEFT JOIN mps_deinost md on  md.deinost_id=edf.id  and   tabl_event_deinost=99 ")
                                .append(" LEFT JOIN obekt_deinost_deinost odd on odd.deinost_id=edf.id and odd.tabl_event_deinost=99   ")
                                .append(" LEFT JOIN obekt_deinost od   on od.id=odd.obekt_deinost_id and od.vid=2  ")//--classif 519 Тип на обекта на дейност
                                .append(" where id_register= :idRegister AND v.status= :status  group by edf.id");// -- интересуват ни само тези със статус - 2 -
                                                                                                                                                        // вписан";

                String sqlString= sql.toString();
                smd.setSql(sqlString);
                

                /* Формиране на заявката за броя резултати */
                StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
                                .append(" FROM vpisvane                   v ")
                                .append(" LEFT JOIN adm_referents         ar   ON ar.code=v.code_ref_vpisvane ")
                                .append(" JOIN adm_ref_addrs         ara  ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
                                .append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=ara.ekatte ")
                                .append(" JOIN event_deinost_furaji edf        ON v.id = edf.id_vpisvane ")
                                .append(" JOIN event_deinost_furaji_predmet edfp     ON edfp.event_deinost_furaji_id = edf.id ")
                                .append(" LEFT JOIN mps_deinost md on  md.deinost_id=edf.id  and   tabl_event_deinost=99 ")
                                .append(" LEFT JOIN obekt_deinost_deinost odd on odd.deinost_id=edf.id and odd.tabl_event_deinost=99   ")
                                .append(" LEFT JOIN obekt_deinost od   on od.id=odd.obekt_deinost_id and od.vid=2  ")//--classif 519 Тип на обекта на дейност
                                .append(" where id_register= :idRegister AND v.status= :status  ");
                String sqlCountString= sqlCountSB.toString();
                smd.setSqlCount(sqlCountString);

                /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerFurajiOperatorTransportChlen_17: "+smd.toString());
        return smd;
    }
    
    
    /**
     * Регистър на одобрените обекти за производство с медикаментозни фуражи
     * Регистър на одобрените обекти за търговия с медикаментозни фуражи
     * int type
     */
    private SelectMetadata registerMedFuraji() {
        LOGGER.debug("registerMedFuraji()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=34;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
                StringBuilder sql = new StringBuilder("SELECT ")
                		.append(" min(v.id   )                                                       AS id_vpisvane , ")//--00
                		.append(" min(od.reg_nom)                                                           as vet_num, ")//--01 Ветеринарен рег. номер на ОЕЗ;
                		.append(" min(od.naimenovanie     )                                                 AS OEZ_name, ")//--02 име на ОЕЗ
                		.append(" min(v.reg_nom_result  )                                                   AS nomer_licenz , ")//--03 Рег. номер на удостоверение за регистрация; 
                		.append(" min(v.date_result     )                                                   AS date_licenz, ")//-- 04 Дата на регистрация;
                		.append(" min(v.registratura_id    )                                                as registratura_id, ")//--05 ОЕЗ регистририла лицензията
                		.append(" min(ar.ref_type    )                                                      AS licenziant_type, ")//--06 3 -firma,4-Lice 
                		.append(" min(ar.ref_name      )                                                    AS licenziant_name, ")//--07 оператор
                		.append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
                		.append(" min(ea.oblast_ime   )                                                     AS oblast_text, ")//--09 Област (ОЕЗ);
                		.append(" min(ea.obstina_ime   )                                                    AS obshtina_text, ")//       --10 Община (ОЕЗ);
                		.append(" min(ea.ime    )                                                           AS ekatte_text,  ")//     --11 Населено място (ОЕЗ);
                		.append(" min(od.address    )                                                       as ulica, ")//--12 Улица/сграда (ОЕЗ);         
                		.append(" min(ara.ekatte  )                                                         AS ref_addr_ekatte, ")//--13 ekkate лицензиант;
                		.append(" min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")//--14 Област (лицензиант);
                		.append(" min(ea1.obstina_ime  )                                                    AS ref_obshtina_text, ")//--15 Община (лицензиант);       
                		.append(" min(ea1.ime   )                                                           AS ref_ekatte_text, ")//-- 16 Населено място (лицензиант);                          
                		.append(" min(ara.addr_text )                                                       AS ref_ulica, ")// --17 Улица/сграда (лицензиант);
                		.append(" min(edf.broi_tehn_linii )                                                 as br_linia, ")// --18 Брой технологични линии;
                		.append(" min(v.info)                                                               as info, ")// --19 забележка
                		.append(" (select TT.animalInfo from ( select edfpj.event_deinost_furaji_id as ttid , ") 
                		.append(" STRING_AGG(COALESCE(vsc3.tekst,'Undefined code:'||506||':'||edfpj.vid_jiv) || '('||edfpj.kolichestvo||')',';') as animalInfo  ")
                		.append(" from event_deinost_furaji_prednazn_jiv edfpj    ")
                		.append(" left JOIN v_system_classif     vsc3 ON ( vsc3.code_classif=508 AND vsc3.date_do IS NULL AND vsc3.code=edfpj.vid_jiv  )  ")
                		.append("  where  edfpj.event_deinost_furaji_id =  edf.id  group by edfpj.event_deinost_furaji_id ) as TT ), ")//   -- 20 видове животни
                		
                		.append(" (select TT1.deinInfo from ( select max(edfv.event_deinost_furaji_id)as ttid , ") 
        				.append(" STRING_AGG(COALESCE(vsc0.tekst,'Undefined code:'||506||':'||edfv.vid),';') as deinInfo ")  
						.append(" from event_deinost_furaji_vid      edfv    ") 
						.append(" left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=506 AND vsc0.date_do IS NULL AND vsc0.code=edfv.vid  )  ") 
						.append(" where  edfv.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT1 )  ") //  -- 18 видове дейност
                		.append(" from vpisvane v  ")
                		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id   ")//-- and vid= :type80 - tyrgowiq , 79 - proizwodstwo
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99  ")
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")//--classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
                         .append(" where id_register= :idRegister AND v.status= :status  group by edf.id");// -- интересуват ни само тези със статус - 2 -
                                                                                                                                                        // вписан";

                String sqlString= sql.toString();
                smd.setSql(sqlString);
                

                /* Формиране на заявката за броя резултати */
                StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
                		.append(" from vpisvane v  ")
                		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant  ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id   ")//--and vid= :type 80 - tyrgowiq , 79 - proizwodstwo
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99  ")
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")//--classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
                                .append(" where id_register= :idRegister AND v.status= :status  ");
                String sqlCountString= sqlCountSB.toString();
                smd.setSqlCount(sqlCountString);

                /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        //sqlParameters.put("type", type);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerMedFuraji: "+smd.toString());
        return smd;
    }
    
    /**
     * Регистър на издадените сертификати за добра практика
     */
    private SelectMetadata registerGoodPractices() {
        LOGGER.debug("registerGoodPractices()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=124;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append(" v.id                                                                 AS id_vpisvane , ")//--00
				.append(" v.reg_nom_result                                                     AS nomer_licenz , ")//--01 Рег. номер на удостоверение за регистрация; 
				.append(" v.date_result                                                        AS date_licenz, ")//-- 02 Дата на регистрация;
				.append(" v.registratura_id                                                    as registratura_id, ")//--03 ОЕЗ регистририла лицензията
				.append(" ar.ref_type                                                         AS licenziant_type, ")//--04 3 -firma,4-Lice 
				.append(" ar.ref_name                                                          AS licenziant_name, ")//--05 оператор
				.append(" COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')                              AS licenziant_egn, ")//--06 ЕИК (за ЮЛ);  
				.append(" ara.ekatte                                                           AS ref_addr_ekatte, ")//--07 ekkate лицензиант;
				.append(" ea1.oblast_ime                                                       AS ref_oblast_text, ")//--08 Област (лицензиант);
				.append(" ea1.obstina_ime                                                      AS ref_obshtina_text, ")//--09 Община (лицензиант);       
				.append(" ea1.ime                                                              AS ref_ekatte_text, ")//-- 10 Населено място (лицензиант);                          
				.append(" ara.addr_text                                                        AS ref_ulica, ")// --11 Улица/сграда (лицензиант);
				.append(" COALESCE(vsc1.tekst,'Undefined code:'||104||':'||edfs.sert_type  )   as sert, ")// --12
				.append(" edfs.language                                                        as lang, ")//--13
				.append(" COALESCE(vsc0.tekst,'Undefined code:'||22||':'||edfs.darj )          as country, ")// --14
				.append(" edfs.jivotni                                                         as animals, ")// --15
				.append(" edfs.vid_furaji                                                      as furaji, ")// --16
				.append(" edfs.targovia_eu                                                     as targovia_eu, ")// --17
				.append(" v.info                                                               as info ")// --18 забележка 
				.append(" from vpisvane v ")
				.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant ")
				.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
				.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane ")
				.append(" JOIN event_deinost_furaji_sert edfs  ON edfs.event_deinost_furaji_id=edf.id ")
				.append(" left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=22 AND vsc0.date_do IS NULL AND vsc0.code=edfs.darj and vsc0.lang=1 ) ")
				.append(" left JOIN v_system_classif     vsc1 ON ( vsc1.code_classif=104 AND vsc1.date_do IS NULL AND vsc1.code=edfs.sert_type )  ")
				.append(" where id_register= :idRegister AND v.status= :status  ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
				.append(" from vpisvane v ")
				.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant ")
				.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
				.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane ")
				.append(" JOIN event_deinost_furaji_sert edfs  ON edfs.event_deinost_furaji_id=edf.id ")
				.append(" left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=22 AND vsc0.date_do IS NULL AND vsc0.code=edfs.darj and vsc0.lang=1 ) ")
				.append(" left JOIN v_system_classif     vsc1 ON ( vsc1.code_classif=104 AND vsc1.date_do IS NULL AND vsc1.code=edfs.sert_type )  ")
				.append(" where id_register= :idRegister AND v.status= :status  ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerGoodPractices: "+smd.toString());
        return smd;
    }
    
    
    
    /**
     * Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти
     */
    private SelectMetadata registerObjectsSJP() {
        LOGGER.debug("registerObjectsSJP()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=44;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
		StringBuilder sql = new StringBuilder("SELECT ")
				.append("min(v.id)                                                                 AS id_vpisvane , ")//--00
				.append("min(od.reg_nom)                                                           as vet_num, ")//--01 Ветеринарен рег. номер на ОЕЗ;
				.append("min(od.naimenovanie     )                                                 AS OEZ_name, ")//--02 име на ОЕЗ
				.append("min(v.reg_nom_result)                                                     AS nomer_licenz , ")//--03 Рег. номер на удостоверение за регистрация; 
				.append("min(v.date_result)                                                        AS date_licenz, ")//-- 04 Дата на регистрация;
				.append("min(v.registratura_id )                                                   as registratura_id, ")//--05 ОЕЗ регистририла лицензията
				.append("min(ar.ref_type )                                                        AS licenziant_type, ")//--06 3 -firma,4-Lice 
				.append("min(ar.ref_name)                                                          AS licenziant_name, ")//--07 оператор
				.append("min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,''))                              AS licenziant_egn, ")//--08 ЕИК (за ЮЛ);
				.append("min(ea.oblast_ime   )                                                     AS oblast_text, ")//--09 Област (ОЕЗ);
				.append("min(ea.obstina_ime   )                                                    AS obshtina_text, ")//       --10 Община (ОЕЗ);
				.append("min(ea.ime    )                                                           AS ekatte_text, ")//      --11 Населено място (ОЕЗ);
				.append("min(od.address    )                                                       as ulica, ")//--12 Улица/сграда (ОЕЗ);         
				.append("min(ara.ekatte)                                                           AS ref_addr_ekatte, ")//--13 ekkate лицензиант;
				.append("min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")//--14 Област (лицензиант);
				.append("min(ea1.obstina_ime)                                                      AS ref_obshtina_text, ")//--15 Община (лицензиант);       
				.append("min(ea1.ime   )                                                           AS ref_ekatte_text, ")//-- 16 Населено място (лицензиант);                          
				.append("min(ara.addr_text )                                                       AS ref_ulica,  ")//--17 Улица/сграда (лицензиант);
				.append("(select TT.deinInfo from ( select max(edfv.event_deinost_furaji_id)as ttid ,  ")
				 .append("STRING_AGG(COALESCE(vsc0.tekst,'Undefined code:'||506||':'||edfv.vid),';') as deinInfo  ")
				.append("from event_deinost_furaji_vid      edfv    ")
				.append("left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=506 AND vsc0.date_do IS NULL AND vsc0.code=edfv.vid  )  ")
				.append("where  edfv.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT ),  ")//  -- 18 видове дейност
				.append("(select TT1.kategoriaInfo from ( select max(edfk.event_deinost_furaji_id)as ttid ,  ")
				 .append("STRING_AGG(COALESCE(vsc1.tekst,'Undefined code:'||527||':'||edfk.kategoria),';') as kategoriaInfo  ")
				.append("from event_deinost_furaji_kategoria edfk    ")
				.append("left JOIN v_system_classif     vsc1 ON ( vsc1.code_classif=527 AND vsc1.date_do IS NULL AND vsc1.code=edfk.kategoria  )  ")
				.append("where  edfk.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT1 ), ")//   -- 19 видове категория
				.append("(select TT2.furajiInfo from ( select max(edfp.event_deinost_furaji_id)as ttid ,  ")
				 .append("STRING_AGG(COALESCE(vsc2.tekst,'Undefined code:'||531||':'||edfp.vid),';') as furajiInfo  ")
				.append("from event_deinost_furaji_predmet edfp   ")
				.append("left JOIN v_system_classif     vsc2 ON ( vsc2.code_classif=531 AND vsc2.date_do IS NULL AND vsc2.code=edfp.vid  )  ")
				.append("where  edfp.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT2 ), ")//   -- 20 видове фуражи
				.append("min(v.info )                                                                as info  ")//--21 забележка 
				.append("from vpisvane v ")
				.append("JOIN adm_referents ar   ON ar.code=v.id_licenziant ")
				.append("left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
				.append("LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				.append("JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane ")
				.append("LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99 ")
				.append("LEFT OUTER JOIN obekt_deinost od  ON odd.obekt_deinost_id = od.id ")
				.append("LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
				.append(" where id_register= :idRegister AND v.status= :status  group by edf.id ");// -- интересуват ни само тези със статус - 2 -
																			// вписан";

		String sqlString= sql.toString();
		smd.setSql(sqlString);
		

		/* Формиране на заявката за броя резултати */
		StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
				.append(" from vpisvane v ")
				.append("JOIN adm_referents ar   ON ar.code=v.id_licenziant ")
				.append("left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1  ")//--1-za  korespondenciq,2-postoqnen
				.append("LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte ")
				.append("JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane ")
				.append("LEFT OUTER JOIN obekt_deinost_deinost odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99 ")
				.append("LEFT OUTER JOIN obekt_deinost od  ON odd.obekt_deinost_id = od.id ")
				.append("LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto  ")
				.append(" where id_register= :idRegister AND v.status= :status  ");
		String sqlCountString= sqlCountSB.toString();
		smd.setSqlCount(sqlCountString);

		/* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerObjectsSJP: "+smd.toString());
        return smd;
    }
    
    
    
    /**
     *  Списък на предприятия, одобрени за износ на царевица за Китай 37
	*	 Списък на предприятия, одобрени за износ на люцерна за Китай 38 
	*	 Списък на предприятия, одобрени за износ на комбинирани фуражи за Китай 39
	*	 Списък на обектите за производства на фуражни добавки, одобрени за износ за Китай 40
	*	 Списък на предприятия за преработка, одобрени за износ на слънчогледов шрот за Китай 41
	*	 Списък на предприятия за преработка, одобрени за износ на български сух зърнен спиртоварен остатък с извлеци за Китай 42
	*	 Регистър на операторите с издаден ветеринарен сертификат за износ на фуражи за трети държави по чл. 53з, ал. 1 от ЗФ 126
     */
    private SelectMetadata registerThirdCountries(Integer type, Integer typeFuraj) {
        LOGGER.debug("registerThirdCountries()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=123;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
                StringBuilder sql = new StringBuilder("SELECT ")
                		.append(" min(v.id   )                                                       AS id_vpisvane , ")// --00
                		.append(" min(od.reg_nom)                                                           as vet_num, ")// --01 Ветеринарен рег. номер на ОЕЗ;
                		.append(" min(od.naimenovanie     )                                                 AS OEZ_name, ")// --02 име на ОЕЗ
                		.append(" min(v.reg_nom_result  )                                                   AS nomer_licenz , ")// --03 Рег. номер на удостоверение за регистрация; 
                		.append(" min(v.date_result     )                                                   AS date_licenz, ")// -- 04 Дата на регистрация;
                		.append(" min(v.registratura_id)                                                    as registratura_id, ")// --05 ОЕЗ регистририла лицензията
                		.append(" min(COALESCE(vsc0.tekst,'Undefined code:'||22||':'||edf.darj)    )                                                         as country, ")//06  --държава
                		.append(" min(ar.ref_type    )                                                      AS licenziant_type, ")// --07 3 -firma,4-Lice 
                		.append(" min(ar.ref_name      )                                                    AS licenziant_name, ")// --08 оператор
                		.append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")// --09 ЕИК (за ЮЛ);
                		.append(" min(ea.oblast_ime   )                                                     AS oblast_text, ")// --10 Област (ОЕЗ);
                		.append(" min(ea.obstina_ime   )                                                    AS obshtina_text, ")//        --11 Община (ОЕЗ);
                		.append(" min(ea.ime    )                                                           AS ekatte_text, ")//       --12 Населено място (ОЕЗ);
                		.append(" min(od.address    )                                                       as ulica, ")// --13 Улица/сграда (ОЕЗ);         
                		.append(" min(ara.ekatte  )                                                         AS ref_addr_ekatte, ")// --14 ekkate лицензиант;
                		.append(" min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")// --15 Област (лицензиант);
                		.append(" min(ea1.obstina_ime  )                                                    AS ref_obshtina_text, ")// --16 Община (лицензиант);       
                		.append(" min(ea1.ime   )                                                           AS ref_ekatte_text, ")// -- 17 Населено място (лицензиант);                          
                		.append(" min(ara.addr_text )                                                       AS ref_ulica, ")//  --18 Улица/сграда (лицензиант);
                		.append(" min(v.info)                                                               as info, ")//  --19 забележка
                		.append(" (select TT2.furajiInfo from ( select max(edfp.event_deinost_furaji_id)as ttid , ")
                		.append("  STRING_AGG(COALESCE(vsc2.tekst,'Undefined code:'||531||':'||edfp.vid),';') as furajiInfo  ")
                		.append(" from event_deinost_furaji_predmet edfp   ")
                		.append(" left JOIN v_system_classif     vsc2 ON ( vsc2.code_classif=531 AND vsc2.date_do IS NULL AND vsc2.code=edfp.vid  )  ")
                		.append(" where  edfp.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT2 )  ")//   -- 20 видове furaji
                		.append(" from vpisvane v    ")
                		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant    ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" left JOIN v_system_classif     vsc0 ON ( vsc0.code_classif=22 AND vsc0.date_do IS null  AND vsc0.code=edf.darj and lang=1) ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id and vid= :type  ")// -- 63 - Китай , 103 - 53з ал1
                		.append(" join event_deinost_furaji_predmet edfp  on edfp.event_deinost_furaji_id=edf.id  ")
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99   ") 
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")// --classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto    ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1   ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte   ")
                        .append(" where id_register= :idRegister AND v.status= :status ");
                        if(typeFuraj != null) {
                   	      sql.append("    AND edfp.vid= :typeFuraj ");//--classif 533 вид-а на фуража
                   	    }
                        sql.append(" group by edf.id ");// -- интересуват ни само тези със статус - 2 -
                                                                                                                                                        // вписан";

                String sqlString= sql.toString();
                smd.setSql(sqlString);
                

                /* Формиране на заявката за броя резултати */
                StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
                		.append(" from vpisvane v  ")
                   		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant    ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id and vid= :type  ")// --  63 - Китай , 103 - 53з ал1
                		.append(" join event_deinost_furaji_predmet edfp  on edfp.event_deinost_furaji_id=edf.id  ")
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99   ") 
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")// --classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto    ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1   ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte   ")
                        .append(" where id_register= :idRegister AND v.status= :status  ");
		                if(typeFuraj != null) {
		                	sqlCountSB.append("  AND  edfp.vid= :typeFuraj ");//--classif 533 вид-а на фуража
		             	    }
                String sqlCountString= sqlCountSB.toString();
                smd.setSqlCount(sqlCountString);

                /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        sqlParameters.put("type", type);
        if(typeFuraj != null) {
        	 sqlParameters.put("typeFuraj", typeFuraj);
        	
        }

        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerThirdCountries: "+smd.toString());
        return smd;
    }
    
    /**
     *  Регистър на оператори, внасящи фуражи от трети държави по чл. 9 от Закона за фуражите (type=39)
     *  Регистър на оператори, внасящи фуражи от трети държави по чл. 10 от Закона за фуражите (type=81)
     */
    private SelectMetadata registerImportThirdCountries(Integer type) {
        LOGGER.debug("registerImportThirdCountries()");
        SelectMetadata smd=new SelectMetadata();
        Integer idRegister=125;
        Integer status=BabhConstants.STATUS_VP_VPISAN;
        //noinspection SpellCheckingInspection
                StringBuilder sql = new StringBuilder("SELECT ")
                		.append(" min(v.id   )                                                       AS id_vpisvane , ")// --00
                		.append(" min(od.reg_nom)                                                           as vet_num, ")// --01 Ветеринарен рег. номер на ОЕЗ;
                		.append(" min(od.naimenovanie     )                                                 AS OEZ_name, ")// --02 име на ОЕЗ
                		.append(" min(v.reg_nom_result  )                                                   AS nomer_licenz , ")// --03 Рег. номер на удостоверение за регистрация; 
                		.append(" min(v.date_result     )                                                   AS date_licenz, ")// -- 04 Дата на регистрация;
                		.append(" min(v.registratura_id)                                                    as registratura_id, ")// --05 ОЕЗ регистририла лицензията
                		.append(" min(ar.ref_type    )                                                      AS licenziant_type, ")// --06 3 -firma,4-Lice 
                		.append(" min(ar.ref_name      )                                                    AS licenziant_name, ")// --07 оператор
                		.append(" min(COALESCE(ar.nfl_eik,fzl_egn,fzl_lnc,'')     )                         AS licenziant_egn, ")// --08 ЕИК (за ЮЛ);
                		.append(" min(ea.oblast_ime   )                                                     AS oblast_text, ")// --09 Област (ОЕЗ);
                		.append(" min(ea.obstina_ime   )                                                    AS obshtina_text, ")//        --10 Община (ОЕЗ);
                		.append(" min(ea.ime    )                                                           AS ekatte_text, ")//       --11 Населено място (ОЕЗ);
                		.append(" min(od.address    )                                                       as ulica, ")// --12 Улица/сграда (ОЕЗ);         
                		.append(" min(ara.ekatte  )                                                         AS ref_addr_ekatte, ")// --13 ekkate лицензиант;
                		.append(" min(ea1.oblast_ime )                                                      AS ref_oblast_text, ")// --14 Област (лицензиант);
                		.append(" min(ea1.obstina_ime  )                                                    AS ref_obshtina_text, ")// --15 Община (лицензиант);       
                		.append(" min(ea1.ime   )                                                           AS ref_ekatte_text, ")// -- 16 Населено място (лицензиант);                          
                		.append(" min(ara.addr_text )                                                       AS ref_ulica, ")//  --17 Улица/сграда (лицензиант);
                		.append(" min(v.info)                                                               as info, ")//  --18 забележка
                		.append(" (select TT2.furajiInfo from ( select max(edfp.event_deinost_furaji_id)as ttid , ")
                		.append("  STRING_AGG(COALESCE(vsc2.tekst,'Undefined code:'||531||':'||edfp.vid) ||' - '||edfp.kolichestvo ||' - '||edfp.dop_info,';') as furajiInfo   ")
                		.append("  from event_deinost_furaji_predmet edfp   ")
                		.append("  left JOIN v_system_classif     vsc2 ON ( vsc2.code_classif=531 AND vsc2.date_do IS NULL AND vsc2.code=edfp.vid  )   ")
                		.append("  where  edfp.event_deinost_furaji_id =  edf.id  group by edf.id ) as TT2 )  ")//   -- 20 видове furaji
                		.append(" from vpisvane v    ")
                		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant    ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id and vid= :type  ")// -- 39 - чл.9 , 81 - чл.10 
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99   ") 
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")// --classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto    ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1   ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte   ")
                        .append(" where id_register= :idRegister AND v.status= :status  group by edf.id ");// -- интересуват ни само тези със статус - 2 -
                                                                                                                                                        // вписан";

                String sqlString= sql.toString();
                smd.setSql(sqlString);
                

                /* Формиране на заявката за броя резултати */
                StringBuilder sqlCountSB= new StringBuilder("select count( distinct v.id) ")
                		.append(" from vpisvane v  ")
                   		.append(" JOIN adm_referents ar   ON ar.code=v.id_licenziant    ")
                		.append(" JOIN event_deinost_furaji     edf ON v.id=edf.id_vpisvane   ")
                		.append(" join event_deinost_furaji_vid edfv on  edfv.event_deinost_furaji_id=edf.id and vid= :type  ")// --   39 - чл.9 , 81 - чл.10 
                		.append(" JOIN obekt_deinost_deinost   odd ON odd.deinost_id=edf.id AND odd.tabl_event_deinost = 99   ") 
                		.append(" JOIN obekt_deinost               od  ON odd.obekt_deinost_id = od.id and od.vid=2  ")// --classif 519 Тип на обекта на дейност
                		.append(" LEFT JOIN ekatte_att       ea   ON ea.ekatte=od.nas_mesto    ")
                		.append(" left join adm_ref_addrs ara          ON ar.code=ara.code_ref AND ara.addr_type=1   ")//--1-za  korespondenciq,2-postoqnen
                		.append(" LEFT JOIN ekatte_att       ea1   ON ea1.ekatte=ara.ekatte   ")
                        .append(" where id_register= :idRegister AND v.status= :status  ");

                String sqlCountString= sqlCountSB.toString();
                smd.setSqlCount(sqlCountString);

                /* Слагаме параметрите */
        Map<String, Object> sqlParameters=new HashMap<>();
        sqlParameters.put("idRegister", idRegister);
        sqlParameters.put("status", status);
        sqlParameters.put("type", type);

        smd.setSqlParameters(sqlParameters);
        
        System.out.println("SMD registerImportThirdCountries: "+smd.toString());
        return smd;
    }
    
}
