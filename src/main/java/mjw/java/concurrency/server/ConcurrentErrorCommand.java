package mjw.java.concurrency.server;

/**
 * Concurrent version of the ErrorCommand. It's executed when an unknown command arrives
 *
 * @author author
 */
public class ConcurrentErrorCommand extends Command {

    /**
     * Constructor of the class
     *
     * @param command String that represents the command
     */
    public ConcurrentErrorCommand(String[] command) {
        super(command);
        setCacheable(false);
    }

    @Override
    /**
     * Method that executes the command
     */
    public String execute() {
        return "Unknown command: " + command[0];
    }

}
