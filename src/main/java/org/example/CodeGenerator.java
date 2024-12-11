package org.example;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import pl.sycamore.filetransformer.code.CommandModel;
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

public class CodeGenerator {
    private static String USE_CASE_TEMPLATE_PATH = "src/main/resources/useCase.txt";

    public static void main(String[] args) throws Exception {
        var useCaseText = Files.lines(Paths.get(USE_CASE_TEMPLATE_PATH))
                .toList();
        generateEvents(useCaseText);
        generateCommands(useCaseText);
//        generateEventPublisherAbility(useCaseText);
    }

    private static void generateEvents(List<String> specText) {
        Stream.concat(
                get(MiroTextNamespace.extractText(specText, "given", "when"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream(),
                get(MiroTextNamespace.extractText(specText, "then"))
                        .then(it -> MiroTextNamespace.findByTag(it, "event"))
                        .value().stream()
        ).map(MiroTextNamespace::removeJson)
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
                });
    }

    private static void generateCommands(List<String> useCaseText) throws IOException, TemplateException {

        for (var cmdTxt : MiroTextNamespace.findByTag(useCaseText, "command")) {

                    var packageName = PACKAGE_NAME + ".application.cmd";
                    var className = className( MiroTextNamespace.removeJson(cmdTxt) ) + "Cmd";

                    var cmd = new CommandModel(packageName, className);

                    var templateData = Map.of("command", cmd);
                    var generatedDirectory = PROJECT_PATH
                            + "/src/main/java/"
                            + PACKAGE_NAME.replaceAll("\\.", "/") +
                            "/application/cmd/";
                    Files.createDirectories(Paths.get(generatedDirectory));
                    File outputFile = new File(generatedDirectory + cmd.className() + ".java");
                    Template template = FREEMARKER_CONFIG.getTemplate("command.ftl");
                    try (Writer writer = new FileWriter(outputFile)) {
                        template.process(templateData, writer);
                    }
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

