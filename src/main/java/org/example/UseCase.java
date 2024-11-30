package org.example;

public class UseCase {
    private final String eventName;
    private final String className;

    public UseCase(String eventName) {
        this.eventName = eventName;
        this.className = eventName.replaceAll("\\s+", "").replaceAll("\\W", "");
    }

    public String getEventName() {
        return eventName;
    }

    public String getClassName() {
        return className;
    }
}
