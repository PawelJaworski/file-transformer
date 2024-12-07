package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pl.sycamore.filetransformer.code.EventFileHandler;
import pl.sycamore.filetransformer.code.TestJavaNamespace;
import pl.sycamore.filetransformer.spock.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static org.example.Functor.get;
import static pl.sycamore.filetransformer.code.MainJavaNamespace.eventJavaCode;
import static pl.sycamore.filetransformer.spock.JavaGeneratorNamespace.className;

public class CodeGenerator {
    private static final String PROJECT_PATH = "/Users/paweljaworski/projects/github/melanz-trans";
    private static final String PACKAGE_NAME = "com.example.melanz_trans";

    public static void main(String[] args) throws Exception {
        String inputFilePath = "src/main/resources/spockSpecification.txt";

        // Initialize Freemarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");

        // Load the template
        Template template = cfg.getTemplate("spockSpecification.ftl");

        // Generate output file
        Map<String, SpockSpecification> templateData = new HashMap<>();

        var specText = Files.lines(Paths.get(inputFilePath))
                .toList();
        generateEvents(specText);
        generateEventPublisherAbility(specText);

        TextFileIntoSpockSpecification.transform(PACKAGE_NAME, Paths.get(inputFilePath))
                .ifPresent(it -> templateData.put("spec", it));

        var generatedDirectory = PROJECT_PATH + "/src/test/groovy/" + PACKAGE_NAME.replaceAll("\\.", "/") + "/";
        Files.createDirectories(Paths.get(generatedDirectory));
        File outputFile = new File(generatedDirectory + templateData.get("spec").className() + ".groovy");
        try (Writer writer = new FileWriter(outputFile)) {
            template.process(templateData, writer);
        }

        System.out.println("Spock test generated at: " + outputFile.getAbsolutePath());
    }

    private static void generateEvents(List<String> specText) {
        Stream.concat(
                get(MiroTextNamespace.extractText(specText, "given", "when"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream(),
                get(MiroTextNamespace.extractText(specText, "then"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream()
        ).distinct()
                .forEach(it -> {

                    var javaFileName =  className(it);
                    var eventJavaFile = new EventFileHandler(PROJECT_PATH, PACKAGE_NAME, javaFileName);
                    if (eventJavaFile.isFileExists()) {
                        System.out.println(eventJavaFile.path() + " already exists. Ignoring.");
                        return;
                    }

                    System.out.println("Generating: " + eventJavaFile.path());
                    var eventJavaCode = eventJavaCode(it, PACKAGE_NAME + ".application.event");
                    try {
                        eventJavaFile.write(eventJavaCode);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                    System.out.println(eventJavaCode);
                });

//        eventJavaFile.write(checkIt);
//        var path = Paths.get("/Users/paweljaworski/projects/github/melanz-trans/src/main/java/com/example/melanz_trans/application/event.java" );
//        Files.write(path, checkIt.getBytes());

    }

    private static void generateEventPublisherAbility(List<String> specText) throws IOException {
        var handler = new EventPublisherAbilityHandler(PROJECT_PATH, PACKAGE_NAME);
        var abilityText = handler.text();
        var checkIt = get(MiroTextNamespace.extractText(specText, "given", "when"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .reduce(abilityText, TestJavaNamespace::addEventPublishingIfNotExists, (f, s) -> s);
//        System.out.println(String.join("\n", checkIt));
        handler.write(String.join("\n", checkIt));
    }
}

