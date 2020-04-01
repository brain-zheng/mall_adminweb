package com.mall.adminweb.controller;


import com.mall.adminweb.request.CarouselRequest;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.goodscenter.client.dto.CarouselDTO;
import com.mall.goodscenter.client.enums.ServiceResultEnum;
import com.mall.goodscenter.client.service.MallCarouselService;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author zheng haijain
 * @createTime 2020-03-22 12:28
 * @description
 */
@Controller
@RequestMapping("/admin")
public class AdminCarouselController {

    @Resource
    private MallCarouselService mallCarouselService;

    @GetMapping("/carousels")
    public String carouselPage(HttpServletRequest request){
        request.getSession().setAttribute("path", "mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    @GetMapping("/carousels/list")
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        PageResult pageResult = mallCarouselService.getCarouselPage(pageUtil);
        if (pageResult != null) {
            return ResultGenerator.genSuccessResult(pageResult);
        } else {
            return ResultGenerator.genFailResult("查询异常！");
        }
    }

    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody CarouselRequest carousel) {
        if (StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        // VO -> DTO
        CarouselDTO carouselDTO = converterCarouselRequest2DTO(carousel);
        String result = mallCarouselService.saveCarousel(carouselDTO);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     */
    @PostMapping(value = "/carousels/update")
    @ResponseBody
    public Result update(@RequestBody CarouselRequest carousel) {
        if (Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        // VO -> DTO
        CarouselDTO carouselDTO = converterCarouselRequest2DTO(carousel);
        String result = mallCarouselService.updateCarousel(carouselDTO);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        CarouselDTO carousel = mallCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (mallCarouselService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }


    public static CarouselDTO converterCarouselRequest2DTO(CarouselRequest request) {
        if (request == null) {
            return null;
        }
        CarouselDTO carouselDTO = new CarouselDTO();
        carouselDTO.setId(request.getCarouselId());
        carouselDTO.setCarouselUrl(request.getCarouselUrl());
        carouselDTO.setRedirectUrl(request.getRedirectUrl());
        carouselDTO.setCarouselRank(request.getCarouselRank());
        carouselDTO.setIsDeleted(request.getIsDeleted());
        carouselDTO.setCreateTime(request.getCreateTime());
        carouselDTO.setUpdateTime(request.getUpdateTime());
        return carouselDTO;
    }

}
