package com.mall.adminweb.controller;

import com.mall.admincenter.client.dto.AdminUserDTO;
import com.mall.admincenter.client.service.LoginRPCService;
import com.mall.adminweb.manager.AdminProfileManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zheng haijain
 * @createTime 2020-03-21 12:45
 * @description
 */
@Controller
@RequestMapping("/admin")
public class AdminProfileController {

    @Resource
    private LoginRPCService loginRPCService;

    @Resource
    private AdminProfileManager adminProfileManager;

    @GetMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer adminUserId = Integer.valueOf(request.getSession().getAttribute("loginUserId").toString());
        AdminUserDTO adminUser = loginRPCService.getAdminUserByUserId(adminUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request,
                                 @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
            return "参数不能为空";
        }
        Integer adminUserId = Integer.valueOf(request.getSession().getAttribute("loginUserId").toString());
        boolean updateSuccess = adminProfileManager.passwordUpdate(adminUserId, newPassword, originalPassword);
        if (updateSuccess) {
            //修改成功后清空session中的数据，前端控制跳转至登录页
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "success";
        } else {
            return "修改失败";
        }
    }

    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request,
                             @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName){
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)) {
            return "参数不能为空";
        }
        Integer adminUserId = Integer.valueOf(request.getSession().getAttribute("loginUserId").toString());
        if (adminProfileManager.updateName(adminUserId, loginUserName, nickName)) {
            return "success";
        } else {
            return "修改失败";
        }
    }
}
