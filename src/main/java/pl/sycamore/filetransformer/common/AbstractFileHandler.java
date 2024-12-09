package pl.sycamore.filetransformer.common;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFileHandler {
    private final Path filePath;

    public AbstractFileHandler(String filePath) {
        this.filePath = Paths.get(filePath);

    }

    public boolean isFileExists() {
        return Files.exists(filePath);
    }

    public List<String> text() throws IOException {
        return Files.lines(filePath)
                .collect(Collectors.toList());
    }

    public void writeNewFileElseLogAlreadyExists(String fileContent) throws IOException {
        if (isFileExists()) {
            System.out.println(filePath + " already exists. Ignoring.");
            return;
        }
        System.out.println("Generating: " + filePath);

        write(fileContent);
    }

    public void write(String fileContent) throws IOException {
        Files.write(filePath, fileContent.getBytes(), StandardOpenOption.CREATE);
    }

    public Path path() {
        return filePath;
    }
}
