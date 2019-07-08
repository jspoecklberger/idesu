package releaseplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {
    private Integer id;
    private List<Integer> requirements;
    private Integer capacity;
}
