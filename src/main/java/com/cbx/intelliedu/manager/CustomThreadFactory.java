package com.cbx.intelliedu.manager;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassName: CustomThreadFactory
 * Package: com.cbx.intelliedu.manager
 * Description:
 *
 * @Author CBX
 * @Create 23/11/24 20:23
 * @Version 1.0
 */
public class CustomThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public CustomThreadFactory(String poolName) {
        this.namePrefix = poolName + "-thread-";
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
        System.out.println("Created thread: " + thread.getName());
        return thread;
    }
}
