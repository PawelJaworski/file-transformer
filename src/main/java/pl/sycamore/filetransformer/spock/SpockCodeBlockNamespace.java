package pl.sycamore.filetransformer.spock;

import org.apache.commons.text.CaseUtils;

final class SpockCodeBlockNamespace {
    private SpockCodeBlockNamespace() {}

    static String eventOccurred(String eventName) {
        return String.format("event_occured(new %s())", CaseUtils.toCamelCase(eventName, true, ' '));
    }

    static String eventOccurrenceAssertion(String eventName) {
        return String.format("assert_event_occurred(new %s())", CaseUtils.toCamelCase(eventName, true, ' '));
    }
}
