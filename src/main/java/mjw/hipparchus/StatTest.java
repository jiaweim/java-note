package mjw.hipparchus;

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
}
