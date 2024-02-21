package bg.bulsi.bfsa.model;


import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_scheduler_counters")
public class ApplicationSchedulerCounter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "document_id", unique = true)
    private Long documentId;

    @Column(name = "entry_number", unique = true)
    private String entryNumber;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "counter")
    private Integer counter;

    @Column(name = "mark_as_finished")
    private String markAsFinished;

    @Column(name = "message", length = 10000)
    private String message;
}
