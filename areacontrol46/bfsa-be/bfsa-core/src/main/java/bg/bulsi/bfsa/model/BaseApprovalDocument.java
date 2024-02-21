package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class BaseApprovalDocument extends BaseEntity {
    @Column(name = "reg_number")
    private String regNumber;

    @Column(name = "reg_date")
    private LocalDate regDate;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "valid_until_date")
    private LocalDate validUntilDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApprovalDocumentStatus status;

    @Column(name = "enabled")
    private Boolean enabled;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id")
    private Record record;
}