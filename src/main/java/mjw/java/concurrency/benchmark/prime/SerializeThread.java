package mjw.java.concurrency.benchmark.prime;

import mjw.java.concurrency.benchmark.Utils;

import java.math.BigInteger;

public class SerializeThread {

    public boolean isprime(BigInteger n) {
        boolean isPrime = true;

        for (BigInteger i = Utils.two; i.compareTo(TestPrime.sqrt) <= 0; i = i.add(BigInteger.ONE)) {
            if (n.mod(i).equals(BigInteger.ZERO)) {
                isPrime = false;
            }
        }

        return isPrime;
    }

}