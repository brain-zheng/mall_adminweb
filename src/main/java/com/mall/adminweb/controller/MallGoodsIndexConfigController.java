package com.mall.adminweb.controller;

import com.mall.admincenter.client.dto.IndexConfigDTO;
import com.mall.admincenter.client.service.IndexConfigService;
import com.mall.adminweb.enums.IndexConfigTypeEnum;
import com.mall.adminweb.manager.MallGoodsIndexConfigManager;
import com.mall.adminweb.request.IndexConfigRequest;
import com.mall.adminweb.response.IndexConfigVO;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.goodscenter.client.enums.ServiceResultEnum;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 19:16
 * @description
 */
@Controller
@RequestMapping("/admin")
public class MallGoodsIndexConfigController {

    @Resource
    private MallGoodsIndexConfigManager indexConfigManager;

    @Resource
    private IndexConfigService indexConfigService;

    @RequestMapping(value = "/indexConfigs", method = RequestMethod.GET)
    public String indexConfigsPage(HttpServletRequest request, @RequestParam("configType") int configType) {
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (IndexConfigTypeEnum.DEFAULT.equals(indexConfigTypeEnum)) {
            return "error/error_5xx";
        }
        request.setAttribute("path", indexConfigTypeEnum.getName());
        request.setAttribute("configType", configType);
        return "admin/newbee_mall_index_config";
    }


    /**
     * 列表
     */
    @RequestMapping(value = "/indexConfigs/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        int page = Integer.parseInt(params.get("page").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        int configType = Integer.parseInt(params.get("configType").toString());
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        PageResult pageResult = indexConfigManager.getConfigsPage(pageUtil, configType);
        return ResultGenerator.genSuccessResult(pageResult);
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/indexConfigs/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody IndexConfigRequest indexConfig) {
        if (Objects.isNull(indexConfig.getConfigType())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        IndexConfigDTO indexConfigDTO = request2dto(indexConfig);
        String result = indexConfigService.saveIndexConfig(indexConfigDTO);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/indexConfigs/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody IndexConfigRequest indexConfig) {
        if (Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigId())
                || StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        IndexConfigDTO indexConfigDTO = request2dto(indexConfig);
        String result = indexConfigService.updateIndexConfig(indexConfigDTO);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/indexConfigs/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id) {
        IndexConfigDTO config = indexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        IndexConfigVO indexConfigVO = MallGoodsIndexConfigManager.dto2vo(config);
        return ResultGenerator.genSuccessResult(indexConfigVO);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/indexConfigs/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (indexConfigService.deleteBatch(ids)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    public static IndexConfigDTO request2dto(IndexConfigRequest request) {
        if (request == null) {
            return null;
        }
        IndexConfigDTO indexConfigDTO = new IndexConfigDTO();
        indexConfigDTO.setId(request.getConfigId());
        indexConfigDTO.setConfigName(request.getConfigName());
        indexConfigDTO.setConfigType(request.getConfigType());
        indexConfigDTO.setGoodsId(request.getGoodsId());
        indexConfigDTO.setRedirectUrl(request.getRedirectUrl());
        indexConfigDTO.setConfigRank(request.getConfigRank());
        indexConfigDTO.setIsDeleted(request.getIsDeleted());
        indexConfigDTO.setCreateTime(request.getCreateTime());
        indexConfigDTO.setUpdateTime(request.getUpdateTime());
        return indexConfigDTO;
    }

}
