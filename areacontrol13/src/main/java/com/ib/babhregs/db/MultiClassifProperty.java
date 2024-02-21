package com.ib.babhregs.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Идеята този интефейс е да може да се използва за множествени класификационни атрибути.</br>
 * Данните за класификацията са в отделна таблица, където да има ФК от основната таблица и код на класификация.</br>
 * По този начин в основния обект, класификационните значения ще са само кодове, което ще улесни използването им през компонентите
 * за класификации.</br>
 * При запис ще се правят сравнения на промените и няма да се пускат корекции към БД при липса на промени.</br>
 * А в случая за БАБХ не винаги ще са нужни някои множествени атрибути и няма да се теглят изобщо.
 *
 * @author belev
 */
public interface MultiClassifProperty extends Serializable {

	/**
	 * @return ИД на обекта от основната таблица.
	 */
	Integer getParentId();

	/**
	 * Създава ново с възможност да се запише с JPA.
	 *
	 * @see {@link EntityManager#persist(Object)}
	 * @param parentId ИД на обекта от основната таблица
	 * @param code     кода от класификацията
	 * @return
	 */
	MultiClassifProperty createNew(Integer parentId, Integer code);

	/**
	 * Създава Query за изтриване.
	 *
	 * @see {@link Query#executeUpdate()}
	 * @param parentId ИД на обекта от основната таблица
	 * @param codeList тези кодове, които не трябват, за да може няколко на веднъж с една заявка.
	 * @return
	 */
	Query createQueryDelete(Integer parentId, List<Integer> codeList);
}
