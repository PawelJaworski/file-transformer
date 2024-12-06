package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import pl.sycamore.string.StringNamespace;

import java.util.List;

public final class TestAbilityNamespace {
    private TestAbilityNamespace() {}

    public static List<String> addEventPublishingIfNotExists(List<String> abilityCodeBlock, String event) {
                    var endIdx = abilityCodeBlock.stream()
                    .map(StringUtils::trimToEmpty)
                    .toList()
                    .lastIndexOf("}");
            var eventCode = JavaGeneratorNamespace.publishEventInEventPublisherAbility(event);
            if (abilityCodeBlock.stream().anyMatch(it -> it.contains(StringNamespace.toSnakeCase(event)))) {
                return abilityCodeBlock;
            }
            abilityCodeBlock.add(endIdx, "    " + eventCode);
            return abilityCodeBlock;
    }
}
