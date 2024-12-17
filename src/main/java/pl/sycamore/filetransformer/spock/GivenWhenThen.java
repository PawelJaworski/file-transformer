package pl.sycamore.filetransformer.spock;

import java.util.List;

public record GivenWhenThen(String description, String given, List<String> givenCodeBlock, String when, List<String> whenCodeBlock,
                            String then, List<String> thenCodeBlock) {
}
