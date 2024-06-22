package mjw.hipparchus.linear;

import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.LUDecomposition;
import org.hipparchus.linear.MatrixUtils;
import org.hipparchus.linear.RealMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 14 May 2024, 6:25 PM
 */
public class RealMatrixTest {

    @Test
    void create() {
        // 工厂方法：创建 2 行 3 列矩阵
        double[][] matrixData = {{1d, 2d, 3d}, {2d, 5d, 3d}};
        RealMatrix m = MatrixUtils.createRealMatrix(matrixData);

        // 构造函数：创建 3 行 2 列矩阵
        double[][] matrixData2 = {{1d, 2d}, {2d, 5d}, {1d, 7d}};
        RealMatrix n = new Array2DRowRealMatrix(matrixData2);

        // 两种语法都会 copy 输入的 double[][]

        // 矩阵乘法
        RealMatrix p = m.multiply(n);
        assertEquals(p.getRowDimension(), 2);
        assertEquals(p.getColumnDimension(), 2);

        // 使用 LU decomposition 计算逆矩阵
        RealMatrix pInverse = new LUDecomposition(p).getSolver().getInverse();
    }
}
