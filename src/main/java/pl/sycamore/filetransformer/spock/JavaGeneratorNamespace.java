package pl.sycamore.filetransformer.spock;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

import java.util.List;
import java.util.function.Function;

public final class JavaGeneratorNamespace {
    private JavaGeneratorNamespace() {}

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
