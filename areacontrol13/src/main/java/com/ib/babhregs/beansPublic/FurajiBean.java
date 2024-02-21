package com.ib.babhregs.beansPublic;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author n.kanev
 */
@Named("furajiBean")
@RequestScoped
public class FurajiBean extends RegisterBean {

	private static final long serialVersionUID = 2008172978614102697L;

	@PostConstruct
	public void init() {

		this.registri = new HashMap<>();
//		this.registri.put(31, "Одобрени обекти в сектор \"Фуражи\" по чл.20, ал. 3 от Закона за фуражите");
//		this.registri.put(32, "Регистрирани обекти в сектор \"Фуражи\" по чл. 17, ал. 3 от Закона за фуражите");
//		this.registri.put(33, "Оператори, транспортиращи фуражи съгласно чл. 17б, ал. 2 от Закона за фуражите");
//		this.registri.put(34, "Одобрени обекти за производство и/или търговия с медикаментозни фуражи");
//		this.registri.put(125, "Оператори, внасящи фуражи от трети държави по чл. 9 или чл. 10 от Закона за фуражите");
//		this.registri.put(123, "Предприятия, одобрени за износ на фуражи за трети страни	");
//		this.registri.put(43, "Транспортни средства за превоз на странични животински продукти");
//		this.registri.put(44, "Оператори, обекти и предприятия, боравещи със странични животински продукти");
//		this.registri.put(45, "Оператори с издаден ветеринарен сертификат/друг документ за износ на СЖП и ПП в трети държави по чл.238, ал.1 от ЗВД");
//		this.registri.put(46, "Издадени становища на земеделски производители, кандидатстващи по подмерки 4.1, 4.2, 5.1, 6.1, 6.3 от ПРСР в сектор \"Фуражи\"");
//		this.registri.put(124, "Издадени сертификати за добра практика");

		initFiltered();
	}
}
