package pl.sycamore.filetransformer.code;

import org.apache.commons.text.CaseUtils;

public final class MainJavaNamespace {
    private MainJavaNamespace() {
    }

    public static String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public static String recordClass(String className, String classPackage) {
        var content =
        """
        package $classPackage;
        
        import lombok.Builder;
      
        @Builder
        public record $className() {}
        """;

        return content.replace("$classPackage", classPackage).replace("$className", className);
    }
}
