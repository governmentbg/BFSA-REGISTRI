package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2697;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2697DTO extends BaseApplicationDTO {

    private List<SubstanceBO> substances = new ArrayList<>();
    private AddressBO warehouseAddress;

    public static ApplicationS2697DTO of(final ApplicationS2697 source, Language language) {
        ApplicationS2697DTO dto = new ApplicationS2697DTO();
        dto.setServiceType(ServiceType.S2697);
        dto.setApplicationStatus(source.getStatus());
        dto.setWarehouseAddress(source.getWarehouseAddress());

        if (!CollectionUtils.isEmpty(source.getSubstances())) {
            dto.setSubstances(source.getSubstances());
        }

        return dto;
    }

    public static List<ApplicationS2697DTO> of(final List<ApplicationS2697> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2697DTO ofRecord(final Record source, Language language) {
        ApplicationS2697DTO dto = new ApplicationS2697DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

//        --- Set ApplicationS2697 ---
        ApplicationS2697 application = source.getApplicationS2697();
        if (application != null) {
            dto.setWarehouseAddress(application.getWarehouseAddress());

            if (!CollectionUtils.isEmpty(application.getSubstances())) {
                dto.setSubstances(application.getSubstances());
            }
        }

        return dto;
    }

    public static List<ApplicationS2697DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2697DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2697DTO dto = new ApplicationS2697DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {

            List<SubstanceBO> substances = getSubstances(findValue(sc, "producedFood"));
            dto.setSubstances(!CollectionUtils.isEmpty(substances) ? substances : null);

            dto.setWarehouseAddress(getWarehouseAddress(findValue(sc, "storageWarehouseAddress")));
        }

        return dto;
    }

    private static AddressBO getWarehouseAddress(final Object addressObj) {
        AddressBO warehouseAddress = null;

        if (addressObj instanceof LinkedHashMap<?, ?> address) {
            warehouseAddress = new AddressBO();
            if (address.get("SettlementSelect") instanceof LinkedHashMap<?, ?> map) {
                warehouseAddress.setSettlementCode(map.get("settlementCode") != null
                        ? map.get("settlementCode").toString()
                        : null);
                warehouseAddress.setSettlementName(map.get("settlementName") != null
                        ? map.get("settlementName").toString()
                        : null);
            }
            if (address.get("FullAddress") != null) {
                warehouseAddress.setFullAddress(address.get("FullAddress").toString());
            }
            if (address.get("CountrySelect") instanceof LinkedHashMap<?, ?> map) {
                warehouseAddress.setCountryCode(map.get("countryCode")
                        != null ? map.get("countryCode").toString()
                        : null);
                warehouseAddress.setCountryName(map.get("countryName")
                        != null ? map.get("countryName").toString()
                        : null);
                warehouseAddress.setFullAddress(warehouseAddress.getCountryName() + ", " + warehouseAddress.getFullAddress());
            }
        }

        return warehouseAddress;
    }

    private static List<SubstanceBO> getSubstances(final Object producedFood) {
        if (producedFood instanceof LinkedHashMap<?, ?> food) {
            SubstanceBO substance = getUnapprovedSubstance(food);
            if (substance != null) {
                List<SubstanceBO> substances = new ArrayList<>();
                substances.add(getUnapprovedSubstance(food));
                return substances;
            }
        } else if (producedFood instanceof ArrayList<?> food) {
            List<SubstanceBO> substances = new ArrayList<>();

            food.forEach(f -> substances.add(getUnapprovedSubstance(f)));
            return substances;
        }

        return null;
    }

    private static SubstanceBO getUnapprovedSubstance(final Object producedFood) {
        if (producedFood instanceof LinkedHashMap<?, ?> food) {
            if (food.get("container") instanceof LinkedHashMap<?, ?> container &&
                    container.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
                SubstanceBO bo = new SubstanceBO();
                bo.setQuantity(sc.get("sunstanceQuantity") != null
                        ? Double.parseDouble(sc.get("sunstanceQuantity").toString())
                        : null);
                bo.setCode(sc.get("substanceCode") != null
                        ? sc.get("substanceCode").toString()
                        : null);
                bo.setNumber(sc.get("substanceBatchNumber") != null
                        ? sc.get("substanceBatchNumber").toString()
                        : null);
                bo.setName(sc.get("substanceName") != null
                        ? sc.get("substanceName").toString()
                        : null);
                return bo;
            }
        }

        return null;
    }
}
