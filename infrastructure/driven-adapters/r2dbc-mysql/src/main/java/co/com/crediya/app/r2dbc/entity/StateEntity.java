package co.com.crediya.app.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateEntity {

    @Id
    @Column("state_id")
    private Long stateId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;
}