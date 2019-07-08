package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Constraint {

    private ConstraintType type;

    private Integer xid;
    private Integer yid;

    private Integer k;

    private Integer weight = 1;
    private Integer constraintId;
    private boolean diagnoseable;

    private Integer capacity;
}
