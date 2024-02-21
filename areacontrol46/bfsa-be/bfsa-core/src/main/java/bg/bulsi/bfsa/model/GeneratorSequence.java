package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "generator_sequence")
public class GeneratorSequence {

    public static final String SEQUENCE_NAME = "gen_sequence";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_sequence")
    @SequenceGenerator(name = "generator_sequence", sequenceName = SEQUENCE_NAME, allocationSize = 1)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;
}
