package com.mall.adminweb.controller;

import com.mall.admincenter.client.dto.AdminUserDTO;
import com.mall.admincenter.client.service.LoginRPCService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author zheng haijain
 * @createTime 2020-03-19 19:16
 * @description
 */
@Controller
@RequestMapping("/admin")
public class LoginController {

    @Resource
    private LoginRPCService loginRPCService;

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String passWord,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session){
        // 1. 参数校验
        if(StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }
        // 2. 验证码校验
        String kaptcha = session.getAttribute("verifyCode").toString();
        if (StringUtils.isEmpty(kaptcha) || !verifyCode.equals(kaptcha)){
            session.setAttribute("errorMsg", "验证码填写错误，请重试");
            return "admin/login";
        }
        // 3. 账户校验
        AdminUserDTO adminUserDTO = loginRPCService.getAdminUserByUserName(userName);
        if (adminUserDTO == null) {
            session.setAttribute("errorMsg", "不存在此用户");
            return "admin/login";
        }
        if (passWord.equals(adminUserDTO.getLoginPassword())){
            session.setAttribute("loginUser", adminUserDTO.getNickName());
            session.setAttribute("loginUserId", adminUserDTO.getId());
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg", "密码填写错误");
            return "admin/login";
        }
    }

}
