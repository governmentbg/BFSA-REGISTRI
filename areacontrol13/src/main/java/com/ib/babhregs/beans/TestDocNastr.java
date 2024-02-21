package com.ib.babhregs.beans;

import com.ib.babhregs.db.dto.RegisterOptionsDisplay;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.system.IndexUIbean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class TestDocNastr extends IndexUIbean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDocNastr.class);
    Integer vidDoc;
    Integer idElement;


    public String actionTest(){
        LOGGER.info("actionTest--vidDco={},idElement={}",vidDoc,idElement);
        LOGGER.info("isElementVisible:{}",getSystemData(SystemData.class).isElementVisible(vidDoc,idElement));
//        getSystemData(SystemData.class).getDocElements().get(123).get(123)
        return "";
    }

    public String actionTest2(){
        LOGGER.info("actionTest--vidDco={}",vidDoc);
        LOGGER.info("isElementVisible:exists:{}, value{}",getSystemData(SystemData.class).getRegisterByVidDoc().containsKey(vidDoc),getSystemData(SystemData.class).getRegisterByVidDoc().get(vidDoc));

        getSystemData(SystemData.class).getRoptions().get(123).getCodeMainObject();
        return "";
    }
    public Integer getVidDoc() {
        return vidDoc;
    }

    public void setVidDoc(Integer vidDoc) {
        LOGGER.info("setVidCoc:{}",vidDoc);
        this.vidDoc = vidDoc;
    }



    public Integer getIdElement() {
        return idElement;
    }

    public void setIdElement(Integer idElement) {
        this.idElement = idElement;
    }


}
