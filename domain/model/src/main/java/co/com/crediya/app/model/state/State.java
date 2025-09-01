package co.com.crediya.app.model.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Long stateId;
    private String name;
    private String description;
}