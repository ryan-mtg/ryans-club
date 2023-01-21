package club.ryans.models.managers;

import club.ryans.data.serializers.LogSerializer;
import club.ryans.models.RawLog;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Component
public class LogManager {
    private static final int CURRENT_VERSION = 1;
    private static final int DEFAULT_FLAGS = 0;

    private final LogSerializer logSerializer;
    private final TagManager tagManager;

    public LogManager(final LogSerializer logSerializer) {
        this.logSerializer = logSerializer;
        this.tagManager = new TagManager(logSerializer::hasTag);
    }

    public int getLogCount() {
        return logSerializer.countLogs();
    }

    public RawLog submitLog(final int ipAddress, final String fileName, final String data, final Instant logTime) {
        String hash = computeHash(data);

        if(logSerializer.hasHash(hash)) {
            return logSerializer.lookupByHash(hash);
        }

        String tag = tagManager.createTag();
        Instant submissionTime = Instant.now();
        return logSerializer.createLog(tag, hash, CURRENT_VERSION, DEFAULT_FLAGS, ipAddress, fileName, logTime,
                submissionTime, data);
    }

    public RawLog getLogFromTag(final String tag) {
        return logSerializer.lookupByTag(tag);
    }

    private String computeHash(final String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes(StandardCharsets.UTF_8.name()));
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Internal Server Error");
        }
    }
}