package club.ryans.controllers;

import club.ryans.parser.Log;
import club.ryans.parser.LogAnalyzer;
import club.ryans.parser.LogParser;
import club.ryans.parser.ParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ApiController {
    private final LogParser parser = new LogParser();

    @Autowired
    private final LogAnalyzer analyzer;

    @PostMapping("/submit_log")
    public ResponseEntity submitLog(@RequestParam("file") final MultipartFile logFile) {
        try {
            ParseResult parseResult = parser.parse(logFile.getInputStream());
            Log log = analyzer.analyze(parseResult);

            return ResponseEntity.ok(log);
        } catch (Exception e)  {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to build");
        }
    }
}