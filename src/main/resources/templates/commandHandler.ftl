package ${commandHandler.package()};

import org.springframework.stereotype.Component;

@Component
public class ${commandHandler.className()} {
    public void handle(${commandHandler.command()} cmd) {}
}