package com.ib.babhregs.db.dao;


import java.util.HashMap;
import java.util.Map;

import com.ib.system.db.SelectMetadata;
import com.ib.system.utils.SearchUtils;

import eu.europa.ema.v1_26.form.Substance;


public class SubstanceDAO  {
	
	private static final String	AND		= " AND ";
	private static final String	WHERE	= " WHERE ";
	
	
	public SelectMetadata buildSelectMetaDataSubstance(String substanceCode, String substanceName) {
		SelectMetadata resultSMD = new SelectMetadata();
		Map<String, Object> params = new HashMap<>();

		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		select.append(" select s.identifier a0, s.name a1 ");
		from.append(" from substances s ");

		/** kod */
		String t = SearchUtils.trimToNULL_Upper(substanceCode);
		if (t != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " upper( s.identifier ) like :code ");
			params.put("code", "%" + t + "%");
		}

		/** name */
		t = SearchUtils.trimToNULL_Upper(substanceName);
		if (t != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " upper(s.name) like :name ");
			params.put("name", "%" + t + "%");
		}


		resultSMD.setSqlCount(" select count(1) " + from.toString() + where.toString());
		resultSMD.setSql(select.toString() + from.toString() + where.toString());
		resultSMD.setSqlParameters(params);
		return resultSMD;
	}

}
