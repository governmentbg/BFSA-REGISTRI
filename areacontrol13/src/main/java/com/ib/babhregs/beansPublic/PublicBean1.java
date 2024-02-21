package com.ib.babhregs.beansPublic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@RequestScoped
@Named
public class PublicBean1 {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicBean1.class);




    String name=this.getClass().getName();

    public String getName() {
        return name;
    }
}
