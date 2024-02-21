package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS1199DTO;
import bg.bulsi.bfsa.dto.ApplicationS1366DTO;
import bg.bulsi.bfsa.dto.ApplicationS1590DTO;
import bg.bulsi.bfsa.dto.ApplicationS16DTO;
import bg.bulsi.bfsa.dto.ApplicationS1811DTO;
import bg.bulsi.bfsa.dto.ApplicationS2170DTO;
import bg.bulsi.bfsa.dto.ApplicationS2272DTO;
import bg.bulsi.bfsa.dto.ApplicationS2274DTO;
import bg.bulsi.bfsa.dto.ApplicationS2695DTO;
import bg.bulsi.bfsa.dto.ApplicationS2697DTO;
import bg.bulsi.bfsa.dto.ApplicationS2698DTO;
import bg.bulsi.bfsa.dto.ApplicationS2699DTO;
import bg.bulsi.bfsa.dto.ApplicationS2700DTO;
import bg.bulsi.bfsa.dto.ApplicationS2701DTO;
import bg.bulsi.bfsa.dto.ApplicationS2702DTO;
import bg.bulsi.bfsa.dto.ApplicationS2711DTO;
import bg.bulsi.bfsa.dto.ApplicationS2869DTO;
import bg.bulsi.bfsa.dto.ApplicationS3125DTO;
import bg.bulsi.bfsa.dto.ApplicationS3180DTO;
import bg.bulsi.bfsa.dto.ApplicationS3181DTO;
import bg.bulsi.bfsa.dto.ApplicationS3182DTO;
import bg.bulsi.bfsa.dto.ApplicationS3201DTO;
import bg.bulsi.bfsa.dto.ApplicationS3362DTO;
import bg.bulsi.bfsa.dto.ApplicationS3363DTO;
import bg.bulsi.bfsa.dto.ApplicationS3365DTO;
import bg.bulsi.bfsa.dto.ApplicationS502DTO;
import bg.bulsi.bfsa.dto.ApplicationS503DTO;
import bg.bulsi.bfsa.dto.ApplicationS7691DTO;
import bg.bulsi.bfsa.dto.ApplicationS7692DTO;
import bg.bulsi.bfsa.dto.ApplicationS7693DTO;
import bg.bulsi.bfsa.dto.ApplicationS7694DTO;
import bg.bulsi.bfsa.dto.ApplicationS7695DTO;
import bg.bulsi.bfsa.dto.ApplicationS7696DTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.dto.index.FileWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationSchedulerCounter;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationSchedulerCounterRepository;
import bg.bulsi.bfsa.util.Constants;
import bg.e_gov.eform.dictionary.identifier.Identifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import generated.ServiceRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
@Profile({"!test", "!h2-unit-test"})
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationsSchedulerService {

    private final HttpClient httpClient;
    private final DocumentService documentService;
    private final ApplicationS16Service applicationS16Service;
    private final ApplicationS502Service applicationS502Service;
    private final ApplicationS503Service applicationS503Service;
    private final ApplicationS1199Service applicationS1199Service;
    private final ApplicationS1366Service applicationS1366Service;
    private final ApplicationS1590Service applicationS1590Service;
    private final ApplicationS1811Service applicationS1811Service;
    private final ApplicationS2170Service applicationS2170Service;
    private final ApplicationS2272Service applicationS2272Service;
    private final ApplicationS2697Service applicationS2697Service;
    private final ApplicationS2274Service applicationS2274Service;
    private final ApplicationS2695Service applicationS2695Service;
    private final ApplicationS2698Service applicationS2698Service;
    private final ApplicationS2699Service applicationS2699Service;
    private final ApplicationS2700Service applicationS2700Service;
    private final ApplicationS2701Service applicationS2701Service;
    private final ApplicationS2702Service applicationS2702Service;
    private final ApplicationS2711Service applicationS2711Service;
    private final ApplicationS2869Service applicationS2869Service;
    private final ApplicationS3125Service applicationS3125Service;
    private final ApplicationS3180Service applicationS3180Service;
    private final ApplicationS3181Service applicationS3181Service;
    private final ApplicationS3182Service applicationS3182Service;
    private final ApplicationS3201Service applicationS3201Service;
    private final ApplicationS3362Service applicationS3362Service;
    private final ApplicationS3363Service applicationS3363Service;
    private final ApplicationS3365Service applicationS3365Service;
    private final ApplicationS7691Service applicationS7691Service;
    private final ApplicationS7692Service applicationS7692Service;
    private final ApplicationS7693Service applicationS7693Service;
    private final ApplicationS7694Service applicationS7694Service;
    private final ApplicationS7695Service applicationS7695Service;
    private final ApplicationS7696Service applicationS7696Service;
    private final ApplicationSchedulerCounterRepository applicationSchedulerCounterRepository;
    private final ObjectMapper objectMapper;
    private final FileStoreService fileStoreService;
    private final SettingService settingService;

    @Scheduled(fixedDelayString = "${bfsa.app-sync.find-all-new-apps-fixed-delay}")
    public void scheduleAllNewApplications() {
        // --- For test with local file (pdf) ---
//       try {
//            localTestWithPdf();
//        } catch (Exception e) {
//            log.error("\n\nError with Schedule applications test: {}\n{}\n\n", e.getMessage(), e.getStackTrace());
//        }
        // --- For test with local file (pdf) ---

        if (settingService.checkByKey("global.app-sync", "true")) {
            List<DocWS> docs = findAllNewDocuments();//.stream().filter(d -> "1366".equals(d.getNomAdmUsluga())).toList();
            //        docs.forEach(d -> System.out.println(d.getNomAdmUsluga()));
//            docs.forEach(d -> markDocAsFinished(d.getId()));
            if (!CollectionUtils.isEmpty(docs)) {
                final long startTime = System.currentTimeMillis();
                int processedDocuments = 0;
                for (DocWS doc : docs) {
                    ApplicationSchedulerCounter schedulerCounter = applicationSchedulerCounterRepository.findByDocumentId(doc.getId());
                    if (schedulerCounter == null || (schedulerCounter.getCounter() > -1 && schedulerCounter.getCounter() < 3)) {
                        log.info(">>> service type: [{}]", doc.getNomAdmUsluga());

                        DocWS docInfo = getDocumentInfo(doc.getId());
                        if (docInfo == null || CollectionUtils.isEmpty(docInfo.getFiles())) {
                            log.error("There are no files in document with id: [{}]", doc.getId());
                            continue;
                        }

                        // The main pdf file
                        FileWS file = docInfo.getFiles().stream().filter(f -> f != null
                                        && "application/pdf".equals(f.getFileType())
                                        && StringUtils.hasText(f.getFileName())
                                        && f.getFileName().contains(docInfo.getIdentificatorUsluga()))
                                .findFirst().orElse(null);
                        if (file == null) {
                            log.error("There is no main pdf file in document with id: [{}]", doc.getId());
                            continue;
                        }

                        String jsonString = documentService.getPdfDataJson(file.getFileContent(), Constants.APPLICATION_JSON_METADATA_KEY);
                        log.debug(">>> jsonString {}", jsonString);

                        ServiceType serviceType = null;
                        ServiceType serviceTypeNomAdmUsluga = ServiceType.valueOf("S" + docInfo.getNomAdmUsluga());
                        try {
                            ServiceRequestWrapper wrapper = objectMapper.readValue(jsonString, ServiceRequestWrapper.class);

                            Identifier identifier = wrapper.getServiceRequest().getPublicService().getIdentifier();
                            log.debug(">>> serviceId: [{}]", identifier.getIdentifier());

                            serviceType = ServiceType.valueOf("S" + identifier.getIdentifier());
                            if (!serviceType.equals(serviceTypeNomAdmUsluga)) {
                                throw new RuntimeException("JSON ServiceType: " + serviceType + " is different form NomAdmUsluga (serviceType) " + serviceTypeNomAdmUsluga);
                            }

                            if (ServiceType.S769.equals(serviceType)) {
                                // We have 5 (1, 2, 3, 4, 5) S769 application variants
                                Object variantObject = BaseApplicationDTO.findValue((LinkedHashMap<?, ?>) wrapper.getServiceRequest().getSpecificContent(), "documentTitle");
                                if (variantObject != null) {
                                    serviceType = ServiceType.valueOf("S" + variantObject.toString().substring(0, 4));
                                }
                            }

                            Record record = register(wrapper, serviceType, docInfo);
                            if (record == null) {
                                updateCounter(docInfo, serviceType, "Invalid service type " + serviceType);
                                throw new RuntimeException("Something went wrong");
                            }

                            processedDocuments++;

                            for (FileWS fileWS : docInfo.getFiles()) {
                                String documentType = fileWS.getId().equals(file.getId())
                                        ? Constants.DOCUMENT_TYPE_APPLICATION_CODE
                                        : Constants.DOCUMENT_TYPE_APPLICATION_ATTACHMENT_CODE;
                                fileStoreService.create(documentType, record.getId(), fileWS.getFileName(),
                                        fileWS.getFileType(), fileWS.getFileContent());
                            }

                            completeCounter(docInfo, serviceType);
                        } catch (Throwable e) {
                            log.error("Error processing document with id: [{}]", docInfo.getId(), e);
                            updateCounter(docInfo, serviceType != null ? serviceType : serviceTypeNomAdmUsluga, e.getMessage());
                        }
                    }
                }
                log.info(">>> scheduleApplications FINISHED for {}ms processed {} applications",
                        System.currentTimeMillis() - startTime, processedDocuments);
            }
        }
    }

    @Scheduled(fixedDelayString = "${bfsa.app-sync.mark-all-finished-fixed-delay}")
    public void scheduleMarkAllCompletedApplicationsAsFinished() {
        if (settingService.checkByKey("global.mark-as-finished", "true")) {
            List<ApplicationSchedulerCounter> completed = applicationSchedulerCounterRepository.findAllByCounter(-1);
            if (!CollectionUtils.isEmpty(completed)) {
                for (ApplicationSchedulerCounter counter : completed) {
                    counter.setMarkAsFinished(markDocAsFinished(counter.getDocumentId()));
                    // TODO Check and if there are problems with concurrency remove the update statement below
                    //  and ApplicationSchedulerCounter#markAsFinished field
                    applicationSchedulerCounterRepository.save(counter);
                }
            }
        }
    }

    private List<DocWS> findAllNewDocuments() {
        String url = "https://demo.indexbg.bg/BabhWS/api/deloWeb/getNewDocuments?systemId=2";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<>() {
                });
            } else {
                log.error("Request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error while invoking " + url, e);
        }
        return null;
    }

    private DocWS getDocumentInfo(final Long docId) {
        if (docId == null || docId <= 0) {
            throw new RuntimeException("Document identifiers is required");
        }
        String url = "https://demo.indexbg.bg/BabhWS/api/deloWeb/getDocumentInfo?docId=" + docId;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), DocWS.class);
            } else {
                log.error("Request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error while invoking " + url, e);
        }
        return null;
    }

    private String markDocAsFinished(final Long docId) {
        String url = "https://demo.indexbg.bg/BabhWS/api/deloWeb/markDocAsFinished?docId=" + docId;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        String result = null;
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                result = objectMapper.readValue(response.body(), String.class);
            } else {
                log.error("Request failed with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error while invoking " + url, e);
        }
        return result;
    }

    private void updateCounter(final DocWS docInfo, final ServiceType serviceType, final String message) {
        ApplicationSchedulerCounter counter = applicationSchedulerCounterRepository.findByDocumentId(docInfo.getId());

        if (counter == null) {
            counter = ApplicationSchedulerCounter.builder()
                    .counter(0)
                    .documentId(docInfo.getId())
                    .entryNumber(docInfo.getRnDoc())
                    .entryDate(docInfo.getDatDoc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .serviceType(serviceType)
                    .message(message)
                    .build();
        } else {
            if (counter.getCounter() > -1 && counter.getCounter() < 3) {
                counter.setCounter(counter.getCounter() + 1);
            }
            if (StringUtils.hasText(message)) {
                counter.setMessage(StringUtils.hasText(counter.getMessage())
                        ? counter.getMessage() + "\n\n" + message
                        : message);
            }
        }

        applicationSchedulerCounterRepository.save(counter);
    }

    private void completeCounter(final DocWS docInfo, final ServiceType serviceType) {
        ApplicationSchedulerCounter counter = applicationSchedulerCounterRepository.findByDocumentId(docInfo.getId());

        if (counter == null) {
            counter = ApplicationSchedulerCounter.builder()
                    .documentId(docInfo.getId())
                    .entryNumber(docInfo.getRnDoc())
                    .entryDate(docInfo.getDatDoc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .serviceType(serviceType)
                    .build();
        }

        counter.setCounter(-1);
//        counter.setMessage(StringUtils.hasText(counter.getMessage())
//                ? counter.getMessage() + "\n\n" + message
//                : message);
        applicationSchedulerCounterRepository.save(counter);
    }

    private Record register(final ServiceRequestWrapper wrapper, final ServiceType serviceType, DocWS docInfo) {
        Language langBG = Language.builder().languageId(Language.BG).build();
        return switch (serviceType) {
            case S16 -> applicationS16Service.register(ApplicationS16DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S502 -> applicationS502Service.register(ApplicationS502DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S503 -> applicationS503Service.register(ApplicationS503DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S1199 -> applicationS1199Service.register(ApplicationS1199DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S1366 -> applicationS1366Service.register(ApplicationS1366DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S1590 -> applicationS1590Service.register(ApplicationS1590DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S1811 -> applicationS1811Service.register(ApplicationS1811DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2170 -> applicationS2170Service.register(ApplicationS2170DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2272 -> applicationS2272Service.register(ApplicationS2272DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2274 -> applicationS2274Service.register(ApplicationS2274DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2695 -> applicationS2695Service.register(ApplicationS2695DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2697 -> applicationS2697Service.register(ApplicationS2697DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2698 -> applicationS2698Service.register(ApplicationS2698DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2699 -> applicationS2699Service.register(ApplicationS2699DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2700 -> applicationS2700Service.register(ApplicationS2700DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2701 -> applicationS2701Service.register(ApplicationS2701DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2702 -> applicationS2702Service.register(ApplicationS2702DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2711 -> applicationS2711Service.register(ApplicationS2711DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S2869 -> applicationS2869Service.register(ApplicationS2869DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3125 -> applicationS3125Service.register(ApplicationS3125DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3180 -> applicationS3180Service.register(ApplicationS3180DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3181 -> applicationS3181Service.register(ApplicationS3181DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3182 -> applicationS3182Service.register(ApplicationS3182DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3201 -> applicationS3201Service.register(ApplicationS3201DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3362 -> applicationS3362Service.register(ApplicationS3362DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3363 -> applicationS3363Service.register(ApplicationS3363DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S3365 -> applicationS3365Service.register(ApplicationS3365DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7691 -> applicationS7691Service.register(ApplicationS7691DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7692 -> applicationS7692Service.register(ApplicationS7692DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7693 -> applicationS7693Service.register(ApplicationS7693DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7694 -> applicationS7694Service.register(ApplicationS7694DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7695 -> applicationS7695Service.register(ApplicationS7695DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            case S7696 -> applicationS7696Service.register(ApplicationS7696DTO.ofServiceRequest(wrapper.getServiceRequest(), docInfo), wrapper, langBG);
            default -> {
                log.error("Invalid service type");
                yield null;
            }
        };
    }

    // --- For test with local file (pdf) ---
    private void localTestWithPdf() {
        try {
            byte[] bytes = Files.readAllBytes(Path.of("bfsa-core/src/test/resources/test-files/7691-v2.pdf"));
            String jsonString = documentService.getPdfDataJson(bytes, Constants.APPLICATION_JSON_METADATA_KEY);
//        String jsonString = documentService.getPdfDataJson(bytes, "application.json_submission");
            log.debug(">>> jsonString {}", jsonString);

            ServiceRequestWrapper serviceRequestWrapper = objectMapper.readValue(jsonString, ServiceRequestWrapper.class);
            ServiceRequest serviceRequest = serviceRequestWrapper.getServiceRequest();

            ApplicationS7691DTO dto = ApplicationS7691DTO.ofServiceRequest(serviceRequest, null);
            Record result = applicationS7691Service.register(
                    dto,
                    serviceRequestWrapper,
                    Language.builder().languageId(Language.BG).build()
            );
            log.info("\n\nResult:\nId: {}\nEntry number: {}\n\n", result.getId(), result.getEntryNumber());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
