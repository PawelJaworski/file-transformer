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
                    .map(it -> {
                        var given = givenText()
                                .andThen(given())
                                .apply(it);
                        var givenCodeBlock = givenText()
                                .andThen(givenCodeBlock())
                                .apply(it);
                        var when = "-";
                        var then = thenText()
                                .andThen(then())
                                .apply(it);
                        var thenCodeBlock = thenText()
                                .andThen(thenCodeBlock())
                                .apply(it);
                        return new GivenWhenThen(given, givenCodeBlock, when, then, thenCodeBlock);
                    })
                    .toList();
            return Optional.of(new SpockSpecification(packageName, className, gwtList));
        } catch (IOException e) {
            System.err.println(e);
            return Optional.empty();
        }
    }

    private static Function<List<String>, String> given() {
        return givenText -> String.join(" and ", givenText);
    }

    private static Function<List<String>, List<String>> givenCodeBlock() {
        return givenText -> givenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(JavaGeneratorNamespace.publishEventInEventPublisherAbility())
                .toList();
    }

    private static Function<List<String>, List<String>> givenText() {
        return it -> it.stream()
                .skip(it.indexOf("given") + 1)
                .limit(it.indexOf("when") - it.indexOf("given") - 1)
                .toList();
    }

    private static Function<List<String>, String> then() {
        return thenText -> String.join(" and ", thenText);
    }

    private static Function<List<String>, List<String>> thenCodeBlock() {
        return thenText -> thenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(SpockCodeBlockNamespace::eventOccurrenceAssertion)
                .toList();
    }

    private static Function<List<String>, List<String>> thenText() {
        return specText -> specText.stream()
                .skip(specText.indexOf("then") + 1)
                .toList();
    }
}
