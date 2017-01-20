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

    public void addDirectory(String directory) {
        stack.add(directory);
    }

    public void goBackDirectory() {
        stack.pop();
    }

    public Stack<String> getStack(){
        return stack;
    }

    public String getPath(){
        String path = "";
        for (int i=0; i<stack.size(); i++) {
            path += "/" + stack.get(i);
        }
        return path;
    }

    // TODO: implement
    String getDirectoryList() {
        return "/" + root.getName() + "";
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

    public String getRoot() {
        return myRoot;
    }
}
