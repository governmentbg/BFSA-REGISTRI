package com.ib.babhregs.beansPublic;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * @author n.kanev
 */
@Named("vlpBean")
@RequestScoped
public class VlpBean extends RegisterBean {

	private static final long serialVersionUID = -6120738768991207695L;

	@PostConstruct
	public void init() {

		this.registri = new HashMap<>();
//		this.registri.put(120, "Издадени разрешения за производство или внос на ВЛП");
//		this.registri.put(23, "Издадени разрешения за търговия на едро с ВЛП");
//		this.registri.put(24, "Издадени разрешения за търговия на дребно с ВЛП");
//		this.registri.put(25, "Издадени разрешения за търговия с ВЛП");
//		this.registri.put(26, "Издадени лицензии за дейности с наркотични вещества за ветеринарномедицински цели");
//		this.registri.put(27, "Инвитро диагностични ВЛП");
//		this.registri.put(28, "Одобрени реклами");
//		this.registri.put(30, "Регистрирани производители, вносители и разпространители на активни вещества");
//		this.registri.put(134, "Търговци с ВЛП с разрешения за продажба от разстояние");
//		this.registri.put(122, "Търговци с разрешение за паралелна търговия с ВЛП");

		initFiltered();
	}
}
