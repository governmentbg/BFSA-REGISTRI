package bg.bulsi.bfsa.util;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.service.LanguageService;
import org.springframework.util.StringUtils;

import java.util.Locale;

public class LocaleUtil {

//    public static String getLanguageId(final Locale locale, final LanguageService languageService) {
//        if (locale != null && StringUtils.hasText(locale.getLanguage())) {
//            return locale.getLanguage();
//        }
//        return languageService.getMainLanguage().getLanguageId();
//    }
    public static Language getLanguage(final Locale locale, final LanguageService languageService) {
        return (locale != null && StringUtils.hasText(locale.getLanguage()))
                ? languageService.findById(locale.getLanguage())
                : languageService.getMainLanguage();
    }
}
