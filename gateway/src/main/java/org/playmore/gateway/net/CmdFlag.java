package org.playmore.gateway.net;

/**
 * @ClassName CmdFlag
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:29
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:29
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public interface CmdFlag {
    byte NORMAL_MSG = 0;
    byte CONFIRM_CMD = 1;
    byte FIRST_GAME_MSG = 2;
    byte HEART_BEAT = 3;
}
