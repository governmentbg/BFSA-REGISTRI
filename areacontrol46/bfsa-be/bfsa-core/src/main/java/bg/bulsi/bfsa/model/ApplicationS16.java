package bg.bulsi.bfsa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
//@NoArgsConstructor
@Table(name = "applications_s16")
public class ApplicationS16 extends BaseApplication{

}
