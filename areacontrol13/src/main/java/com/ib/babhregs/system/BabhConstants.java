package com.ib.babhregs.system;

import com.ib.indexui.system.Constants;

/**
 * Константи за проекта BABHRegs
 *
 * @author belev
 */
public class BabhConstants extends Constants {

	/** тил на логване в състемта */
	public static final String	LOGIN_TYPE			= "DOCU_WORK_LOGIN_TYPE";
	/** тип на логване използвайки LDAP */
	public static final String	LOGIN_TYPE_LDAP		= "DOCU_WORK_LOGIN_TYPE_LDAP";
	/** тип на логване използвайки база данни */
	public static final String	LOGIN_TYPE_DATABASE	= "DOCU_WORK_TYPE_DATABASE";
	/** дефолтен домайн на потребителите, които влизат чрез LDAP протокол */
	public static final String	DEFAULT_LDAP_DOMAIN	= "DOCU_WORK_DEFAULT_LDAP_DOMAIN";
	
	
	//MЕТОД НА ПОЛУЧАВАНЕ НА ВКАРВАНЕ НА ЗАЯВЛЕНИЕТО В БД
	/** Създадено от миграция */
	public static final Integer	METHOD_REG_MIGRATION	= 0;
	
	/** Заявление, дошло от деловодната и създадено през еФорми  */
	public static final Integer	METHOD_REG_EFORMS	= 1;	
	
	/** Заявление, дошло от деловодната и създадено през начин, различен от еформи   */
	public static final Integer	METHOD_REG_OTHER	= 2;
	
	
	
	
	// К О Н С Т А Н Т И ,    за невидимо обновяване на работния плот 
    public static final int CODE_FAKE_NOTIF_RELOAD_DELOVOD =  -1111;
    public static final int CODE_FAKE_NOTIF_RELOAD_DOC =  -2222;
    public static final int CODE_FAKE_NOTIF_RELOAD_ALL =  -3333;
    public static final int CODE_FAKE_NOTIF_RELOAD_FILES_SIGN =  -5555;
    public static final int CODE_FAKE_NOTIF_RELOAD_FILES_SIGN_TASK =  -6666;
	
    // К О Н С Т А Н Т И ,   за дейности на уеб сървиса за подписване на WORD документи 
    public static final int CODE_WS_DEIN_ZA_DELOV =  1;
    public static final int CODE_WS_DEIN_ZA_SAGL =  2;
    public static final int CODE_WS_DEIN_ZA_PODPIS =  3;	
	
	// К О Н С Т А Н Т И ,    за експорт 
    public static final int EXPORT_HTML = 0;
    public static final int EXPORT_WORD = 1;
    public static final int EXPORT_EXCEL = 2;    
    
    // К О Н С Т А Н Т И ,    за използване на Регистратура БАБХ за процедурите 
    public static final int CODE_REGISTRATURA_BABH = 139;
    
    /** гр.	София */
    public static final int CODE_EKATTE_SOFIA = 68134;
    /** ОДБХ София-град */
    public static final int CODE_REGISTRATURA_SOFIA_GRAD = 116;
    /** ОДБХ София-област */
    public static final int CODE_REGISTRATURA_SOFIA_OBLAST = 117;
    
    
    
    
	/** Код на логически списък "Вид ОЕЗ - Вид животно" */
	public static final int CODE_LIST_OEZ_JIV = 44;
	
	/** Код на логически списък "Вид животно - Технология на отглеждане" */
	public static final int CODE_LIST_JIV_TEHNO= 46;
	
	/** Код на логически списък "Вид животно - Предназначение" */
	public static final int CODE_LIST_JIV_PRED = 52;
	
	// СИСТЕМНИ КЛАСИФИКАЦИИ
	
	
    /** Код на класификация "Меню" */
	public static final int CODE_CLASSIF_MENU = 7;
	
	/** Код на класификация "Държави" */
	public static final int CODE_CLASSIF_COUNTRIES = 22; 
	
	/** Код на класификация "Тип участник" */
	public static final int CODE_CLASSIF_REF_TYPE = 101;

	/** Код на класификация "Вид алгоритъм за рег.№" */
	public static final int CODE_CLASSIF_ALG = 103;

	/** Код на класификация "Вид документ" */
	public static final int CODE_CLASSIF_DOC_VID = 104;
	
	/** Код на класификация "Административна структура за справки (+напуснали)" */
	public static final int CODE_CLASSIF_ADMIN_STR_REPORTS = 114;

	/** Код на класификация "Предназначение на файл" */
//	public static final int CODE_CLASSIF_FILE_PURPOSE = 115; // прехвърлена е в SystemConstants

	/** Код на класификация "Причина за нередовност на документ" */
	public static final int CODE_CLASSIF_DOC_IRREGULAR = 116;

	/** Код на класификация "Статус на обработка на документ" */
	public static final int CODE_CLASSIF_DOC_STATUS = 120;

	/** Код на класификация "Статус на изпълнение на етап" */
	public static final int CODE_CLASSIF_ETAP_STAT = 122;

	/** Код на класификация "Статус на изпълнение на услуга/процедура" */
	public static final int CODE_CLASSIF_PROC_STAT = 123;

	/** Код на класификация "Ръководни длъжности" */
	public static final int CODE_CLASSIF_BOSS_POSITIONS = 124;

	/** Код на класификация "Тип група на регистратура" */
	public static final int CODE_CLASSIF_REGISTRATURA_GROUP_TYPE = 128;

	/** Код на класификация "Тип документ" */
	public static final int CODE_CLASSIF_DOC_TYPE = 129;

	/** Код на класификация "Тип регистър" */
	public static final int CODE_CLASSIF_REGISTER_TYPE = 131;

	/** Код на класификация "Настройки на потребител" */
	public static final int CODE_CLASSIF_USER_SETTINGS = 137;

	/** Код на класификация "Дефинитивни права" */
	public static final int CODE_CLASSIF_DEF_PRAVA = 139;

	/** Код на класификация "Вид адрес" */
	public static final int CODE_CLASSIF_ADDR_TYPE = 140;

	/** Код на класификация "Вид документ в прикачен файл" */
//	public static final int CODE_CLASSIF_DOC_VID_ATTACH = 141; // прехвърлена е в SystemConstants

	/** Код на класификация "Регистри" */
	public static final int CODE_CLASSIF_REGISTRI = 144;

	/** Код на класификация "Регистратури" */
	public static final int CODE_CLASSIF_REGISTRATURI = 146;

	/** Код на класификация "Регистри - сортирани за регистратура" - !винаги трябва да се пуска специфика по регистратура! */
	public static final int CODE_CLASSIF_REGISTRI_SORTED = 148;

	/** Код на класификация "Групи служители" */
	public static final int CODE_CLASSIF_GROUP_EMPL = 149;

	/** Код на класификация "Настройки на регистратура" */
	public static final int CODE_CLASSIF_REISTRATURA_SETTINGS = 151;

	/** Код на класификация "Настройки по вид документ" */
	public static final int CODE_CLASSIF_DOC_VID_SETTINGS = 152;

	/** Код на класификация "Статус на предаване на документ" */
	public static final int CODE_CLASSIF_DOC_PREDAVANE_STATUS = 154;
	
	/** Код на класификация "Роли на референт в нотификации" */
	public static final int CODE_CLASSIF_NOTIFF_ROLI = 155;
	
	/** Код на класификация "Събития за нотификации" */
	public static final int CODE_CLASSIF_NOTIFF_EVENTS= 156;
	
	/** Код на класификация "Активни нотификации" */
	public static final int CODE_CLASSIF_NOTIFF_EVENTS_ACTIVE = 157;
	
	/** Код на класификация "Променливи за настройка на мейл акаунт" */
	public static final int CODE_CLASSIF_VARIABLES_SETTINGS_MAIL_ACCOUNT= 158; 

	/** Код на класификация "Пощенски кутии" */
	public static final int CODE_CLASSIF_MAILBOXES= 159; 
	
	/** Код на класификация "Начин на предаване/получаване" */
	public static final int CODE_CLASSIF_DVIJ_METHOD = 160; // TODO това защо се използва !!!

	/** Код на класификация "EDELIVERY_ORGANISATIONS" - ССЕВ */
	public static final int CODE_CLASSIF_EDELIVERY_ORGANISATIONS = 162; 

	/** Код на класификация "EGOV_ORGANISATIONS" - СЕОС */
	public static final int CODE_CLASSIF_EGOV_ORGANISATIONS = 163; 

	/** Код на класификация "Специализирани справки*/
	public static final int CODE_CLASSIF_RPT_SPEC = 165; 

	/** Код на класификация "Специализирани справки, които се използват*/
	public static final int CODE_CLASSIF_RPT_SPEC_ACTIVE = 166; 

