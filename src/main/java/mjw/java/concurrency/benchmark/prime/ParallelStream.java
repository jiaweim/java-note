package mjw.java.concurrency.benchmark.prime;

import mjw.java.concurrency.benchmark.Utils;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelStream {
	public boolean isPrime() {
		Set<BigInteger> collect = Stream
				.iterate(Utils.two, n -> n.add(TestPrime.lengthForThread))
				.limit(TestPrime.numberOfThread.longValue()).parallel()
				.filter(i -> Utils.primeProcessPart(i))
				.collect(Collectors.toSet());
		return collect.size() == 0;
	}
}