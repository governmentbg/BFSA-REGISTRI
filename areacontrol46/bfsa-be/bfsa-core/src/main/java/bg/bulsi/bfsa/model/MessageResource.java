package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_resources")
public class MessageResource extends AuditableEntity {

	@Serial
	private static final long serialVersionUID = -7059345042794405914L;

	@EmbeddedId
	private MessageResourceIdentity messageResourceIdentity;

	@Column(name = "message", length = 5000)
	private String message;

	public MessageResource(final String languageId, final String code, final String message) {
		this.messageResourceIdentity = new MessageResourceIdentity(code, languageId);
		this.message = message;
	}

	@Getter
	@Setter
	@Builder
	@Embeddable
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MessageResourceIdentity implements Serializable {

		@Serial
		private static final long serialVersionUID = 2386467961425779768L;

		@Basic(optional = false)
		@Column(name = "code", length = 300)
		private String code;

		@Basic(optional = false)
		@Column(name = "language_id", length = 5)
		private String languageId;

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof NomenclatureI18nIdentity)) {
//            return false;
//        }
//        MessageResourceIdentity that = (MessageResourceIdentity) obj;
//        EqualsBuilder eb = new EqualsBuilder();
//        eb.append(this.code, that.code);
//        eb.append(this.languageId, that.languageId);
//        return eb.isEquals();
//    }
//
//    @Override
//    public int hashCode() {
//        HashCodeBuilder hcb = new HashCodeBuilder();
//        hcb.append(code);
//        hcb.append(languageId);
//        // System.out.println("HashCode: [" + hcb.toHashCode() + "]");
//        return hcb.toHashCode();
//    }
	}
}
