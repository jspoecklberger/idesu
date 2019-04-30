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

    public RequirementDto(Integer id)
    {
        this.id = id;
    }

}
