package bg.bulsi.bfsa.bootstrap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject implements Serializable {
    @Serial
    private static final long serialVersionUID = -4813263959323155047L;

    private String errorLine;
    private String errorMessage;

    @Override
    public String toString() {
        return errorLine + ": " + errorMessage;
    }
}
