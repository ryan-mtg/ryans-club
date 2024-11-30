package club.ryans.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "building_level")
@IdClass(BuildingLevelRow.BuildingLevelId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingLevelRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "building_id")
    private long buildingId;

    @NotNull
    private int level;

    @Data @NoArgsConstructor
    public static class BuildingLevelId implements Serializable {
        private int accountId;
        private long buildingId;
    }
}