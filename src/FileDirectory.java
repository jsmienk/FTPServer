import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class FileDirectory {

    private final File root;

    FileDirectory(String root) {
        assert root != null : "null root";
        assert !root.isEmpty() : "empty root";

        System.out.println("Setting up file directory '" + root + "'.");
        this.root = new File(Server.USERS_FILEPATH + root);

        // if root does not exist, create it
        createDirectory(this.root);
    }

    // TODO: implement
    String getDirectoryList() {
        return "";
    }

    /**
     * Try to create a directory
     *
     * @param directory the directory
     * @return only returns true if the directory was actually created
     */
    private boolean createDirectory(File directory) {
        final boolean success = directory.mkdir();
        if (success)
            System.out.println("Directory '" + directory.getAbsolutePath() + "' created.");
        return success;
    }
}
