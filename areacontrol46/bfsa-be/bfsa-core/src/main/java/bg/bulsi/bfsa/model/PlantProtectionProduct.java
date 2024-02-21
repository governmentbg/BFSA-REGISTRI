package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * Register of plant protection products
 * authorized for marketing and
 * use on the territory of the Republic of Bulgaria for
 * limited and controlled use according to
 * Art. 53 of Regulation (EC) No. 1107/2009
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plant_protection_products")
public class PlantProtectionProduct extends BaseEntity {

	@Serial
	private static final long serialVersionUID = -6728373961692071967L;

	@Column(name = "quantity")
	private Double quantity;

	// TODO: 1. Add connection with contractor: manufacture and seller(many),
	//  2. add query in repository,
	//  3. uncomment the method in service and controller
	//  4. add http request for test

	@Builder.Default
	@OneToMany(mappedBy = "plantProtectionProduct", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<PlantProtectionProductI18n> i18ns = new HashSet<>();

	public PlantProtectionProductI18n getI18n(final Language language) {
		if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
			for (PlantProtectionProductI18n i18n: i18ns) {
				if (language.getLanguageId().equals(i18n.getPlantProtectionProductI18nIdentity().getLanguageId())) {
					return i18n;
				}
			}
		}
		PlantProtectionProductI18n i18n = new PlantProtectionProductI18n(this, language);
		i18ns.add(i18n);
		return i18n;
	}
}
