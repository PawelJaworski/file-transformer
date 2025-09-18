package pl.sycamore;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.filetransformer.code.CodePackage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public final class JavaGenerator {
    private final String projectPath;

    public JavaGenerator(String projectPath) {
        this.projectPath = projectPath;
    }

    public File getElseCreateJavaFile(String packg, String className) {
        var filePath = projectPath + CodePackage.fromPackageName(packg)
                .mainJavaPath() + "/" + className + ".java";
        var file = new File(filePath);

        try {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            file.createNewFile();
            System.out.println("Is command file exist: " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    public void createRecord(File file, String parameters) throws IOException {
        var className = file.getName().replace(".java", "");
        var record = """
        package $packageName;
        
        import lombok.Builder;
        $imports
        
        @Builder
        public record $className($parameters) {}
        """;

        var imports = "";
        if (parameters.contains("LocalDate")) {
            imports = "import java.time.LocalDate;";
        }

        FileWriter writer = new FileWriter(file);
        writer.write(record
                .replace("$packageName", packageName(file))
                .replace("$imports", imports)
                .replace("$className", className)
                .replace("$parameters", parameters));
        writer.close();
    }

    public void createCommandHandler(File handlerFile, String commandClass) throws IOException {
        var className = handlerFile.getName().replace(".java", "");
        var record = """
        package $packageName;
        
        $imports
        
        @Component
        class $className {
            void handle($commandClass cmd) {
            }
        }
        """;

        var packageName = packageName(handlerFile);
        var imports = String.join("\n", List.of(
                "import org.springframework.stereotype.Component;"
        ));

        FileWriter writer = new FileWriter(handlerFile);
        writer.write(record
                .replace("$packageName", packageName)
                .replace("$imports", imports)
                .replace("$className", className)
                .replace("$commandClass", commandClass));
        writer.close();
    }

    public String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public String packageName(String text) {
        return text.toLowerCase().replaceAll(" ", "");
    }

    private String packageName(File file) {
        return CodePackage
                .fromRelativePath(file.getParent().substring(file.getPath().lastIndexOf("src/main/java") + "src/main/java".length() + 1))
                .packageName();
    }
}
