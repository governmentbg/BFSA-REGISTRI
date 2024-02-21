package com.ib.babhregs.db.dao;

import static com.ib.system.SysConstants.CODE_DEIN_IZTRIVANE;
import static com.ib.system.utils.SearchUtils.trimToNULL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.UserNotifications;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.DialectConstructor;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

public class UserNotificationsDAO  {
	
	
	public UserNotifications findById(Integer id) throws DbErrorException {
		LOGGER.debug("Търси се обект от тип: UserNotifications ID=" + id);
		try {
			return JPA.getUtil().getEntityManager().find(UserNotifications.class, id);
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
	}
	
	
	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationsDAO.class);
	
	
	public void save(UserNotifications entity) throws DbErrorException {
		try {
			JPA.getUtil().getEntityManager().persist(entity);
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
	}
	
	
	
	public void delete(UserNotifications entity) throws DbErrorException, ObjectInUseException {
		LOGGER.debug("BEGIN Изтриване от базата на обект : {}", entity);

		boolean attached = JPA.getUtil().getEntityManager().contains(entity);
		if (!attached) {
			LOGGER.debug("ATTACH dettached object before DELETE: {}", entity);
			entity = findById(entity.getId());
		}
		
		JPA.getUtil().getEntityManager().remove(entity);

		LOGGER.debug("SUCCESSFUL Изтриване от базата на обект : {}", entity);
	}
	
	public void changeStatusMessage(Integer idMessage, Integer status) throws DbErrorException {
		
		try {
			Query q = JPA.getUtil().getEntityManager().createNativeQuery("update USER_NOTIFICATIONS set ibread = ? where id = ?");
			q.setParameter(1, status);
			q.setParameter(2, idMessage);
			q.executeUpdate();
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
		
	}
	
	public void changeStatusMessagesUser(Integer userId, Integer status) throws DbErrorException {
		
		try {
			Query q = JPA.getUtil().getEntityManager().createNativeQuery("update USER_NOTIFICATIONS set ibread = ? where USER_ID = ?");
			q.setParameter(1, status);
			q.setParameter(2, userId);
			q.executeUpdate();
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
		
	}
	
	public void changeStatusMessages(List <Integer> idMessages, Integer status) throws DbErrorException {
		
		try {
			
			float size = idMessages.size();
			float stupka = 500;
			
			double repeat = Math.ceil(size/stupka);
			
			for(int i=0; i<repeat;i++){
				int k = i;
				int start = (k*(int)stupka);
				int stop  = ((++k)*(int)stupka);
				if(stop > idMessages.size())stop = idMessages.size();
			
				Collection<Integer> params = new ArrayList<Integer>();
				for(int j=start; j<stop;j++){
					params.add(idMessages.get(j));
				}
			
				Query q = JPA.getUtil().getEntityManager().createNativeQuery("update USER_NOTIFICATIONS set ibread = :stat where id in (:ids)");
				q.setParameter("stat", status);
				q.setParameter("ids", params);
				q.executeUpdate();
			}
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
		
	}
	
	/** Връща списък на нотификации на потребител
	 * @param idUser - системен идентификатор на потребител
	 * @param status - прочететно/непрочетено (в BabhConstants  -> CODE_NOTIF_STATUS_NEPROCHETENA/CODE_NOTIF_STATUS_PROCHETENA)
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<UserNotifications> findUserNotifications(Integer idUser, Integer status, Integer registraturaId) throws DbErrorException {
		if (registraturaId == null) {  // от миграция може да има потребители, които не са вързани с регистратура
			return new ArrayList<>();
		}
		String dialect = JPA.getUtil().getDbVendorName();
		
		ArrayList<UserNotifications> notifications = new ArrayList<UserNotifications>();
		try {
			String sql = "SELECT " + 
					"	un.ID, " + 
					"	un.USER_ID, " + 
					"	un.DATE_MESSAGE, " + 
					"	un.TITLE, " + 
					DialectConstructor.limitBigString(dialect, "un.DETAILS", 300) + " DETAILS, " +
					"	un.SEVERITY, " + 
					"	un.IBREAD, " + 
					"	un.TASK_ID, " + 
					"	un.OBJECT_CODE, " + 
					"	un.OBJECT_ID, " + 
					"	un.EMAIL_TO, " + 
					"	un.SENT_TO_EMAIL, " + 
					"	un.MESSAGE_TYPE, " + 
					"	un.REGISTRATURA_ID, " + 
					" FROM " + 
					"	USER_NOTIFICATIONS un " + 
					" where un.ibread = ?1 and un.USER_ID = ?2 and (un.REGISTRATURA_ID = ?3 or un.REGISTRATURA_ID is null) order by un.DATE_MESSAGE desc";
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			q.setParameter(1, status);
			q.setParameter(2, idUser);
			q.setParameter(3, registraturaId);
			ArrayList<Object[]> rows = (ArrayList<Object[]>) q.getResultList();
			for (Object[] row : rows) {
				UserNotifications notif = new UserNotifications();
				notif.setId(SearchUtils.asInteger(row[0]));
				notif.setIdUser(SearchUtils.asInteger(row[1]));
				notif.setDateMessage(SearchUtils.asDate(row[2]));
				notif.setTitle(SearchUtils.asString(row[3]));
				notif.setDetails(SearchUtils.asString(row[4]));
				notif.setSeverity(SearchUtils.asString(row[5]));
				notif.setRead(SearchUtils.asInteger(row[6]));
				// В БАБХ НЯМА ЗАДАЧИ	notif.setTaskId(SearchUtils.asInteger(row[7]));
				notif.setCodeObject(SearchUtils.asInteger(row[8]));
				notif.setIdObject(SearchUtils.asInteger(row[9]));
				notif.setEmailTo(SearchUtils.asString(row[10]));
				notif.setSentToEmail(SearchUtils.asInteger(row[11]));
				notif.setMessageType(SearchUtils.asInteger(row[12]));
				notif.setRegistraturaId(SearchUtils.asInteger(row[13]));
				// В БАБХ НЯМА ЗАДАЧИ	notif.setTaskStatus(SearchUtils.asInteger(row[14]));
				// В БАБХ НЯМА ЗАДАЧИ	notif.setTaskDocId(SearchUtils.asInteger(row[15]));
				notifications.add(notif);
			}
			  
			  return notifications;
		} catch (Exception e) {			
			throw new DbErrorException(e);
		}
		
	}
	
	public Integer countUserNotifications(Integer idUser, Integer status, Integer registraturaId) throws DbErrorException {
		if (registraturaId == null) {
			return 0; // от миграция може да има потребители, които не са вързани с регистратура
		}
		try {
			Query q = JPA.getUtil().getEntityManager().createNativeQuery("select count(id) from USER_NOTIFICATIONS where ibread = ?1 and user_id = ?2 and (REGISTRATURA_ID = ?3 or REGISTRATURA_ID is null)");
			q.setParameter(1, status);
			q.setParameter(2, idUser);
			q.setParameter(3, registraturaId);
			 Number singleResult = (Number)q.getResultList().get(0);
			return  singleResult.intValue();
		} catch (Exception e) {
			throw new DbErrorException(e);
		}
	}
	
	public SelectMetadata filterNotifications(Date dateFrom, Date dateTo, Integer messageType, Integer readMess, String title, String details, Integer usersAccess, List<Integer> userIdList) {
		
		SelectMetadata smd = new SelectMetadata();
		
		Map<String, Object> params = new HashMap<>();
		String dialect = JPA.getUtil().getDbVendorName();
		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		select.append(" SELECT UN.id A0, UN.DATE_MESSAGE A1, UN.MESSAGE_TYPE A2, UN.IBREAD A3, UN.TITLE A4, ");
		select.append(DialectConstructor.limitBigString(dialect, "UN.DETAILS", 300) + " A5 ");
		from.append(" FROM USER_NOTIFICATIONS UN  ");
		if (usersAccess != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " UN.USER_ID = :uaP ");
			params.put("uaP", usersAccess);
		} else {
			select.append(" , UN.USER_ID A6 ");
		}
		if (userIdList != null && !userIdList.isEmpty()) {
			where.append((where.length() == 0 ? " where " : " and ") + " UN.USER_ID in (:userIdList) ");
			params.put("userIdList", userIdList);
		}
		
		if (dateFrom != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " UN.DATE_MESSAGE >= :regDateFrom ");
			params.put("regDateFrom", DateUtils.startDate(dateFrom));
		}
		if (dateTo != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " UN.DATE_MESSAGE <= :regDateTo ");
			params.put("regDateTo", DateUtils.endDate(dateTo));
		}
		
		if (messageType != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " UN.MESSAGE_TYPE = :messType ");
			params.put("messType", messageType);
		}
		
		if(readMess != null) {
			if(readMess.intValue() == 1)
				where.append((where.length() == 0 ? " where " : " and ") + " UN.IBREAD = 1 ");
			else
				where.append((where.length() == 0 ? " where " : " and ") + " UN.IBREAD = 2 ");
		}

		String t = trimToNULL(title);
		if (t != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " upper(UN.TITLE) like :title ");
			params.put("title", "%" + t.toUpperCase() + "%");
		}

		t = SearchUtils.trimToNULL_Upper(details);
		if (t != null) {
			where.append((where.length() == 0 ? " where " : " and ") + " upper(UN.DETAILS) like :details ");
			params.put("details", "%" + t + "%");
		}

		smd.setSqlCount(" select count(*) " + from + where); // на този етап бройката е готова

		smd.setSql(select.toString() + from.toString() + where.toString());

		smd.setSqlParameters(params);
		
		return smd;
	}
	
	public void deleteSelected(List<Object[]> selected, Integer userId, BaseSystemData sd) throws DbErrorException {
		if (selected == null || selected.isEmpty()) {
			return;
		}
		
		try {
			List<Integer> idList = new ArrayList<>(selected.size());
			for (Object[] row : selected) {
				idList.add(((Number)row[0]).intValue());
			}
			
			float size = idList.size();
			float stupka = 500;
			
			double repeat = Math.ceil(size/stupka);
			
			for(int i=0; i<repeat;i++){
				int k = i;
				int start = (k*(int)stupka);
				int stop  = ((++k)*(int)stupka);
				if(stop > idList.size())stop = idList.size();
			
				Collection<Integer> params = new ArrayList<Integer>();
				for(int j=start; j<stop;j++){
					params.add(idList.get(j));
				}
		
				@SuppressWarnings("unchecked")
				List<Object[]> toJournal = JPA.getUtil().getEntityManager().createNativeQuery(
					"select ID, USER_ID, TITLE, DETAILS from USER_NOTIFICATIONS where ID in (?1)")
					.setParameter(1, params).getResultList();
				journalNotifDelete(userId, toJournal, sd);
				
				Query query = JPA.getUtil().getEntityManager().createNativeQuery(
					"delete from USER_NOTIFICATIONS where ID in (:idList)")
					.setParameter("idList", params);
				
				int cnt = query.executeUpdate();
				
				LOGGER.debug("deleted {} USER_NOTIFICATIONS", cnt);
			}
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на нотификации!", e);
		}
	}
	
	public int deleteByPeriod(Date from, Date to, Integer userId, BaseSystemData sd) throws DbErrorException {
		if (to == null) { // задължително е
			return 0;
		}
		try {
			from = from == null ? DateUtils.systemMinDate() : DateUtils.startDate(from);
			to = DateUtils.endDate(to);
			
			@SuppressWarnings("unchecked")
			List<Object[]> toJournal = JPA.getUtil().getEntityManager().createNativeQuery(
				"select ID, USER_ID, TITLE, DETAILS from USER_NOTIFICATIONS where IBREAD = :da and DATE_MESSAGE >= :dateFrom and DATE_MESSAGE <= :dateTo")
				.setParameter("da", SysConstants.CODE_ZNACHENIE_DA).setParameter("dateFrom", from).setParameter("dateTo", to)
				.getResultList();
			journalNotifDelete(userId, toJournal, sd);
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(
				"delete from USER_NOTIFICATIONS where IBREAD = :da and DATE_MESSAGE >= :dateFrom and DATE_MESSAGE <= :dateTo")
				.setParameter("da", SysConstants.CODE_ZNACHENIE_DA).setParameter("dateFrom", from).setParameter("dateTo", to);
			
			int cnt = query.executeUpdate();
			
			LOGGER.debug("deleted {} USER_NOTIFICATIONS", cnt);
	
			return cnt;
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на нотификации!", e);
		}
	}

	/**
	 * [0]-ID
	 * [1]-USER_ID
	 * [2]-TITLE
	 * [3]-DETAILS
	 */
	void journalNotifDelete(Integer userId, List<Object[]> selected, BaseSystemData sd) throws DbErrorException {
		for (Object[] row : selected) {
			int id = ((Number)row[0]).intValue();
			int to = ((Number)row[1]).intValue();
			
			String identObject = "Вид: " + row[2] + 
				"; Текст: " + row[3] + 
				"; Изпратена до: " + sd.decodeItem(SysConstants.CODE_CLASSIF_USERS, to, SysConstants.CODE_DEFAULT_LANG, null) + ".";
			
			SystemJournal journal = new SystemJournal(userId, CODE_DEIN_IZTRIVANE, BabhConstants.CODE_ZNACHENIE_JOURNAL_NOTIF, id, identObject, null);
			JPA.getUtil().getEntityManager().persist(journal);
		}
	}
}