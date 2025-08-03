package org.playmore.api.account;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName AccountRoleDto
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/3 22:42
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/3 22:42
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Data
public class AccountRoleDto implements Serializable {
    

    @Serial
    private static final long serialVersionUID = 4559405005024296567L;

    /**
     * 账号唯一ID
     */
    private long accountKey;
    /**
     * 创角时的服务器ID
     */
    private int serverId;
    /**
     * 角色ID
     */
    private long lordId;
    /**
     * 角色名称
     */
    private String lordName;
    /**
     * 角色创建时间
     */
    private Date dateRoleCreate;
    /**
     * 战斗力
     */
    private long fight;
    /**
     * 赛季所属阵营
     */
    private int camp;

    public AccountRoleDto setAccountKey(long accountKey) {
        this.accountKey = accountKey;
        return this;
    }

    public AccountRoleDto setServerId(int serverId) {
        this.serverId = serverId;
        return this;
    }

    public AccountRoleDto setLordId(long lordId) {
        this.lordId = lordId;
        return this;
    }

    public AccountRoleDto setLordName(String lordName) {
        this.lordName = lordName;
        return this;
    }

    public AccountRoleDto setDateRoleCreate(Date dateRoleCreate) {
        this.dateRoleCreate = dateRoleCreate;
        return this;
    }

    public AccountRoleDto setFight(long fight) {
        this.fight = fight;
        return this;
    }

}