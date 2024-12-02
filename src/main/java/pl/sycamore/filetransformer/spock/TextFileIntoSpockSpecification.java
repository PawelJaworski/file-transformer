package pl.sycamore.filetransformer.spock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
                        var givenText = it.stream()
                                .skip(it.indexOf("given") + 1)
                                .limit(it.indexOf("when") - it.indexOf("given") - 1)
                                .toList();
                        var given = String.join(" and ", givenText);
                        var givenCodeBlock = givenText.stream()
                                .filter(t -> t.contains("event"))
                                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                                .map(JavaGeneratorNamespace.publishEventInEventPublisherAbility())
                                .toList();
                        var when = "-";
                        var thenText = it.stream()
                                .skip(it.indexOf("then") + 1)
                                .toList();
                        var then = String.join(" and ", thenText);
                        var thenCodeBlock = thenText.stream()
                                .filter(t -> t.contains("event"))
                                .map(t -> StringUtils.trimToEmpty(t.replace("[event]", "")))
                                .map(SpockCodeBlockNamespace::eventOccurrenceAssertion)
                                .toList();
                        return new GivenWhenThen(given, givenCodeBlock, when, then, thenCodeBlock);
                    })
                    .toList();
            return Optional.of(new SpockSpecification(packageName, className, gwtList));
        } catch (IOException e) {
            System.err.println(e);
            return Optional.empty();
        }
    }




}
