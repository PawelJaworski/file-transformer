package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static pl.sycamore.string.StringNamespace.splitListByText;

public class TextFileIntoSpockSpecification {
    public static List<Ability> abilities() {
        return List.of();
    }

    public static Optional<SpockSpecification> transform(String packageName, Path filePath) {
        try {
            var fileLines = Files.lines(filePath)
                    .toList();
            var className = fileLines.stream()
                    .filter(it -> it.contains("Name: "))
                    .findFirst()
                    .map(it -> it.replace("Name: ", ""))
                    .map(it -> CaseUtils.toCamelCase(it, true, ' '))
                    .orElseThrow();

            var gwtList = splitListByText(fileLines, "Spec: ").stream()
                    .map(specText -> {
                        var given = extractGivenText()
                                .andThen(generateGivenDescription())
                                .apply(specText);
                        var givenCodeBlock = extractGivenText()
                                .andThen(generateGivenCodeBlock())
                                .apply(specText);
                        var when = "-";
                        var then = extractThenText()
                                .andThen(then())
                                .apply(specText);
                        var thenCodeBlock = extractThenText()
                                .andThen(generateThenCodeBlock())
                                .apply(specText);
                        return new GivenWhenThen(given, givenCodeBlock, when, then, thenCodeBlock);
                    })
                    .toList();
            return Optional.of(new SpockSpecification(packageName, className, gwtList));
        } catch (IOException e) {
            System.err.println(e);
            return Optional.empty();
        }
    }

    private static Function<List<String>, String> generateGivenDescription() {
        return givenText -> String.join(" and ", givenText);
    }

    private static Function<List<String>, List<String>> generateGivenCodeBlock() {
        return givenText -> givenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(JavaGeneratorNamespace.publishEventInEventPublisherAbility())
                .toList();
    }

    private static Function<List<String>, List<String>> extractGivenText() {
        return it -> it.stream()
                .skip(it.indexOf("given") + 1)
                .limit(it.indexOf("when") - it.indexOf("given") - 1)
                .toList();
    }

    private static Function<List<String>, String> then() {
        return thenText -> String.join(" and ", thenText);
    }

    private static Function<List<String>, List<String>> generateThenCodeBlock() {
        return thenText -> thenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(SpockCodeBlockNamespace::eventOccurrenceAssertion)
                .toList();
    }

    private static Function<List<String>, List<String>> extractThenText() {
        return specText -> specText.stream()
                .skip(specText.indexOf("then") + 1)
                .toList();
    }
}
