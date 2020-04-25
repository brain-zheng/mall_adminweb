package com.mall.adminweb.manager;

import com.mall.adminweb.enums.MallOrderStatusEnum;
import com.mall.adminweb.request.MallOrderRequest;
import com.mall.adminweb.response.MallOrderItemVO;
import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.common.service.util.BeanUtil;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.ordercenter.client.dto.MallOrderDTO;
import com.mall.ordercenter.client.dto.MallOrderItemDTO;
import com.mall.ordercenter.client.dto.QueryOrderDTO;
import com.mall.ordercenter.client.service.MallOrderItemService;
import com.mall.ordercenter.client.service.MallOrderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MallOrderManager {

    @Resource
    private MallOrderService mallOrderService;

    @Resource
    private MallOrderItemService mallOrderItemService;

    public PageResult getMallOrdersPage(PageQueryUtil pageUtil, Map<String, Object> params){
        QueryOrderDTO queryOrderDTO = params2queryDTO(pageUtil, params);
        List<MallOrderDTO> mallOrders = mallOrderService.findNewBeeMallOrderList(queryOrderDTO);
        int total = mallOrderService.getTotalNewBeeMallOrders(queryOrderDTO);
        PageResult pageResult = new PageResult(mallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Transactional
    public String updateOrderInfo(MallOrderRequest mallOrderRequest) {
        MallOrderDTO temp = mallOrderService.selectByPrimaryKey(mallOrderRequest.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(mallOrderRequest.getTotalPrice());
            temp.setUserAddress(mallOrderRequest.getUserAddress());
            temp.setUpdateTime(new java.util.Date());
            if (mallOrderService.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    public List<MallOrderItemVO> getOrderItems(Integer id){
        MallOrderDTO mallOrder = mallOrderService.selectByPrimaryKey(id);
        if (mallOrder != null) {
            List<MallOrderItemDTO> orderItems = mallOrderItemService.selectByOrderId(mallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<MallOrderItemVO> mallOrderItemVOS = BeanUtil.copyList(orderItems, MallOrderItemVO.class);
                return mallOrderItemVOS;
            }
        }
        return null;
    }

    @Transactional
    public String checkDone(Integer[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrderDTO> orders = Arrays.stream(ids).map(mallOrderService::selectByPrimaryKey).collect(Collectors.toList());
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrderDTO newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (mallOrderService.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Transactional
    public String checkOut(Integer[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrderDTO> orders = Arrays.stream(ids).map(mallOrderService::selectByPrimaryKey).collect(Collectors.toList());
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrderDTO newBeeMallOrder : orders) {
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (newBeeMallOrder.getOrderStatus() != 1 && newBeeMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (mallOrderService.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Transactional
    public String closeOrder(Integer[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<MallOrderDTO> orders = Arrays.stream(ids).map(mallOrderService::selectByPrimaryKey).collect(Collectors.toList());
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (MallOrderDTO newBeeMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (newBeeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (newBeeMallOrder.getOrderStatus() == 4 || newBeeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += newBeeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (mallOrderService.closeOrder(Arrays.asList(ids), MallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    private QueryOrderDTO params2queryDTO(PageQueryUtil pageUtil, Map<String, Object> params) {
        QueryOrderDTO queryOrderDTO = new QueryOrderDTO();
        queryOrderDTO.setLimit(pageUtil.getLimit());
        queryOrderDTO.setStart(pageUtil.getStart());
        if (params.containsKey("orderNo") && !StringUtils.isEmpty(params.get("orderNo") + "")) {
            queryOrderDTO.setOrderNo(params.get("orderNo") + "");
        }
        if (params.containsKey("userId")) {
            queryOrderDTO.setUserId(Integer.valueOf(params.get("userId") + ""));
        }
        if (params.containsKey("payType")) {
            queryOrderDTO.setPayType(Integer.valueOf(params.get("payType") + ""));
        }
        if (params.containsKey("orderStatus")) {
            queryOrderDTO.setOrderStatus(Integer.valueOf(params.get("orderStatus") + ""));
        }
        if (params.containsKey("isDeleted")) {
            queryOrderDTO.setIsDeleted(Integer.valueOf(params.get("isDeleted") + ""));
        }
        if (params.containsKey("startTime")) {
            queryOrderDTO.setStartTime(Date.valueOf(params.get("startTime") + ""));
        }
        if (params.containsKey("endTime")) {
            queryOrderDTO.setEndTime(Date.valueOf(params.get("endTime") + ""));
        }
        return queryOrderDTO;
    }

}
