package club.ryans.data.serializers;

import club.ryans.data.entities.LogRow;
import club.ryans.data.repositories.LogRepository;
import club.ryans.models.RawLog;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class LogSerializer {
    private LogRepository logRepository;

    public int countLogs() {
        return (int) logRepository.count();
    }

    public boolean hasTag(final String tag) {
        return logRepository.existsByTag(tag);
    }

    public boolean hasHash(final String hash) {
        return logRepository.existsByHash(hash);
    }

    public RawLog lookupByTag(final String tag) {
        return createRawLog(logRepository.findByTag(tag));
    }

    public RawLog lookupByHash(final String hash) {
        return createRawLog(logRepository.findByHash(hash));
    }

    public RawLog createLog(final String tag, final String hash, final int version, final int flags,
            final int ipAddress, final String fileName, final Instant logTime, final Instant submissionTime,
            final String data) {
        LogRow logRow = new LogRow();
        logRow.setTag(tag);
        logRow.setFlags(flags);
        logRow.setFlags(version);
        logRow.setHash(hash);
        logRow.setIpAddress(ipAddress);
        logRow.setFileName(fileName);
        logRow.setLogTime(logTime.getEpochSecond());
        logRow.setSubmissionTime(submissionTime.getEpochSecond());
        logRow.setData(data);

        logRepository.save(logRow);
        return createRawLog(logRow);
    }

    private RawLog createRawLog(final LogRow logRow) {
        if (logRow == null) {
            return null;
        }

        Instant logTime = Instant.ofEpochSecond(logRow.getLogTime());
        Instant submissionTime = Instant.ofEpochSecond(logRow.getSubmissionTime());

        return new RawLog(logRow.getId(), logRow.getTag(), logRow.getHash(), logRow.getVersion(), logRow.getFlags(),
                logRow.getIpAddress(), logRow.getFileName(), logTime, submissionTime, logRow.getData());
    }
}