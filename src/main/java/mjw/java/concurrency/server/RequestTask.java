package mjw.java.concurrency.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Task that executes a request to the concurrent server
 *
 * @author author
 */
public class RequestTask implements Runnable {

    /**
     * Socket to communicate with the client
     */
    private final Socket clientSocket;

    /**
     * Constructor of the class
     *
     * @param clientSocket socket to communicate
     */
    public RequestTask(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    /**
     * Method that executes the request
     */
    public void run() {

        // receive a query from client
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     clientSocket.getInputStream()));) {

            String line = in.readLine();

            Logger.sendMessage(line);
            ParallelCache cache = ConcurrentServer.getCache();
            String ret = cache.get(line);

            if (ret == null) {
                Command command;

                String[] commandData = line.split(";");
                System.err.println("Command: " + commandData[0]);
                switch (commandData[0]) {
                    case "q" -> {
                        System.err.println("Query");
                        command = new ConcurrentQueryCommand(commandData);
                    }
                    case "r" -> {
                        System.err.println("Report");
                        command = new ConcurrentReportCommand(commandData);
                    }
                    case "s" -> {
                        System.err.println("Status");
                        command = new ConcurrentStatusCommand(commandData);
                    }
                    case "z" -> {
                        System.err.println("Stop");
                        command = new ConcurrentStopCommand(commandData);
                    }
                    default -> {
                        System.err.println("Error");
                        command = new ConcurrentErrorCommand(commandData);
                    }
                }
                ret = command.execute();
                if (command.isCacheable()) {
                    cache.put(line, ret);
                }
            } else {
                Logger.sendMessage("Command " + line + " was found in the cache");
            }

            System.err.println(ret);
            out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
