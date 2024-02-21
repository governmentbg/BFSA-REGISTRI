package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.dto.BaseVO;
import bg.bulsi.bfsa.model.ActivityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, Long>, RevisionRepository<ActivityGroup, Long, Long> {
    @Query("SELECT new bg.bulsi.bfsa.dto.BaseVO(g.id, i18n.name, i18n.description) " +
            "FROM ActivityGroup g JOIN g.i18ns i18n " +
            "WHERE g.parent IS NULL " +
            "AND i18n.activityGroupI18NIdentity.languageId = :languageId")
    List<BaseVO> findAllParentsVO(@Param("languageId") final String languageId);

    @Query("SELECT new bg.bulsi.bfsa.dto.BaseVO(g.id, i18n.name, i18n.description) " +
            "FROM ActivityGroup g JOIN g.i18ns i18n " +
            "WHERE g.parent.id = :id " +
            "AND i18n.activityGroupI18NIdentity.languageId = :languageId")
    List<BaseVO> findAllByParentIdVO(@Param("id") final Long id, @Param("languageId") final String languageId);

    Set<ActivityGroup> findAllByParentIsNull();
}