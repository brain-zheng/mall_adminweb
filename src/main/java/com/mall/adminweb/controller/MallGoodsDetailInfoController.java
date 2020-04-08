package com.mall.adminweb.controller;

import com.mall.adminweb.common.Constants;
import com.mall.adminweb.enums.MallCategoryLevelEnum;
import com.mall.adminweb.request.MallGoodInfoRequest;
import com.mall.adminweb.response.GoodsCategoryResponse;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.goodscenter.client.dto.GoodsCategoryDTO;
import com.mall.goodscenter.client.dto.MallGoodsInfoDTO;
import com.mall.goodscenter.client.dto.MallGoodsPageDTO;
import com.mall.goodscenter.client.enums.ServiceResultEnum;
import com.mall.goodscenter.client.service.MallCategoryService;
import com.mall.goodscenter.client.service.MallGoodsInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zheng haijain
 * @createTime 2020-04-06 16:35
 * @description
 */
@Controller
@RequestMapping("/admin")
public class MallGoodsDetailInfoController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsInfoService mallGoodsInfoService;

    @RequestMapping(value = "/goods", method = RequestMethod.GET)
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    /**
     * 返回视图前设置 path 字段和分类数据，因为商品信息添加页面需要进行分类选择，
     * 所以需要把分类的三级联动初始化数据也查询并设置到 request 域中
     * @param request
     * @return
     */
    @RequestMapping(value = "/goods/edit", method = RequestMethod.GET)
    public String edit(HttpServletRequest request){
        request.setAttribute("path", "edit");
        // 查询所有一级分类
        List<GoodsCategoryDTO> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategoryDTO> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategoryDTO> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                List<GoodsCategoryResponse> firstGoods = new ArrayList<>();
                List<GoodsCategoryResponse> secondGoods = new ArrayList<>();
                List<GoodsCategoryResponse> thirdGoods = new ArrayList<>();
                for (GoodsCategoryDTO dto : firstLevelCategories) {
                    firstGoods.add(MallGoodsCategoryController.convert(dto));
                }
                for (GoodsCategoryDTO dto : secondLevelCategories) {
                    secondGoods.add(MallGoodsCategoryController.convert(dto));
                }
                for (GoodsCategoryDTO dto : thirdLevelCategories) {
                    thirdGoods.add(MallGoodsCategoryController.convert(dto));
                }
                request.setAttribute("firstLevelCategories", firstGoods);
                request.setAttribute("secondLevelCategories", secondGoods);
                request.setAttribute("thirdLevelCategories", thirdGoods);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result saveGoods(@RequestBody MallGoodInfoRequest request){
        if (StringUtils.isEmpty(request.getGoodsName())
                || StringUtils.isEmpty(request.getGoodsIntro())
                || StringUtils.isEmpty(request.getTag())
                || Objects.isNull(request.getOriginalPrice())
                || Objects.isNull(request.getGoodsCategoryId())
                || Objects.isNull(request.getSellingPrice())
                || Objects.isNull(request.getStockNum())
                || Objects.isNull(request.getGoodsSellStatus())
                || StringUtils.isEmpty(request.getGoodsCoverImg())
                || StringUtils.isEmpty(request.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallGoodsInfoService.saveMallGoods(request2DTO(request));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody MallGoodInfoRequest request) {
        if (Objects.isNull(request.getGoodsId())
                || StringUtils.isEmpty(request.getGoodsName())
                || StringUtils.isEmpty(request.getGoodsIntro())
                || StringUtils.isEmpty(request.getTag())
                || Objects.isNull(request.getOriginalPrice())
                || Objects.isNull(request.getSellingPrice())
                || Objects.isNull(request.getGoodsCategoryId())
                || Objects.isNull(request.getStockNum())
                || Objects.isNull(request.getGoodsSellStatus())
                || StringUtils.isEmpty(request.getGoodsCoverImg())
                || StringUtils.isEmpty(request.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallGoodsInfoService.updateMallGoods(request2DTO(request));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/goods/edit/{goodsId}", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Integer goodsId){
        request.setAttribute("path", "edit");
        MallGoodsInfoDTO goodsInfo = mallGoodsInfoService.getById(goodsId);
        if (goodsInfo == null) {
            return "error/error_400";
        }
        if (goodsInfo.getGoodsCategoryId() > 0) {
            if (goodsInfo.getGoodsCategoryId() != null || goodsInfo.getGoodsCategoryId() > 0) {
                GoodsCategoryDTO goodsCategoryDTO = mallCategoryService.selectByPrimaryKey(goodsInfo.getGoodsCategoryId());
                if (goodsCategoryDTO != null && goodsCategoryDTO.getCategoryLevel() == MallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategoryDTO> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategoryDTO> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(goodsCategoryDTO.getParentId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategoryDTO secondCategory = mallCategoryService.selectByPrimaryKey(goodsCategoryDTO.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategoryDTO> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategoryDTO firestCategory = mallCategoryService.selectByPrimaryKey(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            List<GoodsCategoryResponse> firstResponse = new ArrayList<>();
                            for (GoodsCategoryDTO dto : firstLevelCategories) {
                                firstResponse.add(MallGoodsCategoryController.convert(dto));
                            }
                            request.setAttribute("firstLevelCategories", firstResponse);
                            List<GoodsCategoryResponse> secondResponse = new ArrayList<>();
                            for (GoodsCategoryDTO dto : secondLevelCategories) {
                                secondResponse.add(MallGoodsCategoryController.convert(dto));
                            }
                            request.setAttribute("secondLevelCategories", secondResponse);
                            List<GoodsCategoryResponse> thirdResponse = new ArrayList<>();
                            for (GoodsCategoryDTO dto : thirdLevelCategories) {
                                thirdResponse.add(MallGoodsCategoryController.convert(dto));
                            }
                            request.setAttribute("thirdLevelCategories", thirdResponse);

                            request.setAttribute("firstLevelCategoryId", firestCategory.getId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getId());
                            request.setAttribute("thirdLevelCategoryId", goodsCategoryDTO.getId());
                        }
                    }
                }
            }
        }
        if (goodsInfo.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategoryDTO> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), MallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategoryDTO> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getId()), MallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategoryDTO> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getId()), MallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    List<GoodsCategoryResponse> firstResponse = new ArrayList<>();
                    for (GoodsCategoryDTO dto : firstLevelCategories) {
                        firstResponse.add(MallGoodsCategoryController.convert(dto));
                    }
                    request.setAttribute("firstLevelCategories", firstResponse);
                    List<GoodsCategoryResponse> secondResponse = new ArrayList<>();
                    for (GoodsCategoryDTO dto : secondLevelCategories) {
                        secondResponse.add(MallGoodsCategoryController.convert(dto));
                    }
                    request.setAttribute("secondLevelCategories", secondResponse);
                    List<GoodsCategoryResponse> thirdResponse = new ArrayList<>();
                    for (GoodsCategoryDTO dto : thirdLevelCategories) {
                        thirdResponse.add(MallGoodsCategoryController.convert(dto));
                    }
                    request.setAttribute("thirdLevelCategories", thirdResponse);
                }
            }
        }
        request.setAttribute("goods", goodsInfo);
        request.setAttribute("path", "goods-edit");
        return "admin/newbee_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        MallGoodsPageDTO pageDTO = new MallGoodsPageDTO();
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        int start = (page - 1) * limit;
        pageDTO.setPage(page);
        pageDTO.setLimit(limit);
        pageDTO.setStart(start);
        return ResultGenerator.genSuccessResult(mallGoodsInfoService.getNewBeeMallGoodsPage(pageDTO));
    }

    @RequestMapping(value = "/goods/info/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        MallGoodsInfoDTO goods = mallGoodsInfoService.getById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (mallGoodsInfoService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }


    public static MallGoodsInfoDTO request2DTO(MallGoodInfoRequest request) {
        if (request == null) {
            return null;
        }
        MallGoodsInfoDTO mallGoodsInfoDTO = new MallGoodsInfoDTO();
        mallGoodsInfoDTO.setId(request.getGoodsId());
        mallGoodsInfoDTO.setGoodsName(request.getGoodsName());
        mallGoodsInfoDTO.setGoodsIntro(request.getGoodsIntro());
        mallGoodsInfoDTO.setGoodsCategoryId(request.getGoodsCategoryId());
        mallGoodsInfoDTO.setGoodsCoverImg(request.getGoodsCoverImg());
        mallGoodsInfoDTO.setGoodsCarousel(request.getGoodsCarousel());
        mallGoodsInfoDTO.setOriginalPrice(request.getOriginalPrice());
        mallGoodsInfoDTO.setSellingPrice(request.getSellingPrice());
        mallGoodsInfoDTO.setStockNum(request.getStockNum());
        mallGoodsInfoDTO.setTag(request.getTag());
        mallGoodsInfoDTO.setGoodsSellStatus(request.getGoodsSellStatus());
        mallGoodsInfoDTO.setCreateTime(request.getCreateTime());
        mallGoodsInfoDTO.setUpdateTime(request.getUpdateTime());
        mallGoodsInfoDTO.setGoodsDetailContent(request.getGoodsDetailContent());
        return mallGoodsInfoDTO;
    }



}
