package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_supplements_i18n")
public class FoodSupplementI18n extends AuditableEntity {

    @EmbeddedId
    private FoodSupplementI18nIdentity foodSupplementI18nIdentity;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "purpose", length = 5000)
    private String purpose;
    @Column(name = "ingredients", length = 5000)
    private String ingredients;
    @Column(name = "description", length = 5000)
    private String description;
    @Column(name = "manufacture_company_name")
    private String manufactureCompanyName;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private FoodSupplement foodSupplement;

    public FoodSupplementI18n(final String name, final String purpose, final String ingredients,
                              final String description, final String manufactureCompanyName, final FoodSupplement foodSupplement, final Language language) {
        this.foodSupplementI18nIdentity = new FoodSupplementI18nIdentity(foodSupplement.getId(), language.getLanguageId());
        this.name = name;
        this.purpose = purpose;
        this.ingredients = ingredients;
        this.description = description;
        this.foodSupplement = foodSupplement;
        this.manufactureCompanyName = manufactureCompanyName;
    }

    public FoodSupplementI18n(final FoodSupplement foodSupplement, final Language language) {
        this.foodSupplementI18nIdentity = new FoodSupplementI18nIdentity(foodSupplement.getId(), language.getLanguageId());
        this.foodSupplement = foodSupplement;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoodSupplementI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 2671971100624386293L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
