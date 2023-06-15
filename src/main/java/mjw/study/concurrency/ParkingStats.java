package mjw.study.concurrency;

public class ParkingStats {

    /**
     * This two variables store the number of cars and motorcycles in the
     * parking
     */
    private long numberCars;
    private long numberMotorcycles;

    private ParkingCash cash;

    /**
     * Constructor of the class
     */
    public ParkingStats(ParkingCash cash) {
        numberCars = 0;
        numberMotorcycles = 0;
        this.cash = cash;
    }

	// 车进
    public void carComeIn() {
        numberCars++;
    }

	// 车出
    public void carGoOut() {
        numberCars--;
        cash.vehiclePay();
    }

    public void motoComeIn() {
        numberMotorcycles++;
    }

    public void motoGoOut() {
        numberMotorcycles--;
        cash.vehiclePay();
    }

    public long getNumberCars() {
        return numberCars;
    }

    public long getNumberMotorcycles() {
        return numberMotorcycles;
    }

}
