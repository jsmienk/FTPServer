import java.io.File;
import java.util.Stack;

/**
 * Author: Jeroen
 * Date created: 22-12-16
 */
class FileDirectory {

    private final File root;
    private String myRoot;
    private boolean firstTime=true;
    private Stack<String> stack = new Stack<>();
    FileDirectory(String root) {
        assert root != null : "null root";
        assert !root.isEmpty() : "empty root";

        System.out.println("Setting up file directory '" + root + "'.");
        myRoot = root;
        this.root = new File(Server.USERS_FILEPATH + root);
        // if root does not exist, create it
        stack.add(myRoot);
        createDirectory(this.root);
    }

    void addDirectory(String directory) {
        stack.add(directory);
    }

    void goBackDirectory() {
        stack.pop();
    }

    Stack<String> getStack(){
        return stack;
    }

    String getPath(){
        String path = "";
        for (String aStack : stack) {
            path += "/" + aStack;
        }
        return path;
    }

    String getDirectoryList() {
        return "/" + root.getName() + "";
    }

    /**
     * Try to create a directory
     *
     * @param directory the directory
     * @return only returns true if the directory was actually created
     */
    boolean createDirectory(File directory) {
        final boolean success = directory.mkdir();
        if (success)
            System.out.println("Directory '" + directory.getAbsolutePath() + "' created.");
        return success;
    }

    String getRoot() {
        return myRoot;
    }
}
