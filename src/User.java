/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class User {

    private final String name;
    private final String password;
    private final String rootDirectory;

    User(String name, String password, String rootDirectory) {
        this.name = name;
        this.password = password;
        this.rootDirectory = rootDirectory;
    }

    String getName() {
        return name;
    }

    boolean isUser(final String password) {
        return this.password.equals(password);
    }

    String getRootDirectory() {
        return rootDirectory;
    }
}
