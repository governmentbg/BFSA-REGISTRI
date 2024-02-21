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
@Table(name = "facilities_i18n")
public class FacilityI18n extends AuditableEntity {

    @EmbeddedId
    private FacilityI18nIdentity facilityI18nIdentity;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Facility facility;

    // Описание на дейностите, извършвани в обекта
    @Column(name = "activity_description")
    private String activityDescription;

    // Разрешение за ползване по чл. 177, ал. 2 от Закона за устройство на територията №
    @Column(name = "permission_177")
    private String permission177;

    // Начин на отвеждане на отпадните води
    @Column(name = "disposal_waste_water")
    private String disposalWasteWater;

    // Разрешение за ползване по чл. 177, ал. 2 от Закона за устройство на територията №
    @Column(name = "food_type_description")
    private String foodTypeDescription;

    public FacilityI18n(final String name, final String description,
                        final String activityDescription, final String permission177,
                        final String disposalWasteWater, final String foodTypeDescription,
                        final Facility facility, final Language language) {
        this.facilityI18nIdentity = new FacilityI18nIdentity(facility.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
        this.facility = facility;
        this.activityDescription = activityDescription;
        this.permission177 = permission177;
        this.disposalWasteWater = disposalWasteWater;
        this.foodTypeDescription = foodTypeDescription;
    }

    public FacilityI18n(final Facility facility, final Language language) {
        this.facilityI18nIdentity = new FacilityI18nIdentity(facility.getId(), language.getLanguageId());
        this.facility = facility;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacilityI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 6480740681484415576L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