	/** Код на класификация "Дефинирани процедури*/
	public static final int CODE_CLASSIF_PROCEDURI = 167; 

	/** Код на класификация "Статус на дефиниция на процедура*/
	public static final int CODE_CLASSIF_PROC_DEF_STAT = 168; 

	/** Код на класификация "Тип стартиращ документ на етап от процедура*/
	public static final int CODE_CLASSIF_ETAP_DOC_MODE = 169; 

	/** Код на класификация "Характер на документ" */
	public static final int CODE_CLASSIF_CHARACTER_SPEC_DOC = 175; 	
	
	/** Код на класификация "Атрибути за описание на екранни форми на вписване" */
	public static final int CODE_CLASSIF_ATTR_EKRANI = 497;
	
	/** Код на класификация "Вид на идентифилатор" */
	public static final int CODE_CLASSIF_VID_IDENT_JIV = 499;
	
	/** Код на класификация "Вид обект с епизоотично значение (500)" */
	public static final int CODE_CLASSIF_VID_OEZ = 500;  

	/** Код на класификация "Вид животни за капацитет на ОЕЗ" */
	public static final int CODE_CLASSIF_VID_JIV_KAPACITET = 501;  
	
	/**	Код на класификация "Предназначение на животни" (502)*/
	public static final int CODE_CLASSIF_PREDNAZNACHENIE_JIV = 502;  
	
	/** Код на класификация "Вид на ВЛЗ" */
	public static final int CODE_CLASSIF_VID_VLZ = 505; 
	
	/** Код на класификация "Вид дейност" */
	public static final int CODE_CLASSIF_VID_DEINOST = 506;
	
	/** Код на класификация "Вид на животно" */
	public static final int CODE_CLASSIF_VID_JIVOTNO = 508;
	
	/** Код на класификация "Вид МПС" */
	public static final int CODE_CLASSIF_VID_MPS = 509;

	/** Код на класификация "Отношението на лице към превоз (510)" */
	public static final int CODE_CLASSIF_OTN_LICE_PREVOZ = 510;

	/** Код на класификация "Вид на пътуването" */
	public static final int CODE_CLASSIF_VID_PATUVANE = 511;

	/** Код на класификация "Качество на представляващо лице" */
	public static final int CODE_CLASSIF_KACHESTVO_LICE = 512;
	
	/** Код на класификация "Начин на получаване на резултат" */
	public static final int CODE_CLASSIF_NACHIN_POLUCHAVANE = 513;
	
	/** Код на класификация "Статуса на вписването (516)" */
	public static final int CODE_CLASSIF_STATUS_VPISVANE = 516;
	
	/**  Код на класификация Технология на отглеждане (518)*/
	public static final int CODE_CLASSIF_TEHNOLOGIA_OTGLEJDANE = 518;
	
	/** Код на класификация "Тип на обекта" */
	public static final int CODE_CLASSIF_TIP_OBEKT = 519;

	/** Код на класификация "Видове регистри БАБХ" */
	public static final int CODE_CLASSIF_VID_REGISTRI = 520;
	
	/** Код на класификация "Видове услуги" */
	public static final int CODE_CLASSIF_VIDOVE_USLUGI = 521;
	
	/** Код на класификация "Страници за обработка на обекта за лицензиране" */
	public static final int CODE_CLASSIF_TIP_OBEKT_LICENZ = 522;
	
	/** Код на класификация "Тип на плащането" */
	public static final int CODE_CLASSIF_METOD_PLASHTANE = 524;
	
	/** Код на класификация "Предназначение на заявлението" */
	public static final int CODE_CLASSIF_ZAIAV_PREDNAZNACHENIE = 525;
	
	/** Код на класификация "Категория на СЖП/ПП" */
	public static final int CODE_CLASSIF_CATEGORY_SJP = 527;
	
	/** Код на класификация "Видове продукти за превоз" */
	public static final int CODE_CLASSIF_VID_PRODUCT_PREVOZ = 529;
	
	/** Код на класификация "Видове фуражи"  */
	public static final int CODE_CLASSIF_VIDOVE_FURAJ = 531;
	

	/** Код на класификация "Основание за статус на вписване"  */
	public static final int CODE_CLASSIF_OSN_STATUS_VPISVANE = 523;
	
	/** Код на класификация "Роля на лице в обект"  */
	public static final int CODE_CLASSIF_VRAZ_LICE_OBEKT = 535;
	
	/** Код на класификация "Животни, обслужвани от ВЛЗ (536)"  */
	public static final int CODE_CLASSIF_OBSL_JIV_VLZ = 536;

	/** Код на класификация "Вид документ на лице"  */
	public static final int CODE_CLASSIF_VID_DOC_LICE = 537;
	
	/** Код на класификация "Предназначение на обект" */
	public static final int CODE_CLASSIF_PREDN_OBEKT = 539;

	/** Код на класификация "Вид мероприятие" */
	public static final int CODE_CLASSIF_VID_MEROPRIATIE = 541;

	/** Код на класификация "Ползване на МПС" */
	public static final int CODE_CLASSIF_POLZANE_MPS = 542;
	
	/** Код на класификация "Вид животни за мероприятие " */
	public static final int CODE_CLASSIF_VID_JIV_MEROPRIATIE = 543;
	
	
	
	/** Код на класификация "Вид животни с официална идентификация" */
	public static final int CODE_CLASSIF_VID_JIV_IDENT = 544;
	
	/** Код на класификация "ГКПП" */
	public static final int CODE_CLASSIF_GKPP = 545;

	/** Състояние на фуражи за превоз (550)" */
	public static final int CODE_CLASSIF_SAST_FURAJ_PREVOZ = 550;
	
	/** Код на класификация "Предмет на дейности за фуражи" */
	public static final int CODE_CLASSIF_PREDMET_DEINOST_FURAJ = 551;

	/** Код на класификация "Предназначение на фуражи" */
	public static final int CODE_CLASSIF_PREDNAZNACHENIE_FURAJI = 552;

	/** Код на класификация "Вид инсталация" */
	public static final int CODE_CLASSIF_VID_INSTALACIA = 553;

	/** Код на класификация "Брой фуражни суровини" */
	public static final int CODE_CLASSIF_FURAJI_SUROVINI = 554;

	/** Код на класификация "Произход на суровини" */
	public static final int CODE_CLASSIF_PROIZHOD_SUROVINI = 555;

	/** Код на класификация "Вид мярка (559)" */
	public static final int CODE_CLASSIF_VID_MIARKA = 559;

	/** Код на класификация "Процедурата за одобряване (560)" */
	public static final int CODE_CLASSIF_PROC_ODOBR = 560;

	/** Код на класификация "Фармацевтични форми" */	
	public static final int CODE_CLASSIF_PHARM_FORMI = 693;    //Васил смени това значение. За повече инфо търсете него ;) old=563;
	
	/** Код на класификация "начин на транспортиране" */
	public static final int CODE_CLASSIF_NACHIN_TRANSP = 694;

	/** Код на класификация "Образование" */
	public static final int CODE_CLASSIF_OBRAZOBANIE = 695;
	
	/** Код на класификация "Предмет на дейността за търговия на едро с ВЛП" */
	public static final int CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP = 564;
	
	/** Код на класификация "вид на животни за фуражи"  566*/
	public static final int CODE_CLASSIF_OBEKT_VID_JIVOTNI_FURAJ = 566;
	
	/** Код на класификация "Обект на лицензиране"  567*/
	public static final int CODE_CLASSIF_OBEKT_LICENZIRANE = 567;

	/** Код на класификация "Вид вещества за ВЛП (568)" */
	public static final int CODE_CLASSIF_VID_VESHT_ZA_VLP = 568;

	/** Код на класификация "Мобилност на обект (569)" */
	public static final int CODE_CLASSIF_OBEKT_MOBILNOST = 569;

	/** Код на логическа класификация "комбинирани фуражи чл.18,ал.1"  */
	public static final int CODE_CLASSIF_FURAJI_KOMBINIRANI_18 = 571;
	
	/** Код на логическа класификация "вид животни с предназначение фуражи (комбин, чл. 18)"  */
	public static final int CODE_CLASSIF_FURAJI_VID_JIV_18 = 573;
	
	/** Код на логическа класификация "Комбинирани фуражи по чл.16,ал 1-търговия"  */
	public static final int CODE_CLASSIF_FURAJI_KOMBINIRANI_16 = 574;
	
	/** Код на логическа класификация "Комбинирани фуражи -чл.16,ал1 -производство"  */
	public static final int CODE_CLASSIF_PR_FURAJI_KOMBINIRANI_16 = 575;
	
