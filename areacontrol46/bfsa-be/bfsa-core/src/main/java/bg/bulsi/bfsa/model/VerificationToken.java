package bg.bulsi.bfsa.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;

@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "verification_tokens")
public class VerificationToken extends AbstractVerificationToken {

	@Serial
	private static final long serialVersionUID = -4340431584177779692L;

}
