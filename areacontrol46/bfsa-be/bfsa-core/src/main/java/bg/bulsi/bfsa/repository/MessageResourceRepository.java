package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.MessageResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageResourceRepository extends JpaRepository<MessageResource, MessageResource.MessageResourceIdentity> {
    List<MessageResource> findAllByMessageResourceIdentityLanguageIdOrderByMessageResourceIdentity_code(final String languageId, final Pageable pageable);

    List<MessageResource> findAllByMessageResourceIdentity_Code(final String code);
}