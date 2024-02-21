package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicles_i18n")
public class VehicleI18n extends AuditableEntity {

    @EmbeddedId
    private VehicleI18nIdentity vehicleI18nIdentity;

    @Column(name = "name")
    private String name;

    @Column(name = "brand_model")
    private String brandModel;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Vehicle vehicle;

    public VehicleI18n(final String name, final String description, final String brandModel, final Vehicle vehicle, final Language language) {
        this.vehicleI18nIdentity = new VehicleI18nIdentity(vehicle.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
        this.brandModel = brandModel;
        this.vehicle = vehicle;
    }

    public VehicleI18n(final Vehicle vehicle, final Language language) {
        this.vehicleI18nIdentity = new VehicleI18nIdentity(vehicle.getId(), language.getLanguageId());
        this.vehicle = vehicle;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 6827130210171021926L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
