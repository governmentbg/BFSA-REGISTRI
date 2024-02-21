package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.ib.babhregs.db.dto.ShablonLogic;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link ShablonLogic}
 *
 * @author belev
 */
public class ShablonLogicDAO extends AbstractDAO<ShablonLogic> {

	/** @param user */
	public ShablonLogicDAO(ActiveUser user) {
		super(ShablonLogic.class, user);
	}

	/**
	 * Зарежда обекта за конкретен файл
	 *
	 * @param fileId
	 * @return
	 * @throws DbErrorException
	 */
	public ShablonLogic findByFileId(Integer fileId) throws DbErrorException {
		if (fileId == null) {
			return null;
		}

		ShablonLogic entity;
		try {
			@SuppressWarnings("unchecked")
			List<ShablonLogic> list = createQuery("select x from ShablonLogic x where x.fileId = ?1") //
					.setParameter(1, fileId).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

			entity.getBookmarks().size(); // lazy

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на ShablonLogic по fileId=" + fileId, e);
		}
		return entity;
	}

	/** */
	@Override
	public ShablonLogic findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}

		ShablonLogic entity = super.findById(id);
		if (entity == null) {
			return entity;
		}
		entity.getBookmarks().size(); // lazy

		return entity;
	}

	/**
	 * @return distinct na всички имена на методи
	 * @throws DbErrorException
	 */
	public List<Object> findCurrentlyUsedMethodNames() throws DbErrorException {
		try {
			@SuppressWarnings("unchecked")
			List<Object> list = createNativeQuery( //
					"select distinct(method_name) from shablon_bookmarks order by 1").getResultList();
			return list;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на имена на методи на шаблони.", e);
		}
	}

	/**
	 * Търсене на Логика за попълване на шаблон по име на метод, който се използва.</br>
	 * [0]-logic_id</br>
	 * [1]-file_id</br>
	 * [2]-filename</br>
	 *
	 * @param methodName
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> findMethodUsage(String methodName) throws DbErrorException {
		methodName = SearchUtils.trimToNULL(methodName);
		if (methodName == null) {
			return new ArrayList<>();
		}

		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select distinct l.id logic_id, l.file_id, f.filename ");
			sql.append(" from shablon_bookmarks b ");
			sql.append(" inner join shablon_logic l on l.id = b.shablon_logic_id ");
			sql.append(" inner join files f on f.file_id = l.file_id ");
			sql.append(" where upper(b.method_name) = ?1 ");
			sql.append(" order by f.filename ");

			@SuppressWarnings("unchecked")
			List<Object[]> list = createNativeQuery(sql.toString()).setParameter(1, methodName.toUpperCase()) //
					.getResultList();
			return list;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на Логика за попълване на шаблон по име на метод = " + methodName, e);
		}
	}

	/**
	 * Търсене на файлове темплейти през х-ки на вид документ.</br>
	 * [0]-file_id</br>
	 * [1]-filename</br>
	 * [2]-logic_id - ако няма дефинирано ще е НУЛЛ</br>
	 * [3]-doc_vid</br>
	 *
	 * @param methodName
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> findTemplates() throws DbErrorException {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select f.file_id, f.filename, l.id logic_id, s.doc_vid ");
			sql.append(" from file_objects fo ");
			sql.append(" inner join files f on f.file_id = fo.file_id ");
			sql.append(" inner join doc_vid_settings s on s.id = fo.object_id ");
			sql.append(" left outer join shablon_logic l on l.file_id = f.file_id ");
			sql.append(" where fo.object_code = ?1 ");
			sql.append(" order by f.filename ");

			@SuppressWarnings("unchecked")
			List<Object[]> list = createNativeQuery(sql.toString()) //
					.setParameter(1, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC_VID_SETT) //
					.getResultList();
			return list;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на файлове темплейти", e);
		}
	}

	/**
	 * Коригира имената на методите
	 *
	 * @return броя на коригираните
	 * @throws DbErrorException
	 */
	public int renameUsedMethod(String oldName, String newName) throws DbErrorException {
		// TODO някакво журналиране е добре да се сложи
		try {
			int cnt = createNativeQuery( //
					"update shablon_bookmarks set method_name = :newName where method_name = :oldName") //
					.setParameter("oldName", oldName).setParameter("newName", newName).executeUpdate();
			return cnt;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на имена на методи на шаблони.", e);
		}
	}

	/**
	 * Получава ИД на файл с уърдовски шаблон и връща списък с деловодните документите,
	 * които могат да го ползват за генериране на удостоверителен документ.
	 * Използва се от екрана за попълване на шаблоните за УД с тестова цел.
	 * 
	 * <ul>
	 *   <li>[0] Doc.id</li>
	 *   <li>[1] Vpisvane.id</li>
	 *   <li>[2] Doc.otnosno</li>
	 *   <li>[3] Doc.rnDoc</li>
	 *   <li>[4] Vpisvane.status</li>
	 * </ul>
	 * 
	 * @param fileId ИД на файла от Files.id
	 */
	public List<Object[]> getDocAndVpisvaneThatUseShablon(Integer fileId) throws DbErrorException {
		try {
			
			String sql = "select d.id, v.id, d.otnosno, d.rnDoc, v.status"
					+ " from Files f"
					+ " inner join FileObject fo on fo.fileId = f.id "
					+ " inner join DocVidSetting dvs on dvs.id = fo.objectId"
					+ " inner join Doc d on d.docVid = dvs.docVid"
					+ " inner join VpisvaneDoc vd on vd.idDoc = d.id"
					+ " inner join Vpisvane v on v.id = vd.idVpisvane"
					+ " where f.id = :fileId";
			
			@SuppressWarnings("unchecked")
			List<Object[]> results = createQuery(sql)
					.setParameter("fileId", fileId)
					.getResultList();
			return results;
		}
		catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на документи, които използват файла като шаблон за УД", e);
		}
	}
	
	/** */
	@Override
	public ShablonLogic save(ShablonLogic entity) throws DbErrorException {
		if (entity.getBookmarks() == null) {
			entity.setBookmarks(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		return super.save(entity);
	}
}
