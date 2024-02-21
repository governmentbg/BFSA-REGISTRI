package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role implements Serializable {

	@Serial
	private static final long serialVersionUID = 8066915823866616778L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", unique = true)
	private String name;

	@Builder.Default
	@ManyToMany(mappedBy = "roles") // bidirectional many-to-many association to User
	private List<User> users = new ArrayList<>();

	@Builder.Default
	@ManyToMany(mappedBy = "roles") // bidirectional many-to-many association to Contractor
	private List<Contractor> contractors = new ArrayList<>();
}
