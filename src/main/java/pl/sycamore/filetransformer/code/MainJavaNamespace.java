package pl.sycamore.filetransformer.code;

import org.apache.commons.text.CaseUtils;

public final class MainJavaNamespace {
    private MainJavaNamespace() {
    }

    public static String className(String text) {
        return CaseUtils.toCamelCase(text, true, ' ');
    }

    public static String recordClass(String className, String classPackage) {
        var content =
        """
        package $classPackage;
        
        import lombok.Builder;
      
        @Builder
        public record $className() {}
        """;

        return content.replace("$classPackage", classPackage).replace("$className", className);
    }

    public static String entity(String className, String classPackage) {
        var content =
                """
                package $classPackage;
                
                import jakarta.persistence.*;
              
                @Entity
                public class $classNameEntity {
                    @Id
                    private Long id;
                }
                """;

        return content.replace("$classPackage", classPackage).replace("$className", className);
    }

    public static String repository(String className, String classPackage) {
        var content =
                """
                package $classPackage;
                
                import java.util.Optional;
                
                public interface $classNameRepository {
                    $classNameEntity save($classNameEntity entity);
                    Optional<$classNameEntity> findById(long id);
                    void deleteAll();
                }
                """;

        return content.replace("$classPackage", classPackage).replace("$className", className);
    }
}
