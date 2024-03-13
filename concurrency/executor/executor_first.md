# 运行多个任务，处理第一个

2024-03-13
@author Jiawei Mao

## 简介

并发编程中的一个常见应用：多个并发任务解决同一个问题，但只对第一个返回的结果感兴趣。例如，采用多种排序算法对数组排序，获得最快排序算法的结果。

下面通过 `ThreadPoolExecutor` 实现该场景：尝试使用两种机制来验证用户，如果其中一种机制能够验证，就采用该机制验证用户。

## 示例

1. `UserValidator`: 实现用户验证功能 

```java
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class UserValidator {

    private final String name;

    public UserValidator(String name) {
        this.name = name;
    }

    public boolean validate(String name, String password) {
        Random random = new Random();

        try {
            // 随机 sleep 一下，模拟验证过程
            long duration = (long) (Math.random() * 10);
            System.out.printf("Validator %s: Validating a user during %d seconds\n",
                    this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }
        // 返回一个随机 boolean 值
        return random.nextBoolean();
    }

    public String getName() {
        return name;
    }
}
```

2. `ValidatorTask`: 实现 `Callable` 接口的并发任务类，它使用 `UserValidation` 进行验证。

```java
import java.util.concurrent.Callable;

public class ValidatorTask implements Callable<String> {

    private final UserValidator validator;
    private final String user;
    private final String password;

    public ValidatorTask(UserValidator validator, String user,
            String password) {
        this.validator = validator;
        this.user = user;
        this.password = password;
    }

    @Override
    public String call() throws Exception {
        // 如果验证失败
        if (!validator.validate(user, password)) {
            System.out.printf("%s: The user has not been found\n",
                    validator.getName());
            throw new Exception("Error validating user");
        }
        System.out.printf("%s: The user has been found\n",
                validator.getName());
        return validator.getName();
    }
}
```

3. `ValidatorMain` 

```java
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
```

该示例的核心方法是 `ExecutorService.invokeAny()`。

`invokeAny()` 接收任务列表，启动这些任务，**只要有任何一个任务完成（正常完成，没有抛出异常），线程池会终止其它未完成的任务**。

上面创建了两个 `ValidatorTask`，有 4 种可能：

- 两个任务都返回 `true`：此时 `invokeAny()` 返回率先完成任务的名称，并终止第二个任务；
- 第一个任务返回 `true`，第二个抛出 `Exception`：此时 `invokeAny()` 返回第一个任务的名称；
- 第一个任务抛出 `Exception`，第二个返回 `true`：此时 `invokeAny()` 返回第二个任务的名称；
- 两个任务都抛出 `Exception`：此时 `invokeAny()` 抛出 `ExecutionException`

多次运行上例，可能获得这 4 种结果。



