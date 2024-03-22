package mjw.java.concurrency.server;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * Class that implement the concurrent version of the status command. Returns information about the executor that
 * executes the concurrent tasks of the server
 *
 * @author author
 */
public class ConcurrentStatusCommand extends Command {

    /**
     * Constructor of the class
     *
     * @param command String that represents the command
     */
    public ConcurrentStatusCommand(String[] command) {
        super(command);
        setCacheable(false);
    }

    @Override
    public String execute() {
        StringBuilder sb = new StringBuilder();
        ThreadPoolExecutor executor = ConcurrentServer.getExecutor();

        sb.append("Server Status;");
        sb.append("Actived Threads: ");
        sb.append(executor.getActiveCount());
        sb.append(";");
        sb.append("Maximum Pool Size: ");
        sb.append(executor.getMaximumPoolSize());
        sb.append(";");
        sb.append("Core Pool Size: ");
        sb.append(executor.getCorePoolSize());
        sb.append(";");
        sb.append("Pool Size: ");
        sb.append(executor.getPoolSize());
        sb.append(";");
        sb.append("Largest Pool Size: ");
        sb.append(executor.getLargestPoolSize());
        sb.append(";");
        sb.append("Completed Task Count: ");
        sb.append(executor.getCompletedTaskCount());
        sb.append(";");
        sb.append("Task Count: ");
        sb.append(executor.getTaskCount());
        sb.append(";");
        sb.append("Queue Size: ");
        sb.append(executor.getQueue().size());
        sb.append(";");
        sb.append("Cache Size: ");
        sb.append(ConcurrentServer.getCache().getItemCount());
        sb.append(";");
        Logger.sendMessage(sb.toString());
        return sb.toString();
    }

}
