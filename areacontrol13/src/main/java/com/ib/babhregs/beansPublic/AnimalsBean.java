package com.ib.babhregs.beansPublic;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author n.kanev
 */
@Named("animalsBean")
@RequestScoped
public class AnimalsBean extends RegisterBean {

	private static final long serialVersionUID = 2987877177380309315L;

	@PostConstruct
	public void init() {
		
		String [] titles= {
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
				"regsZooObjects.title",};
		
		
		
		this.registri = new HashMap<>();
//		this.registri.put(4, "Обекти с епизоотично значение");
//		this.registri.put(6, "Търговци на животни, зародишни продукти, внос на пратки СЖП и производни продукти по чл. 71 от ЗВД");
//		this.registri.put(7, "Издадени разрешения за превоз на животни");
//		this.registri.put(8, "Транспортни средства, с които се превозват животни");
//		this.registri.put(9, "Обекти, в които се осъществява ветеринарно медицинска практика, и ветеринарните лекари, работещи в тях");
//		this.registri.put(11, "Издадени разрешителни за провеждане на опити с животни");
//		this.registri.put(12, "Дейности с идентификатори на животни");
//		this.registri.put(13, "Екипи, получили одобрение за добив и съхранение на ембриони и яйцеклетки");
//		this.registri.put(15, "Издадени Заповеди за проведени изложби, състезания и мероприятия с животни");
//		this.registri.put(16, "Фирми и центрове, предоставящи обучение в сферата на животновъдството");
//		this.registri.put(18, "Издадени удостоверения за правоспособност на водачи на транспортни средства, в които се превозват животни, и на придружители на животни при транспортиране");
//		this.registri.put(20, "Издадените разрешен за износ на живи животни и зародишни продукти");
//		this.registri.put(19, "Изплащане на обезщетения за унищожени животни");
		this.registri.put(18, new String[]{"Удостоверения за правоспособност на водачи на TC, в които се превозват животни, и на придружители на животни","register18.xhtml"});
		this.registri.put(72, new String[]{"Производителите на средства за идентификация на животните","register72.xhtml"});
		this.registri.put(73, new String[]{"Търговците на средства за идентификация на животните","register73.xhtml"});
		
		
		for(int i=0;i<titles.length;++i) {
			this.registri.put((47+i), new String[]{getMessageResourceString(LABELS, titles[i]).replace("Регистър на", ""),"registerAnimalDefault.xhtml?idReg="+(47+i)});
		}
		
		initFiltered();
	}

}
