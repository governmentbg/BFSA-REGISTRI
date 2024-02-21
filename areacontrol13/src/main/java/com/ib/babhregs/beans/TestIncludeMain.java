package com.ib.babhregs.beans;

import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.io.Serializable;

@Named
@RequestScoped
public class TestIncludeMain implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestIncludeMain.class);



      @Inject
    @ManagedProperty(value="#{testIncludeDetail}")
    TestIncludeDetail detail;

//    @Inject
//    Provider<TestIncludeDetail> a;
    private String testMain1 = "Some text in Main";

    @PostConstruct
    public void init(){
        LOGGER.info("PostConstruct");
        LOGGER.info("Value in detail is:{}",detail.getTest1());
        detail.setTest1("value from Main");
//        LOGGER.info("get value:{}",a.get().getTest1());
    }
    public String getTestMain1() {
        return testMain1;
    }

    public void setTestMain1(String testMain1) {
        this.testMain1 = testMain1;
    }
    public TestIncludeDetail getDetail() {
        return detail;
    }


}
