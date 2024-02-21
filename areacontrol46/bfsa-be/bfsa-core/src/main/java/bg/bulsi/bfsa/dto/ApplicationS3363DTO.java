package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3363;
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
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3363DTO extends BaseApplicationDTO {
    private OffsetDateTime registrationDate;
    private String seedName;
    private String seedQuantity;
    private String seedTradeName;
    private List<TreatmentBO> treatments = new ArrayList<>();
    private String pppManufacturerName;
    private String pppImporterName;
    private Set<CountryDTO> countries = new HashSet<>();
    private String certificateNumber;
    private OffsetDateTime certificateDate;
    private OffsetDateTime certificateUntilDate;

    public static ApplicationS3363DTO of(final ApplicationS3363 source, final Language language) {
        ApplicationS3363DTO dto = new ApplicationS3363DTO();

        if (source != null) {
            BeanUtils.copyProperties(source, dto);

            dto.setApplicationStatus(source.getStatus());
        }

        return dto;
    }

    public static List<ApplicationS3363DTO> of(final List<ApplicationS3363> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS3363DTO of(final Record source, final Language language) {
        ApplicationS3363DTO dto = new ApplicationS3363DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS3363 application = source.getApplicationS3363();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (!CollectionUtils.isEmpty(application.getTreatments())) {
                dto.getTreatments().addAll(application.getTreatments());
            }

            if (!CollectionUtils.isEmpty(application.getCountries())) {
                dto.setCountries(CountryDTO.of(application.getCountries(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS3363DTO> ofRecords(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS3363DTO ofRecord(final Record source, final Language language) {
        ApplicationS3363DTO dto = new ApplicationS3363DTO();
        dto.ofRecordBase(source, language);

        ApplicationS3363 application = source.getApplicationS3363();

        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (!CollectionUtils.isEmpty(application.getCountries())) {
                dto.setCountries(CountryDTO.of(application.getCountries(), language));
            }
        }

        return dto;
    }

    public static ApplicationS3363DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3363DTO dto = new ApplicationS3363DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setRegistrationDate(sc.get("regDate") != null
                    ? OffsetDateTime.parse(sc.get("regDate").toString())
                    : null);

            dto.setSeedName(sc.get("seedName") != null
                    ? sc.get("seedName").toString()
                    : null);

            dto.setSeedQuantity(sc.get("seedQuantity") != null
                    ? sc.get("seedQuantity").toString()
                    : null);

            dto.setSeedTradeName(sc.get("seedTradeName") != null
                    ? sc.get("seedTradeName").toString()
                    : null);

            List<TreatmentBO> treatments = getTreatments(findValue(sc, "dataGrid"));
            dto.setTreatments(!CollectionUtils.isEmpty(treatments) ? treatments : null);

            dto.setPppManufacturerName(sc.get("pppManufacturerName") != null
                    ? sc.get("pppManufacturerName").toString()
                    : null);

            dto.setPppImporterName(sc.get("pppImporterName") != null
                    ? sc.get("pppImporterName").toString()
                    : null);


            Set<CountryDTO> countryList = getCountries(findValue(sc, "countryCode"));
            dto.setCountries(!CollectionUtils.isEmpty(countryList) ? countryList : null);

            dto.setCertificateNumber(sc.get("certificateNumber") != null
                    ? sc.get("certificateNumber").toString()
                    : null);

            dto.setCertificateDate(sc.get("certificateDate") != null
                    ? OffsetDateTime.parse(sc.get("certificateDate").toString())
                    : null);

            dto.setCertificateUntilDate(sc.get("certificateDate1") != null
                    ? OffsetDateTime.parse(sc.get("certificateDate1").toString())
                    : null);
        }

        return dto;
    }

    private static Set<CountryDTO> getCountries(final Object countryCode) {
        Set<CountryDTO> countries = new HashSet<>();
        if (countryCode instanceof LinkedHashMap<?, ?> c) {
            countries.add(getCountry(c));
        } else if (countryCode instanceof ArrayList<?> country) {
            country.forEach(b -> countries.add(getCountry(b)));
        }

        return countries;
    }

    private static CountryDTO getCountry(final Object sc) {
        CountryDTO dto = new CountryDTO();
        if (sc instanceof LinkedHashMap<?, ?> country) {
            dto.setCode(country.get("value") != null
                    ? country.get("value").toString()
                    : null);
            dto.setName(country.get("label") != null
                    ? country.get("label").toString()
                    : null);
        }

        return dto;
    }

    private static List<TreatmentBO> getTreatments(final Object object) {
        List<TreatmentBO> treatments = new ArrayList<>();
        if (object instanceof LinkedHashMap<?, ?> c) {
            treatments.add(getTreatment(c));
        } else if (object instanceof ArrayList<?> t) {
            t.forEach(tr -> treatments.add(getTreatment(tr)));
        }

        return treatments;
    }

    private static TreatmentBO getTreatment(final Object sc) {
        TreatmentBO bo = new TreatmentBO();
        if (sc instanceof LinkedHashMap<?, ?> country) {
            if (country.get("specificContent") instanceof LinkedHashMap<?, ?> code) {
                bo.setDescription(code.get("treatmentDescription") != null
                        ? code.get("treatmentDescription").toString()
                        : null);
                bo.setAmount(code.get("treatmentAmount") != null
                        ? Double.parseDouble(code.get("treatmentAmount").toString())
                        : null);
            }
        }

        return bo;
    }
}
