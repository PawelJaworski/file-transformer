package org.example;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import pl.sycamore.filetransformer.spock.SpockSpecification;
import pl.sycamore.filetransformer.spock.TextFileIntoSpockSpecification;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.GeneratorConfig.*;

public class TestGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        var specText = Files.lines(Paths.get(INPUT_FILE_PATH))
                .toList();
        generateSpockSpec(specText);
    }

    private static void generateSpockSpec(List<String> specText) throws IOException, TemplateException {
        Map<String, SpockSpecification> templateData = new HashMap<>();
        TextFileIntoSpockSpecification.transform(PACKAGE_NAME, specText)
                .ifPresent(it -> templateData.put("spec", it));

        var generatedDirectory = PROJECT_PATH + "/src/test/groovy/" + PACKAGE_NAME.replaceAll("\\.", "/") + "/";
        Files.createDirectories(Paths.get(generatedDirectory));
        File outputFile = new File(generatedDirectory + templateData.get("spec").className() + ".groovy");
        Template template = FREEMARKER_CONFIG.getTemplate("spockSpecification.ftl");
        try (Writer writer = new FileWriter(outputFile)) {
            template.process(templateData, writer);
        }

        System.out.println("Spock test generated at: " + outputFile.getAbsolutePath());
    }
}
