package club.ryans.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "daily_configuration")
@IdClass(DailyConfigurationRow.DailyConfigurationId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyConfigurationRow {
    @Id
    @Column(name = "account_id")
    private int accountId;

    @Id
    @Column(name = "daily_id")
    private int dailyId;

    private int flags;
    private int chests;
    private int goal;

    @Data @NoArgsConstructor
    public static class DailyConfigurationId implements Serializable {
        private int accountId;
        private int dailyId;
    }
}