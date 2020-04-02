package com.mall.adminweb.controller;

import com.mall.adminweb.manager.MallGoodsCategoryManager;
import com.mall.adminweb.request.GoodsCategoryRequest;
import com.mall.adminweb.response.GoodsCategoryResponse;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.goodscenter.client.dto.GoodsCategoryDTO;
import com.mall.goodscenter.client.enums.ServiceResultEnum;
import com.mall.goodscenter.client.service.MallCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zheng haijain
 * @createTime 2020-04-01 15:35
 * @description 商品分类后台管理
 */
@Controller
@RequestMapping("/admin")
public class MallGoodsCategoryController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsCategoryManager mallGoodsCategoryManager;

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request,
                                 @RequestParam("categoryLevel") Byte categoryLevel,
                                 @RequestParam("parentId") Integer parentId,
                                 @RequestParam("backParentId") Integer backParentId) {
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3) {
            return "error/error_5xx";
        }
        request.setAttribute("path", "newbee_mall_category");
        request.setAttribute("parentId", parentId);
        request.setAttribute("backParentId", backParentId);
        request.setAttribute("categoryLevel", categoryLevel);
        return "admin/newbee_mall_category";
    }

    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        System.out.println(1);
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit")) || StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        PageResult pageResult = mallCategoryService.getCategoriesPage(pageUtil,params.get("categoryLevel").toString(), params.get("parentId").toString());
        if (pageResult == null) {
            return ResultGenerator.genFailResult("查询异常！");
        }
        List<GoodsCategoryDTO> dtos = (List<GoodsCategoryDTO>) pageResult.getList();
        List<GoodsCategoryResponse> responses = new ArrayList<>();
        for (GoodsCategoryDTO dto : dtos){
            responses.add(convert(dto));
        }
        pageResult.setList(responses);
        return ResultGenerator.genSuccessResult(pageResult);
    }

    public static GoodsCategoryResponse convert(GoodsCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        GoodsCategoryResponse goodsCategoryResponse = new GoodsCategoryResponse();
        goodsCategoryResponse.setCategoryLevel(dto.getCategoryLevel());
        goodsCategoryResponse.setParentId(dto.getParentId());
        goodsCategoryResponse.setCategoryName(dto.getCategoryName());
        goodsCategoryResponse.setCategoryRank(dto.getCategoryRank());
        goodsCategoryResponse.setDeleted(dto.getDeleted());
        goodsCategoryResponse.setCategoryId(dto.getId());
        goodsCategoryResponse.setCreateTime(dto.getCreateTime());
        goodsCategoryResponse.setUpdateTime(dto.getUpdateTime());
        return goodsCategoryResponse;
    }

    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody GoodsCategoryRequest request){
        if (Objects.isNull(request.getCategoryLevel())
                || StringUtils.isEmpty(request.getCategoryName())
                || Objects.isNull(request.getParentId())
                || Objects.isNull(request.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallGoodsCategoryManager.saveCategory(request);

        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @PostMapping("/categories/update")
    @ResponseBody
    public Result update(@RequestBody GoodsCategoryRequest request){
        if (Objects.isNull(request.getCategoryId())
                || Objects.isNull(request.getCategoryLevel())
                || StringUtils.isEmpty(request.getCategoryName())
                || Objects.isNull(request.getParentId())
                || Objects.isNull(request.getCategoryRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        String result = mallGoodsCategoryManager.updateCategory(request);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }

    }

    @PostMapping("/categories/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (mallCategoryService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}
