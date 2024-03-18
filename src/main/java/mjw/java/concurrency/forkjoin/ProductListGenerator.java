package mjw.java.concurrency.forkjoin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 8:50 PM
 */
public class ProductListGenerator {

    public List<Product> generate(int size) {
        List<Product> ret = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setPrice(10);
            ret.add(product);
        }
        return ret;
    }
}
