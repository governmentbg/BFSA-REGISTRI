package bg.bulsi.bfsa.util;

import bg.bulsi.bfsa.enums.ServiceType;

import java.util.List;

public class Constants {

    public static final String BG_CODE = "BG";

    public static final String BFSA_SERVICE_SUPPLIER_ID = "1326";
    public static final String APPLICATION_JSON_METADATA_KEY = "application.json_json";
    public static final String APPLICATION_XML_METADATA_KEY = "application.json_xml";

    public static final String TOKEN_INVALID = "INVALID";
    public static final String TOKEN_EXPIRED = "EXPIRED";
    public static final String TOKEN_VALID = "VALID";
    public final static String SUCCESS = "success";


    // --- NOMENCLATURES ---
    public static final String DOCUMENT_TYPE_CODE = "01500";
    public static final String DOCUMENT_TYPE_ORDER_CODE = "01501";
    public static final String DOCUMENT_TYPE_PROTOCOL_CODE = "01502";
    public static final String DOCUMENT_TYPE_CERTIFICATE_CODE = "01503";
    public static final String DOCUMENT_TYPE_AUTHORIZATION_CODE = "01504";
    public static final String DOCUMENT_TYPE_DIPLOMA_CODE = "01505";
    public static final String DOCUMENT_TYPE_APPLICATION_CODE = "01506";
    public static final String DOCUMENT_TYPE_APPLICATION_ATTACHMENT_CODE = "01507";


    public static final String REQUESTOR_AUTHOR_TYPE_CODE = "01300";
    public static final String REQUESTOR_AUTHOR_TYPE_HOLDER_CODE = "01301";
    public static final String REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE = "1006-020001";


    //    --- ACTIVITY TYPE ---
    public static final String ACTIVITY_TYPE_CODE = "01800";
    public static final String ACTIVITY_TYPE_WHOLESALE_CODE = "01805";
    public static final String ACTIVITY_TYPE_FOOD_WHOLESALE_CODE = "01807";
    public static final String ACTIVITY_TYPE_MILK_COLLECTION_CENTER_CODE = "01808";
    public static final String ACTIVITY_TYPE_NON_ANIMAL_FOOD_CODE = "01814";
    public static final String ACTIVITY_OUTSIDE_REGULATION_853_CODE = "01815";
    public static final String ACTIVITY_PUBLIC_CATERING_CODE = "01817";


    // Добив на първични продукти от неживотински произход - ЗЕМЕДЕЛСКИ
    public static final String ACTIVITY_TYPE_PRIMARY_PRODUCT_NON_ANIMAL_AGRICULTURAL_CODE = "01820";

    // Добив на първични продукти от неживотински произход - ДИВОРАСТЯЩИ
    public static final String ACTIVITY_TYPE_PRIMARY_PRODUCT_NON_ANIMAL_WILD_CODE = "01821";

    // Добив на първични продукти от животински произход - ФЕРМЕРСКИ
    public static final String ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_FARM_CODE = "01822";

    // Добив на първични продукти от животински произход - чрез ЛОВ
    public static final String ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_HUNTING_CODE = "01823";

    // Добив на първични продукти от животински произход - чрез УЛОВ
    public static final String ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_CATCH_CODE = "01824";

    // Обработка и/или преработка на храни от животински произход
    public static final String ACTIVITY_TYPE_FOOD_ANIMAL_CODE = "01825";

    // Обработка и/или преработка на храни от неживотински произход
    public static final String ACTIVITY_TYPE_FOOD_NON_ANIMAL_CODE = "01826";

    public static final String SUB_ACTIVITY_TYPE_CODE = "01100";
    public static final String SUB_ACTIVITY_TYPE_WAREHOUSE_CODE = "01101";
    public static final String SUB_ACTIVITY_TYPE_CUSTOMS_WAREHOUSE_CODE = "01102";