	/** Код на логическа класификация "Фуражни добавки -производство, чл.18,ал.1"  */
	public static final int CODE_CLASSIF_PF_DOBAVKI = 576;
	
//	/** Код на логическа класификация "БАБХ- Вид документ - Вписване"  */
//	public static final int CODE_CLASSIF_DOC_VID_VPISVANE = 577;
	
	/** Код на логическа класификация "Вид на активното вещество (578)"  */
	public static final int CODE_CLASSIF_VID_AKT_VESHT = 578;
	
	/** Код на логическа класификация "Наркотични вещества  (579)"  */
	public static final int CODE_CLASSIF_NARK_VESHTESTVA = 579;

	/** Код на класификация "Вид животни за превоз (581)" */
	public static final int CODE_CLASSIF_VID_JIVOTNO_PREVOZ = 581;

	/** Код на класификация "Начин на отпускане на ВЛП (582)" */
	public static final int CODE_CLASSIF_NACHIN_OTPUSK_VLP = 582;

	/** Код на класификация "Предмет на дейността търговия с животни и зародишни продукти"  - логическа*/
	public static final int CODE_CLASSIF_PREDMET_TARG_JIV = 587; 
	
	/** Код на класификация "Фуражи внос чл.9"  - логическа*/
	public static final int CODE_CLASSIF_FURAJI_VNOS_9 = 589; 
	
	/** Код на класификация "СЖП чл.23е"  - логическа*/
	public static final int CODE_CLASSIF_FURAJI_SJP_23 = 590; 
	
	/** Код на класификация "Животни/СЖП 23е"  - логическа*/
	public static final int CODE_CLASSIF_JIVOTNI_SJP_23 = 592; 
	
	/** Код на класификация "Фуражи износ 53з"  - логическа*/
	public static final int CODE_CLASSIF_FURAJI_IZNOS_53 = 594; 
	
	/** Код на класификация "СЖП/ПП 53з"  - логическа*/
	public static final int CODE_CLASSIF_FURAJI_SJP_PP = 595;
	
	/** Код на класификация "Дейности при търговия с животни и зародишни продукти"  - логическа*/
	public static final int CODE_CLASSIF_VID_DEIN_TARG_JIV = 596; 
	
	/** Код на класификация "Видове дейности с идентификатори"  - логическа*/
	public static final int CODE_CLASSIF_VID_DEIN_IDENT_JIV = 597; 
	
	/** Код на класификация "Фуражи превоз"  - логическа*/
	public static final int CODE_CLASSIF_FURAJI_PREVOZ = 599; 
	
	/** Код на класификация "Фуражни добавки търговия"  - логическа*/
	public static final int CODE_CLASSIF_TF_DOBAVKI = 600; 
	
	/** Код на класификация "Премикси търговия"  - логическа*/
	public static final int CODE_CLASSIF_PREMIX_TARGOVIA = 601; 
	
	/** Код на класификация "Вид животни за подмярка от ПРСР"  - логическа*/
	public static final int CODE_CLASSIF_VID_JIVOTNO_PRSR = 602; 
	
	/** Код на класификация "Фуражи -търговия от разстояние"  - логическа*/
	public static final int CODE_CLASSIF_TARGOVIA_RAZSTOIANIE = 603; 
	
	/** Код на класификация "Качество на лицето в обект за производство/внос на ВЛП 604"  - логическа*/
	public static final int CODE_CLASSIF_TIP_VRAZKA_LICE_VNOS_PROIZV_VLP = 604; 
	
	/** Код на класификация "Вид дейност производство/внос ВЛП" */
	public static final int CODE_CLASSIF_VID_DEIN_PROIZV_VNOS_VLP = 605; 
	
	/** Код на класификация "Форми и дейности за внос на ВЛП 606" */
	public static final int CODE_CLASSIF_FORMI_DEINOST_VNOS_VLP = 606; 

	/** Код на класификация "Държави - трети страни"  - логическа*/
	public static final int CODE_CLASSIF_DARJAVI_TRETI_STRANI = 607;
	
	/** Код на класификация "Първична опаковка "  - логическа 608*/
	public static final int CODE_CLASSIF_PARVICHNA_OPAKOVKA = 608; 
	
	/** Код на класификация "фуражни продукти за износ в трети страни"  - логическа*/
	public static final int CODE_CLASSIF_FURAJNI_PRODUKTI_IZNOS = 609; 

	/** Код на класификация "Предназначение на рекламата (612)"*/
	public static final int CODE_CLASSIF_PREDN_REKLAMA = 612; 
	
	/** Код на класификация "Форми и дейности за производство на ВЛП ЛК-614"*/
	public static final int CODE_CLASSIF_FARM_FORM_PROIZV= 614; 

	/** Код на класификация "вид на детоксикация/деконтанимация"  - логическа*/
	public static final int CODE_CLASSIF_VID_DETOKS_DEKON = 617; 
		
	/** Код на класификация "Статус на ОЕЗ"  - логическа*/
	public static final int CODE_CLASSIF_STATUS_OEZ = 618; 
	
	/** Код на класификация "Тип на сертификата (619)" */
	public static final int CODE_CLASSIF_SERT_TYPE = 619; 

	/** Код на логическа класификация "Вид документ за въвеждане"  */
	public static final int CODE_CLASSIF_DOC_VID_INPUT = 620;
	
	/** Тип на връзка между събитие и обект (622)" */
	public static final int CODE_CLASSIF_VRAZ_SYBITIE_OBEKT = 622;
	
	/** Код на класификация "Вид документ сертификати за добра практика" */
	public static final int CODE_CLASSIF_DOCS_DOBRA_PRAKTIKA = 630;
	
	/** Код на класификация "Дейности с наркотични вещества" */
	public static final int CODE_CLASSIF_DEIN_NARK_VESHT = 631;
	
	/** Код на класификация "Дейности с активни вещества" */
	public static final int CODE_CLASSIF_DEIN_ACTIVE_VESHT = 632;
	
	/** Код на класификация "Дейности с растителни масла, мазнини, мастни киселини, биодизел и др" */
	public static final int CODE_CLASSIF_DEIN_RAST_MASLA_MAZNINI = 633;
	
	/** Код на класификация "Тип на идентификатор на физическо лице " */
	public static final int CODE_CLASSIF_TYPE_INDENT_FIZ_LICE = 636;
	
	/** Код на класификация "Тип на връзка на лице с ин-витро диагностични средства" */
	public static final int CODE_CLASSIF_TIP_VRAZ_LICE_INVITRO = 641;
	
	/** Код на класификация "Медикаментозни фуражи и продукти за тях" */
	public static final int CODE_CLASSIF_VID_MEDI_FURAJ = 643;
	
	/** Код на класификация "производство фуражи чл16" */
	public static final int CODE_CLASSIF_VID_FURAJ_PR16 = 649;

	/** Код на класификация "Цели за СЖП по чл. 271 (654)" */
	public static final int CODE_CLASSIF_CEL_SGP = 654;

	/** Код на класификация "фуражни добавки производство/търговия чл.16 " */
	public static final int CODE_CLASSIF_VID_FURAJNI_DOB16 = 655;
	
	/** фуражи складове под наем чл.16 (656)" */
	public static final int CODE_CLASSIF_FURAJI_NAEM16 = 656;
	
	/** Връзка на лице с дейност опити с животни (657)" */
	public static final int CODE_CLASSIF_VRAZ_LICE_JIV_OPIT = 657;
	
	/** Връзка на лице с дейност съхраняване на ембриони (658)" */
	public static final int CODE_CLASSIF_VRAZ_LICE_JIV_EMBR = 658;
	
	/** Код на класификация "премикси -производство/търговия чл.16" */
	public static final int CODE_CLASSIF_VID_FURAJ_PREM16 = 661;
	
	/** Код на класификация "премикси -производство/търговия чл.16" */
	public static final int CODE_CLASSIF_TIP_VRAZKA_LICE_OB_DEIN = 664;
	
	/** Код на класификация "Тип на връзка на лице с обект за търговия на дребно с ВЛП" */
	public static final int CODE_CLASSIF_TIP_VRAZKA_TARG_DREBNO_VLP = 665;
	
	/** Код на класификация "Дейности за внос на фуражи от трети държави" */
	public static final int CODE_CLASSIF_VID_DEIN_VNOS_TRETI_DARJ = 669;
	
	/** Код на класификация "Вид дейности при обезвреждане на СЖП/ПП" */
	public static final int CODE_CLASSIF_VID_DEIN_OBEZVREJDANE_SJP = 670;
	
	/** Код на класификация "Болести - 672" */
	public static final int CODE_CLASSIF_BOLESTI = 672;
	
	/** Код на класификация "Периоди на валидност за удостоверителни документи" */
	public static final int CODE_CLASSIF_PERIODI_VALID_DOCS = 674;

