package org.example;

import pl.sycamore.filetransformer.code.CommandFileHandler;
import pl.sycamore.filetransformer.code.EventFileHandler;
import pl.sycamore.filetransformer.spock.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
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
    }

    private static void generateEvents(List<String> useCaseText) throws IOException {
        for (var eventTxt : MiroTextNamespace.findByTag(useCaseText, "event")) {
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
}

