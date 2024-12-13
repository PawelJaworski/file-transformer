package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public final class MiroTextNamespace {
    private static final String COMMAND_TAG = "<<command>>";
    private static final String EVENT_TAG = "<<event>>";

    private MiroTextNamespace() {}

    public static String removeTags(String text) {
        return text.replace(COMMAND_TAG, "")
                .replace(EVENT_TAG, "");
    }

    public static String removeJson(String text) {
        return text
                .replace(StringUtils.trimToEmpty(StringUtils.substringBetween(text, "{", "}")), "")
                .replace("{", "")
                .replace("}", "");
    }

    public static List<String> extractText(List<String> text, String from, String to) {
        return text.stream()
                .skip(text.indexOf(from) + 1)
                .limit(text.indexOf(to) - text.indexOf(from) -1)
                .toList();
    }

    public static List<String> extractText(List<String> text, String from) {
        return text.stream()
                .skip(text.indexOf("then") + 1)
                .toList();
    }

    public static List<String> findByTag(List<String> text, String tag) {
        return text.stream()
                .filter(it -> it.contains("<<" + tag + ">>"))
                .map(it -> it.replaceAll("<<" + tag + ">>", ""))
                .toList();
    }
}
