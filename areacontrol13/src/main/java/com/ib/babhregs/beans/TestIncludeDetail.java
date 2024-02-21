package com.ib.babhregs.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;

@Named
@Dependent
public class TestIncludeDetail implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestIncludeDetail.class);


    //@Produces
    private String test1="aaa";

    private String srcName="testIncludeDetail.xhtml";
    public String getTest1() {
        LOGGER.info("getTest1:{}",test1);
        return test1;
    }


    public void setTest1(String test1) {
        LOGGER.info("SetTest1:{}",test1);
        this.test1 = test1;
    }

    public String getSrcName() {

        return srcName;
    }
}
