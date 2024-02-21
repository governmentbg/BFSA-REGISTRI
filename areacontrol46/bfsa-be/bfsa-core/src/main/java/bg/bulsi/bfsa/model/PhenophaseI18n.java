//package bg.bulsi.bfsa.model;
//
//import jakarta.persistence.Basic;
//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//import jakarta.persistence.EmbeddedId;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.MapsId;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.experimental.SuperBuilder;
//import org.hibernate.envers.Audited;
//import org.springframework.util.StringUtils;
//
//import java.io.Serial;
//import java.io.Serializable;
//
//@Entity
//@Getter
//@Setter
//@Audited
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "phenophases_i18n")
//public class PhenophaseI18n extends AuditableEntity {
//
//    @Serial
//    private static final long serialVersionUID = 4823436757559820637L;
//
//    @EmbeddedId
//    private PhenophaseI18nIdentity phenophaseI18nIdentity;
//
//    @NotNull
//    @Column(name = "name", length = 5000, nullable = false)
//    private String name;
//
//    @Column(name = "description", length = 5000)
//    private String description;
//
//    @MapsId("code")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "code")
//    private Phenophase phenophase;
//
//    public PhenophaseI18n(final String name,
//                          final String description,
//                          final Phenophase phenophase,
//                          final Language language) {
//
//        this.phenophaseI18nIdentity = new PhenophaseI18nIdentity(phenophase.getCode(), language.getLanguageId());
//        this.name = StringUtils.hasText(name) ? name : "";
//        this.description = description;
//        this.phenophase = phenophase;
//    }
//
//    public PhenophaseI18n(final Phenophase phenophase, final Language language) {
//        this.phenophaseI18nIdentity = new PhenophaseI18nIdentity(phenophase.getCode(), language.getLanguageId());
//        this.phenophase = phenophase;
//    }
//
//    @Getter
//    @Setter
//    @Embeddable
//    @EqualsAndHashCode
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class PhenophaseI18nIdentity implements Serializable {
//
//        @Serial
//        private static final long serialVersionUID = 7659642703687711785L;
//
//        @Column(name = "code")
//        private String code;
//
//        @NotNull
//        @Basic(optional = false)
//        @Column(name = "language_id")
//        private String languageId;
//    }
//}
