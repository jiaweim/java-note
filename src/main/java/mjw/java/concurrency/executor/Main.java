package mjw.java.concurrency.executor;

/**
 * @author Jiawei Mao
 * @version 0.0.1
 * @since 06 Mar 2024, 00:35
 */
public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 100; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }
        // 关闭 Server
        System.out.printf("Main: Shutting down the Executor.\n");
        server.endServer();

        // 发送一个新的 task，该 task 会被拒绝
        System.out.printf("Main: Sending another Task.\n");
        Task task = new Task("Rejected task");
        server.executeTask(task);

        System.out.printf("Main: End.\n");
    }
}