	/** Код на класификация "Сфери на обучение (676)" */
	public static final int CODE_CLASSIF_SFERI_OBUCHENIE = 676;

	/** Код на класификация "Видове животни по ЕС (677)" */
	public static final int CODE_CLASSIF_VID_JIV_ES = 677;
	
	/** Код на класификация "Видове регистри (ЗЖ, КФ, ВЛП)  (678)" */
	public static final int CODE_CLASSIF_VIDOVE_REGISTRI = 678;
	
	/** Код на класификация "Вид документ - заявления  (679)" */
	public static final int CODE_CLASSIF_VIDOVE_ZAIAVLENIA = 679;

	/** Код на класификация "Области на контрол (680)" */
	public static final int CODE_CLASSIF_OBLAST_KONTROL = 680;

	/** Код на класификация "Временни удостоверителни документи (681) " */
	public static final int CODE_CLASSIF_UDOST_DOC_VREM = 681;

	/** Тип на връзката на обект ВЛЗ с лице (682) */
	public static final int CODE_CLASSIF_TIP_VRAZ_VLZ_LICE = 682;

	/** Код на класификация "Начин на пролагане (685)" */
	public static final int CODE_CLASSIF_NACHIN_PRILAGANE = 685;

	/** Код на класификация "Видове удостоверителни документи (688)" */
	public static final int CODE_CLASSIF_VID_UD_DOCS = 688;
	
	/** Код на класификация "вид животно за опити  "  */
	public static final int CODE_CLASSIF_VID_JIV_OPIT = 701;
	
	/** Код на класификация "Ниво на класификация на документите" */
	public static final int CODE_CLASSIF_TLP_LEVEL = 715; 	

	// Константи за нотификации

	/** Код на значение "НОТИФИКАЦИЯ - непрочетена" */
	public static final int CODE_NOTIF_STATUS_NEPROCHETENA = 2;

	/** Код на значение "НОТИФИКАЦИЯ - прочетена" */
	public static final int CODE_NOTIF_STATUS_PROCHETENA = 1;

	// Значения от системни класификации

	// Системна класификация 2 - Информационни обекти (за журналиране)
	/** Документ */
	public static final int	CODE_ZNACHENIE_JOURNAL_DOC					= 51;
	/** Участник в процеса */
	public static final int	CODE_ZNACHENIE_JOURNAL_REFERENT				= 52;
	/** Регистратура */
	public static final int	CODE_ZNACHENIE_JOURNAL_REISTRATURA			= 54;
	/** Регистър */
	public static final int	CODE_ZNACHENIE_JOURNAL_REGISTER				= 55;
	/** Настройка за регистратура */
	public static final int	CODE_ZNACHENIE_JOURNAL_REISTRATURA_SETT		= 56;
	/** Група служители/кореспонденти */
	public static final int	CODE_ZNACHENIE_JOURNAL_REISTRATURA_GROUP	= 57;
	/** Характеристика на вид документ */
	public static final int	CODE_ZNACHENIE_JOURNAL_DOC_VID_SETT			= 58;
	/** Кореспондент в група на регистратура */
	public static final int	CODE_ZNACHENIE_JOURNAL_REG_GROUP_CORRESP	= 63;
	/** Шаблон за нотификации */
	public static final int	CODE_ZNACHENIE_JOURNAL_NOTIF_PATTERN		= 70;	
	/** Пощенска кутия */
	public static final int	CODE_ZNACHENIE_JOURNAL_MAILBOX				= 71;	
	/** СЕОС/ССЕВ съобщение */
	public static final int	CODE_ZNACHENIE_JOURNAL_EGOVMESSAGE			= 72;	
	/** Дефиниция на процедура */
	public static final int	CODE_ZNACHENIE_JOURNAL_PROC_DEF				= 77;	
	/** Дефиниция на етап от процедура */
	public static final int	CODE_ZNACHENIE_JOURNAL_PROC_DEF_ETAP		= 78;	
	/** Изпълнение на процедура */
	public static final int	CODE_ZNACHENIE_JOURNAL_PROC_EXE				= 80;	
	/** Изпълнение на етап от процедура */
	public static final int	CODE_ZNACHENIE_JOURNAL_PROC_EXE_ETAP		= 81;	
	/** Изричен достъп - Достъп до документ */
	public static final int	CODE_ZNACHENIE_JOURNAL_IZR_DOST				= 85;	
	/** Изричен достъп - Достъп до вписване */
	public static final int	CODE_ZNACHENIE_JOURNAL_IZR_DOST_VP				= 110;	
	/** Празничен ден */
	public static final int	CODE_ZNACHENIE_JOURNAL_PRAZNIK				= 88;
	/** Нотификация */
	public static final int	CODE_ZNACHENIE_JOURNAL_NOTIF				= 89;	
	
	/** Настройка на обекти */
	public static final int	CODE_ZNACHENIE_JOURNAL_NAST_OBJECT			= 91;

	/** Настройка на регистри (РЕГИСТЕР–ОПТИОНС */
	public static final int	CODE_ZNACHENIE_JOURNAL_REGISTER_OPTIONS			= 92;
	
	/** Транспортно средство*/
	public static final int	CODE_ZNACHENIE_JOURNAL_MPS		= 93;
	/** Вписване*/
	public static final int	CODE_ZNACHENIE_JOURNAL_VPISVANE		= 94;
	/** Вписване - Документи*/
	public static final int	CODE_ZNACHENIE_JOURNAL_VPISVANE_DOC		= 95;
	/** Дейности с животни*/
	public static final int CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV = 98;
	/** Дейности с фуражи*/
	public static final int CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI = 99;
	/** Дейности с ВЛП*/
	public static final int	CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP		= 100;
	/** Обект на дейност*/
	public static final int	CODE_ZNACHENIE_JOURNAL_OBEKT_DEINOST		= 101;
	/** Вписване - Статус*/
	public static final int	CODE_ZNACHENIE_JOURNAL_VPISVANE_STATUS		= 102;
	/** Ветеринарен лекарствен продукт */
	public static final int	CODE_ZNACHENIE_JOURNAL_VLP		= 105;
	/** Обект с епизоотично значение */
	public static final int	CODE_ZNACHENIE_JOURNAL_OEZ		= 106;
	/** Транспортно средство (фуражи) */
	public static final int	CODE_ZNACHENIE_JOURNAL_MPS_FURAJI		= 107;
	/** Ветеринарно лечебно заведение */
	public static final int	CODE_ZNACHENIE_JOURNAL_VLZ		= 108;
	/** Логика за попълване на шаблон */
	public static final int	CODE_ZNACHENIE_JOURNAL_SHABLON_LOGIC		= 109;

	/** Код на значение "Администратор на процедури" класификация "Бизнес роля" 4 */
	public static final int CODE_ZNACHENIE_BUSINESS_ROLE_PROC_ADMIN  = 2;
	/** Код на значение "Администратор" класификация "Бизнес роля" 4 */
	public static final int CODE_ZNACHENIE_BUSINESS_ROLE_ADMIN  = 6;
	
	/** Код на значение  "Административна структура"  класификация "Меню" 7*/
	public static final int	CODE_ZNACHENIE_MENU_ADM_STRUCT = 32;
	
	/** Код на значение  "Кореспонденти"  класификация "Меню" 7*/
	public static final int	CODE_ZNACHENIE_MENU_CORESP = 33;

	/** Код на значение "България" класификация "Държави" 22 */
	public static final int	CODE_ZNACHENIE_BULGARIA	= 37;
	
	/** Код на значение "Министър" класификация "Длъжности" 25 */
	public static final int	CODE_ZNACHENIE_DLAJN_MINISTER	= 1;
	/** Код на значение "Изпълнителен директор" класификация "Длъжности" 25 */
	public static final int	CODE_ZNACHENIE_DLAJN_IZP_DIREKTOR	= 2;
	/** Код на значение "Заместник изпълнителен директор" класификация "Длъжности" 25 */
	public static final int	CODE_ZNACHENIE_DLAJN_ZAM_IZP_DIREKTOR	= 3;
	/** Код на значение "Директор на ОДБХ" класификация "Длъжности" 25 */
	public static final int	CODE_ZNACHENIE_DLAJN_DIREKTOR_ODBH	= 4;
	
	/** Код на значение "звено" класификация "Тип участник" 101 */
	public static final int	CODE_ZNACHENIE_REF_TYPE_ZVENO	= 1;
	/** Код на значение "служител" класификация "Тип участник" 101 */
	public static final int	CODE_ZNACHENIE_REF_TYPE_EMPL	= 2;
	/** Код на значение "Юридическо лице" класификация "Тип участник" 101 */
	public static final int	CODE_ZNACHENIE_REF_TYPE_NFL		= 3;
	/** Код на значение "физическо лице" класификация "Тип участник" 101 */
	public static final int	CODE_ZNACHENIE_REF_TYPE_FZL		= 4;
	/** Код на значение "мигриран" класификация "Тип участник" 101 */
	public static final int	CODE_ZNACHENIE_REF_TYPE_MIG		= 5;

