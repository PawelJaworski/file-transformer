package pl.sycamore.filetransformer.code;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

public final class MainJavaNamespace {
    private MainJavaNamespace() {
    }

    public static String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public static String eventJavaCode(String eventText, String classPackage) {
        var className = CaseUtils.toCamelCase(eventText, true, ' ');
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
