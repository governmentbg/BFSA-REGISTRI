package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.VerificationToken;

import java.math.BigDecimal;

public interface MailService {
    void sendRegisterUserVerificationToken(VerificationToken verificationToken, Language language);
    void sendForgotPasswordVerificationToken(VerificationToken verificationToken, Language language);
    void sendApplicationPaymentNotification(BigDecimal price, Record record, Language language, String accessCode);
    void sendApplicationCorrectionNotification(Record record, String message, Language language);
}
