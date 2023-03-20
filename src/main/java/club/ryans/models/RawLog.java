package club.ryans.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class RawLog {
    private int id;
    private String tag;
    private String hash;
    private int version;
    private int flags;
    private Integer userId;
    private int ipAddress;
    private String fileName;
    private Instant logTime;
    private Instant submissionTime;
    private String data;
}