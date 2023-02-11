package club.ryans.controllers;

import club.ryans.models.RawLog;
import club.ryans.models.managers.LogManager;
import club.ryans.parser.Log;
import club.ryans.parser.LogAnalyzer;
import club.ryans.parser.LogParser;
import club.ryans.parser.ParseResult;
import club.ryans.utility.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ApiController {
    Pattern IP_ADDRESS_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");

    private final LogParser parser = new LogParser();

    @Autowired
    private final LogManager logManager;

    @Autowired
    private final LogAnalyzer analyzer;

    @PostMapping("/submit_log")
    public ResponseEntity submitLog(final HttpServletRequest request,
                @RequestParam("file") final MultipartFile logFile) {
        try {
            ParseResult parseResult = parser.parse(logFile.getInputStream());
            String fileName = logFile.getOriginalFilename();
            Log log = analyzer.analyze(parseResult, fileName, null);
            int ipAddress = convertIpAddress(request.getRemoteAddr());
            ObjectMapper mapper = Json.createObjectMapper();
            String data = mapper.writeValueAsString(parseResult);
            RawLog rawLog = logManager.submitLog(ipAddress, fileName, data, log.getTime());
            log.setTag(rawLog.getTag());

            return ResponseEntity.ok(log);
        } catch (Exception e)  {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to build");
        }
    }

    @GetMapping("/log")
    public ResponseEntity log(@RequestParam final String tag) {
        try {
            RawLog rawLog = logManager.getLogFromTag(tag);

            ObjectMapper mapper = Json.createObjectMapper();
            ParseResult parseResult = mapper.readValue(rawLog.getData(), ParseResult.class);
            Log log = analyzer.analyze(parseResult, rawLog.getFileName(), rawLog.getTag());

            return ResponseEntity.ok(log);
        } catch (Exception e)  {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to load");
        }
    }


    private int convertIpAddress(final String ipString) {
        Matcher matcher = IP_ADDRESS_PATTERN.matcher(ipString);
        if (!matcher.matches()) {
            throw new RuntimeException("unknown address pattern");
        }

        int a = checkIpAddressSegment(matcher.group(1));
        int b = checkIpAddressSegment(matcher.group(2));
        int c = checkIpAddressSegment(matcher.group(3));
        int d = checkIpAddressSegment(matcher.group(4));

        return (((((a << 8) + b) << 8) + c) << 8) + d;
    }

    private int checkIpAddressSegment(final String segment) {
        int value = Integer.parseInt(segment);
        if (value >= 256) {
            throw new RuntimeException("unknown address segment");
        }
        return value;
    }
}