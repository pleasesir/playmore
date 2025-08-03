package org.playmore.common.component;

/**
 * @ClassName AbsComponent
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:16
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:16
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface AbsComponent<T> {

    /**
     * 检查一个整数是否为2的幂。
     * <p>
     * 本方法通过位运算来判断一个整数是否为2的幂。具体的判断逻辑是，一个2的幂的二进制表示中只有一个位是1，
     * 其余位都是0。因此，对于任意的2的幂n，n与-n（n的补码，即所有位取反再加1）按位与的结果等于n本身。
     * 这是因为-n的二进制表示中，原来为1的位现在是0，而原来为0的位现在是1，按位与操作后，只有n本身的那个1位会得到保留。
     * 所以，如果(val & -val) == val，则说明val是2的幂；否则，val不是2的幂。
     *
     * @param val 待检查的整数
     * @return 如果val是2的幂，则返回true；否则返回false。
     */
    default boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }


    /**
     * 获取下一个可用的Disruptor对象。
     * <p>
     * 此方法用于从Disruptor池中获取下一个可用的Disruptor实例。Disruptor是一个高性能的事件处理框架，
     * 它通过使用环形缓冲区和生产者-消费者模式来提高事件处理的效率。在这个上下文中，调用者将获得一个
     * 已经准备好用于处理事件的Disruptor实例。
     *
     * @return 返回下一个可用的Disruptor实例。这个实例可以被用来发布事件或者处理已经发布的事件。
     */
    T next();
}
