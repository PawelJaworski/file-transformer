package pl.sycamore.filetransformer.spock;

import pl.sycamore.filetransformer.common.AbstractFileHandler;

public class EventPublisherAbilityHandler extends AbstractFileHandler {

    public EventPublisherAbilityHandler(String projectPath, String packageName) {
        super(filePath(projectPath, packageName));
    }

    private static String filePath(String projectPath, String packageName) {
        return projectPath
                + "/src/test/java/"
                + JavaGeneratorNamespace.relativePathFromPackage(packageName)
                + "/application/event/EventPublisherAbility.java";
    }
}
