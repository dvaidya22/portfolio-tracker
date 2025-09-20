package com.example.portfolio.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AssetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Asset getAssetSample1() {
        return new Asset().id(1L).ticker("ticker1").quantity(1);
    }

    public static Asset getAssetSample2() {
        return new Asset().id(2L).ticker("ticker2").quantity(2);
    }

    public static Asset getAssetRandomSampleGenerator() {
        return new Asset().id(longCount.incrementAndGet()).ticker(UUID.randomUUID().toString()).quantity(intCount.incrementAndGet());
    }
}
