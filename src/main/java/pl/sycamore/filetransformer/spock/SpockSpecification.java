package pl.sycamore.filetransformer.spock;

import java.util.List;

public record SpockSpecification(String className, List<GivenWhenThen> givenWhenThenList) {
}
