package org.playmore.api.disruptor.queue;

import org.playmore.api.disruptor.task.BaseTask;

import java.io.Serial;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName ConcurrentArrayQueue
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/7/31 22:52
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/7/31 22:52
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class ConcurrentArrayQueue<E extends BaseTask> extends ArrayDeque<E> {

    @Serial
    private static final long serialVersionUID = -347387570791748926L;
    private final ReentrantLock lock = new ReentrantLock();

    public ConcurrentArrayQueue() {
    }

    /**
     * 添加一个元素，并返回队列长度
     *
     * @param e 添加的元素
     * @return 队列长度
     */
    @SuppressWarnings("unchecked")
    public int addOne(E e) {
        e.setQueue((Queue<BaseTask>) this);
        lock.lock();
        try {
            super.add(e);
            return size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 删除一个元素，并返回队列中第一个元素
     *
     * @return 队列中第一个元素
     */
    public E pollAndPeek() {
        lock.lock();
        try {
            super.poll();
            return super.peek();
        } finally {
            lock.unlock();
        }
    }
}
