package pl.sycamore.string;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class StringNamespace {
    private StringNamespace() {
    }

    public static String toSnakeCase(String s) {
        return StringUtils.trimToEmpty(s).replaceAll(" ", "_")
                .toLowerCase();
    }

    public static List<List<String>> splitListByText(List<String> originalList, String text) {
        List<List<String>> result = new ArrayList<>();
        List<String> currentList = null;

        for (String item : originalList) {
            if (item.startsWith(text)) {
                // If we already have a current list, add it to the result before starting a new one
                if (currentList != null) {
                    result.add(currentList);
                }
                // Start a new list for the new spec
                currentList = new ArrayList<>();
                currentList.add(item);
            } else {
                // Add the current item to the current list
                if (currentList != null) {
                    currentList.add(item);
                }
            }
        }

        // Add the last collected list if it exists
        if (currentList != null) {
            result.add(currentList);
        }

        return result;
    }
}
