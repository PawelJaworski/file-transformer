package org.example;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import pl.sycamore.filetransformer.code.TestJavaNamespace;
import pl.sycamore.filetransformer.spock.EventPublisherAbilityHandler;
import pl.sycamore.filetransformer.spock.MiroTextNamespace;
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

import static org.example.Functor.get;
import static org.example.GeneratorConfig.*;

public class TestGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        var specText = Files.lines(Paths.get(INPUT_FILE_PATH))
                .toList();
        generateEventPublisherAbility(specText);
        generateSpockSpec(specText);
    }

    private static void generateEventPublisherAbility(List<String> specText) throws IOException {
        var handler = new EventPublisherAbilityHandler(PROJECT_PATH, PACKAGE_NAME);
        var abilityText = handler.text();
        var eventPublishing = get(MiroTextNamespace.extractText(specText, "given", "when"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .map(TestGenerator::eventName)
                .reduce(abilityText, TestJavaNamespace::addEventPublishingIfNotExists, (f, s) -> s);

        handler.write(String.join("\n", eventPublishing));

        var eventAssertions = get(MiroTextNamespace.extractText(specText, "then"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .map(TestGenerator::eventName)
                .reduce(abilityText, TestJavaNamespace::addEventOccurredAssertionIfNotExists, (f, s) -> s);
        handler.write(String.join("\n", eventAssertions));
    }

    private static String eventName(String eventText) {
        return eventText.replaceAll(StringUtils.trimToEmpty(StringUtils.substringBetween(eventText, "{", "}")), "")
                .replace("{", "")
                .replace("}", "");
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
