package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import bg.bulsi.bfsa.model.PlantProtectionProductI18n;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlantProtectionProductBuilder {
    
    private static final String NAME = "Name";
    private static final String ACTIVE_SUBSTANCE = "Substance";
    private static final String PURPOSE = "Purpose";
    private static final String PEST = "Pest";
    private static final String CROP = "Crop";
    private static final String APPLICATION = "App";
    
    public PlantProtectionProduct mockPlantProtectionProduct(Double quantity) {
    	PlantProtectionProduct prod = PlantProtectionProduct.builder()
                .quantity(quantity)
                .build();
    	PlantProtectionProductI18n i18n = mockPlantProtectionProductI18n(prod, PlantProtectionProductServiceTest.langBG);
    	prod.getI18ns().add(i18n);
		return prod;
    }

    public PlantProtectionProductI18n mockPlantProtectionProductI18n(PlantProtectionProduct plantProtectionProduct, Language language) {
    	PlantProtectionProductI18n i18n = new PlantProtectionProductI18n(
    			NAME,  
    			ACTIVE_SUBSTANCE,
				PURPOSE,
    			PEST, 
    			CROP, 
    			APPLICATION, 
    			plantProtectionProduct, 
    			language);

        return i18n;
    }

    public List<PlantProtectionProduct> getList(int size) {
    	List<PlantProtectionProduct> list = new ArrayList<>(size);
    	for (int i = 0; i < size; i++) {
    		PlantProtectionProduct plantProtectionProduct = mockPlantProtectionProduct(Math.random() * 100);
    		list.add(plantProtectionProduct);
    	}
    	return list;
    }
    
}
