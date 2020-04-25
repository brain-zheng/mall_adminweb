package com.mall.adminweb.controller;

import com.mall.account.client.service.MallUserService;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class MallUserController {

    @Resource
    private MallUserService mallUserService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String usersPage(HttpServletRequest request) {
        request.setAttribute("path", "users");
        return "admin/newbee_mall_user";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/users/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Integer page = Integer.valueOf(params.get("page") + "");
        Integer limit = Integer.valueOf(params.get("limit") + "");
        String loginName = null;
        if (params.containsKey("loginName") && !StringUtils.isEmpty(params.get("loginName"))) {
            loginName = params.get("loginName") + "";
        }
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        return ResultGenerator.genSuccessResult(mallUserService.getMallUsersPage(pageUtil, loginName));
    }

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     */
    @RequestMapping(value = "/users/lock/{lockStatus}", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids, @PathVariable int lockStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus != 1) {
            return ResultGenerator.genFailResult("操作非法！");
        }
        if (mallUserService.lockUsers(ids, lockStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("禁用失败");
        }
    }

}
