package org.example;

import org.apache.commons.lang3.StringUtils;
import pl.sycamore.filetransformer.code.CommandFileHandler;
import pl.sycamore.filetransformer.code.EventFileHandler;
import pl.sycamore.filetransformer.code.TestJavaNamespace;
import pl.sycamore.filetransformer.spock.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static org.example.Functor.get;
import static org.example.GeneratorConfig.*;
import static pl.sycamore.filetransformer.code.MainJavaNamespace.recordClass;
import static pl.sycamore.filetransformer.spock.JavaGeneratorNamespace.className;

public class CodeGenerator {
    private static String USE_CASE_TEMPLATE_PATH = "src/main/resources/useCase.txt";

    public static void main(String[] args) throws Exception {
        var useCaseText = Files.lines(Paths.get(USE_CASE_TEMPLATE_PATH))
                .toList();
        generateEvents(useCaseText);
        generateCommands(useCaseText);
        generateEventPublisherAbility(useCaseText);
    }

    private static void generateEvents(List<String> specText) throws IOException {
        var events = Stream.concat(
                get(MiroTextNamespace.extractText(specText, "given", "when"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream(),
                get(MiroTextNamespace.extractText(specText, "then"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream()
        ).map(MiroTextNamespace::removeJson)
                .distinct()
                .toList();
        for (var eventTxt : events) {
            var className = className(MiroTextNamespace.removeJson(eventTxt));
            var eventJavaFile = new EventFileHandler(PROJECT_PATH, PACKAGE_NAME, className);
            var eventJavaCode = recordClass(className, PACKAGE_NAME + ".application.event");
            eventJavaFile.writeNewFileElseLogAlreadyExists(eventJavaCode);
        }
    }

    private static void generateCommands(List<String> useCaseText) throws IOException {

        for (var cmdTxt : MiroTextNamespace.findByTag(useCaseText, "command")) {
            var className =  className( MiroTextNamespace.removeJson(cmdTxt) ) + "Cmd";
            var eventJavaFile = new CommandFileHandler(PROJECT_PATH, PACKAGE_NAME, className);
            var eventJavaCode = recordClass( className, PACKAGE_NAME + ".application.cmd");
            eventJavaFile.writeNewFileElseLogAlreadyExists(eventJavaCode);
        };
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

