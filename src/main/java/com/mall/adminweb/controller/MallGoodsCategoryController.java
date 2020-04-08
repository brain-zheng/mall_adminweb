package com.mall.adminweb.controller;

import com.mall.adminweb.enums.MallCategoryLevelEnum;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    @RequestMapping(value = "/categories/listForSelect", method = RequestMethod.GET)
    @ResponseBody
    public Result listForSelect(@RequestParam("categoryId") Integer categoryId){
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        GoodsCategoryDTO goodsCategoryDTO = mallCategoryService.selectByPrimaryKey(categoryId);
        // 既不是一级分类也不是二级分类
        if (goodsCategoryDTO == null || goodsCategoryDTO.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Map<String, List<GoodsCategoryResponse>> categoryResult = new HashMap<>(2);
        if (goodsCategoryDTO.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
            List<GoodsCategoryDTO> goodsCategoryTwoDTOS = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(goodsCategoryTwoDTOS)) {
                List<GoodsCategoryDTO> goodsCategoryThreeDTOS = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategoryTwoDTOS.get(0).getId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                if (goodsCategoryThreeDTOS == null || goodsCategoryThreeDTOS.size() == 0) {
                    return ResultGenerator.genFailResult("该分类下没有数据");
                }
                List<GoodsCategoryResponse> secondGoods = new ArrayList<>();
                List<GoodsCategoryResponse> thirdGoods = new ArrayList<>();
                for (GoodsCategoryDTO dto : goodsCategoryTwoDTOS) {
                    secondGoods.add(convert(dto));
                }
                for (GoodsCategoryDTO dto : goodsCategoryThreeDTOS) {
                    thirdGoods.add(convert(dto));
                }
                categoryResult.put("secondLevelCategories", secondGoods);
                categoryResult.put("thirdLevelCategories", thirdGoods);
            }
        }
        //如果是二级分类则返回当前分类下的所有三级分类列表
        if (goodsCategoryDTO.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            List<GoodsCategoryDTO> goodsCategoryThreeDTOS = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
            if (goodsCategoryThreeDTOS == null || goodsCategoryThreeDTOS.size() == 0) {
                return ResultGenerator.genFailResult("该分类下没有数据");
            }
            List<GoodsCategoryResponse> thirdGoods = new ArrayList<>();
            for (GoodsCategoryDTO dto : goodsCategoryThreeDTOS) {
                thirdGoods.add(convert(dto));
            }
            categoryResult.put("thirdLevelCategories", thirdGoods);
        }
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    /**
     * 详情
     */
    @GetMapping("/categories/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        GoodsCategoryDTO goodsCategory = mallCategoryService.selectByPrimaryKey(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(convert(goodsCategory));
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

}
