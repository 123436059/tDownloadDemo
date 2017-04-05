package com.originalandtest.tx.downloaddemo.download;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorManger {
    private static ThreadPoolExecutorManger mThreadPoolExecutorManger;

    private static final int REQUEST_POOL_COREPOLL_SIZE = 10;
    private static final int REQUEST_POOL_MAXPOLL_SIZE = 10;
    private static final int REQUEST_POOL_KEEP_ALIVETIME = 30;

    private static final int IMAGE_POOL_COREPOLL_SIZE = 10;
    private static final int IMAGE_POOL_MAXPOLL_SIZE = 10;
    private static final int IMAGE_POOL_KEEP_ALIVETIME = 30;

    private static final int GENERAL_POOL_COREPOLL_SIZE = 10;
    private static final int GENERAL_POOL_MAXPOLL_SIZE = 10;
    private static final int GENERAL_POOL_KEEP_ALIVETIME = 30;

    private ThreadPoolExecutorManger() {
    }

    public synchronized static ThreadPoolExecutorManger getInstance() {
        if (mThreadPoolExecutorManger == null) {
            mThreadPoolExecutorManger = new ThreadPoolExecutorManger();
        }
        return mThreadPoolExecutorManger;
    }

    private ThreadPoolExecutor sRequestThreadPoolExecutor;

    public synchronized ThreadPoolExecutor getRequestExecutor() {
        if (sRequestThreadPoolExecutor == null) {
            sRequestThreadPoolExecutor = new ThreadPoolExecutor(REQUEST_POOL_COREPOLL_SIZE,
                    REQUEST_POOL_MAXPOLL_SIZE, REQUEST_POOL_KEEP_ALIVETIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new RejectedExecutionHandler() {

                        @Override
                        public void rejectedExecution(Runnable r,
                                                      ThreadPoolExecutor executor) {
                        }
                    });
        }
        return sRequestThreadPoolExecutor;
    }

    private ThreadPoolExecutor sImageThreadPoolExecutor;

    public synchronized ThreadPoolExecutor getImageExecutor() {
        if (sImageThreadPoolExecutor == null) {
            sImageThreadPoolExecutor = new ThreadPoolExecutor(IMAGE_POOL_COREPOLL_SIZE,
                    IMAGE_POOL_MAXPOLL_SIZE, IMAGE_POOL_KEEP_ALIVETIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new RejectedExecutionHandler() {

                        @Override
                        public void rejectedExecution(Runnable r,
                                                      ThreadPoolExecutor executor) {
                        }
                    });
        }
        return sImageThreadPoolExecutor;
    }

    private ThreadPoolExecutor sGeneralThreadPoolExecutor;

    public synchronized ThreadPoolExecutor getGeneralExecutor() {
        if (sGeneralThreadPoolExecutor == null) {
            sGeneralThreadPoolExecutor = new ThreadPoolExecutor(GENERAL_POOL_COREPOLL_SIZE,
                    GENERAL_POOL_MAXPOLL_SIZE, GENERAL_POOL_KEEP_ALIVETIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new RejectedExecutionHandler() {

                        @Override
                        public void rejectedExecution(Runnable r,
                                                      ThreadPoolExecutor executor) {
                        }
                    });
        }
        return sGeneralThreadPoolExecutor;
    }


    private ThreadPoolExecutor sVideoThreadPoolExecutor;

    public synchronized ThreadPoolExecutor getVideoExecutor() {
        if (sVideoThreadPoolExecutor == null) {
            sVideoThreadPoolExecutor = new ThreadPoolExecutor(GENERAL_POOL_COREPOLL_SIZE,
                    GENERAL_POOL_MAXPOLL_SIZE, GENERAL_POOL_KEEP_ALIVETIME, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new RejectedExecutionHandler() {

                        @Override
                        public void rejectedExecution(Runnable r,
                                                      ThreadPoolExecutor executor) {
                        }
                    });
        }
        return sVideoThreadPoolExecutor;
    }
}
