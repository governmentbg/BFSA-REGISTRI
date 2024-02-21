package bg.bulsi.bfsa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DaeuRegisterPaymentRequestDTO {

    public List<Actor> actors = new ArrayList<>();
    public PaymentData paymentData;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Actor {
        private String name;
        private ActorType type;
        private ParticipantType participantType;
        private ActorUid uid;
        private ActorInfo info;
    }

    public enum ActorType {
        PERSON,
        SATEADMINISTRATION
    }

    public enum ParticipantType {
        APPLICANT,
        PROVIDER
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActorUid {
        private String type = "EIK";
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActorInfo {
        private BankAccount bankAccount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankAccount {
        private String name;
        private String bank;
        private String bic;
        private String iban;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        private String currency;
        private BigDecimal amount;
        private String reason;
        private String referenceType;
        private String referenceNumber;
        private LocalDate referenceDate;
        private LocalDate expirationDate;
    }
}
