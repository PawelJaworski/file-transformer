package pl.sycamore.filetransformer.code;

import pl.sycamore.filetransformer.common.AbstractFileHandler;
import pl.sycamore.filetransformer.spock.JavaGeneratorNamespace;

public class EventFileHandler extends AbstractFileHandler {

    public EventFileHandler(String projectPath, String packageName, String fileName) {
        super(filePath(projectPath, packageName, fileName));
    }

    private static String filePath(String projectPath, String packageName, String fileName) {
        var codePackage = CodePackage.fromPackageName(String.format("%s.application.event", packageName));
        return projectPath
                + codePackage.mainJavaPath()
                + "/"
                + fileName
                + ".java";
    }
}
