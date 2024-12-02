package org.example;

import freemarker.template.Configuration;
import freemarker.template.Template;
import pl.sycamore.filetransformer.spock.TextFileIntoSpockSpecification;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CodeGenerator {
    private static final String PACKAGE_NAME = "com.example.shipment";

    public static void main(String[] args) throws Exception {
        String inputFilePath = "src/main/resources/spockSpecification.txt";

        // Initialize Freemarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");

        // Load the template
        Template template = cfg.getTemplate("spockSpecification.ftl");

        // Generate output file
        Map<String, Object> templateData = new HashMap<>();


        TextFileIntoSpockSpecification.transform(PACKAGE_NAME, Paths.get(inputFilePath))
                .ifPresent(it -> templateData.put("spec", it));

        var generatedDirectory = "generated/" + PACKAGE_NAME.replaceAll("\\.", "/");
        Files.createDirectories(Paths.get(generatedDirectory));
        File outputFile = new File(generatedDirectory + "/SpockTest.groovy");
        try (Writer writer = new FileWriter(outputFile)) {
            template.process(templateData, writer);
        }

        System.out.println("Spock test generated at: " + outputFile.getAbsolutePath());
    }
}