	/** Код на значение "с индекс и стъпка" класификация "Вид алгоритъм за рег.№" 103 */
	public static final int	CODE_ZNACHENIE_ALG_INDEX_STEP	= 1;
	/** Код на значение "индекс по вид документ" класификация "Вид алгоритъм за рег.№" 103 */
	public static final int	CODE_ZNACHENIE_ALG_VID_DOC		= 2;
	/** Код на значение "произволен рег.номер" класификация "Вид алгоритъм за рег.№" 103 */
	public static final int	CODE_ZNACHENIE_ALG_FREE			= 3;
	/** Код на значение "разрешение за превоз на животни" класификация "Вид алгоритъм за рег.№" 103 */
	public static final int	CODE_ZNACHENIE_ALG_RAZR_PREV_JIV		= 4;
	
	/** Код на значение "заявление" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAVLENIE		= 6;
	/** Код на значение "становище" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_STANOVISHTE		= 11;
	/** Код на значение "Заявление за издаване на разрешение за търговия на едро с ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_EDRO_VLP	= 17;
	/** Код на значение "Заявление за издаване на разрешение за производство или внос на ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_PROIZV_VLP	= 20;
	/** Код на значение "Заявление за издаване на разрешение за внос на ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_VNOS_VLP= 225;	
	/** Код на значение "Заявление за издаване на разрешение за търговия на дребно с ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_DREBNO_VLP= 75;
	/** Код на значение "Заявление за издаване на лицензия за дейности с наркотични вещества за ВМ цели" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_NARK_VESHT_VLP= 81;
	/** Код на значение "Заявление за издаване на сертификат за регистрация на инвитро диагностични средства" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_INVITRO_VLP= 90;
	/** Код на значение "Заявление за издаване на разрешение за дейности с активни вещества" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_AKTIVNI_VLP= 99;
	/** Код на значение "Заявление за одобряване на реклами на ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_REKLAMI_VLP= 93;
	/** Код на значение "Заявление за издаване на разрешение за търговия от разстояние" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_TARG_RAZST_VLP= 96;
	/** Код на значение "Заявление за издаване на разрешение за паралелна търговия" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_PARALELNA_VLP= 235;
	/** Код на значение "Разрешение за производство на ВЛП" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_RAZRESH_PROIZV_VLP = 272;
	/** Код на значение "Заявление за одобрение на оператори по чл. 18, ал.1 от ЗФ" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_ODOB_OPERATORI_18 = 275;
	/** Код на значение "Заявление за регистрация на оператори по чл. 16, ал. 1 от ЗФ" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_REG_OPERATORI_16 = 276;
	/** Код на значение "Заявление за издаване на разрешение за производство или внос на ВЛП "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_VNOS_PROIZV_VLP = 282;
	/** Код на значение "Заявление за издаване на разрешение за търговия от разстояние или паралелна търговия "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_TARG_RAZST_PARALEL_VLP = 283;
	/** Код на значение "Заявление за одобрение на оператори за производство или търговия с медикаментозни фуражи и/или междинни продукти по чл. 55, ал. 1 от ЗФ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_MEDIKAMENTOZNI_FURAJI = 284;
	/** Код на значение "Заявление за одобрение на български предприятия за износ на фуражи за трети държави" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_IZNOS_FURAJI = 285;
	
	
	
	/** Код на значение "Заявление за одобрение на български предприятия за износ на фуражи за трети държави (Китай)" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_ODOB_PREDPRIIATIA_IZNOS	= 160;
	/** Код на значение "Заявление за регистрация на търговци, които получават пратки СЖП и ПП от държави членки по чл. 71 от ЗВД" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_REGTARGOVTSI_SJP	= 166;
	/** Код на значение "Заявление за издаване на ветеринарен сертификат за износ на фуражи в трети държави по чл. 53з, ал. 1 от ЗФ" класификация "Вид документ" 104 */
	public static final int	CODE_ZNACHENIE_ZAIAV_SERT_IZNOS_FURAJ	= 184;
	
	

	/** Код на значение "основен документ" класификация "Предназначение на файл" 115 */
	public static final int	CODE_ZNACHENIE_FILE_PURPOSE_MAIN_DOC = 1;
	/** Код на значение "приложение" класификация "Предназначение на файл" 115 */
	public static final int	CODE_ZNACHENIE_FILE_PURPOSE_APPLICATION	= 2;
	/** Код на значение "помощен файл" класификация "Предназначение на файл" 115 */
	public static final int	CODE_ZNACHENIE_FILE_PURPOSE_HELP_FILE = 3;

	/** Код на значение "Чака обработка" класификация Статус на обработка на документ" 120 */
	public static final int	CODE_ZNACHENIE_DOC_STATUS_WAIT	= 14;
	/** Код на значение "Обработен" класификация Статус на обработка на документ" 120 */
	public static final int	CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN	= 15;
	

	/** Код на значение "група служители" класификация "Тип група на регистратура" 128 */
	public static final int	CODE_ZNACHENIE_REG_GROUP_TYPE_EMPL		= 1;

	/** Код на значение "входящ" класификация "Тип документ" 129 */
	public static final int	CODE_ZNACHENIE_DOC_TYPE_IN	= 1;
	/** Код на значение "собствен" класификация "Тип документ" 129 */
	public static final int	CODE_ZNACHENIE_DOC_TYPE_OWN	= 2;
	/** Код на значение "работен" класификация "Тип документ" 129 */
	public static final int	CODE_ZNACHENIE_DOC_TYPE_WRK	= 3;


	/** Код на значение "автор" класификация "регистър за документи" 131 */
	public static final int	CODE_ZNACHENIE_REGISTER_DNEV	= 1;
	/** Код на значение "съгласувал" класификация "кореспондентска група" 131 */
	public static final int	CODE_ZNACHENIE_REGISTER_COR_GR	= 2;
	/** Код на значение "подписал" класификация "ОДР" 131 */
	public static final int	CODE_ZNACHENIE_REGISTER_ODR		= 3;
	
	
	/** Код на значение "Дублиране на съобщения по e-mail" класификация "Настройки на потребител" 137 */
	public static final int CODE_ZNACHENIE_USER_SETT_DUBL_MAIL = 1;
	
	/** Код на значение "адрес за кореспонденция" класификация "Вид адрес" 140 */
	public static final int CODE_ZNACHENIE_ADDR_TYPE_CORRESP = 1;
	/** Код на значение "адрес постоянен" класификация "Вид адрес" 140 */
	public static final int CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN = 2;
	
	// Класификация 155 - Роли на референт в нотификации
	/** Код на значение "Автор на документ" */
	public static final int	CODE_ZNACHENIE_NOTIF_ROLIA_AVTOR	= 2;	
	/** Код на значение "Служител, получил достъп" */
	public static final int	CODE_ZNACHENIE_NOTIF_ROLIA_SLUJIT_DOST	= 3;	
	/** Код на значение "Контролиращ на задача" */
	public static final int	CODE_ZNACHENIE_NOTIF_ROLIA_CONTR	= 5;	
	/** Код на значение "Всички с достъп" */
	public static final int	CODE_ZNACHENIE_NOTIF_ROLIA_ALL_DOST	= 8;
	
	// Класификация 146 - Регистратури
	/** Код на значение "БАБХ" */
	public static final int	CODE_ZNACHENIE_REGISTRATURA_BABH	= 139;	

	// Класификация 156 - Събития за нотификации
	/** Код на значение "Даване на достъп до документ" */
	public static final int	CODE_ZNACHENIE_NOTIFF_EVENTS_DOC_ACCESS				= 16;
	/** Код на значение "Заключен потребител" */
	public static final int	CODE_ZNACHENIE_NOTIFF_EVENTS_USER_LOCKED			= 49;
	/** Код на значение "Опит за неоторизиран достъп до страница" */
	public static final int	CODE_ZNACHENIE_NOTIFF_EVENTS_UNAUTHORIZED_PAGE		= 50;
	/** Код на значение "Опит за неоторизиран достъп до обект" */
	public static final int	CODE_ZNACHENIE_NOTIFF_EVENTS_UNAUTHORIZED_OBJECT	= 51;

