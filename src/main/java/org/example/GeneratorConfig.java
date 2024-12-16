package org.example;

import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;

public final class GeneratorConfig {
    public static final String USE_CASE = "propose date of event";
    public static final String PROJECT_PATH = "/Users/paweljaworski/projects/github/ralt-lab";
    public static final String PACKAGE_NAME = "com.schenker.ralt.integration_event";
    public static final String INPUT_FILE_PATH = "src/main/resources/spockSpecification.txt";
    public static final Configuration FREEMARKER_CONFIG = new Configuration(Configuration.VERSION_2_3_31);

    static {
        try {
            FREEMARKER_CONFIG.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FREEMARKER_CONFIG.setDefaultEncoding("UTF-8");
    }
}