    public static final String FACILITY_TYPE_CODE = "02000";
    public static final String FACILITY_TYPE_PRODUCTION_CODE = "02001";
    public static final String FACILITY_TYPE_AGRICULTURAL_PHARMACY_CODE = "02008";


    //    --- CLASSIFIERS ---
    /**
     * Дирекция Контрол на храните
     */
    public static final String FOOD_DIRECTORATE_CODE = "0002001";

    /**
     * Дирекция Продукти за растителна защита, торове и контрол
     */
    public static final String PPP_DIRECTORATE_CODE = "0002002";

    /**
     * Дирекция Фитосанитарен Контрол
     */
    public static final String PHYTO_DIRECTORATE_CODE = "0002003";

    /**
     * Списък услуги към Дирекция Фитосанитарен контрол.
     */
    public static final List<String> PHYTO_DIRECTORATE_SERVICE_TYPES = List.of(
            ServiceType.S2700.name(),
            ServiceType.S2702.name(),
            ServiceType.S3201.name(),
            ServiceType.S3363.name(),
            ServiceType.S3365.name()
    );

    /**
     * Списък услуги към Дирекция растителна защита, торове и контрол.
     */
    public static final List<String> PPP_DIRECTORATE_SERVICE_TYPES = List.of(
            ServiceType.S2274.name(),
            ServiceType.S502.name(),
            ServiceType.S503.name(),
            ServiceType.S1590.name(),
            ServiceType.S2695.name(),
            ServiceType.S2698.name(),
            ServiceType.S2699.name(),
            ServiceType.S2170.name(),
            ServiceType.S2701.name(),
            ServiceType.S2711.name(),
            ServiceType.S3362.name()
    );

    /**
     * Списък услуги към Дирекция Контрол на храните.
     */
    public static final List<String> FOOD_DIRECTORATE_SERVICE_TYPES = List.of(
            ServiceType.S769.name(),
            ServiceType.S1199.name(),
            ServiceType.S1811.name(),
            ServiceType.S2272.name(),
            ServiceType.S2869.name(),
            ServiceType.S3125.name(),
            ServiceType.S3180.name(),
            ServiceType.S3182.name()
    );

    /**
     * S3180: Регистър на одобрените обекти за производство, преработка и/или
     * дистрибуция на храни от животински произход
     **/
    public static final String CLASSIFIER_REGISTER_0002004_CODE = "0002004";
    public static final String CLASSIFIER_REGISTER_0002004_NAME = "Регистър на одобрените обекти за производство," +
            " преработка и/или дистрибуция на храни от животински произход";

    /**
     * S3180: Млекосъбирателни пунктове и помещения за съхранение на мляко
     **/
    public static final String CLASSIFIER_REGISTER_0002005_CODE = "0002005";
    public static final String CLASSIFIER_REGISTER_0002005_NAME = "Млекосъбирателни пунктове и помещения за съхранение на мляко";

    /**
     * S7693
     * Списък на одобрените предприятия за храни от животински произход, които не са обект на Приложение ІІІ на Регламент 853/2004/ЕЕС
     **/
    public static final String CLASSIFIER_REGISTER_0002006_CODE = "0002006";
    public static final String CLASSIFIER_REGISTER_0002006_NAME = "Списък на одобрените предприятия за храни от" +
            " животински произход, които не са обект на Приложение ІІІ на Регламент 853/2004/ЕЕС";

    /**
     * S1811: Митнически складове
     **/
    public static final String CLASSIFIER_REGISTER_0002007_CODE = "0002007";
    public static final String CLASSIFIER_REGISTER_0002007_NAME = "Митнически складове";


    /**
     * S7691
     * Първични продукти от неживотински произход
     **/
    public static final String CLASSIFIER_REGISTER_0002013_CODE = "0002013";
    public static final String CLASSIFIER_REGISTER_0002013_NAME = "Първични продукти от неживотински произход";

