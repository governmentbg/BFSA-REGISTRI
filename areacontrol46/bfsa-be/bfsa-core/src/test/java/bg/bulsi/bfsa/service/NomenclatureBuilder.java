package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.NomenclatureI18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NomenclatureBuilder {
    @Autowired
    private NomenclatureService service;

    public Nomenclature mockNomenclature(Language language) {
        return mockNomenclature(language, null);
    }

    public Nomenclature mockNomenclature(Language language, Nomenclature parent) {
        Nomenclature nom = Nomenclature.builder().parent(parent).enabled(true).build();
        nom.getI18ns().add(new NomenclatureI18n(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), nom, language)
        );
        return nom;
    }

    public Nomenclature mockNomenclature(String name, String desc, Language lang) {
        Nomenclature nom = Nomenclature.builder().enabled(true).build();
        nom.getI18ns().add(new NomenclatureI18n(name, desc, nom, lang));

        return nom;
    }

    public Nomenclature mockNomenclatureWithSubNomenclatures(Language language) {
        Nomenclature parent = mockNomenclature(language);
        Nomenclature sub1 = mockNomenclature(language);
        Nomenclature sub2 = mockNomenclature(language);
        sub1.setParent(parent);
        sub2.setParent(parent);

        parent.getSubNomenclatures().add(sub1);
        parent.getSubNomenclatures().add(sub2);

        return parent;
    }

    public Nomenclature saveNomenclature(Language language){
       return service.createNext(mockNomenclature(language));
    }

    public Nomenclature saveNomenclatureWithSubNomenclatures(Language language){
        return service.createNext(mockNomenclatureWithSubNomenclatures(language));
    }
}
