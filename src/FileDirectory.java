import java.io.File;

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
        return "\"/" + root.getName() + "\"";
    }

    String getList() {
        String list = "";

        list += "d map " + Code.NL;
        list += " d ook een map " + Code.NL;
        list += " - bestand " + Code.NL;
        list += " - bestand2 " + Code.NL;
        list += " l link " + Code.NL;

        return list;
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
