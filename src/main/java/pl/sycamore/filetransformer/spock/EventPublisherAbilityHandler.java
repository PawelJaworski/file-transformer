package pl.sycamore.filetransformer.spock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class EventPublisherAbilityHandler {
    private final Path filePath;

    public EventPublisherAbilityHandler(String projectPath, String packageName) {
        this.filePath = Paths.get(projectPath
                + "/src/test/java/"
                + JavaGeneratorNamespace.relativePathFromPackage(packageName)
                + "/application/event/EventPublisherAbility.java");

    }

    public List<String> text() throws IOException {
        return Files.lines(filePath)
                .collect(Collectors.toList());
    }

    public void write(String fileContent) throws IOException {
        Files.write(filePath, fileContent.getBytes());
    }
}
