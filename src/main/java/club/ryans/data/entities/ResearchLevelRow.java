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
@Table(name = "research_level")
@IdClass(ResearchLevelRow.ResearchLevelId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchLevelRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "research_id")
    private long researchId;

    @NotNull
    private int level;

    @Data @NoArgsConstructor
    public static class ResearchLevelId implements Serializable {
        private int accountId;
        private long researchId;
    }
}