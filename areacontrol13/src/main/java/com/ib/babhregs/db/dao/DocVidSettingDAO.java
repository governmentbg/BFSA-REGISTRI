package com.ib.babhregs.db.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ib.babhregs.db.dto.DocVidSetting;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.SelectMetadata;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link DocVidSetting}
 *
 * @author belev
 */
public class DocVidSettingDAO extends AbstractDAO<DocVidSetting> {

	/** @param user */
	public DocVidSettingDAO(ActiveUser user) {
		super(DocVidSetting.class, user);
	}

	/**
	 * Намира х-ките за подадената регистраура, като изтегля само данните от вида:<br>
	 * [0]-ID<br>
	 * [1]-DOC_VID<br>
	 * [2]-REGISTER_ID<br>
	 * [3]-ACT_NOMER<br>
	 * [4]-PROC_DEF_IN<br>
	 * [5]-PROC_DEF_OWN<br>
	 * [6]-PROC_DEF_WORK<br>
	 *
	 * @param registraturaId
	 * @return
	 */
	public SelectMetadata createSelectMetadataByRegistraturaId(Integer registraturaId) {
		Map<String, Object> params = new HashMap<>();

		String select = " select s.ID a0, s.DOC_VID a1, s.REGISTER_ID a2, s.ACT_NOMER a3, defIn.DEF_ID a4, defOwn.DEF_ID a5, defWork.DEF_ID a6, m.TEKST a7, r.REGISTER a8 ";
		StringBuilder from = new StringBuilder(" from DOC_VID_SETTINGS s ");

		from.append(" left outer join PROC_DEF defIn on defIn.DEF_ID = s.PROC_DEF_IN and defIn.STATUS = :defActSts ");
		from.append(" left outer join PROC_DEF defOwn on defOwn.DEF_ID = s.PROC_DEF_OWN and defOwn.STATUS = :defActSts ");
		from.append(" left outer join PROC_DEF defWork on defWork.DEF_ID = s.PROC_DEF_WORK and defWork.STATUS = :defActSts ");

		from.append(" inner join SYSTEM_CLASSIF c on c.CODE_CLASSIF = :cVidDoc and c.CODE = s.DOC_VID ");
		from.append("     and c.ID = (select max (v.ID) from SYSTEM_CLASSIF v where v.CODE_CLASSIF = c.CODE_CLASSIF and v.CODE = c.CODE) ");
		from.append(" inner join SYSCLASSIF_MULTILANG m on m.TEKST_KEY = c.TEKST_KEY and m.LANG = :langBG ");
		from.append(" left outer join REGISTRI r on r.REGISTER_ID = s.REGISTER_ID ");
		
		String where = " where s.REGISTRATURA_ID = :registratura ";

		params.put("registratura", registraturaId);
		params.put("defActSts", BabhConstants.CODE_ZNACHENIE_PROC_DEF_STAT_ACTIVE);

		params.put("cVidDoc", BabhConstants.CODE_CLASSIF_DOC_VID);
		params.put("langBG", SysConstants.CODE_LANG_BG);

		SelectMetadata sm = new SelectMetadata();

		sm.setSql(select + from + where);
		sm.setSqlCount(" select count(*) " + from + where);
		sm.setSqlParameters(params);

		return sm;
	}

	/** */
	@Override
	public void delete(DocVidSetting entity) throws DbErrorException, ObjectInUseException {
		if (entity.getRegisterId() != null) { // ако е празно няма смисъл тази валидация
			Number cnt = (Number) createNativeQuery("select count (*) as cnt from DOC where REGISTER_ID = :regId and DOC_VID = :docVid") //
				.setParameter("regId", entity.getRegisterId()).setParameter("docVid", entity.getDocVid()) //
				.getResultList().get(0);
			if (cnt.intValue() > 0) {
				throw new ObjectInUseException("В регистъра по вид документ има регистрирани документи и не може да бъде изтрит!");
			}
		}

		super.delete(entity);
	}

	/** */
	@Override
	public DocVidSetting findById(Object id) throws DbErrorException {
		DocVidSetting setting = super.findById(id);

		if (setting == null) {
			return setting;
		}
		setting.setDbPrefix(SearchUtils.trimToNULL(setting.getPrefix()));

		return setting;
	}

	/**
	 * @param entity
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public DocVidSetting save(DocVidSetting entity, BaseSystemData sd) throws DbErrorException, ObjectInUseException {
		entity.setPrefix(SearchUtils.trimToNULL(entity.getPrefix())); // за да е винаги еднакво

		if (entity.getId() != null) {
			if (!Objects.equals(entity.getPrefix(), entity.getDbPrefix())) { // трябва да се провери дали няма документи с този
																				// регистър
				Number cnt; // ако има вече документи трябва да се даде грешка
				try {
					cnt = (Number) createNativeQuery("select count (*) as cnt from DOC where REGISTER_ID = :regId and DOC_VID = :docVid") //
						.setParameter("regId", entity.getRegisterId()).setParameter("docVid", entity.getDocVid()) //
						.getResultList().get(0);
				} catch (Exception e) {
					throw new DbErrorException("Грешка при търсене на документи за регистър по вид документ!", e);
				}
				if (cnt.intValue() > 0) {
					String error = "Префиксът не може да бъде променен, защото в регистъра по вид документ има регистрирани документи!";

					throw new ObjectInUseException(error);
				}
			}
		}

		DocVidSetting saved = super.save(entity);

		saved.setDbPrefix(entity.getPrefix());

		return saved;
	}

}