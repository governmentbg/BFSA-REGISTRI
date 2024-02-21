package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Setting;
import bg.bulsi.bfsa.repository.SettingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SettingService {

    private final SettingRepository repository;

    public Optional<Setting> getByKey(String key) {
        return repository.findByKey(key);
    }

    public boolean checkByKey(final String key, final String value) {
        return repository.findByKey(key)
                .filter(setting -> value.equals(setting.getValue()))
                .isPresent();
    }

    public void saveSetting(Setting setting) {
        repository.save(setting);
    }
}