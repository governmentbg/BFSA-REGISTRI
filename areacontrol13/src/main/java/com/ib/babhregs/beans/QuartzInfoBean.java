package com.ib.babhregs.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.Trigger.TriggerState;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.search.JobHistorySearch;
import com.ib.system.utils.DateUtils;

/**
 * СИСТЕМНИ ПРОЦЕСИ
 */

@Named("quartzInfoEx")
@ViewScoped
public class QuartzInfoBean extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8725953233254590010L;
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzInfoBean.class);
	
	
	/**Търсене в историята на джобовете
	 * 
	 * !!! има подобен метод в JobHistoryDAO, но имената на колоните от таблицата са различни..
	 * */
	private transient JobHistorySearch jobSearch; 
	private LazyDataModelSQL2Array jobHistory;
	
	private Scheduler scheduler;
	
	private List<IBTriggers> allTrigger = new ArrayList<>();
	private List<String[]> testList = new ArrayList<String[]>();
	private IBTriggers selectedTrigger;
	private transient Object[] selectedHistory;

	private Integer period;
	private Date dateOt;
	private Date dateDo;

	@PostConstruct
	public void init() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("SYSTEM QUARTZ INIT!!!!");
		}

		try {
			
			loadTriggersAndJobs();
			
		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на системни процеси", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализиране на системни процеси!");
		}
	}

	
	/**
	 * Зарежда всички тригери и джобове
	 */
	@SuppressWarnings("unchecked")
	private void loadTriggersAndJobs() {
		
		try {
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			setScheduler(((StdSchedulerFactory) facesContext.getExternalContext().getApplicationMap().get(QuartzInitializerListener.QUARTZ_FACTORY_KEY)).getScheduler());
			String[] aa = { "13", "14", "15" };

			getTestList().add(aa);

			allTrigger.clear();
			Set<String> myGroups = new HashSet<>();

			myGroups.add("SystemTriggersIB"); // TODO освен това.. друго ще има ли?

			for (String groupName : scheduler.getJobGroupNames()) {
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Grop:{}", groupName);
				}
				
				if (!myGroups.contains(groupName)) {
					continue;
				}
				
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();
					String jobDescription = scheduler.getJobDetail(jobKey).getDescription();
					
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					Date nextFireTime = triggers.get(0).getNextFireTime();
					Date prevFireTime = triggers.get(0).getPreviousFireTime();
			
					allTrigger.add(new IBTriggers(triggers.get(0).getKey().getName(), groupName, triggers.get(0).getKey(), scheduler.getTriggerState(triggers.get(0).getKey()), prevFireTime, nextFireTime, jobName, jobGroup, jobKey, jobDescription));
				}
			}


		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на тригери и джобове " + e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Проблем с инициализацията на Quartz Scheduler");
			
		}
	}

	
	/**
	 * В зависимост от състоянието на тригера - Pause/Resume ако е в стейт != от
	 * NORMAL/PAUSE - не прави нищо
	 * 
	 * @param trgName
	 * @param trgGroup
	 */
	public void actionPauseResumeTrg(String trgName, String trgGroup) {
		LOGGER.debug("actionPauseResumeTrg:trgName:{},trgGroup:{}", trgName, trgGroup);
		
		try {
			TriggerKey trgKey = new TriggerKey(trgName, trgGroup);
			if (scheduler.getTriggerState(trgKey) == TriggerState.NORMAL) {
				scheduler.pauseTrigger(new TriggerKey(trgName, trgGroup));
			} else if (scheduler.getTriggerState(trgKey) == TriggerState.PAUSED) {
				scheduler.resumeTrigger(trgKey);
			}
			
			loadTriggersAndJobs();
			
		} catch (SchedulerException e) {
			LOGGER.error("Грешка при промяна на състиянието на тригера " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при промяна на състиянието на тригера" + e.getMessage());
		}

	}

	public void actionFireTrigger(String jobName, String jobGroup) {
		LOGGER.debug("actionFireTrigger:jobName:{},jobGroup {}", jobName, jobGroup);
		
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			scheduler.triggerJob(jobKey);

			loadTriggersAndJobs();
			
		} catch (SchedulerException e) {
			LOGGER.error("Грешка при промяна на състиянието на тригера " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при промяна на състиянието на тригера" + e.getMessage());
		}

	}

	public void actionStartStopScheduler() {
		try {
			
			if (scheduler.getMetaData().isStarted()) {
				scheduler.shutdown();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Scheduler was stopped");
			} else {
				scheduler.start();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Scheduler was started");
			}
		} catch (SchedulerException e) {
			LOGGER.error("Грешка при стартиране/спиране на процеси " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
	}

	public void actionStandbyOnOff() {
		try {
			
			if (!scheduler.getMetaData().isInStandbyMode()) {
				scheduler.standby();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Scheduler was put in standby");
			} else {
				scheduler.start();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Scheduler was started");
			}
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
	}

	/**
	 * Дава историята от IBG_JOBS zза конкретен job trgName -то ни трябва единствено
	 * за да маркираме дали job-a минал регулярно или е стартиран звънредно (ако
	 * IBG_JOBE.TRG_NAME != trgName => e пуснат ръчно. По някаква причина при
	 * извънредно пускане TR)GEER_NAME е някакво ебемсимамата странно
	 * 
	 * @param jobName
	 * @param trgName
	 * @param dateOt
	 * @param dateDo
	 */
	public void actionGetJobHistory(Date dateOt, Date dateDo, String jobName, String trgName) {
		LOGGER.debug("actionGetJobHistory:jobName: {},trgName:{}", jobName, trgName);

		if (jobName == null || jobName.isEmpty() || trgName == null || trgName.isEmpty()) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "actionGetJobHistory:Empti parameter");
			return;
		}

		try {
			
			this.jobSearch = new JobHistorySearch();
			this.jobSearch.setJobKeyName(jobName);
			this.jobSearch.setTrigKeyName(trgName);
			
			if (this.dateOt != null) {
				jobSearch.setStartTime(this.dateOt);
			} 
			
			if (this.dateDo != null) {
				jobSearch.setEndTime(this.dateDo);
			}
			
			jobSearch.buildQuery();
			jobHistory = new LazyDataModelSQL2Array(jobSearch, "START_TIME desc");

		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на история " + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на история" + e.getMessage());
		}
	}

	public void actionSelectTrigger() {
		this.period = 13;
		actionPeriod();
		actionSearchTrigger();
	}

	
	public void actionSearchTrigger() {
		LOGGER.info(this.selectedTrigger.getJobName());
		actionGetJobHistory(this.dateOt, this.dateDo, this.selectedTrigger.getJobName(), this.selectedTrigger.getTriggerName());
	}

	
	public String getImageSource(Integer historyStatus) {

		if (historyStatus == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Невалиден параметър");
			return null;
		}

		String imageSource;
		switch (historyStatus) {
			case 1:
				imageSource = "ready.png"; // изпълнена
				break;
			case 2:
				imageSource = "warning.png"; // внимание
				break;
			case 3:
				imageSource = "error.png"; // грешка
				break;
			case 4:
				imageSource = "readyEmpty.png"; // без информация
				break;
			default:
				imageSource = "readyEmpty.png";
		}
		return imageSource;
	}

	
	public String getImageSourceAlt(Integer historyStatus) {

		if (historyStatus == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Невалиден параметър");
			return null;
		}

		String imageSource;
		switch (historyStatus) {
			case 1:
				imageSource = "Изпълнена";
				break;
			case 2:
				imageSource = "Внимание";
				break;
			case 3:
				imageSource = "Грешка";
				break;
			case 4:
				imageSource = "Без информация";
				break;
			default:
				imageSource = "Без информация";
		}
		return imageSource;
	}

	public String actionPeriod() {

		if (this.period != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.period);
			this.dateOt = di[0];
			this.dateDo = di[1];

		} else {
			setDateOt(null);
			setDateDo(null);
		}

		return null;
	}

	public void changeDate() {
		this.setPeriod(null);
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<IBTriggers> getAllTrigger() {
		LOGGER.debug("------------- get all triggers");
		return allTrigger;
	}

	public void setAllTrigger(List<IBTriggers> allTrigger) {
		this.allTrigger = allTrigger;
	}

	public List<String[]> getTestList() {
		return testList;
	}

	public void setTestList(List<String[]> testList) {
		this.testList = testList;
	}

	public IBTriggers getSelectedTrigger() {
		return selectedTrigger;
	}

	public void setSelectedTrigger(IBTriggers selectedTrigger) {
		this.selectedTrigger = selectedTrigger;
	}

	public Object[] getSelectedHistory() {
		return selectedHistory;
	}

	public void setSelectedHistory(Object[] selectedHistory) {
		this.selectedHistory = selectedHistory;
	}

	public Date getToday() {
		return new Date();
	}

	public Date getDateOt() {
		return dateOt;
	}

	public void setDateOt(Date dateOt) {
		this.dateOt = dateOt;
	}

	public Date getDateDo() {
		return dateDo;
	}

	public void setDateDo(Date dateDo) {
		this.dateDo = dateDo;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	public LazyDataModelSQL2Array getJobHistory() {
		return jobHistory;
	}

	public void setJobHistory(LazyDataModelSQL2Array jobHistory) {
		this.jobHistory = jobHistory;
	}
	
	public JobHistorySearch getJobSearch() {
		return jobSearch;
	}

	public void setJobSearch(JobHistorySearch jobSearch) {
		this.jobSearch = jobSearch;
	}



	
	/********************************************** IBTriggers *****************************************************************/
	
	public class IBTriggers implements Serializable {
		
		private static final long serialVersionUID = -7312050128775328291L;
		String triggerName;
		String triggerGroup;
		TriggerKey triggerKey;
		TriggerState triggerState;
		Date triggerPrevFireTime;
		Date triggerNextFireTime;
		String jobName;
		String jobGroup;
		JobKey jobKey;
		String jobDescription;

		public IBTriggers(String triggerName, String triggerGroup, TriggerKey triggerKey, TriggerState triggerState, Date triggerPrevFireTime, Date triggerNextFireTime, String jobName, String jobGroup, JobKey jobKey, String jobDescription) {
			super();
			this.triggerName = triggerName;
			this.triggerGroup = triggerGroup;
			this.triggerKey = triggerKey;
			this.triggerState = triggerState;
			this.triggerPrevFireTime = triggerPrevFireTime;
			this.triggerNextFireTime = triggerNextFireTime;
			this.jobName = jobName;
			this.jobGroup = jobGroup;
			this.jobKey = jobKey;
			this.jobDescription = jobDescription;
		}

		public String getTriggerName() {
			return triggerName;
		}

		public String getTriggerGroup() {
			return triggerGroup;
		}

		public TriggerKey getTriggerKey() {
			return triggerKey;
		}

		public TriggerState getTriggerState() {
			return triggerState;
		}

		public Date getTriggerPrevFireTime() {
			return triggerPrevFireTime;
		}

		public Date getTriggerNextFireTime() {
			return triggerNextFireTime;
		}

		public String getJobName() {
			return jobName;
		}

		public String getJobGroup() {
			return jobGroup;
		}

		public JobKey getJobKey() {
			return jobKey;
		}

		public String getJobDescription() {
			return jobDescription;
		}

	}

}
