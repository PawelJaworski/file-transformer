package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import org.example.GeneratorConfig;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.Functor.get;
import static pl.sycamore.string.StringNamespace.splitListByText;

public class TextFileIntoSpockSpecification {
    private static final String GIVEN = "given";
    private static final String WHEN = "when";
    private static final String THEN = "then";

    public static Optional<SpockSpecification> transform(String packageName, List<String> fileLines) {

        var className = GroovyGeneratorNamespace.className(GeneratorConfig.USE_CASE).concat("Specification");

        var gwtList = splitListByText(fileLines, "Spec: ").stream()
                .map(specText -> {
                    var description = specText.stream()
                            .filter(it -> it.contains("Spec: "))
                            .findFirst()
                            .map(it -> it.replace("Spec: ", ""))
                            .orElseThrow();
                    var given = get(MiroTextNamespace.extractText(specText, GIVEN, WHEN))
                            .then(TextFileIntoSpockSpecification::generateGivenOrThenDescription)
                            .value();
                    var givenCodeBlock = get(MiroTextNamespace.extractText(specText, GIVEN, WHEN))
                            .then(generateGivenCodeBlock())
                            .value();
                    var when = get(MiroTextNamespace.extractText(specText, WHEN, THEN))
                            .then(TextFileIntoSpockSpecification::generateGivenOrThenDescription)
                            .value();
                    var whenCodeBlock = List.of("true");
                    var then = get(MiroTextNamespace.extractText(specText, THEN))
                            .then(TextFileIntoSpockSpecification::generateGivenOrThenDescription)
                            .value();
                    var thenCodeBlock = get(MiroTextNamespace.extractText(specText, THEN))
                            .then(TextFileIntoSpockSpecification::generateThenCodeBlock)
                            .value();
                    return new GivenWhenThen(description, given, givenCodeBlock, when, whenCodeBlock, then, thenCodeBlock);
                })
                .toList();
        return Optional.of(new SpockSpecification(packageName, className, gwtList));
    }

    private static String generateGivenOrThenDescription(List<String> givenText) {
        var description = givenText.stream()
                .map(MiroTextNamespace::removeJson)
                .map(MiroTextNamespace::removeTags)
                .map(StringUtils::trimToEmpty)
                .toList();
        return String.join(" and ", description);
    }

    private static Function<List<String>, List<String>> generateGivenCodeBlock() {
        return givenText -> givenText.stream()
                .filter(t -> t.contains("<<event>>"))
                .map(MiroTextNamespace::removeJson)
                .map(MiroTextNamespace::removeTags)
                .map(GroovyGeneratorNamespace::eventOccurredCodeBlock)
                .toList();
    }

    private static List<String> generateThenCodeBlock(List<String> thenText) {
        var code = Stream.concat(
                thenText.stream()
                        .filter(t -> t.contains("<<event>>"))
                        .map(MiroTextNamespace::removeJson)
                        .map(MiroTextNamespace::removeTags)
                        .map(SpockCodeBlockNamespace::eventOccurrenceAssertion),
                thenText.stream()
                        .filter(t -> t.contains("<<comment>>"))
                        .map(MiroTextNamespace::removeJson)
                        .map(MiroTextNamespace::removeTags)
                        .map(SpockCodeBlockNamespace::comment)
        ).collect(Collectors.toList());
        code.add("false");

        return code;
    }
}
