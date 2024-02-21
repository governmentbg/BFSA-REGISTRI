package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Profile({"development", "test"})
public class EPaymentDevService implements EPaymentService {
    public DaeuRegisterPaymentResponseDTO registerEPayment(Record record) {
        return DaeuRegisterPaymentResponseDTO.builder()
                .paymentId(UUID.randomUUID().toString())
                .accessCode(UUID.randomUUID().toString())
                .registrationTime(LocalDateTime.now().toString())
                .build();
    }
}
