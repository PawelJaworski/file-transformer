package org.example;

import org.apache.commons.lang3.StringUtils;
import pl.sycamore.JavaGenerator;

import java.io.IOException;

public class CommandGenerator implements Generator {
    private final JavaGenerator javaGenerator;
    private final String basePackage;

    public CommandGenerator(String projectPath, String basePackage) {
        this.javaGenerator = new JavaGenerator(projectPath);
        this.basePackage = basePackage;
    }

    @Override
    public String pattern() {
        return "COMMAND";
    }

    @Override
    public void generate(String syntax) throws IOException {
        generateCommand(syntax);
    }

    private void generateCommand(String syntax) throws IOException {
        var commandName = javaGenerator.className(StringUtils.substringsBetween(syntax, "'", "'")[0]) + "Cmd";
        var packageName =  javaGenerator.packageName(basePackage + ".application."  + commandName);
        var file = javaGenerator.getElseCreateJavaFile(packageName, commandName);
        javaGenerator.createRecord(file, "String nip, LocalDate invoiceDate");
    }
}
