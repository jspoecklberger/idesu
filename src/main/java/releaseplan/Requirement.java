package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Requirement {
    private Integer id;
    private int effort;
    private int priority = 1;

    public Requirement(Integer id)
    {
        this.id = id;
    }

}
