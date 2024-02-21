
package eu.europa.ema.v1_26.form;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "form"
})
@XmlRootElement(name = "data", namespace = "http://www.xfa.org/schema/xfa-data/1.0/")
public class Data {

    @XmlElement(name = "eu_application_form", namespace = "http://www.ema.eaf/maa/")
    EuApplicationForm form;

	public EuApplicationForm getForm() {
		return form;
	}

	public void setForm(EuApplicationForm form) {
		this.form = form;
	}
    

}
