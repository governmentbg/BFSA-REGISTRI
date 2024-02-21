package eu.europa.ema.v1_26;

import static com.ib.system.utils.SearchUtils.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Substance;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.VlpLice;
import com.ib.babhregs.db.dto.VlpPrilagane;
import com.ib.babhregs.db.dto.VlpPrilaganeVid;
import com.ib.babhregs.db.dto.VlpVeshtva;
import com.ib.babhregs.db.dto.VlpVidJiv;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemTranscode;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.JAXBHelper;
import com.ib.system.utils.SearchUtils;

import eu.europa.ema.v1_26.form.AdminOffice;
import eu.europa.ema.v1_26.form.Atc.AtcCode;
import eu.europa.ema.v1_26.form.ContactDetailsType;
import eu.europa.ema.v1_26.form.Data;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.ContactPharmacoVigilances.ContactPharmacoVigilance;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.LegalStatus;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode.Section213.TargetSpecies;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode.Section214.WithdrawalPeriod;
import eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.TypeOfApplication.Concerns;
import eu.europa.ema.v1_26.form.Ingredient;
import eu.europa.ema.v1_26.form.ManuFacility;
import eu.europa.ema.v1_26.form.ManufacturerActiveSubstanceVet;
import eu.europa.ema.v1_26.form.ManufacturerActiveSubstanceVet.ActiveSubstances;
import eu.europa.ema.v1_26.form.ManufacturerActiveSubstanceVet.ManuSteps;
import eu.europa.ema.v1_26.form.ManufacturerBatchRelease;
import eu.europa.ema.v1_26.form.ManufacturerPharmaceuticalProduct;
import eu.europa.ema.v1_26.form.MarketingAuthorisationHolder;
import eu.europa.ema.v1_26.form.MedicinalProduct;
import eu.europa.ema.v1_26.form.Packages.Package;
import eu.europa.ema.v1_26.form.Packages.Package.Containers.Container;
import eu.europa.ema.v1_26.form.Packages.Package.Containers.Container.ContainerDetails;
import eu.europa.ema.v1_26.form.Packages.Package.PackageSize;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormDetails;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormDetails.ActiveSubstances.ActiveSubstancesDetails;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormDetails.Excipients;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormDetails.Excipients.ExcipientsDetails;
import eu.europa.ema.v1_26.form.PharmaceuticalProduct.PharmaceuticalFormNames;
import eu.europa.ema.v1_26.form.RouteOfAdministrations.RouteOfAdministration;
import eu.europa.ema.v1_26.form.ShelfLife;
import eu.europa.ema.v1_26.form.SubstanceCertificateVet;

