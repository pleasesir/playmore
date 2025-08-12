package org.playmore.common.pb;

/**
 * @ClassName GamePb
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 23:17
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 23:17
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface GamePb<T> {
    /**
     * 序列化数据
     *
     * @param saveDb 是否存入数据库
     * @return 数据库内pb数据
     * @see GamePb#deserialize(Object)
     */
    T serialize(boolean saveDb);

    /**
     * 反序列化数据
     *
     * @param pb 数据库内pb数据
     * @return 对象实体
     */
    GamePb<T> deserialize(T pb);
}
