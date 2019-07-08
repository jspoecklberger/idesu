package csp;

import lombok.Data;
import releaseplan.Constraint;

//Maps dtos to an applied choco constraint
@Data
public class ConstraintMapping {

    public org.chocosolver.solver.constraints.Constraint c;
    public Constraint dto;

    public int getWeight()
    {
        return dto.getWeight();
    }
}
