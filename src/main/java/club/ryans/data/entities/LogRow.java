package club.ryans.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 1, max = Constraints.TAG_SIZE)
    private String tag;

    @NotNull
    private int version;

    @NotNull
    private int flags;

    @NotNull
    @Size(min = 1, max = Constraints.HASH_SIZE)
    private String hash;

    @NotNull
    @Column(name = "ip_address")
    private int ipAddress;

    @NotNull
    @Column(name = "file_name")
    @Size(min = 1, max = Constraints.FILE_NAME_SIZE)
    private String fileName;

    @NotNull
    @Column(name = "log_time")
    private long logTime;

    @NotNull
    @Column(name = "submission_time")
    private long submissionTime;

    @NotNull
    private String data;
}
