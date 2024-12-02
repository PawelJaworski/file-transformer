package pl.sycamore.filetransformer.spock;

import java.util.List;

public record SpockSpecification(String packageName, String className, List<GivenWhenThen> givenWhenThenList) {
}
