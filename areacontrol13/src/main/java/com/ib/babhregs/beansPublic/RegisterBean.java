package com.ib.babhregs.beansPublic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ib.indexui.system.IndexUIbean;

public abstract class RegisterBean extends IndexUIbean {

	private static final long serialVersionUID = 6917554020236457861L;

	protected Map<Integer, String[]> registri;
	protected Map<Integer, String[]> registriFiltered;
	protected String filterText;
	
	@PostConstruct
	public abstract void init(); 
	
	public void onFilterText() {
		
		if(this.filterText.trim().length() < 3) return;
		
		this.registriFiltered.clear();
		
		this.registri.forEach((k, v) -> {
			if(v[0].trim().toUpperCase().contains(this.filterText.trim().toUpperCase())) {
				this.registriFiltered.put(k, v);
			}
		});

	}
	
	protected void initFiltered() {
		this.registriFiltered = new HashMap<>();
		this.registri.forEach((k, v) -> {
			this.registriFiltered.put(k, v);
		});
	}

	public Map<Integer, String[]> getRegistriFiltered() {
		return registriFiltered;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}
}
