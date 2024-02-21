//package bg.bulsi.bfsa.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.experimental.SuperBuilder;
//import org.hibernate.envers.Audited;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Entity
//@Getter
//@Setter
//@Audited
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "phenophases")
//public class Phenophase extends AuditableEntity {
//
//    @Id
//    @Column(name = "code", length = 30)
//    private String code;
//
//    @Column(name = "external_code", length = 100)
//    private String externalCode;
//
//    @Column(name = "enabled")
//    private Boolean enabled;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_code")
//    private Phenophase parent;
//
//    @Column(name = "symbol", length = 20000)
//    private String symbol;
//
//    @Builder.Default
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//    private final List<Phenophase> subPhenophases = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "phenophase", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    private Set<PhenophaseI18n> i18ns = new HashSet<>();
//
//
//}
