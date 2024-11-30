package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class CodeGenerator {
    public static void main(String[] args) throws Exception {
        // Input file (describing use cases)
        String inputFilePath = "src/main/resources/usecases.txt";

        // Read the use case descriptions from the text file
        List<UseCase> useCases = Files.lines(Paths.get(inputFilePath))
                .map(UseCase::new)
                .collect(Collectors.toList());

        // Initialize Freemarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");

        // Load the template
        Template template = cfg.getTemplate("spockTestTemplate.ftl");

        // Generate output file
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("useCases", useCases);

        Files.createDirectories(Paths.get("generated"));
        File outputFile = new File("generated/SpockTest.groovy");
        try (Writer writer = new FileWriter(outputFile)) {
            template.process(templateData, writer);
        }

        System.out.println("Spock test generated at: " + outputFile.getAbsolutePath());
    }
}

