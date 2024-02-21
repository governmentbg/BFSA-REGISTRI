package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.model.Record;

public interface EPaymentService {
    DaeuRegisterPaymentResponseDTO registerEPayment(Record record) throws Exception;
}
