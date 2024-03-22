package mjw.java.concurrency.server;

/**
 * Class that implements the serial version of the Stop command. Finish the execution of the server
 *
 * @author author
 */
public class StopCommand extends Command {

    /**
     * Constructor of the class
     *
     * @param command String that represents the command
     */
    public StopCommand(String[] command) {
        super(command);
    }

    @Override
    /**
     * Method that executes the command
     */
    public String execute() {
        return "Server stopped";
    }

}
