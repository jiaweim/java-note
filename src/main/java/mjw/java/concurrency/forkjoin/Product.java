package mjw.java.concurrency.forkjoin;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 8:46 PM
 */
public class Product {

    private String name;
    private double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
