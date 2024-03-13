package mjw.java.concurrency.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Mar 2024, 5:09 PM
 */
public class ValidatorMain {

    public static void main(String[] args) {
        String username = "test";
        String password = "test";

        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");

        ValidatorTask ldapTask = new ValidatorTask(ldapValidator,
                username, password);
        ValidatorTask dbTask = new ValidatorTask(dbValidator,
                username, password);
        List<ValidatorTask> taskList = new ArrayList<>();
        taskList.add(ldapTask);
        taskList.add(dbTask);
        ExecutorService executor = Executors.newCachedThreadPool();
        String result;
        try {
            result = executor.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n", result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }
}
