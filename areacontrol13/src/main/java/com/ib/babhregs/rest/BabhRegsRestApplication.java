package com.ib.babhregs.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Map;
import java.util.Set;

/**
 * Class registers REST endpoints capabilities into the application
 * @author nikolai.kosev
 */
@ApplicationPath("rest")
public class BabhRegsRestApplication extends Application {
    public BabhRegsRestApplication() {
        super();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return super.getClasses();
    }

    @Override
    public Set<Object> getSingletons() {
        return super.getSingletons();
    }

    @Override
    public Map<String, Object> getProperties() {
        return super.getProperties();
    }
}
