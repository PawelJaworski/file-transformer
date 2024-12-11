package pl.sycamore.filetransformer.code;

import pl.sycamore.filetransformer.common.AbstractFileHandler;
import pl.sycamore.filetransformer.spock.JavaGeneratorNamespace;

public class CommandFileHandler extends AbstractFileHandler {

    public CommandFileHandler(String projectPath, String packageName, String fileName) {
        super(filePath(projectPath, packageName, fileName));
    }

    private static String filePath(String projectPath, String packageName, String fileName) {
        return projectPath
                + "/src/main/java/"
                + JavaGeneratorNamespace.relativePathFromPackage(packageName)
                + "/application/cmd/"
                + fileName
                + ".java";
    }
}
