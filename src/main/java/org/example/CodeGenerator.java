package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
import static pl.sycamore.string.StringNamespace.splitListByText;

public class CodeGenerator {
    private static final String PROJECT_PATH = "/Users/paweljaworski/projects/github/melanz-trans";
    private static final String PACKAGE_NAME = "com.example.melanz_trans";
    private static final String INPUT_FILE_PATH = "src/main/resources/spockSpecification.txt";
    private static final Configuration FREEMARKER_CONFIG = new Configuration(Configuration.VERSION_2_3_31);

    static {
        try {
            FREEMARKER_CONFIG.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FREEMARKER_CONFIG.setDefaultEncoding("UTF-8");
    }

    public static void main(String[] args) throws Exception {
        var specText = Files.lines(Paths.get(INPUT_FILE_PATH))
                .toList();
        for (var it : splitListByText(specText, "Spec: ")) {
            generateEvents(it);
            generateEventPublisherAbility(it);
        };

        generateSpockSpec(specText);

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
        var eventPublishing = get(MiroTextNamespace.extractText(specText, "given", "when"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .reduce(abilityText, TestJavaNamespace::addEventPublishingIfNotExists, (f, s) -> s);
//        System.out.println(String.join("\n", checkIt));
        handler.write(String.join("\n", eventPublishing));

        var eventAssertions = get(MiroTextNamespace.extractText(specText, "then"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .reduce(abilityText, TestJavaNamespace::addEventOccurredAssertionIfNotExists, (f, s) -> s);
//        System.out.println(String.join("\n", checkIt));
        handler.write(String.join("\n", eventAssertions));
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

