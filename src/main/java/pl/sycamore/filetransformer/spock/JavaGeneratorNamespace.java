package pl.sycamore.filetransformer.spock;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

public final class JavaGeneratorNamespace {
    private JavaGeneratorNamespace() {}

    public static String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public static String assertEventOccurredPublisherAbility(String eventText) {
        var useCase = "Consumer<"
                + CaseUtils.toCamelCase(eventText, true, ' ')
                + "."
                + CaseUtils.toCamelCase(eventText, true, ' ')
                + "Builder> useCase";
        return String.format("default void assert_%s(%s) {}", StringNamespace.toSnakeCase(eventText), useCase);
    }

    public static String publishEventInEventPublisherAbility(String eventText) {
        var methodName = StringNamespace.toSnakeCase(eventText);
        var useCase = "Consumer<"
                + CaseUtils.toCamelCase(eventText, true, ' ')
                + "."
                + CaseUtils.toCamelCase(eventText, true, ' ')
                + "Builder> useCase";
        return String.format("default void %s(%s) {}", methodName, useCase);
    }

    public static String relativePathFromPackage(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }
}
