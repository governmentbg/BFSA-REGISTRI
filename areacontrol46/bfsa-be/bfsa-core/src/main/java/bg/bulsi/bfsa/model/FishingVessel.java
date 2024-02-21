package bg.bulsi.bfsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fishing_vessels")
public class FishingVessel extends BaseEntity {

    @Column(name = "registration_number")
    private String regNumber;

    @Column(name = "external_marking")
    private String externalMarking;

    @Column(name = "entry_number")
    private String entryNumber;

    @Column(name = "hull_length")
    private Double hullLength;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_type_code")
    private Nomenclature assignmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_code")
    private Nomenclature type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch")
    private Branch branch;
}
