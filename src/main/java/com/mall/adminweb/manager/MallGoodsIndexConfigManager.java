package com.mall.adminweb.manager;

import com.mall.admincenter.client.dto.IndexConfigDTO;
import com.mall.admincenter.client.service.IndexConfigService;
import com.mall.adminweb.response.IndexConfigVO;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zheng haijain
 * @createTime 2020-04-13 14:05
 * @description
 */
@Component
public class MallGoodsIndexConfigManager {

    @Resource
    private IndexConfigService indexConfigService;

    public PageResult getConfigsPage(PageQueryUtil pageUtil, Integer configType){
        List<IndexConfigDTO> indexConfigDTOS = indexConfigService.findIndexConfigList(pageUtil, configType);
        int total = indexConfigService.getTotalIndexConfigs(pageUtil, configType);
        List<IndexConfigVO> indexConfigs = indexConfigDTOS.stream().map(MallGoodsIndexConfigManager::dto2vo).collect(Collectors.toList());
        return new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
    }


    public static IndexConfigVO dto2vo(IndexConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        IndexConfigVO indexConfigVO = new IndexConfigVO();
        indexConfigVO.setConfigName(dto.getConfigName());
        indexConfigVO.setConfigType(dto.getConfigType());
        indexConfigVO.setGoodsId(dto.getGoodsId());
        indexConfigVO.setRedirectUrl(dto.getRedirectUrl());
        indexConfigVO.setConfigRank(dto.getConfigRank());
        indexConfigVO.setIsDeleted(dto.getIsDeleted());
        indexConfigVO.setConfigId(dto.getId());
        indexConfigVO.setCreateTime(dto.getCreateTime());
        indexConfigVO.setUpdateTime(dto.getUpdateTime());
        return indexConfigVO;
    }

    public static IndexConfigDTO vo2dto(IndexConfigVO vo) {
        if (vo == null) {
            return null;
        }
        IndexConfigDTO indexConfigDTO = new IndexConfigDTO();
        indexConfigDTO.setId(vo.getConfigId());
        indexConfigDTO.setConfigName(vo.getConfigName());
        indexConfigDTO.setConfigType(vo.getConfigType());
        indexConfigDTO.setGoodsId(vo.getGoodsId());
        indexConfigDTO.setRedirectUrl(vo.getRedirectUrl());
        indexConfigDTO.setConfigRank(vo.getConfigRank());
        indexConfigDTO.setIsDeleted(vo.getIsDeleted());
        indexConfigDTO.setCreateTime(vo.getCreateTime());
        indexConfigDTO.setUpdateTime(vo.getUpdateTime());
        return indexConfigDTO;
    }

}
