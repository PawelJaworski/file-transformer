package pl.sycamore.filetransformer.spock;

import org.apache.commons.text.CaseUtils;
import pl.sycamore.string.StringNamespace;

final class SpockCodeBlockNamespace {
    private SpockCodeBlockNamespace() {}

    static String eventOccurred(String eventName) {
        return String.format("event_occured(new %s())", CaseUtils.toCamelCase(eventName, true, ' '));
    }

    static String eventOccurrenceAssertion(String eventName) {
        return String.format("assert_%s {}", StringNamespace.toSnakeCase(eventName));
    }

    static String comment(String text) {
        return String.format("// %s", text);
    }
}
