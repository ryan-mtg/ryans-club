package club.ryans.models.generators;

import club.ryans.utility.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Slf4j
public class DataFileManager {
    private static final String DATA_RESOURCE_DIRECTORY = "data";

    private boolean generate;
    private File dataFileDirectory;

    public DataFileManager(final boolean generate, final File dataFileDirectory) {
        this.generate = generate;
        this.dataFileDirectory = dataFileDirectory;

        if (generate) {
            if(!dataFileDirectory.exists()) {
                dataFileDirectory.mkdir();
            }
        }
    }

    public InputStream getStream(final String fileName) {
        if (!generate) {
            return DataFileManager.class.getResourceAsStream(
                    String.format("/%s/%s", DATA_RESOURCE_DIRECTORY, fileName));
        }

        try {
            return new FileInputStream(getFile(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public File getFile(final String fileName) {
        return new File(dataFileDirectory, fileName);
    }

    public void writeFile(final String fileName, final Object contents) {
        try {
            ObjectMapper mapper = Json.createObjectMapper();
            File file = getFile(fileName);
            LOGGER.info("writing file to: {}", file.getAbsolutePath());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, contents);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}