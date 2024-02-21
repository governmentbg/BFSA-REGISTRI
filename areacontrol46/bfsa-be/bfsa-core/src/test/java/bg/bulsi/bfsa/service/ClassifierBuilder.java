package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.ClassifierI18n;
import bg.bulsi.bfsa.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClassifierBuilder {
    @Autowired
    private ClassifierService service;

    public Classifier mockClassifier(Language language) {
        return mockClassifier(language, null);
    }

    public Classifier mockClassifier(Language language, Classifier parent) {
        Classifier classifier = Classifier.builder().parent(parent).enabled(true).build();
        classifier.getI18ns().add(new ClassifierI18n(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), classifier, language)
        );
        return classifier;
    }

    public Classifier mockClassifier(String name, String desc, Language lang) {
        Classifier classifier = Classifier.builder().enabled(true).build();
        classifier.getI18ns().add(new ClassifierI18n(name, desc, classifier, lang));

        return classifier;
    }

    public Classifier mockClassifierWithSubClassifiers(Language language) {
        Classifier parent = mockClassifier(language);
        Classifier sub1 = mockClassifier(language);
        Classifier sub2 = mockClassifier(language);
        sub1.setParent(parent);
        sub2.setParent(parent);

        parent.getSubClassifiers().add(sub1);
        parent.getSubClassifiers().add(sub2);

        return parent;
    }

    public Classifier saveClassifier(Language language){
       return service.createNext(mockClassifier(language));
    }

    public Classifier saveClassifierWithSubClassifiers(Language language){
        return service.createNext(mockClassifierWithSubClassifiers(language));
    }
}
