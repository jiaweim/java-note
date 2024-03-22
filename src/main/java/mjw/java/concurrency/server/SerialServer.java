package mjw.java.concurrency.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that implements the serial server.
 *
 * @author author
 */
public class SerialServer {

    public static void main(String[] args) throws IOException {
        WDIDAO dao = WDIDAO.getDAO();
        boolean stopServer = false;
        System.out.println("Initialization completed.");

        try (ServerSocket serverSocket = new ServerSocket(Constants.SERIAL_PORT)) {
            do {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String line = in.readLine();
                    Command command;
                    // 解析指令
                    String[] commandData = line.split(";");
                    System.err.println("Command: " + commandData[0]);
                    switch (commandData[0]) {
                        case "q" -> {
                            System.err.println("Query");
                            command = new QueryCommand(commandData);
                        }
                        case "r" -> {
                            System.err.println("Report");
                            command = new ReportCommand(commandData);
                        }
                        case "z" -> {
                            System.err.println("Stop");
                            command = new StopCommand(commandData);
                            stopServer = true;
                        }
                        default -> {
                            System.err.println("Error");
                            command = new ErrorCommand(commandData);
                        }
                    }
                    // 执行指令
                    String response = command.execute();
                    System.err.println(response);
                    // 返回结果
                    out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (!stopServer);
        }

    }

}
