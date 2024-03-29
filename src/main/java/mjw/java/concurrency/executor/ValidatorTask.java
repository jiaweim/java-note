package mjw.java.concurrency.executor;

import java.util.concurrent.Callable;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 11 Mar 2024, 5:07 PM
 */
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
