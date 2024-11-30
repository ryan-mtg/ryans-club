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
@Table(name = "resource_amount")
@IdClass(ResourceAmountRow.ResourceAmountId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAmountRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "resource_id")
    private long resourceId;

    @NotNull
    private long amount;

    @Data @NoArgsConstructor
    public static class ResourceAmountId implements Serializable {
        private int accountId;
        private long resourceId;
    }
}