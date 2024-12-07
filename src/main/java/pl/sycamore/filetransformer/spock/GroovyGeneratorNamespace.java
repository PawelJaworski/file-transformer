package pl.sycamore.filetransformer.spock;

import pl.sycamore.string.StringNamespace;

public final class GroovyGeneratorNamespace {

    private GroovyGeneratorNamespace() {}

    public static String eventOccurredCodeBlock(String eventText) {
        var codeBlock =
                """
                $event() {}
                """;
        return codeBlock.replace("$event", StringNamespace.toSnakeCase(eventText));
    }
}