	/** Код на значение "чака изпълнение" класификация "Статус на изпълнение на услуга/процедура" 123 */
	public static final int	CODE_ZNACHENIE_PROC_STAT_WAIT 		= 1;
	/** Код на значение "изпълнява се" класификация "Статус на изпълнение на услуга/процедура" 123 */
	public static final int	CODE_ZNACHENIE_PROC_STAT_EXE 		= 2;
	/** Код на значение "изпълнена" класификация "Статус на изпълнение на услуга/процедура" 123 */
	public static final int	CODE_ZNACHENIE_PROC_STAT_IZP 		= 3;
	/** Код на значение "изпълнена след срока" класификация "Статус на изпълнение на услуга/процедура" 123 */
	public static final int	CODE_ZNACHENIE_PROC_STAT_IZP_SROK 	= 4;
	/** Код на значение "прекратена" класификация "Статус на изпълнение на услуга/процедура" 123 */
	public static final int	CODE_ZNACHENIE_PROC_STAT_STOP 		= 5;
	
	/** Код на значение "чака изпълнение" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_WAIT 		= 1;
	/** Код на значение "изпълнява се" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_EXE 		= 2;
	/** Код на значение "чака решение" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_DECISION 	= 3;
	/** Код на значение "изпълнен" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_IZP 		= 4;
	/** Код на значение "изпълнен след срока" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_IZP_SROK 	= 5;
	/** Код на значение "отменен" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_CANCEL 	= 6;
	/** Код на значение "прекратен" класификация "Статус на изпълнение на етап" 122 */
	public static final int	CODE_ZNACHENIE_ETAP_STAT_STOP 		= 7;

	/** Код на значение "Удостоверителен документ" класификация 141" */
	public static final int CODE_ZNACHENIE_DOC_VID_ATTACH_UD = 1;
	/** Код на значение "Кратка характеристика на ВЛП"  класификация 141 */
	public static final int CODE_ZNACHENIE_DOC_VID_ATTACH_HAR_VLP = 4;
	/** Код на значение "Фармацевтични форми и дейности"  класификация 141 */	
	public static final int CODE_ZNACHENIE_DOC_VID_ATTACH_FARM_FORM = 5;
	
	/** Код на значение "разработва се" класификация "Статус на дефиниция на процедура" 168 */
	public static final int	CODE_ZNACHENIE_PROC_DEF_STAT_DEV 		= 1;
	/** Код на значение "отпаднала" класификация "Статус на дефиниция на процедура" 168 */
	public static final int	CODE_ZNACHENIE_PROC_DEF_STAT_CANCEL 	= 2;
	/** Код на значение "активна" класификация "Статус на дефиниция на процедура" 168 */
	public static final int	CODE_ZNACHENIE_PROC_DEF_STAT_ACTIVE 	= 3;
	
	/** Код на значение "стартиращ предходния етап" класификация "Тип стартиращ документ на етап от процедура" 169 */
	public static final int	CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_IN 		= 1;
	/** Код на значение "резултат от предходния етап" класификация "Тип стартиращ документ на етап от процедура" 169 */
	public static final int	CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_OUT 		= 2;

	/** Код на значение "удостоверителен документ" класификация "Характер на специализиран документ" 175 */
	public static final int	CODE_ZNACHENIE_HAR_DOC_UDOST		= 1;
	/** Код на значение "изменение към УД" класификация "Характер на специализиран документ" 175 */
	public static final int	CODE_ZNACHENIE_HAR_IZMENENIE_UD		= 2;
	
	
	/** Код на значение "Притежател на лиценз от Класиф: Тип на връзка на лице с инвитро диагностични средства -641" */
	public static final int	CODE_ZNACHENIE_PRITEJATE_LICENZ_LICA_OTN		= 16;

	// Класификация 154 - Статус на предаване
	/** Код на значение "Чака антивирусна проверка" */
	public static final int	DS_МALWARE_CHECK = 1;	
	/** Код на значение "Чака регистрация" */
	public static final int	DS_WAIT_REGISTRATION = 3;	
	/** Код на значение "Регистриран" */
	public static final int	DS_REGISTERED = 8;	
	/** Код на значение "Спряна работата" */
	public static final int	DS_STOPPED	= 4; 
	/** Код на значение "Приключена работата" */
	public static final int	DS_CLOSED	= 12;
	/** Код на значение "Не е намрен" */
	public static final int	DS_NOT_FOUND = 11;	
	/** Код на значение "Вече е получен" */
	public static final int	DS_ALREADY_RECEIVED = 13; 	
	/** Код на значение "Отказан" */
	public static final int	DS_REJECTED	= 7; 
	/** Код на значение "Не е изпратено" */
	public static final int	DS_WAIT_SENDING	= 2;
	/** Код на значение "Изпратен" */
	public static final int	DS_SENT	= 5;
	/** Код на значение "Върнато съобщение за грешка" */
	public static final int	DS_RETURNED_ERROR = 6;	
	/** Код на значение "Не е връчено" */
	public static final int	DS_NOT_OPENED = 10;	
	/** Код на значение "Връчено" */
	public static final int	DS_OPENED = 9;		
	/** Код на значение "Чака формиране на пакет" */
	public static final int	DS_WAIT_PROCESSING = 14;
	
	
	/** Код на значение "Тип физическо лице в ССЕВ" */
	public static final String	SSEV_TYPE_PERSON = "Person";
	/** Код на значение "Тип юридическо лице в ССЕВ" */
	public static final String	SSEV_TYPE_LEGALPERSON = "LegalPerson";
	/** Код на значение "Тип администрация в ССЕВ" */
	public static final String	SSEV_TYPE_ADMINISTRATION = "Institution";
	/** Код на значение "Тип отговор" */
	public static final String	SSEV_TYPE_REPLY = "Reply";
	
	
	/** TODO */
	/** Код на класификация "Атрибути за описание на екранни форми на заявление(вписване??) (екранни форми)"  497*/
	/**  Позволява въвеждане на физическо лице*/
	public static final int	EKRAN_ATTR_FL = 1;
	/** Позволява въвеждане на чужди физически лица*/
	public static final int	EKRAN_ATTR_FL_F = 2;
	/** Позволява въвеждане на юридически лица*/
	public static final int	EKRAN_ATTR_UL = 3;
	/** Позволява въвеждане на чужди юридически лица*/
	public static final int	EKRAN_ATTR_UL_F = 4;
	/** Позволявa въвеждане на латиница*/
	public static final int	EKRAN_ATTR_LATINICA = 5;		
//	/** МПС - капацитет за превоз на животни*/
//	public static final int	EKRAN_ATTR_MPS_KAPACITET= 6;
//	/** МПС - СЖП за превоз*/
//	public static final int	EKRAN_ATTR_MPS_SJP= 11;
	/******** Удостоверителен документ 17 *****************/
	public static final int	EKRAN_ATTR_UDOST_DOC= 17;
	
