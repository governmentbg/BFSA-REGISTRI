package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.DaeuGetTokenResponseDTO;
import bg.bulsi.bfsa.dto.DaeuRegisterPaymentRequestDTO;
import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Profile("production")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EPaymentProdService implements EPaymentService {

    private final HttpClient httpClient;

    @Value("${bfsa.e-payment.base-url}")
    private String BASE_URL;
    @Value("${bfsa.e-payment.scope}")
    private String SCOPE;
    @Value("${bfsa.e-payment.register-scope}")
    private String REGISTER_SCOPE;
    @Value("${bfsa.e-payment.register-connector}")
    private String REGISTER_CONNECTOR;
    @Value("${bfsa.e-payment.register-version}")
    private String REGISTER_VERSION;
    @Value("${bfsa.e-payment.register-endpoint}")
    private String REGISTER_ENDPOINT;
    @Value("${bfsa.e-payment.client-id}")
    private String CLIENT_ID;
    @Value("${bfsa.e-payment.grant-type}")
    private String GRANT_TYPE;

    @Value("${bfsa.e-payment.bank-account.name}")
    private String BANK_ACCOUNT_NAME;
    @Value("${bfsa.e-payment.bank-account.iban}")
    private String BANK_ACCOUNT_IBAN;
    @Value("${bfsa.e-payment.bank-account.bank}")
    private String BANK_ACCOUNT_BANK;
    @Value("${bfsa.e-payment.bank-account.bank-bic}")
    private String BANK_ACCOUNT_BANK_BIC;
    @Value("${bfsa.e-payment.bank-account.bulstat}")
    private String BANK_ACCOUNT_BULSTAT;
    @Value("${bfsa.e-payment.bank-account.currency}")
    private String BANK_ACCOUNT_CURRENCY;
    @Value("${bfsa.e-payment.token-port}")
    private String TOKEN_PORT;
    @Value("${bfsa.e-payment.token-endpoint}")
    private String TOKEN_ENDPOINT;
    @Value("${bfsa.e-payment.register-port}")
    private String REGISTER_PORT;

    private static final String AUTHORIZATION_TYPE = "Bearer ";
    private static final String GRANT_TYPE_URL = "?grant_type=";
    private static final String CLIENT_ID_URL = "&client_id=";
    private static final String SCOPE_URL = "&scope=";

    private static DaeuGetTokenResponseDTO daeuGetTokenResponseDTO;

    public DaeuRegisterPaymentResponseDTO registerEPayment(Record record) throws Exception {
        DaeuGetTokenResponseDTO daeuGetTokenResponseDTO = getToken();

        DaeuRegisterPaymentRequestDTO dto = new DaeuRegisterPaymentRequestDTO();
        dto.setPaymentData(getPaymentDataForRequestBody(record,"плащане")); // TODO: Въвеждане на правилно основание, когато е ясно какво да бъде

        List<DaeuRegisterPaymentRequestDTO.Actor> actors = new ArrayList<>();
        actors.add(getPayerForRequestBody(record));
        actors.add(getRecipientForRequestBody());
        dto.setActors(actors);

        String url = BASE_URL + ":" + REGISTER_PORT + REGISTER_SCOPE +
                     REGISTER_CONNECTOR + REGISTER_VERSION + REGISTER_ENDPOINT;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONObject(dto).toString()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_TYPE + daeuGetTokenResponseDTO.getAccessToken())
                .build();

        CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result = futureResponse.thenApply(HttpResponse::body).get(10, TimeUnit.SECONDS);

//        futureResponse
//                .thenApply(HttpResponse::body)
//                .thenAccept(response -> {
//                    log.info("Response received: {}", response);
//                }).exceptionally(e -> {
//                    log.error(e.getMessage(), result);
//                    throw new RuntimeException(e);
//                });

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, DaeuRegisterPaymentResponseDTO.class);
    }

    public DaeuGetTokenResponseDTO getToken() throws IOException, InterruptedException {
        if (daeuGetTokenResponseDTO != null
                && StringUtils.hasText(daeuGetTokenResponseDTO.getAccessToken())
                && daeuGetTokenResponseDTO.getExpiresIn() > 0
                && daeuGetTokenResponseDTO.getConsentedOn() > 0) {

            LocalDateTime createdOnTime =
                    LocalDateTime.ofInstant(Instant.ofEpochSecond(daeuGetTokenResponseDTO.getConsentedOn()),
                            TimeZone.getDefault().toZoneId());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expireTime = createdOnTime
                    .plusSeconds(daeuGetTokenResponseDTO.getExpiresIn())
                    .minusSeconds(20);

            if (now.isBefore(expireTime)) {
                return daeuGetTokenResponseDTO;
            }
        }

        String grantType = GRANT_TYPE_URL + GRANT_TYPE;
        String clientId = CLIENT_ID_URL + CLIENT_ID;
        String scope = SCOPE_URL + SCOPE;
        String uri = BASE_URL + ":" + TOKEN_PORT + TOKEN_ENDPOINT +
                     grantType + clientId + scope;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.info(">>> HttpClient response status code: [{}]", response.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        daeuGetTokenResponseDTO = mapper.readValue(response.body(), DaeuGetTokenResponseDTO.class);
        return daeuGetTokenResponseDTO;
    }

    private DaeuRegisterPaymentRequestDTO.Actor getPayerForRequestBody(Record record) {
        DaeuRegisterPaymentRequestDTO.Actor actor = new DaeuRegisterPaymentRequestDTO.Actor();
        actor.setType(DaeuRegisterPaymentRequestDTO.ActorType.PERSON);
        actor.setParticipantType(DaeuRegisterPaymentRequestDTO.ParticipantType.APPLICANT);
        actor.setName(record.getRequestor().getFullName());

        DaeuRegisterPaymentRequestDTO.ActorUid uid = new DaeuRegisterPaymentRequestDTO.ActorUid();
        uid.setType(ServiceType.S2701.equals(record.getServiceType()) ? "EGN" : "EIK");
        uid.setValue(record.getRequestor().getIdentifier());
        actor.setUid(uid);

        return actor;
    }

    private DaeuRegisterPaymentRequestDTO.Actor getRecipientForRequestBody() {
        DaeuRegisterPaymentRequestDTO.Actor actor = new DaeuRegisterPaymentRequestDTO.Actor();
        actor.setName(BANK_ACCOUNT_NAME);
        actor.setType(DaeuRegisterPaymentRequestDTO.ActorType.SATEADMINISTRATION);
        actor.setParticipantType(DaeuRegisterPaymentRequestDTO.ParticipantType.PROVIDER);

        DaeuRegisterPaymentRequestDTO.ActorUid uid = new DaeuRegisterPaymentRequestDTO.ActorUid();
        uid.setType("EIK");
        uid.setValue(BANK_ACCOUNT_BULSTAT);
        actor.setUid(uid);

        DaeuRegisterPaymentRequestDTO.BankAccount bankAccount = new DaeuRegisterPaymentRequestDTO.BankAccount();
        bankAccount.setBank(BANK_ACCOUNT_BANK);
        bankAccount.setBic(BANK_ACCOUNT_BANK_BIC);
        bankAccount.setIban(BANK_ACCOUNT_IBAN);
        bankAccount.setName(BANK_ACCOUNT_NAME);

        DaeuRegisterPaymentRequestDTO.ActorInfo info = new DaeuRegisterPaymentRequestDTO.ActorInfo();
        info.setBankAccount(bankAccount);
        actor.setInfo(info);

        return actor;
    }

    private DaeuRegisterPaymentRequestDTO.PaymentData getPaymentDataForRequestBody(Record record, String reason) {
        DaeuRegisterPaymentRequestDTO.PaymentData paymentData = new DaeuRegisterPaymentRequestDTO.PaymentData();
        paymentData.setAmount(record.getPrice());
        paymentData.setReferenceNumber(UUID.randomUUID().toString().substring(0, 5)); // TODO: Да се добави, след като получим номер от деловодната с-ма
        paymentData.setReferenceType("1"); // TODO: Да се провери дали е валидно и необходимо, след като БАБХ предоставят данни за е-плащане
        paymentData.setCurrency(BANK_ACCOUNT_CURRENCY);
        paymentData.setReason(reason);
        LocalDate referenceDate = LocalDate.now();
        LocalDate expirationDate = LocalDate.now().plusDays(7L); // TODO: Въвеждане на правилни дни, след като БАБХ ги оточнят
        paymentData.setReferenceDate(referenceDate);
        paymentData.setExpirationDate(expirationDate);

        return paymentData;
    }
}
