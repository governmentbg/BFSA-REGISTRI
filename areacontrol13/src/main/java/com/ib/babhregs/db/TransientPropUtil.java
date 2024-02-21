package com.ib.babhregs.db;

import java.util.ArrayList;
import java.util.List;

import com.ib.system.db.AbstractDAO;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;

/**
 * Помощни методи за обработка на transient полета в обектите. Принципно ако нещата са ОК и е удобно може да се качат тези методи
 * в {@link AbstractDAO}. За сега само пробно тук.
 *
 * @author belev
 */
public class TransientPropUtil {

	/**
	 * Запис/изтриване на множествени класификационни атрибути
	 *
	 * @param dbList      кодовете на класиф. от БД, като може да са null или празно
	 * @param currentList кодовете на класиф. от екрана, като може да са null или празно
	 * @param blank       помощен обект от типа, който ще се обработва.</br>
	 *                    ВАЖНО ! в него трябва да е сетнат съответния ParentId !
	 * @throws DbErrorException
	 */
	public static void saveMultiClassifPropList(List<Integer> dbList, List<Integer> currentList, MultiClassifProperty blank) throws DbErrorException {
		if (blank.getParentId() == null) {
			throw new IllegalArgumentException("Системна грешка! За " + blank.getClass().getSimpleName() + " не е сетната стойност за ParentId!");
		}

		// dbList !!! каквото остане тук е за изтриване
		List<Integer> newList = new ArrayList<>(); // !!! каквото остане тука е нов запис

		if (dbList == null || dbList.isEmpty()) { // нямало е нищо в БД
			newList = currentList; // всичко от екрана е ново

		} else if (currentList != null && !currentList.isEmpty()) { // има и от БД и от екрана

			for (Integer code : currentList) { // трябва да се гледат разлики

				if (!dbList.remove(code)) { // ако не е махнат значи текущия е новодобавен
					newList.add(code);
				}
			}
		} // за else остава да е имало нещо в БД и сега от екрана да няма нищо->всичко за триене

		try {
			if (dbList != null && !dbList.isEmpty()) { // delete
				blank.createQueryDelete(blank.getParentId(), dbList).executeUpdate();
			}
			if (newList != null && !newList.isEmpty()) { // save
				for (Integer code : newList) {
					JPA.getUtil().getEntityManager().persist(blank.createNew(blank.getParentId(), code));
				}
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при запис на свързан обект " + blank.getClass(), e);
		}
	}

	/** */
	private TransientPropUtil() {
		super();
	}
}
