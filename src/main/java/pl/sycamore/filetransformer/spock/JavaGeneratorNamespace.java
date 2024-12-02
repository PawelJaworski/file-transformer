package pl.sycamore.filetransformer.spock;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

import java.util.List;
import java.util.function.Function;

public final class JavaGeneratorNamespace {
    private JavaGeneratorNamespace() {}

    public static Function<String, String> publishEventInEventPublisherAbility() {
        return eventText -> String.format("%s()", StringNamespace.toSnakeCase(eventText));
    }
}
