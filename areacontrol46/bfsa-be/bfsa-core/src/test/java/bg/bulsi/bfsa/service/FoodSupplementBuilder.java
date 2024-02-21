package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.FoodSupplementI18n;
import bg.bulsi.bfsa.model.Nomenclature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FoodSupplementBuilder {
    @Autowired
    private FoodSupplementService service;
    @Autowired
    private ContractorBuilder contractorBuilder;

    public static final Boolean ENABLED = true;
    public static final String TEST_NAME_BG = "Протейн";
    private static final String PURPOSE = "Предназначение";
    private static final String INGREDIENTS = "Съставки";
    private static final String DESCRIPTION = "Описание";
    public static final String TEST_NAME_EN = "Protein";
    private static final String PURPOSE_EN = "Purpose";
    private static final String INGREDIENTS_EN = "Ingredients";
    private static final String DESCRIPTION_EN = "Description";
    private final String MANUFACTURE_COMPANY_NAME = "Company Name";
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalDate NOTIFICATION_DATE = LocalDate.now().plusDays(20L);

    public FoodSupplement saveFoodSupplement(final Nomenclature nomenclature) {
        return service.create(getFoodSupplement(nomenclature));
    }

    public FoodSupplement getFoodSupplement(final Nomenclature nomenclature) {
        Contractor contractor = contractorBuilder.saveContractor();

        FoodSupplement foodSupplement = FoodSupplement.builder()
                .applicant(contractor)
                .facilityType(nomenclature)
                .regNumber(UUID.randomUUID().toString())
                .regDate(DATE)
                .enabled(ENABLED).build();
        foodSupplement.getI18ns().add(new FoodSupplementI18n(
                TEST_NAME_BG, PURPOSE, INGREDIENTS,
                DESCRIPTION,MANUFACTURE_COMPANY_NAME, foodSupplement, BaseServiceTest.langBG));
        foodSupplement.getI18ns().add(new FoodSupplementI18n(
                TEST_NAME_EN, PURPOSE_EN, INGREDIENTS_EN,
                DESCRIPTION_EN,MANUFACTURE_COMPANY_NAME, foodSupplement, BaseServiceTest.langEN));
        return foodSupplement;
    }

    public List<FoodSupplement> saveFoodSupplements(final Nomenclature nomenclature) {
        List<FoodSupplement> foodSupplements = new ArrayList<>();
        foodSupplements.add(saveFoodSupplement(nomenclature));
        foodSupplements.add(saveFoodSupplement(nomenclature));
        foodSupplements.add(saveFoodSupplement(nomenclature));
        foodSupplements.add(saveFoodSupplement(nomenclature));
        return foodSupplements;
    }
}
