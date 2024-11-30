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
@Table(name = "officer_level")
@IdClass(OfficerLevelRow.OfficerLevelId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficerLevelRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "officer_id")
    private long officerId;

    @NotNull
    @Column(name = "officer_rank")
    private int rank;

    @NotNull
    private int level;

    @Data @NoArgsConstructor
    public static class OfficerLevelId implements Serializable {
        private int accountId;
        private long officerId;
    }
}