    /**
     * S7691
     * Първични продукти от животински произход
     **/
    public static final String CLASSIFIER_REGISTER_0002014_CODE = "0002014";
    public static final String CLASSIFIER_REGISTER_0002014_NAME = "Първични продукти от животински произход";

    /**
     * S7691
     * Първични продукти от неживотински произход
     **/
    public static final String CLASSIFIER_REGISTER_0002015_CODE = "0002015";
    public static final String CLASSIFIER_REGISTER_0002015_NAME = "Риболовни съдове за улов";


    /**
     * S7692
     * Регистър на обекти за търговия на дребно (ТО)
     **/
    public static final String CLASSIFIER_REGISTER_0002010_CODE = "0002010";
    public static final String CLASSIFIER_REGISTER_0002010_NAME = "Регистър на обекти за търговия на дребно (ТО)";

    /**
     * S7692
     * Регистър на обекти за обществено хранене (ОН)
     **/
    public static final String CLASSIFIER_REGISTER_0002011_CODE = "0002011";
    public static final String CLASSIFIER_REGISTER_0002011_NAME = "Регистър на обекти за обществено хранене (ОН)";

    /**
     * S7693
     * Регистър на регистрирани обекти за производство, преработка и/или дистрибуция на храни от неживотински произход
     **/
    public static final String CLASSIFIER_REGISTER_0002008_CODE = "0002008";
    public static final String CLASSIFIER_REGISTER_0002008_NAME = "Регистър на регистрирани обекти за производство," +
            " преработка и/или дистрибуция на храни от неживотински произход";

    /**
     * S7694
     * Регистрирани дейности по дистрибуция на храни
     **/
    public static final String CLASSIFIER_REGISTER_0002009_CODE = "0002009";
    public static final String CLASSIFIER_REGISTER_0002009_NAME = "Регистрирани дейности по дистрибуция на храни";

    /**
     * S7695
     * Наематели - търговци
     **/
    public static final String CLASSIFIER_REGISTER_0002012_CODE = "0002012";
    public static final String CLASSIFIER_REGISTER_0002012_NAME = "Наематели - търговци";

    /**
     * S7696
     * Регистър на подвижни/временни обекти
     **/
    public static final String CLASSIFIER_REGISTER_0002046_CODE = "0002046";
    public static final String CLASSIFIER_REGISTER_0002046_NAME = "Наематели - търговци";

    /**
     * S3182: Регистър на обекти за производство и търговия на едро с материали и предмети,
     * предназначени за контакт с храни
     */
    public static final String CLASSIFIER_REGISTER_0002016_CODE = "0002016";
    public static final String CLASSIFIER_REGISTER_0002016_NAME = "Регистър на обекти за производство и търговия " +
            "на едро с материали и предмети предназначени за контакт с храни";

    /**
     * С3125: Регистър на хранителните добавки и храните, предназначени за употреба при
     * интензивно мускулно натоварване
     **/
    public static final String CLASSIFIER_REGISTER_0002017_CODE = "0002017";
    public static final String CLASSIFIER_REGISTER_0002017_NAME = "Регистър на хранителните добавки и храните," +
            " предназначени за употреба при интензивно мускулно натоварване";

    /**
     * S2272: Национален регистър на моторните превозни средства за транспорт на храни
     **/
    public static final String CLASSIFIER_REGISTER_0002018_CODE = "0002018";
    public static final String CLASSIFIER_REGISTER_0002018_NAME = "Национален регистър на моторните превозни" +
            " средства за транспорт на храни";

    /**
     * S3181: Регистър на бизнес операторите, които извършват търговия с храни от разстояние
     **/
    public static final String CLASSIFIER_REGISTER_0002019_CODE = "0002019";
    public static final String CLASSIFIER_REGISTER_0002019_NAME = "Регистър на бизнес операторите, които извършват" +
            " търговия с храни от разстояние";

