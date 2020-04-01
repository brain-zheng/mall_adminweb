package com.mall.adminweb.controller;

import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author zheng haijain
 * @createTime 2020-04-01 15:35
 * @description 商品分类后台管理
 */
@Controller
@RequestMapping("/admin")
public class MallGoodsCategoryController {

    @GetMapping("/catagories/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        return null;
    }

}