public class EAFUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EAFUtils.class);
	
	public Vlp parseXML (String xml, SystemData sd) throws UnexpectedResultException, DbErrorException {
		
		Data data = null;
		Vlp vlp = new Vlp();
		vlp.setSrok("");
		vlp.setVlpLice(new ArrayList<VlpLice>());
		
		
		HashMap<String, String> timeMap = createTimeMap();
		String pre = " ";
		
		
		
		ArrayList<String> warnings = new ArrayList<String>();
		
		
		
		if (xml != null) {
			try {
				data = JAXBHelper.xmlToObject(Data.class, xml);
			} catch (Exception e) {
				LOGGER.error("Грешка при конвертиране на XML", e);
				throw new UnexpectedResultException("Грешка при конвертиране на XML", e);
			}
			
		}
		
		
		//2.4.1
		///data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/marketing-authorisation-holders/marketing-authorisation-holder
		
		//2.4.4
		///data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/contact-pharmaco-vigilances/contact-pharmaco-vigilance/contact-details
		
		//2.5.1.
		//data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/manufacturer-batch-releases/manufacturer-batch-release
		
		if (data != null && data.getForm() != null) {
			
			
			if (data.getForm().getInitialApplicationFormVeterinary() != null) {
				
				InitialApplicationFormVeterinary appFromVet = data.getForm().getInitialApplicationFormVeterinary();
				
				
				if (appFromVet.getMarketingAuthorisationApplicationParticulars() != null) {
					
					MarketingAuthorisationApplicationParticulars maap = appFromVet.getMarketingAuthorisationApplicationParticulars();
					
					///data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/names_and_atc_code/section2-1-4
					if (maap.getNamesAndAtcCode() != null) {
						
						for (NamesAndAtcCode nameAndCode : maap.getNamesAndAtcCode()) {
						
							if (nameAndCode != null && nameAndCode.getSection214() != null && nameAndCode.getSection214().getWithdrawalPeriod() != null && nameAndCode.getSection214().getWithdrawalPeriodCheck() != 0) {
								
								String karent = "";
								for (WithdrawalPeriod period : nameAndCode.getSection214().getWithdrawalPeriod() ) {
									
									String upotreba = "";
									if (period.getRouteOfAdministration() != null) {
										for (eu.europa.ema.v1_26.form.EuApplicationForm.InitialApplicationFormVeterinary.MarketingAuthorisationApplicationParticulars.NamesAndAtcCode.Section214.WithdrawalPeriod.RouteOfAdministration route : period.getRouteOfAdministration()) {
											String codeExt = route.getCode();
											String nachin = route.getName();
											if (! isEmpty(codeExt)) {
												List<SystemClassif> result = sd.getItemsByCodeExt(685, codeExt, 1, new Date());
												if (result.size() > 0) {
													nachin = result.get(0).getTekst();
												}
												if (! isEmpty(nachin)) {
													upotreba += nachin + ",";
												}
											}
										}
									}
									if (! isEmpty(upotreba)) {
										upotreba = upotreba.substring(0, upotreba.length()-1) + " за ";
									}
									
									String jivotno = period.getTargetSpecies().substring(period.getTargetSpecies().indexOf("|")+ 1);									
									Integer jivCode = transodeString(period.getTargetSpecies(), 677, warnings, sd);
									if (jivCode != null) {
										jivotno = sd.decodeItem(677, jivCode, 1, new Date());
									}
									
									String product = "";
									if ( ! isEmpty(period.getTargetTissue())) { 									
										product = sd.decodeByExtCode(692, period.getTargetTissue().substring(0, period.getTargetTissue().indexOf("|")), 1, new Date());
									}
									
									String vreme = timeMap.get(period.getUnits().toUpperCase());
									if (vreme == null) {
										vreme = "";
									}
									if (! isEmpty(period.getPeriod())){
										karent += upotreba + jivotno + ": " + product + " - " + period.getPeriod() + " " + vreme + "\r\n";
									}
									if (!isEmpty(period.getNote())) {
										karent += "    ЗАБЕЛЕЖКА: " + period.getNote() + "\r\n";
									}
								}
								
								
								vlp.setKarentenSrok(karent);
							}
							
							
							if (nameAndCode != null && nameAndCode.getSection211() != null && nameAndCode.getSection211().getInventedName() != null) {
								vlp.setNaimenovanieLat(nameAndCode.getSection211().getInventedName());
								vlp.setNaimenovanieCyr(nameAndCode.getSection211().getInventedName());
								LOGGER.debug("Търговско име на латиница: " + vlp.getNaimenovanieLat());
							}
							
							TreeSet<Integer> animals = new TreeSet<Integer>();
							for (TargetSpecies jiv: nameAndCode.getSection213().getTargetSpecies()) {
								//System.out.println(jiv.getSpecies());
								Integer jivCode = transodeString(jiv.getSpecies(), 677, warnings, sd);
								if (jivCode != null) {
									animals.add(jivCode);
								}
								LOGGER.debug("Животно 2.1.3: " + jiv.getSpecies() + " (transcoded to "+jivCode+")");
							}
							if (animals.size() > 0) {
								vlp.setVlpVidJiv(new ArrayList<VlpVidJiv>());
								for (Integer animal : animals) {
									VlpVidJiv jiv = new VlpVidJiv();
									jiv.setVidJiv(animal);
									vlp.getVlpVidJiv().add(jiv);
								}
							}
							
							
							TreeSet<String> atcCodes = new TreeSet<String>();
							for (AtcCode code: nameAndCode.getSection213().getAtc().getAtcCode() ) {
								System.out.println(code.getCode());		
								atcCodes.add(code.getCode());
								LOGGER.debug("ATC Код  2.1.3: " + code.getCode() );
							}
							if (atcCodes.size() > 0) {
								String codesString = "";
								for (String code : atcCodes) {
									codesString += code + ",";
								}
								codesString = codesString.substring(0, codesString.length()-1);
								vlp.setVetMedCode(codesString);
							}
							
							//System.out.println(nameAndCode.getSection213().getAtc().getAtcName());
							
							TreeSet<Integer> activeSubs = new TreeSet<Integer>();
							for (Ingredient subs : nameAndCode.getSection212().getActiveSubstances().getActiveSubstance()) {
								String code = subs.getSubstanceName().getCode() + "|" + subs.getSubstanceName().getName() ;
								
								Integer aCode = transodeString(code, 578, warnings, sd);
								if (aCode != null) {
									activeSubs.add(aCode);
								}
								LOGGER.debug("Активна съставка 2.1.2: " + code + " (transcoded to "+aCode+")");
							}
							
							
							
							
							
							
							
							
						}
					}
					
					TreeSet<Integer> phForms = new TreeSet<Integer>();
					if (maap.getMedicinalProduct() != null ) { 
						
						MedicinalProduct mp = maap.getMedicinalProduct();
						
						if (mp.getPharmaceuticalProducts() != null && mp.getPharmaceuticalProducts().getPharmaceuticalProduct() != null) {						
							for (PharmaceuticalProduct pProduct : mp.getPharmaceuticalProducts().getPharmaceuticalProduct()) {
								if (pProduct != null) {
									for (PharmaceuticalFormNames phName : pProduct.getPharmaceuticalFormNames()) {
										if (phName != null) {										
	//										Integer forma = transodeString(phName.getPharmaceuticalFormName(), 563, warnings, sd);
	//										LOGGER.debug("Фармацевтична форма 2.2: " + phName.getPharmaceuticalFormName() + " (transcoded to "+forma+")");
	//										if (forma != null) {
	//											phForms.add(forma);
	//										}
											String codeExt = phName.getPharmaceuticalFormName().substring(0, phName.getPharmaceuticalFormName().indexOf("|"));
											List<SystemClassif> result = sd.getItemsByCodeExt(693, codeExt, 1, new Date());
											if (result.size() > 0) {											
												phForms.add(result.get(0).getCode());
											}
											
										}
										
										
									}
								}
							}
						}
						
						String packages = "";
						if (mp.getPackages() != null && mp.getPackages().getPackage() != null) {	
							for (Package pack : mp.getPackages().getPackage()) {
								if (! SearchUtils.isEmpty(pack.getPackageName())) {
									packages += "Наименование а опаковката: " + pack.getPackageName() + "\r\n";
								}
								
								if ( pack.getPackageSize() != null && pack.getPackageSize().size() > 0) {
									packages += "Видове опаковки: \r\n";
									for (PackageSize ps : pack.getPackageSize()) {										
										packages += "\t" + ps.getSize() + "\r\n";
									}
									
								}
								
								if ( pack.getPackageDescription() != null) {
									packages += "Допълнително описание: " + pack.getPackageDescription() + "\r\n";
								}
								
							}
						}
						
						System.out.println("*************************************************************************************");
						System.out.println(packages);
						System.out.println("*************************************************************************************");
						vlp.setKolichestvaOpakovka(packages);
					}
					if (phForms.size() > 0) {
						vlp.setFarmFormList(new ArrayList<Integer>());
						vlp.getFarmFormList().addAll(phForms);
					}
					
					
					if (maap.getLegalStatus() != null) {
						LegalStatus ls = maap.getLegalStatus();
						Integer legalStatus = null;
						
						if (ls.getNoPrescription()==1  ) {
							legalStatus = 1;
						}
						
						if (ls.getNoPrescriptionRetailRestrictedNo()==1  ) {
							legalStatus = 4;
						}
						
						if (ls.getNoPrescriptionRetailRestrictedYes()==1  ) {
							legalStatus = 5;
						}
						
						
						if (ls.getPrescriptionExceptSomePackages()==1  ) {
							legalStatus = 3;
						}
						
						if (ls.getPrescriptionNarcoticsAdministration()==1  ) {
							legalStatus = 14;
						}
						
						if (ls.getPrescriptionNarcoticsSpecialSupervision()==1  ) {
							legalStatus = 13;
						}
						
						if (ls.getPrescriptionOfficialVet()==1  ) {
							legalStatus = 7;
						}
						
						if (ls.getPrescriptionPsychotropicAdministration()==1  ) {
							legalStatus = 10;
						}
						
						if (ls.getPrescriptionPsychotropicSpecialSupervision()==1  ) {
							legalStatus = 11;
						}
						
						if (ls.getPrescriptionQualifiedProfessionals()==1  ) {
							legalStatus = 6;
						}
						
						if (ls.getPrescriptionRenewableDelivery()==1  ) {
							legalStatus = 15;
						}
						
						if (ls.getPrescriptionRenewableDeliveryNo()==1  ) {
							legalStatus = 20;
						}
						
						if (ls.getPrescriptionRenewableDeliveryYes()==1  ) {
							legalStatus = 21;
						}
						
						if (ls.getPrescriptionRenewal()==1  ) {
							legalStatus = 12;
						}
						
						if (ls.getPrescriptionRenewalNo()==1  ) {
							legalStatus = 19;
						}
						
						if (ls.getPrescriptionRenewalYes()==1  ) {
							legalStatus = 18;
						}
						
						if (ls.getPrescriptionSpecialSupervision()==1  ) {
							legalStatus = 9;
						}
						
						if (ls.getPrescriptionVeterinarians()==1  ) {
							legalStatus = 8;
						}
						
						if (ls.getPrescriptionVeterinariansAdministration()==1  ) {
							legalStatus = 17;
						}
						
						if (ls.getPrescriptionVeterinariansUsePossession()==1  ) {
							legalStatus = 16;
						}
						
						if (ls.getPrescription()==1  ) {
							legalStatus = 2;
						}
						
						
						
					
						LOGGER.debug("Режим на отпускане 2.3" + legalStatus);
						vlp.setRejimOtpuskane(legalStatus);
						
					}
					
					if (maap.getMedicinalProduct() != null && maap.getMedicinalProduct().getPackages() != null && maap.getMedicinalProduct().getPackages().getPackage() != null) {
						
						vlp.setOpakovkaList(new ArrayList<Integer>());
						for (Package p : maap.getMedicinalProduct().getPackages().getPackage() ) {
							for (Container con : p.getContainers().getContainer()) {
								
								String curSrok = "";
								
								for (ContainerDetails det : con.getContainerDetails()) {										
									String codeExt = det.getContainerType();
									String device = det.getAdministrativeDevice();
									
									Integer code = transodeString(codeExt, 608, warnings, sd);									
									LOGGER.debug("Първична опаковка: " + codeExt + " (transcoded to "+code+")");
									if (! vlp.getOpakovkaList().contains(code)) {
										vlp.getOpakovkaList().add(code);									
										String tekst = sd.decodeItem(608, code, 1, new Date());
										curSrok += tekst + ",";
									}
									
									if (device != null && ! SearchUtils.isEmpty(device) && ! device.equalsIgnoreCase("n/a")) {
										//device = device.substring(0, device.indexOf("|"));
										code = transodeString(device, 608, warnings, sd);
										if (! vlp.getOpakovkaList().contains(code)) {
											vlp.getOpakovkaList().add(code);
										}
									}
									
								}
								
								if (! isEmpty(curSrok)) {
									curSrok = curSrok.substring(0, curSrok.length()-1) + ":   ";
								}
								
								for (ShelfLife shelfLife : con.getShelfLife()) {
									curSrok += shelfLife.getShelfLifeBeforeOpen() + " " + timeMap.get(shelfLife.getShelfLifeBeforeOpenUnits().toUpperCase()) + ";";
								}
								
								vlp.setSrok(curSrok);
							}
						}
						
						
					}
					
					
					if (maap.getMedicinalProduct() != null && maap.getMedicinalProduct().getRouteOfAdministrations() != null && maap.getMedicinalProduct().getRouteOfAdministrations().getRouteOfAdministration() != null) {
						vlp.setVlpPrilagane(new ArrayList<VlpPrilagane>());
						for (RouteOfAdministration route : maap.getMedicinalProduct().getRouteOfAdministrations().getRouteOfAdministration()) {
							
							VlpPrilagane pril = new VlpPrilagane();
							
							String codeExt = route.getCode();
							String nachin = route.getName();
							List<SystemClassif> result = sd.getItemsByCodeExt(685, codeExt, 1, new Date());
							if (result.size() > 0) {
								nachin = result.get(0).getTekst();
							}
							
							pril.setNachin(nachin);
																
							LOGGER.debug("Начин на прилагане: " + nachin);
							
							
							pril.setVlpPrilaganeVid(new ArrayList<VlpPrilaganeVid>());
							for (eu.europa.ema.v1_26.form.RouteOfAdministrations.RouteOfAdministration.TargetSpecies jiv : route.getTargetSpecies()) {
								Integer jivCode = transodeString(jiv.getSpecies(), 677, warnings, sd);
								if (jivCode != null) {
									VlpPrilaganeVid vid = new VlpPrilaganeVid();
									vid.setVid(jivCode);
									pril.getVlpPrilaganeVid().add(vid);
								}
								LOGGER.debug("Животно 2.2.2: " + jiv.getSpecies() + " (transcoded to "+jivCode+")");
							}
							
							vlp.getVlpPrilagane().add(pril);
						}
					}
					
					
					///data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/compositions/medicinal-product/pharmaceutical-products/pharmaceutical-product/pharmaceutical-form-details/active-substances/active-substances-details/active-substance
					if (maap.getCompositions() != null && maap.getCompositions().getMedicinalProduct() != null && maap.getCompositions().getMedicinalProduct().getPharmaceuticalProducts() != null && maap.getCompositions().getMedicinalProduct().getPharmaceuticalProducts().getPharmaceuticalProduct() != null) {
						vlp.setVlpVeshtva(new ArrayList<VlpVeshtva>());
						for (PharmaceuticalProduct product : maap.getCompositions().getMedicinalProduct().getPharmaceuticalProducts().getPharmaceuticalProduct()) {
							if (product.getPharmaceuticalFormDetails() != null) {
								for (PharmaceuticalFormDetails detail : product.getPharmaceuticalFormDetails()) {
									
									
									
									if (detail.getActiveSubstances() != null && detail.getActiveSubstances().getActiveSubstancesDetails() != null) {
										for (ActiveSubstancesDetails asd : detail.getActiveSubstances().getActiveSubstancesDetails()) {
											if(asd.getActiveSubstance() != null) {
												for (Ingredient ingredient : asd.getActiveSubstance()) {
													
													String monograf = "";
													if (ingredient.getReferenceMonographStandard() instanceof String) {
														monograf = ""+ingredient.getReferenceMonographStandard();
													}else {														
														monograf = extractParagraphText((Node)ingredient.getReferenceMonographStandard());
													}
													
													
													String operator = "";
													if (! isEmpty(ingredient.getIngredientStrength().getQuantityOperator())) {
														operator = ingredient.getIngredientStrength().getQuantityOperator();
														String codeO = operator.substring(0, operator.indexOf("|"));
														operator = operator.substring(operator.indexOf("|")+1);
														
														List<SystemClassif> result = sd.getItemsByCodeExt(686, codeO, 1, new Date());
														if (result.size() > 0) {
															operator = result.get(0).getTekst();
														}
													}
													
													String edinica = ingredient.getIngredientStrength().getLowStrengthNumeratorUnit();
													if (! isEmpty(ingredient.getIngredientStrength().getLowStrengthNumeratorUnit())) {
														
														edinica = edinica.substring(edinica.indexOf("|")+1);
													}
													
													 
//													if (as.getReferenceMonographStandard() != null && as.getReferenceMonographStandard().getClass().equals(Node.class)){
//														Node node = (Node)as.getReferenceMonographStandard();
//														
//													}
													
													
													VlpVeshtva v1 = new VlpVeshtva();
													Substance subs = new Substance();
													v1.setVidIdentifier(subs);
													
													v1.setQuantity(operator + " " + ingredient.getIngredientStrength().getLowStrengthNumeratorValue()); 
													v1.setMeasurement(edinica);													
													v1.getVidIdentifier().setName(ingredient.getSubstanceName().getName());
													v1.getVidIdentifier().setIdentifier(ingredient.getSubstanceName().getCode());													
													v1.setType(1);
													v1.setStandart(monograf);													
													vlp.getVlpVeshtva().add(v1);
													
													if (! isEmpty(ingredient.getMoietySubstanceName().getCode())) {
														VlpVeshtva v2 = new VlpVeshtva();
														Substance subs2 = new Substance();
														v2.setVidIdentifier(subs2);
														v2.setQuantity(operator + " " + ingredient.getMoietyIngredientStrength().getLowStrengthNumeratorValue()); 
														v2.setMeasurement(edinica);
														v2.getVidIdentifier().setName((ingredient.getMoietySubstanceName().getName()));
														v2.getVidIdentifier().setIdentifier((ingredient.getMoietySubstanceName().getCode()));
														v2.setType(1);
														v2.setStandart(monograf);														
														vlp.getVlpVeshtva().add(v2);
													}
													
												}
												
											}
											
											
											
										}
										
										
									}
									
									//pomostni
									
									if (detail.getExcipients() != null ) {
										
										for (Excipients excipient : detail.getExcipients()) {
											
											if (excipient.getExcipientsDetails() != null){
										
												for (ExcipientsDetails exd : excipient.getExcipientsDetails()) {
													if(exd.getExcipient() != null) {
														for (Ingredient ingredient : exd.getExcipient()) {
															
															String monograf = "";
															if (ingredient.getReferenceMonographStandard() instanceof String) {
																monograf = ""+ingredient.getReferenceMonographStandard();
															}else {														
																monograf = extractParagraphText((Node)ingredient.getReferenceMonographStandard());
															}
															
															
															String operator = "";
															if (! isEmpty(ingredient.getIngredientStrength().getQuantityOperator())) {
																operator = ingredient.getIngredientStrength().getQuantityOperator();
																String codeO = operator.substring(0, operator.indexOf("|"));
																operator = operator.substring(operator.indexOf("|")+1);
																
																List<SystemClassif> result = sd.getItemsByCodeExt(686, codeO, 1, new Date());
																if (result.size() > 0) {
																	operator = result.get(0).getTekst();
																}
															}
															
															String edinica = ingredient.getIngredientStrength().getLowStrengthNumeratorUnit();
															if (! isEmpty(ingredient.getIngredientStrength().getLowStrengthNumeratorUnit())) {
																
																edinica = edinica.substring(edinica.indexOf("|")+1);
															}
															
															 
		//													if (as.getReferenceMonographStandard() != null && as.getReferenceMonographStandard().getClass().equals(Node.class)){
		//														Node node = (Node)as.getReferenceMonographStandard();
		//														
		//													}
															
															
															VlpVeshtva v1 = new VlpVeshtva();
															Substance subs = new Substance();
															v1.setVidIdentifier(subs);
															
															v1.setQuantity(operator + " " + ingredient.getIngredientStrength().getLowStrengthNumeratorValue()); 
															v1.setMeasurement(edinica);													
															v1.getVidIdentifier().setName(ingredient.getSubstanceName().getName());
															v1.getVidIdentifier().setIdentifier(ingredient.getSubstanceName().getCode());													
															v1.setType(2);
															v1.setStandart(monograf);													
															vlp.getVlpVeshtva().add(v1);
															
															if (! isEmpty(ingredient.getMoietySubstanceName().getCode())) {
																VlpVeshtva v2 = new VlpVeshtva();
																Substance subs2 = new Substance();
																v2.setVidIdentifier(subs2);
																v2.setQuantity(operator + " " + ingredient.getMoietyIngredientStrength().getLowStrengthNumeratorValue()); 
																v2.setMeasurement(edinica);
																v2.getVidIdentifier().setName((ingredient.getMoietySubstanceName().getName()));
																v2.getVidIdentifier().setIdentifier((ingredient.getMoietySubstanceName().getCode()));
																v2.setType(2);
																v2.setStandart(monograf);														
																vlp.getVlpVeshtva().add(v2);
															}
															
														}
														
													}
													
													
													
												}
											}
										}
										
										
									}   //end of excipients
									
									
									
								}
							}
						}
						
						
						
						//data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/manufacturer-active-substances
						if (maap.getManufacturerActiveSubstances() != null && maap.getManufacturerActiveSubstances().getManufacturerActiveSubstance() != null) {
							
							for (ManufacturerActiveSubstanceVet manifacturer : maap.getManufacturerActiveSubstances().getManufacturerActiveSubstance()) {
								
								List<ManuSteps> steps = manifacturer.getManuSteps();
								
								String stepsFull = "";
								if (steps != null) {
									for (ManuSteps step : steps) {
										stepsFull += "<p>" + step.getManuStep()+"</p>";
									}
								}
								
								String certsFull = "";
								if (manifacturer.getPhEurCert() != null && manifacturer.getPhEurCert().getSubstanceCertificate() != null ) {
									for (SubstanceCertificateVet cert : manifacturer.getPhEurCert().getSubstanceCertificate()) {
										certsFull += cert.getReferenceNumberCertificate() + ";";
									}
								}
								if (!isEmpty(certsFull)) {
									certsFull = certsFull.substring(0, certsFull.length()-1);
								}
								
								
								ContactDetailsType contact = manifacturer.getContactDetails();
								
								
								
								String proizvoditelName = null;
								String proizvoditelAddr = null;
								String proizvoditelFull = "";
								
								if (contact != null) {
									
									if (contact.getAdminManuAddress() == 0) {
										//Няма адрес на офис и производство.
										//Очакваме евинтуално на това ниво да има нещо
									
									
									
									
										if (! isEmpty(contact.getCompanyName())) {
											proizvoditelName = contact.getCompanyName();
										}else {
											if (! isEmpty(contact.getFamilyName() )) {											
												if (! isEmpty(contact.getGivenName())) {
													proizvoditelName = contact.getGivenName() + " " + contact.getFamilyName();
												}else {
													proizvoditelName = contact.getFamilyName();
												}
											}
										}
									
										if (proizvoditelName != null) {
											//Намерихме производителя още на това ниво
											proizvoditelAddr = "";
											if (! isEmpty(contact.getCountry())) {
												proizvoditelAddr += contact.getCountry() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getState())) {
												proizvoditelAddr += contact.getState() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getCounty())) {
												proizvoditelAddr += contact.getCounty() + "\r\n";											
												
											}
											if (! isEmpty(contact.getCity())) {
												
												if (! isEmpty(contact.getPostCode())) {
													proizvoditelAddr += contact.getCity() + " " +contact.getPostCode() + "\r\n";
												}else {											
													proizvoditelAddr += contact.getCity() + "\r\n";
												}
											}
											
											if (! isEmpty(contact.getAddress1())) {
												proizvoditelAddr += contact.getAddress1() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getAddress2())) {
												proizvoditelAddr += contact.getAddress2() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getAddress3())) {
												proizvoditelAddr += contact.getAddress3() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getAddress4())) {
												proizvoditelAddr += contact.getAddress4() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getPhone())) {
												proizvoditelAddr += "phone: " + contact.getPhone() + "\r\n";											
												
											}
											
											if (! isEmpty(contact.getEmail())) {
												proizvoditelAddr += "email: " + contact.getEmail() + "\r\n";											
												
											}
										}
										
										if (! isEmpty(proizvoditelName)) {
											proizvoditelFull += "Производител: " +  proizvoditelName + "\r\n";
										}
										if (! isEmpty(proizvoditelAddr)) {
											proizvoditelFull += proizvoditelAddr + "\r\n";
										}
										
												
									}else {
										
										
											//Търсим адм. офис
											if (contact.getAdminOffice() != null && ! isEmpty(contact.getAdminOffice().getCompanyName())) {
												AdminOffice tek = contact.getAdminOffice();
												proizvoditelName = "<p><strong>Име на компания:</strong>"+ pre  +tek.getCompanyName() + "</p>";	
												
												proizvoditelAddr = "";
												
												if (! isEmpty(tek.getCountry())) {
													proizvoditelAddr += "<p><strong>Държава:</strong>"+ pre  + tek.getCountry().substring(tek.getCountry().indexOf("|") + 1) + "</p>";											
													
												}
												
												if (! isEmpty(tek.getState())) {
													proizvoditelAddr += "<p><strong>Щат:</strong>"+ pre  +tek.getState() + "</p>";									
													
												}
												
												if (! isEmpty(tek.getCounty())) {
													proizvoditelAddr += "<p><strong>Област:</strong>"+ pre  +tek.getCounty() + "</p>";										
													
												}
												if (! isEmpty(tek.getCity())) {
													proizvoditelAddr += "<p><strong>Населено място:</strong>"+ pre  + tek.getCity() + "</p>";
												}
													
												if (! isEmpty(tek.getPostCode())) {
													proizvoditelAddr += "<p><strong>Пощенски код:</strong>"+ pre  + tek.getPostCode() + "</p>";
												}
												
												if (! isEmpty(tek.getAddress1())) {
													proizvoditelAddr += "<p><strong>Адрес :</strong>"+ pre  + tek.getAddress1() + "</p>";										
													
												}
												
												if (! isEmpty(tek.getAddress2())) {
													proizvoditelAddr += "<p><strong>Адрес 2:</strong>"+ pre  + tek.getAddress2() + "</p>";										
													
												}
												
												if (! isEmpty(tek.getAddress3())) {
													proizvoditelAddr += "<p><strong>Адрес 3:</strong>"+ pre  + tek.getAddress3() + "</p>";											
													
												}
												
												if (! isEmpty(tek.getAddress4())) {
													proizvoditelAddr += "<p><strong>Адрес 4:</strong>"+ pre  + tek.getAddress4() + "</p>";											
													
												}
												
												if (! isEmpty(tek.getPhone())) {
													proizvoditelAddr += "<p><strong>phone:</strong>"+ pre  + tek.getPhone() + "</p>";											
													
												}
												
												if (! isEmpty(tek.getEmail())) {
													proizvoditelAddr += "<p><strong>email:</strong>"+ pre  + tek.getEmail() + "</p>";											
													
												}
											}
											
											
											if (! isEmpty(proizvoditelName)) {
												proizvoditelFull += "<br><p><h4>Административен офис: </h4></p><p>" +  proizvoditelName + "</p>";
											}else {
												proizvoditelFull += "<p><i>(няма отделни административен и производствен адрес)</i></p>";
											}
											if (! isEmpty(proizvoditelAddr)) {
												proizvoditelFull += "<p>" + proizvoditelAddr + "</p>";
											}
											
											//Търсим място на прозиводство
											if (contact.getManuFacility() != null && contact.getManuFacility().size() > 0) {
												
												
												for (ManuFacility tek : contact.getManuFacility() ) {
													
													//proizvoditelName = tek.getCompanyName();
													proizvoditelName = "<p><strong>Име на компания:</strong>"+ pre  +tek.getCompanyName() + "</p>";	
													
													proizvoditelAddr = "";
													
													if (! isEmpty(tek.getCountry())) {
														proizvoditelAddr += "<p><strong>Държава:</strong>"+ pre  + tek.getCountry().substring(tek.getCountry().indexOf("|") + 1) + "</p>";											
														
													}
													
													if (! isEmpty(tek.getState())) {
														proizvoditelAddr += "<p><strong>Щат:</strong>"+ pre  +tek.getState() + "</p>";									
														
													}
													
													if (! isEmpty(tek.getCounty())) {
														proizvoditelAddr += "<p><strong>Област:</strong>"+ pre  +tek.getCounty() + "</p>";										
														
													}
													if (! isEmpty(tek.getCity())) {
														proizvoditelAddr += "<p><strong>Населено място:</strong>"+ pre  + tek.getCity() + "</p>";
													}
														
													if (! isEmpty(tek.getPostCode())) {
														proizvoditelAddr += "<p><strong>Пощенски код:</strong>"+ pre  + tek.getPostCode() + "</p>";
													}
													
													if (! isEmpty(tek.getAddress1())) {
														proizvoditelAddr += "<p><strong>Адрес :</strong>"+ pre  + tek.getAddress1() + "</p>";										
														
													}
													
													if (! isEmpty(tek.getAddress2())) {
														proizvoditelAddr += "<p><strong>Адрес 2:</strong>"+ pre  + tek.getAddress2() + "</p>";										
														
													}
													
													if (! isEmpty(tek.getAddress3())) {
														proizvoditelAddr += "<p><strong>Адрес 3:</strong>"+ pre  + tek.getAddress3() + "</p>";											
														
													}
													
													if (! isEmpty(tek.getAddress4())) {
														proizvoditelAddr += "<p><strong>Адрес 4:</strong>"+ pre  + tek.getAddress4() + "</p>";											
														
													}
													
													if (! isEmpty(tek.getPhone())) {
														proizvoditelAddr += "<p><strong>phone:</strong>"+ pre  + tek.getPhone() + "</p>";											
														
													}
													
													if (! isEmpty(tek.getEmail())) {
														proizvoditelAddr += "<p><strong>email:</strong>"+ pre  + tek.getEmail() + "</p>";											
														
													}
													
													if (! isEmpty(proizvoditelName)) {
														proizvoditelFull += "<br><p><h4>Място за производство: </h4></p><p>" +  proizvoditelName + "</p>";
													}
													if (! isEmpty(proizvoditelAddr)) {
														proizvoditelFull += "<p>" + proizvoditelAddr + "</p><br>";
													}
												}
												
												
												
												
											}
											
										}
										
										
									LOGGER.debug(proizvoditelFull);	
									
									if (! isEmpty(proizvoditelFull)) {
										for (ActiveSubstances subs : manifacturer.getActiveSubstances()) {
											if (subs.getActiveSubstance() != null) {
												for (Ingredient ingr : subs.getActiveSubstance()) {
													if (ingr.getSubstanceName() != null && ! isEmpty(ingr.getSubstanceName().getCode()) ) {
														//Търсим активните вещества за да им сложим производителя
														for ( VlpVeshtva v : vlp.getVlpVeshtva()) {
															if (ingr.getSubstanceName().getCode().equals(v.getVidIdentifier().getIdentifier())) {
																
																if (!isEmpty(stepsFull)) {
																	proizvoditelFull += "<p><h4>Описание на производствени етапи:</h4></p><br> " + stepsFull + "<br>";
																}
																
																if (!isEmpty(certsFull)) {
																	proizvoditelFull += "<p><h4>Номер на разрешение:</h4></p> " + certsFull + "<br>";
																}
																
																proizvoditelFull += "<br>";
																
																int cnt = v.getCntProizvoditeli() + 1;
																v.setProizvoditel(v.getProizvoditel() + "<p><h3>ПРОИЗВОДИТЕЛ "+cnt+":</h3></p>" + proizvoditelFull);
																v.setCntProizvoditeli(cnt);
																
																proizvoditelFull = "";
																break;
															}
														}
													}
													if (isEmpty(proizvoditelFull)) {
														break;
													}
												}
											}
										}
									}
								}
								
								
							}
						}
						
					}
					////2.4.1
					//marketing-authorisation-holders/marketing-authorisation-holder
					if (maap.getMarketingAuthorisationHolders() != null && maap.getMarketingAuthorisationHolders().getMarketingAuthorisationHolder() != null) {
						for (MarketingAuthorisationHolder tek : maap.getMarketingAuthorisationHolders().getMarketingAuthorisationHolder()) {
							ArrayList<Referent> all = convertContactToReferents(tek, warnings, sd);
							for (Referent ref : all) {
								if (! isEmpty(ref.getRefName())){
									VlpLice lice = new VlpLice();
									lice.setTip(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PRITEJ_LICENZ);
									lice.setCodeRef(ref.getCode());
									lice.setReferent(ref);
									vlp.getVlpLice().add(lice);
								}
							}
						}
					}
					
					
					//2.4.4
					///data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/contact-pharmaco-vigilances/contact-pharmaco-vigilance/contact-details
					if (maap.getContactPharmacoVigilances() != null && maap.getContactPharmacoVigilances().getContactPharmacoVigilance()  != null) {
						for (ContactPharmacoVigilance tek : maap.getContactPharmacoVigilances().getContactPharmacoVigilance()) {
							ArrayList<Referent> all = convertContactToReferents(tek.getContactDetails(), warnings, sd);
							for (Referent ref : all) {
								if (! isEmpty(ref.getRefName())){
									VlpLice lice = new VlpLice();
									lice.setTip(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_FARM_BDITELNOST);
									lice.setCodeRef(ref.getCode());
									lice.setReferent(ref);
									vlp.getVlpLice().add(lice);
								}
							}
						}
					}
					
					//2.5.1.
					//data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/manufacturer-batch-releases/manufacturer-batch-release
					if (maap.getManufacturerBatchReleases() != null && maap.getManufacturerBatchReleases().getManufacturerBatchRelease() != null) {
						for ( ManufacturerBatchRelease tek : maap.getManufacturerBatchReleases().getManufacturerBatchRelease()) {
							ArrayList<Referent> all = convertContactToReferents(tek.getContactDetails(), warnings, sd);
							for (Referent ref : all) {
								if (! isEmpty(ref.getRefName())){
									VlpLice lice = new VlpLice();
									lice.setTip(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL_OTG_RELEASE);
									lice.setCodeRef(ref.getCode());
									lice.setReferent(ref);
									vlp.getVlpLice().add(lice);
								}
							}
						}
					}
					
					//2.5.2.
					//data/eu_application_form/initial-application-form-veterinary/marketing_authorisation_application_particulars/manufacturer-batch-releases/manufacturer-batch-release
					if (maap.getManufacturerBatchReleases() != null && maap.getManufacturerPharmaceuticalProducts().getManufacturerPharmaceuticalProduct() != null) {
						for ( ManufacturerPharmaceuticalProduct tek : maap.getManufacturerPharmaceuticalProducts().getManufacturerPharmaceuticalProduct()) {
							
							String dopInfo = null;
							if (! SearchUtils.isEmpty(tek.getDesc())){
								dopInfo = tek.getDesc()	;							
							}
							
							ArrayList<Referent> all = convertContactToReferents(tek.getContactDetails(), warnings, sd);
							if (all.size() == 1) {
								VlpLice lice = new VlpLice();
								lice.setTip(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL);
								lice.setCodeRef(all.get(0).getCode());
								lice.setReferent(all.get(0));
								lice.setDopInfo(dopInfo);
								vlp.getVlpLice().add(lice);
							}else {
								if (all.size() > 1) {
									//Работим само с първите 2
									
									Referent ref1 = all.get(0);
									Referent ref2 = all.get(1);
									
									VlpLice lice = new VlpLice();
									lice.setTip(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL);
									lice.setCodeRef(ref1.getCode());
									lice.setReferent(ref1);
									lice.setDopInfo(dopInfo);
									
									if (! ref1.getRefName().trim().equalsIgnoreCase(ref2.getRefName().trim())) {
										ReferentAddress addr = ref2.getAddressKoresp();
										if (addr != null) {
											if (addr.getAddrText() == null) {
												addr.setAddrText(ref2.getRefName());
											}else {
												addr.setAddrText(ref2.getRefName() + "\r\n" + addr.getAddrText());
											}
											ref1.setAddress(addr);
										}
									}else {
										ref1.setAddress(ref2.getAddressKoresp());
									}
									
									
									vlp.getVlpLice().add(lice);
									
									
								}
							}
							
							
							
							for (Referent ref : all) {
								if (! isEmpty(ref.getRefName())){
									
								}
							}
						}
					}
					
						
				}
				
				
				if (appFromVet.getTypeOfApplication() != null && appFromVet.getTypeOfApplication().getConcerns() != null) {
					
					Concerns concerns = appFromVet.getTypeOfApplication().getConcerns();
					if (concerns.getCentralisedProcedure() == 1) {
						LOGGER.debug("Form is for CentralisedProcedure 1.1.1 . !!!!!!!!!!!!!!!! NOT AVAILABLE IN SYSTEM !!!!!!!!!!!!!!!");						
						vlp.setVidProcOdobrenie(null);
					}
					
					if (concerns.getMutualProcedure() == 1) {
						LOGGER.debug("Form is for MutualProcedure 1.1.2");
						vlp.setVidProcOdobrenie(3);
					}
					
					if (concerns.getDecentralisedProcedure() == 1) {
						LOGGER.debug("Form is for DecentralisedProcedure 1.1.3");
						vlp.setVidProcOdobrenie(2);
					}
					
					if (concerns.getNationalProcedure() == 1) {
						LOGGER.debug("Form is for NationalProcedure 1.1.4");
						vlp.setVidProcOdobrenie(1);
					}
					
					String procNumber = "";
					if (concerns.getDp() != null && ! SearchUtils.isEmpty(concerns.getDp().getProcedureNumber())) {
						procNumber = concerns.getDp().getProcedureNumber();
					}
					
					if (concerns.getMutualRecognitionProcedure() != null && ! SearchUtils.isEmpty(concerns.getMutualRecognitionProcedure().getProcedureNumber())) {
						procNumber = concerns.getMutualRecognitionProcedure().getProcedureNumber();						
					}
					vlp.setProcNumber(procNumber);
					
				}
				
				
			}
			
			
			
			
		
		}
		
		
		
		
		
		return vlp;
	}
	

	
	
	
	





	private Integer transodeString(String value, Integer codeClassif, ArrayList<String> warnings, SystemData sd) throws DbErrorException {
		if (value == null || isEmpty(value)) {
			return null;
		}else {
			
			String codeExt = null;
			
			String[] split = value.split("\\|");
			if (split.length == 2) {
				codeExt = split[0];
			}else {
				LOGGER.error("Не може да извлече кода на значението от стойността: " + value);
				warnings.add("Не може да разбие външен код " + value);
				return null;
			}
			
			List<SystemTranscode> result = sd.transcodeByCodeExt(codeClassif, codeExt, 2);
			if (result.size() == 1) {
				return result.get(0).getCodeNative();
			}else {
				if (result.size() > 1) {
					warnings.add("Нанерени са повече от едно съответствие за външен код " + value + " за класификация с код " + codeClassif + " за система eAF" );
					return result.get(0).getCodeNative();
				}else {
					warnings.add("Не може да се намери съответствие за външен код " + value + " за класификация с код " + codeClassif + " за система eAF" );
					return null;
				}
			}
		}
	}

	private String extractParagraphText(Node element) {
		
	    StringBuilder paragraphText = new StringBuilder();   

	    if (element.getTextContent() != null && element.getTextContent().trim().length() > 2) {
	        paragraphText.append(element.getTextContent().trim());	    
	    }else {
	    	// Recursively process child elements
		    NodeList childNodes = element.getChildNodes();
		    for (int i = 0; i < childNodes.getLength(); i++) {
		        Node node = childNodes.item(i);
		        paragraphText.append(extractParagraphText(node));		      
		    }
	    }

	    
	    
	    
	    return paragraphText.toString();
	    	
	    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + element.getTextContent() );
	    	
	    

	    
	}

	private HashMap<String, String> createTimeMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("MILISECONDS", "милисекунди");
		map.put("MILISECOND", "милисекунда");
		
		map.put("SECONDS", "секунди");
		map.put("SECOND", "секунда");
		
		map.put("MINUTES", "минути");
		map.put("MINUTE", "минута");
		
		map.put("HOURS", "часове");
		map.put("HOUR", "час");
		
		map.put("DAYS", "дни");
		map.put("DAY", "ден");
		
		map.put("WEEKS", "седмици");
		map.put("WEEK", "седмица");
		
		map.put("MONTHS", "месецa");
		map.put("MONTH", "месец");
		
		map.put("YEARS", "години");
		map.put("YEAR", "година");
		
		return map;
	}
	
	public ArrayList<Referent> convertContactToReferents(ContactDetailsType contactDetail, ArrayList<String> warnings, SystemData sd) throws DbErrorException{
		
		ArrayList<Referent> result = new ArrayList<Referent>();
		
		
		
		
		
		
		Referent ref = generateRefs(new BaseAddress(contactDetail), warnings, sd);
		if (ref != null) {
			result.add(ref);
		}
		
		if (contactDetail.getAdminOffice() != null) {
			Referent refA = generateRefs(new BaseAddress(contactDetail.getAdminOffice()), warnings, sd);
			if (refA != null) {
				result.add(refA);
			}
		}
		
		if (contactDetail.getManuFacility() != null) {
			for (ManuFacility fas : contactDetail.getManuFacility()) {
				Referent refM = generateRefs(new BaseAddress(fas), warnings, sd);
				if (refM != null) {
					result.add(refM);
				}
			}
		}
		
		return result;
	}
	
	
	public Referent generateRefs(BaseAddress contact, ArrayList<String> warnings, SystemData sd) throws DbErrorException{
		
		
		
		
		Referent ref = new Referent();
		//Първо тръсим в основните данни
		
		if (! isEmpty(contact.getCompanyName())){
			ref.setRefName(contact.getCompanyName());
			ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
		}else {
			if ( ! isEmpty(contact.getFamilyName())) {
				String name = "";
				ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				if ( ! isEmpty(contact.getGivenName())) {
					name = contact.getGivenName() + " " + contact.getFamilyName();
				}else {
					name = contact.getFamilyName();
				}
				if ( ! isEmpty(contact.getPersonalTitle())) {
					name = contact.getPersonalTitle() + " " + name;
				}
				ref.setRefName(name);
				
				if ( ! isEmpty(contact.getOrgID())) {
					ref.setFzlLnEs(contact.getOrgID());
				}
			}
		}
		
		if (! isEmpty(ref.getRefName())) {
			//Създали сме референт с име
			//Процедираме с адреса
			
			ReferentAddress refAddr = new ReferentAddress();
			boolean hasAddrData = false;
			
			if (! isEmpty(contact.getPostCode())) {
				refAddr.setPostCode(contact.getPostCode());
				hasAddrData = true;
			}
			
			if (! isEmpty(contact.getPhone()) && contact.getPhone().trim().length() > 3) {				
				ref.setContactPhone(contact.getPhone());	
				hasAddrData = true;
			}
			
			if (! isEmpty(contact.getEmail())) {				
				ref.setContactEmail(contact.getEmail());
				hasAddrData = true;
			}
			
			if (! isEmpty(contact.getCountry())) {
				
				Integer darj = transodeString(contact.getCountry(), 22, warnings, sd);
				String darjText = contact.getCountry().substring(contact.getCountry().indexOf("|") + 1);
				LOGGER.debug("Country : " + contact.getCountry() + " (transcoded to "+darj+")");
				
				refAddr.setAddrCountry(darj);
				refAddr.setCountryName(darjText);
				hasAddrData = true;
			}
										
			String addrString = "";
			if (! isEmpty(contact.getState())) {
				addrString += "State: " + contact.getState() + "\r\n";
			}
			
			if (! isEmpty(contact.getCounty())) {
				addrString += "County: " + contact.getCounty() + "\r\n";									
				
			}
			if (! isEmpty(contact.getCity())) {
				addrString += "City/Locality/Town/Village: " + contact.getCity() + "\r\n";
			}
			
			if (! isEmpty(contact.getAddress1())) {
				addrString += "Address : " + contact.getAddress1() + "\r\n";									
				
			}
			
			if (! isEmpty(contact.getAddress2())) {
				addrString += "Address 2: " + contact.getAddress2() + "\r\n";										
				
			}
			
			if (! isEmpty(contact.getAddress3())) {
				addrString += "Address 3: " + contact.getAddress3() + "\r\n";											
				
			}
			
			if (! isEmpty(contact.getAddress4())) {
				addrString += "Address 4: " + contact.getAddress4() + "\r\n";											
				
			}
			
			
			
			if (! isEmpty(addrString) && addrString.length() > 3) {
				refAddr.setAddrText(addrString);
				hasAddrData = true;
			}
			
			if (hasAddrData) {				
				refAddr.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
				ref.setAddressKoresp(refAddr);
			}
			
			return ref;
			
		}
		
		
		
		return null;
	}
	
	
}
