package com.mall.adminweb.controller;

import com.mall.account.client.service.MallUserService;
import com.mall.adminweb.manager.MallOrderManager;
import com.mall.adminweb.request.MallOrderRequest;
import com.mall.adminweb.response.MallOrderItemVO;
import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.ordercenter.client.dto.MallOrderItemDTO;
import com.mall.ordercenter.client.service.MallOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class MallOrderController {

    @Resource
    private MallOrderManager mallOrderManager;


    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public String ordersPage(HttpServletRequest request) {
        request.setAttribute("path", "orders");
        return "admin/newbee_mall_order";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/orders/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Integer page = Integer.valueOf(params.get("page") + "");
        Integer limit = Integer.valueOf(params.get("limit") + "");
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        return ResultGenerator.genSuccessResult(mallOrderManager.getMallOrdersPage(pageUtil, params));
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/orders/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody MallOrderRequest newBeeMallOrder) {
        if (Objects.isNull(newBeeMallOrder.getTotalPrice())
                || Objects.isNull(newBeeMallOrder.getOrderId())
                || newBeeMallOrder.getOrderId() < 1
                || newBeeMallOrder.getTotalPrice() < 1
                || StringUtils.isEmpty(newBeeMallOrder.getUserAddress())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallOrderManager.updateOrderInfo(newBeeMallOrder);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/order-items/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        List<MallOrderItemVO> orderItems = mallOrderManager.getOrderItems(id);
        if (!CollectionUtils.isEmpty(orderItems)) {
            return ResultGenerator.genSuccessResult(orderItems);
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.POST)
    @ResponseBody
    public Result checkDone(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallOrderManager.checkDone(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result checkOut(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallOrderManager.checkOut(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = mallOrderManager.closeOrder(ids);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

}