    /**
     * С1366: Регистър на здравни сертификати за износ на суровини и храни от неживотински произход,
     * материали и предмети за контакт с храни
     **/
    public static final String CLASSIFIER_REGISTER_0002020_CODE = "0002020";
    public static final String CLASSIFIER_REGISTER_0002020_NAME = "Регистър на здравни сертификати за износ на" +
            " суровини и храни от неживотински произход, материали и предмети за контакт с храни";

    /**
     * С1366: Регистър на ветеринарни сертификати и документи за износ на суровини и храни от животински произход,
     * предназначени за консумация от хора, от страна на оператори в областта на храните
     **/
    public static final String CLASSIFIER_REGISTER_0002021_CODE = "0002021";
    public static final String CLASSIFIER_REGISTER_0002021_NAME = "Регистър на ветеринарни сертификати и документи" +
            " за износ на суровини и храни от животински произход, предназначени за консумация от хора, от страна" +
            " на оператори в областта на храните";

    /**
     * 1199: Регистър на лицата които получават пратки храни от животински произход от ЕС
     */
    public static final String CLASSIFIER_REGISTER_0002022_CODE = "0002022";
    public static final String CLASSIFIER_REGISTER_0002022_NAME = "Регистър на лицата които получават пратки храни от" +
            " животински произход от ЕС";

    /**
     * S2869: Регистър на издадените разрешения за оператори на хранителни банки
     */
    public static final String CLASSIFIER_REGISTER_0002023_CODE = "0002023";
    public static final String CLASSIFIER_REGISTER_0002023_NAME = "Регистър на издадените разрешения за оператори на хранителни банки";

    /**
     * S2274: Национален електронен регистър на продуктите за растителна защита,
     * за които е издадено разрешение за паралелна търговия
     **/
    public static final String CLASSIFIER_REGISTER_0002027_CODE = "0002027";

    /**
     * С3362: Национален електронен регистър на продуктите за растителна защита, които са преопаковани
     **/
    public static final String CLASSIFIER_REGISTER_0002028_CODE = "0002028";

    public static final String CLASSIFIER_REGISTER_0002027_NAME = "Национален електронен регистър на продуктите" +
            " за растителна защита, за които е издадено разрешение за паралелна търговия";

    /**
     * S502: Регистър на официалните бази на БАБХ и одобрените бази на физически и юридически лица
     * за биологично изпитване на продукти за растителна защита
     **/
    public static final String CLASSIFIER_REGISTER_0002029_CODE = "0002029";
    public static final String CLASSIFIER_REGISTER_0002029_NAME = "Регистър на официалните бази на БАБХ и одобрените" +
            " бази на физически и юридически лица за биологично изпитване на продукти за растителна защита";

    /**
     * S2170: Национален електронен регистър на регистрираните торове, подобрители на почвата, биологично
     * активни вещества и хранителни субстрати, за които е издадено удостоверение за пускане на пазара и употреба
     **/
    public static final String CLASSIFIER_REGISTER_0002030_CODE = "0002030";
    public static final String CLASSIFIER_REGISTER_0002030_NAME = "Национален електронен регистър на регистрираните" +
            " торове, подобрители на почвата, биологично активни вещества и хранителни субстрати, за които е издадено" +
            " удостоверение за пускане на пазара и употреба";

    /**
     * S2698: Национален електронен регистър на адювантите, които се пускат на пазара и се употребяват
     */
    public static final String CLASSIFIER_REGISTER_0002031_CODE = "0002031";
    public static final String CLASSIFIER_REGISTER_0002031_NAME = "Национален електронен регистър на адювантите," +
            " които се пускат на пазара и се употребяват";

    /**
     * S2711: Национален електронен регистър на лицата, които притежават удостоверение за търговия
     * с продукти за растителна защита, и на съответните обекти за търговия с продукти за растителна защита
     */
    public static final String CLASSIFIER_REGISTER_0002032_CODE = "0002032";
    public static final String CLASSIFIER_REGISTER_0002032_NAME = "Национален електронен регистър на лицата, които" +
            " притежават удостоверение за търговия с продукти за растителна защита, и на съответните обекти за" +
            " търговия с продукти за растителна защита";

