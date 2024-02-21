package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serial;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "languages")
public class Language extends AuditableEntity {

    @Serial
    private static final long serialVersionUID = -8218497949968953851L;

    @Transient
    public static final String BG = "bg";
    @Transient
    public static final String EN = "en";

    @Id
    @Column(name = "language_id", length = 5)
    private String languageId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "locale", length = 100)
    private String locale;

    @Column(name = "description", length = 5000)
    private String description;

    @Column(name = "main")
    private Boolean main = false;

    @Column(name = "enabled")
    private Boolean enabled;
}
