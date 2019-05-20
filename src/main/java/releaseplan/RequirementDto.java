package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class RequirementDto {
    private Integer id;
    private int effort;
    private int priority = 1;

    public RequirementDto(Integer id)
    {
        this.id = id;
    }

}
