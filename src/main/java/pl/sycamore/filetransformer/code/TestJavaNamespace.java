package pl.sycamore.filetransformer.code;

import org.apache.commons.lang3.StringUtils;
import pl.sycamore.filetransformer.spock.JavaGeneratorNamespace;
import pl.sycamore.string.StringNamespace;

import java.util.List;

public final class TestJavaNamespace {
    private TestJavaNamespace() {}

    public static List<String> addEventOccurredAssertionIfNotExists(List<String> abilityCodeBlock, String event) {
        var endIdx = abilityCodeBlock.stream()
                .map(StringUtils::trimToEmpty)
                .toList()
                .lastIndexOf("}");
        var eventCode = JavaGeneratorNamespace.assertEventOccurredPublisherAbility(event);
        if (abilityCodeBlock.stream().anyMatch(it -> it.contains( "assert_" + StringNamespace.toSnakeCase(event) ))) {
            System.out.println("Ability method already exists " + eventCode);
            return abilityCodeBlock;
        }
        abilityCodeBlock.add(endIdx, "    " + eventCode);
        return abilityCodeBlock;
    }

    public static List<String> addEventPublishingIfNotExists(List<String> abilityCodeBlock, String event) {
        var endIdx = abilityCodeBlock.stream()
                .map(StringUtils::trimToEmpty)
                .toList()
                .lastIndexOf("}");
        var eventCode = JavaGeneratorNamespace.publishEventInEventPublisherAbility(event);
        if (abilityCodeBlock.stream().anyMatch(it -> it.contains(StringNamespace.toSnakeCase(event)))) {
            System.out.println("Ability method already exists " + eventCode);
            return abilityCodeBlock;
        }
        abilityCodeBlock.add(endIdx, "    " + eventCode);
        return abilityCodeBlock;
    }
}
