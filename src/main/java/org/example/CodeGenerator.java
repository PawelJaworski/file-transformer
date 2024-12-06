package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pl.sycamore.filetransformer.spock.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.Functor.get;

public class CodeGenerator {
    private static final String PATH = "/Users/paweljaworski/projects/github/melanz-trans";
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
        generatesEventPublisherAbility(specText);

        TextFileIntoSpockSpecification.transform(PACKAGE_NAME, Paths.get(inputFilePath))
                .ifPresent(it -> templateData.put("spec", it));

        var generatedDirectory = PATH + "/src/test/groovy/" + PACKAGE_NAME.replaceAll("\\.", "/") + "/";
        Files.createDirectories(Paths.get(generatedDirectory));
        File outputFile = new File(generatedDirectory + templateData.get("spec").className() + ".groovy");
        try (Writer writer = new FileWriter(outputFile)) {
            template.process(templateData, writer);
        }

        System.out.println("Spock test generated at: " + outputFile.getAbsolutePath());
    }

    private static void generatesEventPublisherAbility(List<String> specText) throws IOException {
        var abilityFilePath = Paths.get(PATH
                + "/src/test/java/"
                + JavaGeneratorNamespace.relativePathFromPackage(PACKAGE_NAME)
                + "/application/event/EventPublisherAbility.java");
        var abilityText = Files.lines(abilityFilePath)
                .collect(Collectors.toList());
        var checkIt = get(MiroTextNamespace.extractText(specText, "given", "when"))
                .then(it -> MiroTextNamespace.findByTag(it, "event"))
                .value().stream()
                .reduce(abilityText, TestAbilityNamespace::addEventPublishingIfNotExists, (f, s) -> s);
        System.out.println(String.join("\n", checkIt));
        Files.write(abilityFilePath, abilityText);
    }
}

