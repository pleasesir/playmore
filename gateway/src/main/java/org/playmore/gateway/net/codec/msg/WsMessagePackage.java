package org.playmore.gateway.net.codec.msg;

import lombok.Getter;

/**
 * @ClassName WsMessagePackage
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:05
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:05
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
public class WsMessagePackage {
    //协议类型标识
    private final byte cmdFlag;
    private final byte[] body;
    private Integer serverId;
    private int cmdId;

    public WsMessagePackage(byte cmdFlag, byte[] body) {
        this.cmdFlag = cmdFlag;
        this.body = body;
    }

    public WsMessagePackage(byte cmdFlag, byte[] body, Integer serverId) {
        this.cmdFlag = cmdFlag;
        this.body = body;
        this.serverId = serverId;
    }

    public WsMessagePackage setCmdId(int cmdId) {
        this.cmdId = cmdId;
        return this;
    }
}
