package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import pl.sycamore.filetransformer.code.EventFileHandler;
import pl.sycamore.filetransformer.code.TestJavaNamespace;
import pl.sycamore.filetransformer.spock.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static org.example.Functor.get;
import static org.example.GeneratorConfig.*;
import static pl.sycamore.filetransformer.code.MainJavaNamespace.eventJavaCode;
import static pl.sycamore.filetransformer.spock.JavaGeneratorNamespace.className;
import static pl.sycamore.string.StringNamespace.splitListByText;

public class CodeGenerator {

    public static void main(String[] args) throws Exception {
        var specText = Files.lines(Paths.get(INPUT_FILE_PATH))
                .toList();
        for (var it : splitListByText(specText, "Spec: ")) {
            generateEvents(it);
            generateEventPublisherAbility(it);
        };
    }

    private static void generateEvents(List<String> specText) {
        Stream.concat(
                get(MiroTextNamespace.extractText(specText, "given", "when"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream(),
                get(MiroTextNamespace.extractText(specText, "then"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream()
        ).map(it -> it.replaceAll(StringUtils.trimToEmpty(StringUtils.substringBetween(it, "{", "}")), "")
                        .replace("{", "")
                        .replace("}", ""))
                .distinct()
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
                .map(CodeGenerator::eventName)
                .reduce(abilityText, TestJavaNamespace::addEventPublishingIfNotExists, (f, s) -> s);

        handler.write(String.join("\n", eventPublishing));

        var eventAssertions = get(MiroTextNamespace.extractText(specText, "then"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .map(CodeGenerator::eventName)
                .reduce(abilityText, TestJavaNamespace::addEventOccurredAssertionIfNotExists, (f, s) -> s);
        handler.write(String.join("\n", eventAssertions));
    }

    private static String eventName(String eventText) {
        return eventText.replaceAll(StringUtils.trimToEmpty(StringUtils.substringBetween(eventText, "{", "}")), "")
                .replace("{", "")
                .replace("}", "");
    }
}

