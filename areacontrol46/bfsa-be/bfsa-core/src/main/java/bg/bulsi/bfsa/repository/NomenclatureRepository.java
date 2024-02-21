package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.dto.NomenclatureVO;
import bg.bulsi.bfsa.model.Nomenclature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomenclatureRepository extends JpaRepository<Nomenclature, String> {

	@Query("SELECT n FROM Nomenclature n LEFT JOIN FETCH n.subNomenclatures WHERE n.code = :code")
	Optional<Nomenclature> findByCode(String code);

	@Query("SELECT new bg.bulsi.bfsa.dto.NomenclatureVO(n.code, n.symbol, i18n.name, i18n.description) " +
			"FROM Nomenclature n JOIN n.i18ns i18n " +
			"WHERE n.enabled = TRUE " +
			"AND n.parent.code = :code " +
			"AND i18n.nomenclatureI18nIdentity.languageId = :languageId")
	List<NomenclatureVO> findAllByParentCodeVO(@Param("code") final String code, @Param("languageId") final String languageId);

	List<Nomenclature> findAllByParentIsNull();

	@Query("SELECT new bg.bulsi.bfsa.dto.NomenclatureVO(n.code, n.symbol, i18n.name, i18n.description) " +
			"FROM Nomenclature n JOIN n.i18ns i18n " +
			"WHERE n.parent IS NULL " +
			"AND i18n.nomenclatureI18nIdentity.languageId = :languageId")
	List<NomenclatureVO> findAllByParentNomenclatureVO(@Param("languageId") final String languageId);

	@Query("SELECT MAX(nom.code) FROM Nomenclature nom WHERE nom.enabled = TRUE AND nom.parent.code = :parentCode")
	String findLastCode(@Param("parentCode") String parentCode);

	@Query("SELECT MAX(nom.code) FROM Nomenclature nom WHERE nom.parent IS NULL")
	String findLastParentCode();

	Optional<Nomenclature> findByExternalCode(String externalCode);
}