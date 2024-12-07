package pl.sycamore.filetransformer.code;

import org.apache.commons.text.CaseUtils;

public final class MainJavaNamespace {
    private MainJavaNamespace() {
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
