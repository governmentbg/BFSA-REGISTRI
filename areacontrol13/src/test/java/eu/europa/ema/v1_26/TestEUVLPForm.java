package eu.europa.ema.v1_26;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.beans.RegTargovtsiFuraj;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.VlpVeshtva;
import com.ib.babhregs.db.dto.VlpVidJiv;
import com.ib.babhregs.system.SystemData;
//import com.ib.system.db.dto.SystemTranscode;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.JAXBHelper;
import com.ib.system.utils.SearchUtils;

import eu.europa.ema.v1_26.form.Atc.AtcCode;
import eu.europa.ema.v1_26.form.Data;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.LegalStatus;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode.Section213.TargetSpecies;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.TypeOfApplication.Concerns;
import eu.europa.ema.v1_26.form.Ingredient;
import eu.europa.ema.v1_26.form.Packages.Package;
import eu.europa.ema.v1_26.form.Packages.Package.Containers.Container;
import eu.europa.ema.v1_26.form.Packages.Package.Containers.Container.ContainerDetails;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormNames;
import eu.europa.ema.v1_26.form.RouteOfAdministrations.RouteOfAdministration;
import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.SelfInjection.Split;
import eu.europa.ema.v1_26.form.ShelfLife;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestEUVLPForm {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestEUVLPForm.class);

	public static void main(String[] args) {

		try {
			
			String fileName = "D:\\_VLP\\eAF-Pyrocam-20-inj-Huvepharma\\eAF-Pyrocam-20-inj-Huvepharma_data.xml";
			//String fileName = "D:\\_VLP\\eAF-Hydrotrim-SDZ-Huvepharma\\eAF-Hydrotrim-SDZ-Huvepharma_data.xml";
			//String fileName = "D:\\_VLP\\eAF-clavusan-500-125\\eAF-clavusan-500-125_data.xml";
			
			byte[] bytes = FileUtils.getBytesFromFile(new File(fileName));
			
			String xml = new String(bytes);
			
			Vlp vlp = new EAFUtils().parseXML(xml, new SystemData());
			
			if (vlp.getVlpVeshtva() != null) {
				for (VlpVeshtva v : vlp.getVlpVeshtva()) {
					System.out.println(v.getVidIdentifier().getIdentifier() + " --> " + v.getVidIdentifier().getName());
				}
			}
	       

	      
	        
//	        
			
			System.out.println("---------END------------");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}

	}
	
	
	
	
	

}
