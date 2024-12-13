package pl.sycamore.filetransformer.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFileHandler {
    private final Path filePath;

    public AbstractFileHandler(String filePath) {
        this.filePath = Paths.get(filePath);

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

        if (isDirectoryNotExists()) {
            var directory = filePath.getParent().toFile();
            System.out.println("Creating directory " + directory);
            directory.mkdirs();
        }

        System.out.println("Generating: " + filePath);

        write(fileContent);
    }

    public boolean isFileExists() {
        return Files.exists(filePath);
    }

    public void write(String fileContent) throws IOException {
        Files.write(filePath, fileContent.getBytes(), StandardOpenOption.CREATE);
    }

    private boolean isDirectoryNotExists() {
        return !Files.exists(filePath.getParent());
    }
}