	/******** ВЛП - Лице имащо отн. към обекта - едно значение 12*****************/
	public static final int	EKRAN_ATTR_VLP_LICE_OTN_EDNO= 12;
	/******** ВЛП - Обект дейност - единичен избор 14*****************/
	public static final int	EKRAN_ATTR_VLP_OBEKT_DEIN_EDNO= 14;	
	/******** ВЛП - Event_deinost_VLP_EDNO*****************/
	public static final int	EKRAN_ATTR_VLP_EVENT_DEIN_EDNO= 16;
	/******** ВЛП - event_deinost_vlp_predmet без таблица от modalMany директно *****************/
	public static final int	EKRAN_ATTR_VLP_PREDMET_MANY= 18;
	/******** ВЛП - event_deinost_vlp_lice - Лица имащи отношение - много *****************/
	public static final int	EKRAN_ATTR_VLP_LICA_MNOGO= 19;
	/******** ВЛП - event_deinost_vlp_predmet - EDNO  *****************/
	public static final int	EKRAN_ATTR_VLP_PREDMET_ONE= 20;
	/******** ВЛП - event_deinost_vlp_bolesti - Mnogo 21  *****************/
	public static final int	EKRAN_ATTR_VLP_BOLESTI_MNOGO= 21;
	/******** ВЛП - obekt_deinost - Mnogo 22  *****************/
	public static final int	EKRAN_ATTR_VLP_OBEKT_DEIN_M= 22;
	/******** ВЛП - Активни в-ва много  23  *****************/
	public static final int	EKRAN_ATTR_VLP_ACTIVE_VESHT_M= 23;
	/******** ВЛП - Вид влп много  24  *****************/
	public static final int	EKRAN_ATTR_VLP_EVENT_VID_М= 24;
	/******** ВЛП - event_deinost_vlp_predmet ТАБЛИЦА С различни полета и модален *****************/
	public static final int	EKRAN_ATTR_VLP_PREDMET_MANY_TABLE= 25;
	/******** ВЛП - event_deinost_vlp_prvnos ТАБЛИЦА С различни полета и модален *****************/
	public static final int	EKRAN_ATTR_VLP_PRVNOS_PROIZV_MANY_TABLE= 26;
	/******** ВЛП - obekt_deinost_lica ТАБЛИЦА с лица много *****************/
	public static final int	EKRAN_ATTR_VLP_OB_DEIN_LICA_M_TABLE= 27;
	
	
	/** Код на класификация "Вид на дейността в регистъра" 506 */	
	/**....TODO ще се попълва постепенно!!.....*/
	/** Дейности Животни*/
	/** Търговия с животни Код на класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV = 1;
	/** Търговия със зародишни продукти Код на класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD = 2;
	/** Издаване на становище за учебна програмаКод на класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR = 3;	
	/** Изплащане на обезщетение за животни - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_OBEZSHЕТЕНИЕ_JIV = 4;	
	/** Износ на животни - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV = 5;	
	/** Опити с животни - класификация "Вид на дейността в регистъра" 506*/
	public static final int	 CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV = 6;
	/** Търговия с идентификатори" Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT = 7;
	/** Производство на идентификатори" Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT = 8;
	/** Превоз на животни - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV = 9;
	/** Добив и съхранение на ембриони и яйцеклетки - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI = 10;
	/** Провеждане на мероприятия с животни - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV = 11;
	/** Издаване на удостоверение за правоспособност - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_PRAVOSPOSOBN_JIV = 12;
	/** Одобрение на ТС за превоз на животни- класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_REG_MPS = 70;
	/** Издаване на документи за преминато обучение 102- класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE = 102;
	/** Търговия с пратки СЖП и производни продукти по чл. 71 от ЗВД - класификация "Вид на дейността в регистъра" 506*/
	public static final int	CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP = 105;
	
	
	/** Дейности Фуражи*/
	/** Производство на комбинирани фуражи по чл. 18, ал. 1 от Закона за фуражите --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_FR_PROIZV_KF18 = 17;
	/** Производство на фуражни суровини по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PR_FSUROVINI18 = 18;
	/** Производство на премикси по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PR_PREM18 = 19;
	/** Производство на фуражни добавки по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PR_FRDOB18 = 20;
	/** Търговия с фуражни добавки по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TRG_FR18 = 21;
	/** Търговия на премикси по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TRG_PREMIKS18 = 22;
	/** Отдаване по наем на склад --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_SKLAD_NAEM = 23;
	/** Обработка на сурово растително масло по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_OBRABOTKA_SRM_18 = 24;
	/** Деконтаминация-физична по чл 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_FIZ_DEKON_F18 = 31;
	/** Детоксикация-биологична по чл. 18 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_BIO_DETOX_F18 = 32;
	/** Търговия  с  фуражи от разстояние по чл. 23и --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TRG_F23I = 33;
	/** Търговия на СЖП/ПП от категория 3 по чл. 17 --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TRG_SJP17 = 34;
	/** Производство на фуражи по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_F16 = 37;
	/** Регистрация на превозвачи --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_REG_PREVOZVACHI = 38;
	/** Внос на фуражи --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_VNOS_FURAJI = 39;
	/**  Детоксикация - микробиологична  --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_MICRO_DETOX_F18 = 43;
	/** Търговия на фуражи по чл. 16, ал.1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TARG_F16 = 62;	
	/**Вписване в списъка на одобрените представители за износ на фуражи за трети държави (Китай) --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_IZNOS_FURAJI = 63;
	/** Производство на СЖП/ПП --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_SJP = 64;
	/** Изгаряне на СЖП/ПП --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_IZGARIANE_SJP = 66;
	/** Одобрение на ТС за превоз на СЖП/ПП --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_ODOBRENIE_TS_SJP = 69;
	/** Производство на комбинирани фуражи, съдържащи фуражни добавки и/или премикси по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_KF_FDOB_PREM_16 = 74;
	/** Производство на премикси по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_PREM_16 = 75;
	/** Търговия с премикси по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TARG_PREMIKS_16 = 76;
	/** Производство на фуражни добавки по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_F_DOBAVKI_16 = 77;
	/** Търговия с фуражни добавки по чл. 16, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TRG_F_DOBAVKI_16 = 78;
	/** Производство на медикаментозни фуражи по чл. 55, ал.1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_PROIZ_MEDIKAMENT_FURAJI = 79;
	/** Търговия на медикаментозни фуражи по чл. 55, ал.1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TARG_MEDIKAMENT_FURAJI = 80;	
	/** Междинни дейнсти със СЖП --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_MEJDINNI_DEIN = 92;
	/** Търговия със СЖП/ПП без задържане пратки на склад --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TARG_SJP_BEZ_SAHRANENIE = 94;
	/** Използване на СЖП и ПП за научни и други цели по чл. 271 от ЗВД --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_SJP_NAUCHNI_CELI = 95;
	/** Издаване на сертификат за добра практика --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_CERT_DOBRA_PRAKTIKA = 96;
	/** Износ на СЖП и ПП за трети държави по чл. 238, ал. 1 от ЗВД --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_IZNOS_SJP = 97;
	/** Издаване на становище за прилагане на подмерки 4.1, 4.2, 5.1, 6.1 и 6.3 от ПРСР --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_STAN_PODMERKI = 98;
	/** Търговия със СЖП и/или ПП по чл. 23е, ал. 1 от ЗВД --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23 = 99;
	/**Износ на фуражи в трети държави по чл. 53з, ал. 1 от ЗФ --  класификация "Вид на дейността в регистъра" 506   */
	public static final int	CODE_ZNACHENIE_DEIN_CERT_IZNOS_FURAJI_53 = 103;
	

	
	/** Дейности ВЛП*/
	/** Търговия на едро с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_TR_EDRO = 40;
	/** Производтсво на ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_PROIZV = 41;
	/** Търговия с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_TARGOVIA = 42;
	/** Внос с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_VNOS = 44;
	/** Търговия на дребно с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_DREBNO = 72;
	/** Паралелна търговия с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_PARALEL_TARG = 47;
	/** инвитро с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_INVITRO = 48;
	/** Reklama с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_REKLAMA = 73;
	/** Razstoqnie с ВЛП -  класификация "Вид на дейността в регистъра" 506 */
	public static final int	CODE_ZNACHENIE_DEIN_VLP_RAZSTOQNIE = 46;
	
	/** Код на класификация "Качество на представляващо лице" */
	public static final int	 CODE_ZNACHENIE_PALNOM = 4;   // Пълномощник
	
	/** Код на класификация "Статус на вписването" 516 */
	/** в обработка  */
	public static final int	STATUS_VP_WAITING = 1;
	/** вписан */
	public static final int	STATUS_VP_VPISAN= 2;
	/** заличен */
	public static final int	STATUS_VP_ZALICHEN = 3;
	/** отнет */
	public static final int	STATUS_VP_OTNET = 4;
	/** прекрантен */
	public static final int	STATUS_VP_PREKRATEN = 5;
	/** временно преустановен */
	public static final int	STATUS_VP_VR_PREUSTANOVEN= 11;
	
//	/** оставен без последствие*/
//	public static final int	STATUS_VP_BEZPOSLEDSTVIA= 6;
//	/** оттеглен*/
//	public static final int	STATUS_VP_OTTEGLEN = 7;
	
	/** отказ */
	public static final int	STATUS_VP_OTKAZ = 10;
	/** временно преустановен */
	public static final int	STATUS_VP_VREM_PREUST = 11;

	/** Код на класификация "Тип на обекта" 519 */
	/** ОЕЗ */
	public static final int CODE_ZNACHENIE_TIP_OBEKT_OEZ = 1;
	/** Обекти за дейности с фуражи */
	public static final int CODE_ZNACHENIE_TIP_OBEKT_FURAJI = 2;
	/** Обекти за дейности с ВЛП */
	public static final int CODE_ZNACHENIE_TIP_OBEKT_VLP = 3;
	/** ВЛЗ */
	public static final int CODE_ZNACHENIE_TIP_OBEKT_VLZ = 4;
	
	
	/** Код на класификация "Видове регистри" 520 */
	/** Регистър ЗЖ   */
	public static final int	REGISTER_ZJ = 1;
	/** Регистър ВЛП  */
	public static final int	REGISTER_VLP = 2;
	/** Регистър фруражи  */
	public static final int	REGISTER_KF = 3;
	/** Регистър на одобрените обекти в сектор "Фуражи" по чл.20, ал. 3 от Закона за фуражите, класиф=520  */
	public static final int	REGISTER_KF_31 = 31;
	/** Регистър на регистрираните обекти в сектор „Фуражи“ по чл. 17, ал. 3 от Закона за фуражите, класиф=520  */
	public static final int	REGISTER_KF_32 = 32;
	/** Регистър на операторите, транспортиращи фуражи съгласно чл. 17е, ал. 2 от Закона за фуражите, класиф=520  */
	public static final int	REGISTER_KF_33 = 33;
	/** Регистър на одобрените обекти за производство и/или търговия с медикаментозни фуражи, класиф=520  */
	public static final int	REGISTER_KF_34 = 34;
	/** Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти, класиф=520  */
	public static final int	REGISTER_KF_44 = 44;
	/** Регистър на операторите с издаден ветеринарен сертификат/друг документ за износ на СЖП и ПП в трети държави по чл.238, ал.1 от ЗВД, класиф=520  */
	public static final int	REGISTER_KF_45 = 45;

