package pl.sycamore.filetransformer.code;

import pl.sycamore.filetransformer.common.AbstractFileHandler;

public class RepositoryFileHandler extends AbstractFileHandler {

    public RepositoryFileHandler(String projectPath, String packageName, String fileName) {
        super(filePath(projectPath, packageName, fileName));
    }

    private static String filePath(String projectPath, String packageName, String entityName) {
        var codePackage = CodePackage.fromPackageName(packageName);
        return projectPath
                + codePackage.mainJavaPath()
                + "/"
                + entityName
                + "Repository.java";
    }
}
