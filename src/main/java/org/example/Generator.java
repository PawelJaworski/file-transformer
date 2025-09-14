package org.example;

import java.io.IOException;

public interface Generator {
    String pattern();

    void generate(String syntax) throws IOException;
}