	/** Код на класификация "Страници за обработка на обекта за лицензиране" 522 */
	/** регистриране на ОЕЗ */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ = 1;
//	/** Лице */
//	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE = 2;
	/** регистриране на МПС  - фуражи */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS = 3;
	/** регистриране на ВЛЗ */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ = 4;
//	/** ВЛ */
//	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VL = 5;
	/** регистриране на ВЛП  */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP = 6;
	/** Лице - ЗЖ */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ = 7;
	/** Лице - фуражи */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI = 8;
	/** Лице - ВЛП */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP = 9;
	/** регистриране на МПС - животни */
	public static final int	CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ = 11;
	
	/** без плащане - за класификация "Тип на плащането" 524 */
	public static final int	CODE_ZNACHENIE_PAY_TYPE_NOPAY = 1;
	/**Фиксирана натрифа - за класификация "Тип на плащането" 524 */
	public static final int	CODE_ZNACHENIE_PAY_TYPE_FIXED = 2;
	/**плаваща тарифа -  за класификация "Тип на плащането" 524 */
	public static final int	CODE_ZNACHENIE_PAY_TYPE_FLOAD = 3;
	
	
	/** за първоначално вписване - за класификация "Предназначение на заявлението" 525 */
	public static final int	CODE_ZNACHENIE_ZAIAV_PARVONACHALNO = 1;
	/** за промяна на обстоятелства - за класификация "Предназначение на заявлениет" 525 */
	public static final int	CODE_ZNACHENIE_ZAIAV_PROMIANA = 2;
	/** за заличаване -  за класификация "Предназначение на заявлениет" 525 */
	public static final int	CODE_ZNACHENIE_ZAIAV_ZALICHAVANE = 3;
	
	
	/** собственик - класиф. Тип на връзка МПС - лице (528) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK = 1;
	
	/** наемател - класиф. Тип на връзка МПС - лице (528) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_MPS_NAEMATEL = 2;
	
	/** собственик - класиф. Видове фуражни продукти и СЖП (531) */
	public static final int	CODE_ZNACHENIE_SJP_PP = 105;
	
	/** Лице собственик - класиф. Роля на лице в обект (535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK = 1;

	/** Лице ползвател - класиф. Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OBEKT_POLZVATEL = 2;
	/** Квалифицирано лице - класиф. Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OBEKT_KVALIF_LICE = 5;
	/** Отговорно лице - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OBEKT_OTG = 6;
	
	/** Вет. лекар - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_VET_LEKAR = 11;
	/** Управител - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL = 12;
	/** Оператор - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OPERATOR = 13;
	/** Притежател на лиценз за търговия на ВЛП - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_PRITEJ_LICENZ = 15;
	
	/** Отговорно лице за пускане на продукт на пазара(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_OTG_PAZAR = 16;
	
	/**Квалифицирано лице по фармакологична бдителност(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_FARM_BDITELNOST = 17;
	
	/** Квалифицирано лице по фармакологична бдителност (535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL_OTG_RELEASE = 20;
	
	
	/** Производител - класиф.Роля на лице в обект(535) */
	public static final int	CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL = 19;
	
	/** Лиценз за международен превоз - класификация "Вид документ на лице"(537) */
	public static final int	CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ = 6;
	/** Лиценз за вътрешен превоз - класификация "Вид документ на лице"(537) */
	public static final int	CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ = 7;
	/** Диплома - класификация "Вид документ на лице"(537) */
	public static final int	CODE_ZNACHENIE_VIDDOC_DIPLOMA = 4;
	/** Удостоверение за членство в БВС - класификация "Вид документ на лице"(537) */
	public static final int	CODE_ZNACHENIE_VIDDOC_UDOST_BVS = 5;

	/** За контрол на качеството - класификация "Предназначение на обект (539)" */
	public static final int	CODE_ZNACHENIE_PREDNAZN_KONTRL_KACH = 3;
	
	/** класификация "Вид на подмярка от ПРСР" 559 */
	/** подмярка 5.1 - класификация "Вид на подмярка от ПРСР" (559) */
	public static final int	CODE_ZNACHENIE_MIARKA_5_1 = 5;
	
	/** класификация "Обекта на лицензиране" 567 */
	/** ОЕЗ */
	public static final int	CODE_ZNACHENIE_OBEKT_LICENZ_OEZ = 1;
	/** Лице */
	public static final int	CODE_ZNACHENIE_OBEKT_LICENZ_LICE = 2;
	/**МПС */
	public static final int	CODE_ZNACHENIE_TIP_LICENZ_MPS = 3;
	/** ВЛЗ */
	public static final int	CODE_ZNACHENIE_TIP_LICENZ_VLZ = 4;

	/** Активни - класиф. Вид вещества за ВЛП (568) */
	public static final int CODE_ZNACHENIE_VID_VESHTESTVO_AKTIVNO = 1;
	/** Помощни - класиф. Вид вещества за ВЛП (568) */
	public static final int CODE_ZNACHENIE_VID_VESHTESTVO_POM = 2;

	/** значение "Китай"  класификация 607 */
	public static final int CODE_ZNACHENIE_DARJ_KITAI = 97;
	
	/** класификация "Статус на ОЕЗ"  618*/
	public static final int	CODE_ZNACHENIE_STATUS_ACTIVE = 1;
	
	/** значение "ръководител на екип"  класификация 622 */
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP = 4;
	/** значение "член на екип"  класификация 622 */
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CHLEN_NA_EKIP = 5;
	/** значение "Предлаган притежател на лиценз"  класификация 622*/
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PREDL_PRITEJ_LICENZ = 10;
	/** значение "Производител"  класификация 622*/
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL = 13;
	/** значение "Отговорно лице по чл. 35 от ЗКНВП"  класификация 622*/
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_OTG_LICE = 14;
	/** значение "следящ за спазването на изискванията за хуманно отношение към животните"  класификация 622*/
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_HUMANNO_OTN = 28;
	/** значение "контролиращ"  класификация 622*/
	public static final int	CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE = 34;
	
	/** значение "Производител, отговорен за освобождаване на партидата"  класификация 667*/
	public static final int	CODE_ZNACHENIE_VLP_LICE_TIP_PROIZVODITEL = 19;
	/** значение "Притежател на разрешение за търговия"  класификация 667*/
	public static final int	CODE_ZNACHENIE_VLP_LICE_TIP_PRITEZATEL = 15;
	
	
	
	
	/** значение "Регистър собствени документи - произволен номер" За регистратура БАБХ, класификация 144*/
	public static final int	CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER = 891;
	
	/** значение "Служебно лице " - класификация референти 52*/
	public static final int	CODE_ZNACHENIE_REF_SLUJEBEN = -3;
	

	/** константи за полето FromМigr в таблица vpisvane */
	/**0 - миграция, ръчно въвеждане*/
	public static final int	CODE_ZNACHENIE_MIGR_MANUAL = 0;
	/**1 - миграция - завършена*/
	public static final int	CODE_ZNACHENIE_MIGR_END = 1;
	/**1 - не е миграция*/
	public static final int	CODE_ZNACHENIE_MIGR_NO = 2;
	
	

	/** Код на "Ниво 0 - [TLP-WHITE]" класификация: "Ниво на класификация на документите" 715 */
	public static final int CODE_ZNACHENIE_TLP_LEVEL_WHITE = 1; 	
	/** Код на "Ниво 1 - [TLP-GREEN]" класификация: "Ниво на класификация на документите" 715*/
	public static final int CODE_ZNACHENIE_TLP_LEVEL_GREEN = 2; 	
	/** Код на "Ниво 2 - [TLP-AMBER]" класификация: "Ниво на класификация на документите" 715*/
	public static final int CODE_ZNACHENIE_TLP_LEVEL_AMBER = 3; 	
	/** Код на "Ниво 3 - [TLP-RED]"  класификация: "Ниво на класификация на документите" 715*/
	public static final int CODE_ZNACHENIE_TLP_LEVEL_RED = 4; 
	
	/** */
	private BabhConstants() {
		super();
	}
}