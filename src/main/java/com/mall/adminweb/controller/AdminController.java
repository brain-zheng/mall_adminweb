package com.mall.adminweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zheng haijain
 * @createTime 2020-03-19 14:40
 * @description
 */
@Controller
@RequestMapping("/admin")
public class AdminController  {

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request){
        // 传递模板数据，可以利用ModelAndView来传递数据
        request.setAttribute("path","index");
        return "admin/index";
    }

    @GetMapping("/category")
    public String category(HttpServletRequest request) {
        request.setAttribute("path", "category");
        return "admin/category";
    }

}
