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
        var command = javaGenerator.className(StringUtils.substringsBetween(syntax, "'", "'")[0]);
        var packageName = javaGenerator.packageName(basePackage + ".application."  + command);

        var commandClass =  command + "Cmd";
        var commandFile = javaGenerator.getElseCreateJavaFile(packageName, commandClass);
        javaGenerator.createRecord(commandFile, "String nip, LocalDate invoiceDate");

        var handlerClass = command + "Handler";
        var handlerFile = javaGenerator.getElseCreateJavaFile(packageName, handlerClass);
        javaGenerator.createCommandHandler(handlerFile, commandClass);

        var handlerAbilityClass = command + "HandlerAbility";
        var handlerAbilityFile = javaGenerator.getElseCreateJavaTestFile(packageName, handlerAbilityClass);
        javaGenerator.createCommandHandlerAbility(handlerAbilityFile);
    }
}
