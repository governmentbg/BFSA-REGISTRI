package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.MessageResourceDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.repository.MessageResourceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageResourceService {

	private final MessageResourceRepository repository;

	public MessageResource findById(final MessageResource.MessageResourceIdentity id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageResource.class, id));
	}

	public String get(final String code, Language language) {
		return findById(new MessageResource.MessageResourceIdentity(code, language.getLanguageId())).getMessage();
	}

	public String get(final String code, String languageId) {
		return findById(new MessageResource.MessageResourceIdentity(code, languageId)).getMessage();
	}

	public MessageResource findByIdOrNull(final MessageResource.MessageResourceIdentity id) {
		return repository.findById(id).orElse(null);
	}

	public List<MessageResource> findAll() {
		return repository.findAll();
	}

	public List<MessageResource> findAll(final String languageId, final Pageable pageable) {
		return repository.findAllByMessageResourceIdentityLanguageIdOrderByMessageResourceIdentity_code(languageId, pageable);
	}

	public List<MessageResource> findAllByCode(final String code) {
		return repository.findAllByMessageResourceIdentity_Code(code);
	}

	@Transactional
	public MessageResource update(final MessageResource.MessageResourceIdentity identity, final MessageResourceDTO dto) {
		if (identity == null || !StringUtils.hasText(identity.getCode()) || dto == null || !identity.getCode().equals(dto.getCode())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		MessageResource entity = findById(identity);

		entity.setMessage(dto.getMessage());

		return repository.save(entity);
	}

	@Transactional
	public MessageResource updateOrCreate(final MessageResource.MessageResourceIdentity identity, final MessageResourceDTO dto) {
		if (identity == null || !StringUtils.hasText(identity.getCode()) || dto == null || !identity.getCode().equals(dto.getCode())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		MessageResource entity = findByIdOrNull(identity);

		if (entity == null) {
			entity = MessageResource.builder().messageResourceIdentity(identity).build();
		}

		entity.setMessage(dto.getMessage());

		return repository.save(entity);
	}
}
