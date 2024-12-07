package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import pl.sycamore.string.StringNamespace;

public final class GroovyGeneratorNamespace {

    private GroovyGeneratorNamespace() {}

    public static String eventOccurredCodeBlock(String eventText) {
        var codeBlock =
                """
                $event() {}
                """;
        return StringUtils.trimToEmpty(codeBlock)
                .replace("$event", StringNamespace.toSnakeCase(eventText));
    }
}
