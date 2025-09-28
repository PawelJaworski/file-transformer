package pl.sycamore.filetransformer.code;

public record CodePackage(String packageName, String relativePath) {
    public static CodePackage fromPackageName(String packageName) {
        var relativePath = packageName.replace('.', '/');
        return new CodePackage(packageName, relativePath);
    }

    public static CodePackage fromRelativePath(String relativePath) {
        var codePackage = relativePath.replace('/', '.');
        return new CodePackage(codePackage, relativePath);
    }

    public String mainJavaPath() {
        return "/src/main/java/"
                + relativePath;
    }

    public String testJavaPath() {
        return "/src/test/java/"
                + relativePath;
    }
}
