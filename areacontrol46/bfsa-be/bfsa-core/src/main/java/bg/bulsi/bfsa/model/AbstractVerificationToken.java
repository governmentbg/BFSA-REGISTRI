package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.util.GeneralUtils;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractVerificationToken implements Serializable {
	@Serial
	private static final long serialVersionUID = -8604121570653894244L;

	public static final int EXPIRATION = 60 * 24;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "identifier", nullable = false, unique = true)
	private String identifier;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "verification_token", unique = true)
	private String verificationToken;

	@Column(name = "exp_date")
	private Date expDate;

	public void updateToken(final String token) {
		this.verificationToken = token;
		this.expDate = GeneralUtils.calculateExpiryDate(EXPIRATION);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expDate == null) ? 0 : expDate.hashCode());
		result = prime * result + ((verificationToken == null) ? 0 : verificationToken.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractVerificationToken other = (AbstractVerificationToken) obj;
		if (expDate == null) {
			if (other.expDate != null) {
				return false;
			}
		} else if (!expDate.equals(other.expDate)) {
			return false;
		}
		if (verificationToken == null) {
			if (other.verificationToken != null) {
				return false;
			}
		} else if (!verificationToken.equals(other.verificationToken)) {
			return false;
		}
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Token [String=" + verificationToken + "]" + "[Expires" + expDate + "]";
	}
}