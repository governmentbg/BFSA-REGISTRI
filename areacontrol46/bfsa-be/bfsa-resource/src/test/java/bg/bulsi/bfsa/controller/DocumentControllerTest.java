//package bg.bulsi.bfsa.controller;
//
//import bg.bulsi.bfsa.enums.RecordStatus;
//import bg.bulsi.bfsa.enums.ServiceType;
//import bg.bulsi.bfsa.model.Address;
//import bg.bulsi.bfsa.model.Branch;
//import bg.bulsi.bfsa.model.Contractor;
//import bg.bulsi.bfsa.model.Facility;
//import bg.bulsi.bfsa.model.Nomenclature;
//import bg.bulsi.bfsa.model.Record;
//import bg.bulsi.bfsa.model.Settlement;
//import bg.bulsi.bfsa.service.DocumentService;
//import bg.bulsi.bfsa.service.NomenclatureService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Slf4j
//class DocumentControllerTest extends BaseControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private DocumentService documentService;
//    @Autowired
//    private NomenclatureService nomenclatureService;
//
//    @Test
//    @WithMockUser(username = "user", authorities = { RolesConstants.ROLE_ADMIN })
//    void givenRecordId_whenExportS2701_thenReturnResource() throws Exception {
//        Resource resource = mock(Resource.class);
//        Record mockRecord = mockRecord();
//        mockRecord.setServiceType(ServiceType.S2701);
//        when(documentService.exportApplicationS2701(any(), any())).thenReturn(resource);
//
//        mockMvc.perform(get("/documents/{recordId}/export-application-s3180", mockRecord.getId())
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(resource)))
//                .andExpect(status().isOk())
//                .andReturn();
//    }
//
//    @Test
//    @WithMockUser(username = "user", authorities = { RolesConstants.ROLE_ADMIN })
//    void givenRecordId_whenExportS3180_thenReturnResource() throws Exception {
//        Resource resource = mock(Resource.class);
//        Record mockRecord = mockRecord();
//        mockRecord.setServiceType(ServiceType.S3180);
//        when(documentService.exportApplicationS3180(any(), any())).thenReturn(resource);
//        mockMvc.perform(post("/documents/{recordId}/export-application-s2701", mockRecord.getId())
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(Resource.class)))
//                .andExpect(status().isOk())
//                .andReturn();
//    }
//
//    private Record mockRecord() {
//        List<Address> addresses = new ArrayList<>();
//        addresses.add(mockAddress());
//
//        return Record.builder()
//                .id(new Random().nextLong(1000))
//                .identifier(UUID.randomUUID().toString())
//                .entryNumber(UUID.randomUUID().toString())
//                .entryDate(LocalDate.now())
//                .branch(Branch.builder().build())
//                .status(RecordStatus.ENTERED)
//                .applicant(Contractor.builder()
//                        .identifier(UUID.randomUUID().toString())
//                        .fullName(UUID.randomUUID().toString())
//                        .build())
//                .applicantAuthorType(Nomenclature.builder().build())
//                .contractor(Contractor.builder()
//                        .identifier(UUID.randomUUID().toString())
//                        .fullName(UUID.randomUUID().toString())
//                        .addresses(addresses)
//                        .build())
//                .facility(mockFacility())
//                .build();
//    }
//
//    private Address mockAddress() {
//        return Address.builder()
//                .id(new Random().nextLong(1000))
//                .settlement(Settlement.builder().code(UUID.randomUUID().toString()).build())
//                .addressType(nomenclatureService.findByCode(Address.ADDRESS_TYPE_HEAD_OFFICE_CODE))
//                .address(UUID.randomUUID().toString())
//                .addressLat(UUID.randomUUID().toString())
//                .enabled(true)
//                .build();
//    }
//
//    private Facility mockFacility() {
//        return Facility.builder()
//                .address(mockAddress())
//                .branch(Branch.builder().id(new Random().nextLong(1000))
//                        .sequenceNumber(UUID.randomUUID().toString())
//                        .settlement(Settlement.builder().code(UUID.randomUUID().toString()).build())
//                        .build())
//                .build();
//    }
//}