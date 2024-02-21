package com.ib.babhregs.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.indexui.system.IndexUIbean;

@Named
@ViewScoped
public class PublicRegistersBean extends IndexUIbean  implements Serializable {
	/**
	 * Списък на публичните регистри
	 */
	private static final long serialVersionUID = -5368283227142989807L;
	private static final Logger LOGGER = LoggerFactory.getLogger(PublicRegistersBean.class);
	private List<Object[]> regsList; 
	
	private String [] titles= {
			"regsChickenCages.title",
			"regsChickenFlock.title",
			"regsChickenWater.title",
			"regsChickenFarms.title",
			"regsChickenOthers.title",
			"regsIncubators.title",
			"regsBees.title",
			"regsAquaCultures.title",
			"regsHostelsAnimals.title",
			"regsDogFarms.title",
			"regsPetHostels.title",
			"regsFurryDeer.title",
			"regsAnimalObjectsBig.title",
			"regsAnimalObjectsSmall.title",
			"regsAnimalObjectsHoof.title",
			"regsPigObjects.title",
			"regsAnimalObjectsOthers.title",
			"regsPrivateFarms.title",
			"regsLiveAnimalMarkets.title",
			"regsCollectingAnimals.title",
			"regsAnimalTransportBreaks.title",
			"regsEmbrionsInstall.title",
			"regsZooObjects.title",
			//"regsAnimalTraders.title",
			//"regsEmbrioTraders.title"
			};
	
	
	@PostConstruct
	void initData() {

		regsList =  new ArrayList<Object[]>();
		//label, page name, regID
		for(int i=0;i<titles.length;++i) {
			Object[] reg = {getMessageResourceString(LABELS, titles[i]),"regsAnimalDefault",47+i};
			regsList.add(reg); 
		}
		
		Object[] reg = {getMessageResourceString(LABELS, "regsSellersAnim.title"),"regsSellersAnim",""};
		regsList.add(reg); 
		
		Object[] regEmbrion = {getMessageResourceString(LABELS, "regsSellersFodder.title"),"regsSellersEmbrion",""};
		regsList.add(regEmbrion); 
		
//		Object[] regAppr = {getMessageResourceString(LABELS, "regsApprovedSellers.title"),"regsApprovedSellers",""};
//		regsList.add(regAppr); дублира се с Регистър на одобрените обекти, съгласно чл.20, ал. 3 от Закона за фуражите
		
//		Object[] regWholesel = {getMessageResourceString(LABELS, "regsWholesalersVLP.title"),"regsWholesalersVLP",""};
//		regsList.add(regWholesel);
		
		Object[] regIdentif72 = {"Регистър на производителите на средства за идентификация на животните","regsSellersIdentif72",""};
		regsList.add(regIdentif72);
		
		Object[] regIdentif73 = {"Регистър на търговците на средства за идентификация на животните","regsSellersIdentif73",""};
		regsList.add(regIdentif73);
		
		Object[] regsMeropriatia = {getMessageResourceString(LABELS, "regsMeropriatia.title"),"regsMeropriatia",""};
		regsList.add(regsMeropriatia);
		
		Object[] regsOrgsCources1 = {getMessageResourceString(LABELS, "regsOrgsCources.title1"),"regsOrgsCources","132"};
		regsList.add(regsOrgsCources1);
		
		Object[] regsOrgsCources2 = {getMessageResourceString(LABELS, "regsOrgsCources.title2"),"regsOrgsCources","133"};
		regsList.add(regsOrgsCources2);
		
		Object[] regsTransportSvidetelstvo = {getMessageResourceString(LABELS, "regsTransportSvidetelstvo.title"),"regsTransportSvidetelstvo",""};
		regsList.add(regsTransportSvidetelstvo);
		
		
		Object[] regsMPSAnimals = {getMessageResourceString(LABELS, "regsMPSAnimals.title"),"regsMPSAnimals",""};
		regsList.add(regsMPSAnimals);
		
//		Object[] regsApprovedMPStransport = {getMessageResourceString(LABELS, "regsMPSAnimals.title"),"regsApprovedMPStransport",""};
//		regsList.add(regsApprovedMPStransport);
		

		
		Object[] regsEmbrionTeams = {getMessageResourceString(LABELS, "regsEmbrionTeams.title"),"regsEmbrionTeams",""};
		regsList.add(regsEmbrionTeams);
		
		Object[] regsAnimalTrials = {getMessageResourceString(LABELS, "regsAnimalTrials.title"),"regsAnimalTrials",""};
		regsList.add(regsAnimalTrials);
		
		Object[] regsPayDeadAnimals= {getMessageResourceString(LABELS, "regsPayDeadAnimals.title"),"regsPayDeadAnimals",""};
		regsList.add(regsPayDeadAnimals);
		
		Object[] regsSellAnimalsAbroad = {getMessageResourceString(LABELS, "regsSellAnimalsAbroad.title"),"regsSellAnimalsAbroad",""};
		regsList.add(regsSellAnimalsAbroad);
		

		
//		Object[] regsListVetsVLZ = {getMessageResourceString(LABELS, "regsListVetsVLZ.title"),"regsListVetsVLZ",""};
//		regsList.add(regsListVetsVLZ);

//		Object[] reg48 = {"Регистър на ферми за развъдни стада птици","reg48",""}; регистъра се повтаря с 2рия от анимал Дефаулт
//		regsList.add(reg48);
//		Object[] reg31 = {"Регистър на одобрените обекти в сектор Фуражи по чл.20, ал. 3 от Закона за фуражите","reg31","31"};
//		regsList.add(reg31);
		
		Object[] reg31 = {getMessageResourceString(LABELS, "regsFurajniDobavki.title"),"regsFurajniDobavki",""};
		regsList.add(reg31);
		
//		Object[] reg32 = {"Регистър на регистрираните обекти в сектор „Фуражи“ по чл. 17, ал. 3 от Закона за фуражите","reg32","32"};
//		regsList.add(reg32);
		
		Object[] reg32 = {getMessageResourceString(LABELS, "regsRegObecti17.title"),"regsRegObecti17",""};
		regsList.add(reg32);
		
		Object[] regsListMpsSJP = {getMessageResourceString(LABELS, "regsListMpsSJP.title"),"regsListMpsSJP",""};
		regsList.add(regsListMpsSJP);
		
		
		Object[] regsImportThirdCountries1 = {getMessageResourceString(LABELS, "regsImportThirdCountries1.title"),"regsImportThirdCountries","35"};
		regsList.add(regsImportThirdCountries1);
		Object[] regsImportThirdCountries2 = {getMessageResourceString(LABELS, "regsImportThirdCountries2.title"),"regsImportThirdCountries","36"};
		regsList.add(regsImportThirdCountries2);
		
		
		Object[] regsThirdCountries1 = {getMessageResourceString(LABELS, "regsThirdCountries1.title"),"regsThirdCountries","37"};
		regsList.add(regsThirdCountries1);
		Object[] regsThirdCountries2 = {getMessageResourceString(LABELS, "regsThirdCountries2.title"),"regsThirdCountries","38"};
		regsList.add(regsThirdCountries2);
		Object[] regsThirdCountries3 = {getMessageResourceString(LABELS, "regsThirdCountries3.title"),"regsThirdCountries","39"};
		regsList.add(regsThirdCountries3);
		Object[] regsThirdCountries4 = {getMessageResourceString(LABELS, "regsThirdCountries4.title"),"regsThirdCountries","40"};
		regsList.add(regsThirdCountries4);
		Object[] regsThirdCountries5 = {getMessageResourceString(LABELS, "regsThirdCountries5.title"),"regsThirdCountries","41"};
		regsList.add(regsThirdCountries5);
		Object[] regsThirdCountries6 = {getMessageResourceString(LABELS, "regsThirdCountries6.title"),"regsThirdCountries","42"};
		regsList.add(regsThirdCountries6);
		Object[] regsThirdCountries7 = {getMessageResourceString(LABELS, "regsThirdCountries7.title"),"regsThirdCountries","126"};
		regsList.add(regsThirdCountries7);
		
//		Object[] regsMedFuraji1 = {getMessageResourceString(LABELS, "regsMedFuraji1.title"),"regsMedFuraji","79"};
//		regsList.add(regsMedFuraji1);
//		Object[] regsMedFuraji2 = {getMessageResourceString(LABELS, "regsMedFuraji2.title"),"regsMedFuraji","80"};
//		regsList.add(regsMedFuraji2);
		
		Object[] regsMedFuraji = {getMessageResourceString(LABELS, "regsMedFuraji.title"),"regsMedFuraji",""};
		regsList.add(regsMedFuraji);
		
		
		Object[] regsProizvoditeliMiarka41 = {getMessageResourceString(LABELS, "regsProizvoditeliMiarka41.title"),"regsProizvoditeliMiarka","127"};
		regsList.add(regsProizvoditeliMiarka41);
		Object[] regsProizvoditeliMiarka42 = {getMessageResourceString(LABELS, "regsProizvoditeliMiarka42.title"),"regsProizvoditeliMiarka","128"};
		regsList.add(regsProizvoditeliMiarka42);
		Object[] regsProizvoditeliMiarka51 = {getMessageResourceString(LABELS, "regsProizvoditeliMiarka51.title"),"regsProizvoditeliMiarka","129"};
		regsList.add(regsProizvoditeliMiarka51);
		Object[] regsProizvoditeliMiarka61 = {getMessageResourceString(LABELS, "regsProizvoditeliMiarka61.title"),"regsProizvoditeliMiarka","130"};
		regsList.add(regsProizvoditeliMiarka61);
		Object[] regsProizvoditeliMiarka63 = {getMessageResourceString(LABELS, "regsProizvoditeliMiarka63.title"),"regsProizvoditeliMiarka","131"};
		regsList.add(regsProizvoditeliMiarka63);
		Object[] regsFurajiOperatorTransport17 = {getMessageResourceString(LABELS, "regsFurajiOperatorTransport17.title"),"regsFurajiOperatorTransport17",""};
		regsList.add(regsFurajiOperatorTransport17);
		
//		Object[] reg44 = {"Регистър на операторите, обектите и предприятията, боравещи със странични животински продукти","reg44","44"};
//		regsList.add(reg44);
		
		Object[] regsObjectsSJP = {getMessageResourceString(LABELS, "regsObjectsSJP.title"),"regsObjectsSJP",""};
		regsList.add(regsObjectsSJP);
		
		Object[] regsGoodPractices = {getMessageResourceString(LABELS, "regsGoodPractices.title"),"regsGoodPractices",""};
		regsList.add(regsGoodPractices);
		
		Object[] reg120 = {"Регистър на издадените разрешения за производство или внос на ВЛП","reg120","120"};//Rosi код 121 се обединяват в един s 120
		regsList.add(reg120);
//		Object[] reg120 = {"Регистър на издадените разрешения за производство на ВЛП","reg120","120"};//star
//		regsList.add(reg120);
//		Object[] reg121 = {"Регистър на издадените разрешения за внос на ВЛП ","reg120","121"};//Rosi Регистъра за внос - с код 121 е изтрит
//		regsList.add(reg121);
//		Object[] reg29 = {"Списък на търговците с разрешение за продажба на ВЛП от разстояние","reg0","29"};
//		regsList.add(reg29);
//		Object[] reg122 = {"Списък на търговците с разрешение за паралелна търговия с ВЛП","reg0","122"};
//		regsList.add(reg122);
		Object[] reg29 = {"Списък на търговците с разрешения за продажба от разстояние или паралелна търговия с ВЛП","reg0","29"};
		regsList.add(reg29);

		Object[] reg23 = {"Регистър на издадените разрешения за търговия на едро с ВЛП","reg24","23"};
		regsList.add(reg23);
		Object[] reg24 = {"Регистър на издадените разрешения за търговия на дребно с ВЛП ","reg24","24"};
		regsList.add(reg24);
		Object[] reg28 = {"Регистър на одобрените реклами","reg28","28"};
		regsList.add(reg28);
		Object[] reg30 = {"Списък на регистрираните производители, вносители и разпространители на активни вещества ","reg30","30"};
		regsList.add(reg30);
		Object[] reg27 = {"Регистър на инвитро диагностичните ВЛП ","reg27","27"};
		regsList.add(reg27);
		Object[] reg26 = {"Регистър на издадените разрешения за дейности с наркотични вещества за ветеринарномедицински цели ","reg26","26"};
		regsList.add(reg26);
		Object[] reg25 = {"Регистър на издадените разрешения за търговия с ВЛП","reg25","25"};
		regsList.add(reg25);

	
		           

	}
	
	public String actionGoTo(String link, String idReg) {
							
		if(link!=null && !"".equals(link)) {
			link+=".jsf?faces-redirect=true";
			if(idReg!=null && !"".equals(idReg)) {
				link+="&idReg=" + idReg;
			}
		}else {
			link = "empty.jsf?faces-redirect=true";
		}
		return link;
	}

	public List<Object[]> getRegsList() {
		return regsList;
	}

	public void setRegsList(List<Object[]> regsList) {
		this.regsList = regsList;
	}	
}