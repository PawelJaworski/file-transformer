package pl.sycamore.filetransformer.adapter;

import org.apache.commons.lang3.StringUtils;
import java.util.Optional;

public final class MiroContentNamespace {
    private MiroContentNamespace() {}

    public static Optional<String> generateCodeFromTag(String text) {
        return Optional.of(text)
                .filter(it -> it.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")));
    }
}
