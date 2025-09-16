package org.playmore.game.domain.entity.role;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName DbRole
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/12 22:56
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/12 22:56
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
@Getter
@Setter
public class DbRole extends RoleFunctionData {

    private final long roleId;

    private boolean needSave;

    public DbRole(long roleId) {
        super();
        this.roleId = roleId;
    }

}
