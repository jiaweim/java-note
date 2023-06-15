package mjw.study.concurrency;

public class ParkingCash {

    private static final int cost = 2;

    // 赚的钱
    private long cash;

    public ParkingCash() {
        cash = 0;
    }

    /**
     * 当有车离开时调用，收钱
     */
    public void vehiclePay() {
        cash += cost;
    }

    /**
     * 将 cash 输出到控制台，然后初始化为 0
     */
    public void close() {
        System.out.printf("Closing accounting");
        long totalAmmount;
        totalAmmount = cash;
        cash = 0;
        System.out.printf("The total ammount is : %d", totalAmmount);
    }
}
