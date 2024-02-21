package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileStoreRepository extends JpaRepository<File, Long> {
    List<File> findAllByRecord_Id(final Long recordId);
    List<File> findAllByInspection_Id(final Long inspectionId);
}
