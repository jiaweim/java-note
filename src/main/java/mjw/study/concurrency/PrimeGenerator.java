package mjw.study.concurrency;

public class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;

        // 无效循环，直到被中断
        while (true) {
            // 将质数输出到控制台
            if (isPrime(number))
                System.out.printf("Number %d is Prime\n", number);

            // 每处理完一个数，检测是否有中断请求
            // 如果中断，则输出信息，结束执行
            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }
            number++;
        }
    }

    /**
     * Method that calculate if a number is prime or not
     *
     * @param number : The number
     * @return A boolean value. True if the number is prime, false if not.
     */
    private boolean isPrime(long number) {
        if (number <= 2)
            return true;
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0)
                return false;
        }
        return true;
    }
}
