package com.tuanbaol.messageclient;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Title:ThreadHelper
 */
public class ThreadHelper {

    public final static String DEFAULT_THREADPOOL = "default_threadpool";//默认线程池

    private static ThreadHelper threadHelper;

    private static Map<String, ThreadPoolExecutor> threadPoolsMap = new HashMap<String, ThreadPoolExecutor>();

    private static int defaultCoreThreadCount = 10;//默认线程池中核心线程的数量
    private static int defaultMaxThreadCount = 32;//默认线程池中线程的最大数量
    private static Long keepAliveTime = 60L;//默认空闲线程的回收阀值
    private static TimeUnit timeUnit = TimeUnit.SECONDS;//默认线程池回收阀值的量化单位
    private static int defaultAppendCoreThreadCount = 50;//后续添加线程池默认的核心线程数量
    private static int defaultAppendMaxThreadCount = 150;//后续添加线程池默认的最大线程数量

    /**
     * 默认在调用线程池帮助类的时候会创建一个默认的线程池
     */
    private ThreadHelper() {
        synchronized (threadPoolsMap) {
            //默认使用无大小限制的阻塞式队列 Integer.MAX_VALUE
            threadPoolsMap.put(DEFAULT_THREADPOOL,
                    new ThreadPoolExecutor(defaultCoreThreadCount, defaultMaxThreadCount, keepAliveTime, timeUnit, new LinkedBlockingQueue<Runnable>()));
        }
    }

    /**
     * 实现单例,防止并发导致的实例对象不一致
     *
     * @return
     */
    public static ThreadHelper getInstance() {
        return SingletonFactory.instance;
    }

    private static class SingletonFactory {
        private static ThreadHelper instance = new ThreadHelper();
    }

    /**
     * 默认提交，poolName 可以为空，则提交至默认线程池 --不推荐使用
     * 如果提交至一个新的 poolName 如线程池管理Map 中不存在这个线程池，那会新建一个线程池实例
     * 建议不要使用太多线程池
     * <p>
     * 默认的核心线程池大小为80,后续添加线程池时默认的线程池核心线程数量不要超过50
     *
     * @param t
     */
    public <T extends Runnable> void submit(T t) {
        threadPoolsMap.get(DEFAULT_THREADPOOL).submit(t);
    }

    /**
     * 默认提交，poolName 可以为空，则提交至默认线程池 --不推荐使用
     * 如果提交至一个新的 poolName 如线程池管理Map 中不存在这个线程池，那会新建一个线程池实例
     * 建议不要使用太多线程池
     * <p>
     * 默认的核心线程池大小为80,后续添加线程池时默认的线程池核心线程数量不要超过50
     *
     * @param t
     */
    public <T extends Callable> Future futureSubmit(T t) {
        return threadPoolsMap.get(DEFAULT_THREADPOOL).submit(t);
    }

    /**
     * 关闭所有线程池，谨慎使用
     */
    public void destoryPool() {
        synchronized (threadPoolsMap) {
            Iterator<String> it = threadPoolsMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                threadPoolsMap.get(key).shutdown();
            }
        }
    }

    /**
     * 杀掉指定线程池名称的所有线程
     *
     * @param poolName
     */
    public void destoryPool(String poolName) {
        if (threadPoolsMap.get(poolName) != null)
            threadPoolsMap.get(poolName).shutdown();
    }

    /**
     * 获取对应线程池队列的长度
     *
     * @return
     */
    public int getQueneSize(String poolName) {
        ThreadPoolExecutor pool = threadPoolsMap.get(poolName);
        return pool.getQueue().size();
    }

}
