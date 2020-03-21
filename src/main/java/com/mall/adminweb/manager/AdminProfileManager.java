package com.mall.adminweb.manager;

import com.mall.admincenter.client.dto.AdminUserDTO;
import com.mall.admincenter.client.service.LoginRPCService;
import com.mall.adminweb.util.MD5Util;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zheng haijain
 * @createTime 2020-03-21 13:13
 * @description
 */
@Component
public class AdminProfileManager {

    @Resource
    private LoginRPCService loginRPCService;

    public boolean passwordUpdate(Integer userId, String newPassWord, String originPassWord){
        if (userId == null){
            return false;
        }
        AdminUserDTO adminUser = loginRPCService.getAdminUserByUserId(userId);
        if (adminUser == null){
            return false;
        }
        String oldPassword = adminUser.getLoginPassword();
        String originalPasswordMd5 = MD5Util.MD5Encode(originPassWord, "UTF-8");
        String newPasswordMd5 = MD5Util.MD5Encode(newPassWord, "UTF-8");
        if (originalPasswordMd5.equals(oldPassword)){
            // 相等则设置新密码
            adminUser.setLoginPassword(newPasswordMd5);
            if (loginRPCService.udpateAdminUser(adminUser) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean updateName(Integer userId, String loginUserName, String nickName) {
        if (userId == null) {
            return false;
        }
        AdminUserDTO adminUser = loginRPCService.getAdminUserByUserId(userId);
        if (adminUser != null) {
            adminUser.setNickName(nickName);
            adminUser.setLoginUserName(loginUserName);
            if (loginRPCService.udpateAdminUser(adminUser) > 0) {
                return true;
            }
        }
        return false;
    }

}
