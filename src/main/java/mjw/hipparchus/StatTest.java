package mjw.hipparchus;

import org.hipparchus.stat.descriptive.DescriptiveStatistics;
import org.hipparchus.stat.descriptive.StatisticalSummary;
import org.hipparchus.stat.descriptive.StreamingStatistics;
import org.junit.jupiter.api.Test;

public class StatTest {

    @Test
    void testDescriptiveStatistics() {
// 为每个样本创建 StreamingStatistic
        StreamingStatistics setOneStats = new StreamingStatistics();
        StreamingStatistics setTwoStats = new StreamingStatistics();

// 添加数据
        setOneStats.addValue(2);
        setOneStats.addValue(3);
        setTwoStats.addValue(2);
        setTwoStats.addValue(4);
// Aggregate the subsample statistics
        StatisticalSummary aggregatedStats = StatisticalSummary.aggregate(setOneStats, setTwoStats);

// Full sample data is reported by aggregatedStats
        double totalSampleSum = aggregatedStats.getSum();
        System.out.println(totalSampleSum);
    }

    @Test
    void testPercentile() {
        double[] data = new double[]{
                122, 126, 133, 140, 145, 145, 149, 150, 157,
                162, 166, 175, 177, 177, 183, 188, 199, 212
        };

        DescriptiveStatistics statistics = new DescriptiveStatistics(data);
        double v1 = statistics.getPercentile(20);
        double v2 = statistics.getPercentile(25);
        double v3 = statistics.getPercentile(50);
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
    }

}
