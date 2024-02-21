package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.ActivityGroupI18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityGroupBuilder {

    @Autowired
    public ActivityGroupService service;

    public static final Boolean ENABLED = true;
    public static final String TEST_NAME_BG = "Секция I ";
    public static final String TEST_NAME_EN = "Group I ";
    public static final String TEST_DESC_BG = "Месо от домашни копитни ";
    public static final String TEST_DESC_EN = "Meet from home animals ";

    public ActivityGroup save() {
        return service.create(build());
    }

    public ActivityGroup saveWithSubs() {
        return service.create(buildWithSubs());
    }

    public ActivityGroup build() {
        ActivityGroup group = ActivityGroup.builder().enabled(ENABLED).build();
        group.getI18ns().add(new ActivityGroupI18n(TEST_NAME_BG, TEST_DESC_BG, group, BaseServiceTest.langBG));
        group.getI18ns().add(new ActivityGroupI18n(TEST_NAME_EN, TEST_DESC_EN, group, BaseServiceTest.langEN));
        return group;
    }

    public List<ActivityGroup> buildSome() {
        return List.of(build(), build(), build(), build());
    }

    public ActivityGroup buildWithSubs() {
        ActivityGroup group = build();
        ActivityGroup group0 = build();
        ActivityGroup group1 = build();

        group0.setParent(group);
        group1.setParent(group);
        group.getSubActivityGroups().add(group0);
        group.getSubActivityGroups().add(group1);

        return group;
    }
}
