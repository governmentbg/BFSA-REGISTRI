package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.dto.SettlementVO;
import bg.bulsi.bfsa.model.Settlement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, String>,
        RevisionRepository<Settlement, String, Long> {
    List<Settlement> findAllByParentCodeIsNull();

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s.code, s.name, s.nameLat) " +
            "FROM Settlement s " +
            "WHERE s.parentCode IS NULL " +
            "ORDER BY s.name")
    List<SettlementVO> findAllSettlementVO();

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s.code, s.name, s.nameLat) " +
            "FROM Settlement s " +
            "WHERE s.parentCode = :parentCode " +
            "ORDER BY s.name")
    List<SettlementVO> findAllByParentCodeSettlementVO(final String parentCode);

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s1.code, s1.name, s1.nameLat) " +
            "FROM Settlement  s1 " +
            "JOIN Settlement s2 " +
            "ON s1.regionCode = s2.regionCode " +
            "WHERE s2.code = :code " +
            "ORDER BY s1.name")
    List<SettlementVO> findAllRegionSettlementsVO(final String code);

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s.code, s.name, s.nameLat) " +
            "FROM Settlement  s " +
            "WHERE s.parentCode = :code " +
            "AND s.municipalityCode != (" +
            "SELECT c.municipalityCode FROM Settlement c WHERE c.code = s.parentCode) " +
            "OR s.code = :code " +
            "ORDER BY s.name")
    List<SettlementVO> findAllMunicipalitySettlementsVO(final String code);

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s.code, s.name, s.nameLat, s.municipalityName, " +
            "s.municipalityNameLat, s.regionName, s.regionNameLat, s.placeType) " +
            "FROM Settlement s " +
            "WHERE s.code = :code " +
            "ORDER BY s.name")
    SettlementVO findByCodeSettlementVO(final String code);

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementDTO(s) " +
            "FROM Settlement s " +
            "WHERE s.parentCode IS NULL OR s.parentCode = '' ORDER BY s.name")
    Page<SettlementDTO> findAllSettlementDTOWithoutSubs(Pageable pageable);

    //    @Query("SELECT s FROM Settlement s LEFT JOIN FETCH s.subSettlements WHERE s.code = :code")
    Optional<Settlement> findByCode(String code);

    @Query("SELECT s FROM Settlement s WHERE s.regionCode = (SELECT s1.regionCode FROM Settlement s1 WHERE s1.code = :code) AND s.parentCode IS NULL")
    Optional<Settlement> findRegionBySettlementCode(String code);

    @Query("SELECT new bg.bulsi.bfsa.dto.SettlementVO(s.code, s.name, s.nameLat, s.municipalityName, " +
            "s.municipalityNameLat, s.regionName, s.regionNameLat, s.placeType) " +
            "FROM Settlement s " +
            "WHERE :param IS NULL " +
            "OR lower(s.name) like lower(concat('%', :param, '%')) " +
            "OR lower(s.nameLat) like lower(concat('%', :param, '%'))"
    )
    List<SettlementVO> search(@Param("param") final String param);

    @Query("SELECT NEW bg.bulsi.bfsa.dto.SettlementVO(" +
            "CASE WHEN s.placeType = 'RESORT' THEN s3.code " +
            "     WHEN s.placeType = 'VILLAGE' OR s.placeType = 'MONASTERY' THEN s2.code " +
            "     WHEN s.placeType = 'CITY' THEN s1.code " +
            "     WHEN s.placeType = 'REGION' THEN s.code END, " +
            "CASE WHEN s.placeType = 'RESORT' THEN s2.code " +
            "     WHEN s.placeType = 'VILLAGE' OR s.placeType = 'MONASTERY' THEN s1.code " +
            "     WHEN s.placeType = 'CITY' THEN s.code END, " +
            "CASE WHEN s.placeType = 'RESORT' THEN s1.code " +
            "     WHEN s.placeType = 'VILLAGE' OR s.placeType = 'MONASTERY' THEN s.code END, " +
            "CASE WHEN s.placeType = 'RESORT' THEN s.code END) " +
            "FROM Settlement s " +
            "LEFT JOIN Settlement s1 ON s.parentCode = s1.code " +
            "LEFT JOIN Settlement s2 ON s1.parentCode = s2.code " +
            "LEFT JOIN Settlement s3 ON s2.parentCode = s3.code " +
            "WHERE s.code = :code")
    SettlementVO findSettlementVOByCode(final String code);
}