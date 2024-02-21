package com.ib.babhregs.system;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.quartz.GetFromDeloWebListener;

public class QuartzStartInitializationListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzStartInitializationListener.class);
    private Scheduler scheduler;

    public QuartzStartInitializationListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent ctx) {
        LOGGER.debug("QuartzStartInitializationListener contextInitialized");
        try {
            scheduler = ((StdSchedulerFactory) ctx.getServletContext()
                    .getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY)).getScheduler();

            if (scheduler == null) {
                LOGGER.debug("NUUUUUUUUUUUUUUUL");
            } else {
                LOGGER.debug("--- start ---");
            }
            scheduler.getListenerManager().addJobListener(new GetFromDeloWebListener("Get data from DeloWeb"), KeyMatcher.keyEquals(new JobKey("DocsFromDeloWebJob", "SystemTriggersIB")));




        } catch (SchedulerException e) {
            LOGGER.error("Error",e);

        }




    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
