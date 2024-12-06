package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.example.Functor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.example.Functor.get;
import static pl.sycamore.string.StringNamespace.splitListByText;

public class TextFileIntoSpockSpecification {
    private static final String GIVEN = "given";
    private static final String WHEN = "when";
    private static final String THEN = "then";

    public static Optional<SpockSpecification> transform(String packageName, Path filePath) {
        try {
            var fileLines = Files.lines(filePath)
                    .toList();
            var className = fileLines.stream()
                    .filter(it -> it.contains("Name: "))
                    .findFirst()
                    .map(it -> it.replace("Name: ", ""))
                    .map(it -> CaseUtils.toCamelCase(it, true, ' '))
                    .map(it -> it + "Specification")
                    .orElseThrow();

            var gwtList = splitListByText(fileLines, "Spec: ").stream()
                    .map(specText -> {
                        var given = get(MiroTextNamespace.extractText(specText, GIVEN, WHEN))
                                .then(TextFileIntoSpockSpecification::generateGivenDescription)
                                .value();
                        var givenCodeBlock = get(MiroTextNamespace.extractText(specText, GIVEN, WHEN))
                                .then(generateGivenCodeBlock())
                                .value();
                        var when = "-";
                        var then = get(MiroTextNamespace.extractText(specText, THEN))
                                .then(TextFileIntoSpockSpecification::generateThenDescription)
                                .value();
                        var thenCodeBlock = get(MiroTextNamespace.extractText(specText, THEN))
                                .then(generateThenCodeBlock())
                                .value();
                        return new GivenWhenThen(given, givenCodeBlock, when, then, thenCodeBlock);
                    })
                    .toList();
            return Optional.of(new SpockSpecification(packageName, className, gwtList));
        } catch (IOException e) {
            System.err.println(e);
            return Optional.empty();
        }
    }

    private static String generateGivenDescription(List<String> givenText) {
        return String.join(" and ", givenText);
    }

    private static Function<List<String>, List<String>> generateGivenCodeBlock() {
        return givenText -> givenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(JavaGeneratorNamespace::publishEventInEventPublisherAbility)
                .toList();
    }

    private static String generateThenDescription(List<String> thenText) {
        return String.join(" and ", thenText);
    }

    private static Function<List<String>, List<String>> generateThenCodeBlock() {
        return thenText -> thenText.stream()
                .filter(t -> t.contains("event"))
                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                .map(SpockCodeBlockNamespace::eventOccurrenceAssertion)
                .toList();
    }
}
