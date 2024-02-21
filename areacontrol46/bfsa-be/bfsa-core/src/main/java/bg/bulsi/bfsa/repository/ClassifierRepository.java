package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.dto.BaseVO;
import bg.bulsi.bfsa.dto.ClassifierVO;
import bg.bulsi.bfsa.model.Classifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassifierRepository extends JpaRepository<Classifier, String>, RevisionRepository<Classifier, String, Long> {

//    @Query("SELECT c FROM Classifier c JOIN c.i18ns i18n WHERE c.code = :code AND i18n.classifierI18nIdentity.languageId = :languageId")
//    Optional<Classifier> findByCodeAndLanguageId(final String code, final String languageId);

    @Query("SELECT c FROM Classifier c LEFT JOIN FETCH c.subClassifiers WHERE c.code = :code")
    Optional<Classifier> findByCode(String code);

    Optional<Classifier> findByExternalCode(final String externalCode);

    @Query("SELECT new bg.bulsi.bfsa.dto.BaseVO(c.code, i18n.name, i18n.description) " +
            "FROM Classifier c JOIN c.i18ns i18n " +
            "WHERE c.parent IS NULL " +
            "AND i18n.classifierI18nIdentity.languageId = :languageId")
    List<BaseVO> findAllClassifierVO(@Param("languageId") final String languageId);

    @Query("SELECT new bg.bulsi.bfsa.dto.BaseVO(c.code, i18n.name, i18n.description) " +
            "FROM Classifier c JOIN c.i18ns i18n " +
            "WHERE c.parent.code = :code " +
            "AND i18n.classifierI18nIdentity.languageId = :languageId")
    List<BaseVO> findAllByCodeVO(@Param("code") final String code, @Param("languageId") final String languageId);

    @Query("SELECT MAX(c.code) FROM Classifier c WHERE c.enabled = TRUE AND c.code like lower(concat(:parentCode, '%'))")
    String findLastCode(@Param("parentCode") String parentCode);

    @Query("SELECT MAX(c.code) FROM Classifier c WHERE c.parent IS NULL")
    String findLastParentCode();

    List<Classifier> findAllByParentIsNull();
    @Query("SELECT new bg.bulsi.bfsa.dto.ClassifierVO(c.code, c.symbol, i18n.name, i18n.description) " +
            "FROM Classifier c JOIN c.i18ns i18n " +
            "WHERE c.parent IS NULL AND i18n.classifierI18nIdentity.languageId = :languageId")
    List<ClassifierVO> findAllByParentsClassifierVO(@Param("languageId") final String languageId);

    @Query("SELECT new bg.bulsi.bfsa.dto.ClassifierVO(c.code, c.symbol, i18n.name, i18n.description) " +
            "FROM Classifier c JOIN c.i18ns i18n " +
            "WHERE c.parent.code = :code " +
            "AND i18n.classifierI18nIdentity.languageId = :languageId")
    List<ClassifierVO> findAllByParentCodeVO(@Param("code") final String code, @Param("languageId") final String languageId);
}