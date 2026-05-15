package org.flossware.jcurses.testutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Helper methods for testing thread safety and concurrency.
 */
public class ThreadSafetyTestHelper {

    /**
     * Run a task concurrently with the specified number of platform threads.
     *
     * @param threadCount Number of threads to spawn
     * @param task The task to run concurrently
     * @throws InterruptedException if waiting for threads is interrupted
     */
    public static void runConcurrent(int threadCount, Runnable task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        if (!latch.await(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            throw new AssertionError("Concurrent test timed out after 10 seconds");
        }
        executor.shutdown();
    }

    /**
     * Run a task concurrently with the specified number of Virtual Threads.
     * Tests Java 21 Virtual Thread compatibility.
     *
     * @param threadCount Number of virtual threads to spawn
     * @param task The task to run concurrently
     * @throws InterruptedException if waiting for threads is interrupted
     */
    public static void runWithVirtualThreads(int threadCount, Runnable task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Thread.ofVirtual().start(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("Virtual thread test timed out after 10 seconds");
        }
    }

    /**
     * Run a task with two different operations concurrently to test for race conditions.
     *
     * @param threadCount Number of threads for each operation
     * @param operation1 First concurrent operation
     * @param operation2 Second concurrent operation
     * @throws InterruptedException if waiting for threads is interrupted
     */
    public static void runTwoOperationsConcurrently(int threadCount, Runnable operation1, Runnable operation2)
            throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount * 2);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount * 2);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    operation1.run();
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    operation2.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        if (!latch.await(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            throw new AssertionError("Concurrent test timed out after 10 seconds");
        }
        executor.shutdown();
    }
}
