package bg.bulsi.bfsa.model;

import jakarta.validation.constraints.NotNull;
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
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plant_protection_products_i18n")
public class PlantProtectionProductI18n extends AuditableEntity {

	@Serial
	private static final long serialVersionUID = -3624628096081131452L;

	@EmbeddedId
	private PlantProtectionProductI18nIdentity plantProtectionProductI18nIdentity;

	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "active_substances")
	private String activeSubstances;
	@Column(name = "purpose")
	private String purpose;
	@Column(name = "pest")
	private String pest;
	@Column(name = "crop")
	private String crop;
	@Column(name = "application")
	private String application;

	@MapsId("id")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id")
	private PlantProtectionProduct plantProtectionProduct;


	public PlantProtectionProductI18n(final String name, final String activeSubstances, final String purpose,
			final String pest, final String crop, final String application,
			final PlantProtectionProduct plantProtectionProduct, final Language language) {
		this.plantProtectionProductI18nIdentity = new PlantProtectionProductI18nIdentity(plantProtectionProduct.getId(), language.getLanguageId());
		this.name = name;
		this.activeSubstances = activeSubstances;
		this.purpose = purpose;
		this.pest = pest;
		this.crop = crop;
		this.application = application;
		this.plantProtectionProduct = plantProtectionProduct;
	}


	public PlantProtectionProductI18n(final PlantProtectionProduct plantProtectionProduct, final Language language) {
		this.plantProtectionProductI18nIdentity = new PlantProtectionProductI18nIdentity(plantProtectionProduct.getId(), language.getLanguageId());
		this.plantProtectionProduct = plantProtectionProduct;
	}


	@Getter
	@Setter
	@Embeddable
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PlantProtectionProductI18nIdentity implements Serializable {

		@Serial
		private static final long serialVersionUID = -6944669027836117786L;

		@Column(name = "id")
		private Long id;

		@NotNull
		@Basic(optional = false)
		@Column(name = "language_id")
		private String languageId;

	}
}