    /**
     * S503: Национален електронен регистър на лицата, които притежават
     * удостоверение за преопаковане на продукти за растителна защита,
     * и на съответните обекти за преопаковане на продукти за растителна защита
     **/
    public static final String CLASSIFIER_REGISTER_0002033_CODE = "0002033";
    public static final String CLASSIFIER_REGISTER_0002033_NAME = "Национален електронен регистър на лицата," +
            " които притежават удостоверение за преопаковане на продукти за растителна защита, и на съответните обекти" +
            " за преопаковане на продукти за растителна защита";

    /**
     * S2697: Регистър на лицата, които притежават удостоверение за внос или въвеждане
     * на партида от неодобрени активни вещества.
     */
    public static final String CLASSIFIER_REGISTER_0002035_CODE = "0002035";
    public static final String CLASSIFIER_REGISTER_0002035_NAME = "Регистър на лицата, които притежават удостоверение" +
            " за внос или въвеждане на партида от неодобрени активни вещества";

    /**
     * S2695: Национален електронен регистър на издадените разрешения за прилагане на продукти
     * за растителна защита чрез въздушно пръскане
     */
    public static final String CLASSIFIER_REGISTER_0002036_CODE = "0002036";
    public static final String CLASSIFIER_REGISTER_0002036_NAME = "Национален електронен регистър на издадените " +
            "разрешения за прилагане на продукти за растителна защита чрез въздушно пръскане";

    /**
     * S1590: Национален електронен регистър на лицата, които извършват специализирани
     * растителнозащитни услуги фумигация на растения, растителни продукти и други обекти
     **/
    public static final String CLASSIFIER_REGISTER_0002037_CODE = "0002037";
    public static final String CLASSIFIER_REGISTER_0002037_NAME = "Национален електронен регистър на лицата, които" +
            " извършват специализирани растителнозащитни услуги фумигация на растения, растителни продукти и други обекти";

    /**
     * S2699: Национален електронен регистър на лицата, които извършват специализирани
     * растителнозащитни услуги третиране с продукти за растителна защита на семена за посев
     **/
    public static final String CLASSIFIER_REGISTER_0002038_CODE = "0002038";
    public static final String CLASSIFIER_REGISTER_0002038_NAME = "Национален електронен регистър на лицата, които" +
            " извършват специализирани растителнозащитни услуги третиране с продукти за растителна защита на семена за посев";

    /**
     * S2700: Национален електронен регистър на лицата, които извършват специализирани растителнозащитни
     * услуги консултантски услуги за интегрирано управление на вредители
     **/
    public static final String CLASSIFIER_REGISTER_0002039_CODE = "0002039";
    public static final String CLASSIFIER_REGISTER_0002039_NAME = "Национален електронен регистър на лицата, " +
            "които извършват специализирани растителнозащитни услуги консултантски услуги за интегрирано" +
            " управление на вредители";

    /**
     * Национален електронен регистър на лицата, които извършват интегрирано производство на растения
     * и растителни продукти
     */
    public static final String CLASSIFIER_REGISTER_0002040_CODE = "0002040";
    public static final String CLASSIFIER_REGISTER_0002040_NAME = "Национален електронен регистър на лицата, които " +
            "извършват интегрирано производство на растения и растителни продукти";

    /**
     * S2701: Национален електронен регистър на лицата, които притежават сертификат по чл. 83 ЗЗР
     **/
    public static final String CLASSIFIER_REGISTER_0002041_CODE = "0002041";
    public static final String CLASSIFIER_REGISTER_0002041_NAME = "Национален електронен регистър на лицата, " +
            "които притежават сертификат по чл. 83 ЗЗР";

