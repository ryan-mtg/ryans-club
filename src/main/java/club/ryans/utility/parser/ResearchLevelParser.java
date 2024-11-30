package club.ryans.utility.parser;

import club.ryans.models.Research;
import club.ryans.models.managers.ResearchManager;
import club.ryans.models.player.ResearchLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ResearchLevelParser {
    private static Pattern DELIMITER = Pattern.compile("\\s*(,|\\})\\s*");

    private final ResearchManager researchManager;

    public ResearchLevel parseValue(final String text) {
        if (!text.startsWith("{")) {
            Parse.throwError("expecting { at the start of research level: %s", text);
        }

        if (!text.endsWith("}")) {
            Parse.throwError("expecting } at the end of research level: %s", text);
        }

        Scanner scanner = new Scanner(text);

        scanner.findInLine("\\{");

        scanner.useDelimiter(DELIMITER);

        long researchId = scanner.nextLong();
        Research research = researchManager.getResearch(researchId);
        if (research == null && researchId == -1) {
            Parse.throwError("Unknown research: %d", researchId);
        }

        int level = scanner.nextInt();
        return new ResearchLevel(research, level);
    }
}
