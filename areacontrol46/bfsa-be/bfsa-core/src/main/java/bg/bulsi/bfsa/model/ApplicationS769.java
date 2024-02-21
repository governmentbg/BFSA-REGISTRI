package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

/**
 * ЗАЯВЛЕНИЕ за регистрация на обекти за производство и търговия с храни
 * Това е базово заявление. Съответните разновидности са:
 *  <p>1. Производство на първични продукти</p>
 *  <p>2. Търговия на ДРЕБНО и заведения за обществено хранене</p>
 *  <p>3. Производство и търговия на ЕДРО с храни от неживотински произход и храни от животински произход извън Регламент 853</p>
 *  <p>4. Транспортиране на храни</p>
 *  <p>5. Регистрация на наематели-търговци</p>
 */

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationS769 extends BaseApplication {

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;
}
