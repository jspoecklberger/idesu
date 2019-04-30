package csp;

import lombok.Data;
import org.chocosolver.solver.constraints.Constraint;
import releaseplan.ConstraintDto;

import java.util.ArrayList;
import java.util.List;

//Maps dtos to an applied choco constraint
@Data
public class ConstraintMapping {

    public Constraint c;
    public ConstraintDto dto;

    public int getWeight()
    {
        return dto.getWeight();
    }
}
