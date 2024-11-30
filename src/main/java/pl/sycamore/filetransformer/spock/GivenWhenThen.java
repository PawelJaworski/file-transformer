package pl.sycamore.filetransformer.spock;

import java.util.List;

public record GivenWhenThen(String given, List<String> givenCodeBlock, String when, String then, List<String> thenCodeBlock) {
}
