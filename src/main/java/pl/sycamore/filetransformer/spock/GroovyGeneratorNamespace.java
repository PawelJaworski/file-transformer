package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

public final class GroovyGeneratorNamespace {

    private GroovyGeneratorNamespace() {}

    public static String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public static String eventOccurredCodeBlock(String eventText) {
        var codeBlock =
                """
                $event() {}
                """;
        return StringUtils.trimToEmpty(codeBlock)
                .replace("$event", StringNamespace.toSnakeCase(eventText));
    }
}