    /**
     * S3201: Официален Регистър на професионалните оператори - лицата по чл. 6, ал. 1, т. 11 от ЗЗР,
     * включително лицата, които внасят, произвеждат, преработват и/или отглеждат растения и растителни продукти,
     * както и събирателните и разпределителните центрове, стоковите тържища и пазарите на производители
     * на такива растения и растителни продукти
     */
    public static final String CLASSIFIER_REGISTER_0002042_CODE = "0002042";
    public static final String CLASSIFIER_REGISTER_0002042_NAME = "Официален Регистър на професионалните оператори -" +
            " лицата по чл. 6, ал. 1, т. 11 от ЗЗР, включително лицата, които внасят, произвеждат, преработват и/или" +
            " отглеждат растения и растителни продукти, както и събирателните и разпределителните центрове, стоковите" +
            " тържища и пазарите на производители на такива растения и растителни продукти";

    /**
     * S16: Публичен електронен регистър търговците на пресни плодове и зеленчуци –
     * чл. 41 от Закона за прилагане на Общата организация на пазарите на земеделски продукти на ЕС
     */
    public static final String CLASSIFIER_REGISTER_0002044_CODE = "0002044";

    /**
     * С3363: Национален електронен регистър на търгуваните на територията на Република България семена, третирани
     * с продукти за растителна защита, които са разрешени в поне една държава-членка на Европейския съюз, по смисъла
     * на чл. 49, параграф 1 от Регламент (ЕО) № 1107/2009;
     **/
    public static final String CLASSIFIER_REGISTER_0002045_CODE = "0002045";

    public static final String CLASSIFIER_REGISTER_0002044_NAME = "Публичен електронен регистър търговците на пресни" +
            " плодове и зеленчуци – чл. 41 от Закона за прилагане на Общата организация на пазарите на земеделски продукти на ЕС";

    /**
     * С2702: Регистър на лицата по чл. 6, ал. 1, т. 12 от ЗЗР, на които се издава временно разрешение за въвеждане, движение,
     * притежаване и размножаване на карантинни вредители, растения, растителни продукти и други обекти за официални изпитвания,
     * научноизследователски или образователни цели, сортов подбор и селекция
     **/
    public static final String CLASSIFIER_REGISTER_0002043_CODE = "0002043";


    public static final String CLASSIFIER_REGISTER_0002043_NAME = "Регистър на лицата по чл. 6, ал. 1, т. 12 от ЗЗР," +
            " на които се издава временно разрешение за въвеждане, движение, притежаване и размножаване на" +
            " карантинни вредители, растения, растителни продукти и други обекти за официални изпитвания," +
            " научноизследователски или образователни цели, сортов подбор и селекция";


    //    --- ADDRESSES ---
    public static final String ADDRESS_TYPE_CODE = "00200";
    public static final String ADDRESS_TYPE_HEAD_OFFICE_CODE = "00201";
    public static final String ADDRESS_TYPE_PERMANENT_ADDRESS_CODE = "00202";
    public static final String ADDRESS_TYPE_CORRESPONDENCE_CODE = "00203";
    public static final String ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE = "00204";
    public static final String ADDRESS_TYPE_FOREIGN_FACILITY_CODE = "00205";
    public static final String ADDRESS_TYPE_SUPPLIER_CODE = "00206";
    public static final String ADDRESS_TYPE_TREATMENT_ADDRESS_CODE = "00209";


    //    --- MEASURING UNITS ---
    public static final String MEASURING_UNITS_TON_CODE = "01907";
    public static final String MEASURING_UNITS_CUBIC_METER_CODE = "01909";
    public static final String MEASURING_UNITS_LOAD_CODE = "01905";
    public static final String MEASURING_UNITS_VOLUME_CODE = "01906";


    //    --- VEHICLE OWNER TYPE ---
    public static final String VEHICLE_OWN_OWNER_TYPE_CODE = "01601";
    public static final String VEHICLE_RENT_OWNER_TYPE_CODE = "01602";


    //    --- CONTRACTOR RELATION TYPE ---
    public static final String TENANT = "00506";

}
