package com.mall.adminweb.manager;

import com.mall.adminweb.request.GoodsCategoryRequest;
import com.mall.goodscenter.client.dto.GoodsCategoryDTO;
import com.mall.goodscenter.client.enums.ServiceResultEnum;
import com.mall.goodscenter.client.service.MallCategoryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zheng haijain
 * @createTime 2020-04-01 19:23
 * @description
 */
@Component
public class MallGoodsCategoryManager {

    @Resource
    private MallCategoryService mallCategoryService;

    public String saveCategory(GoodsCategoryRequest goodsCategory) {
        GoodsCategoryDTO goodsCategoryDTO = mallCategoryService.selectByLevelAndName(goodsCategory.getCategoryLevel(),
                goodsCategory.getCategoryName());
        // 说明存在相同的类目
        if (goodsCategoryDTO != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }

        if (mallCategoryService.insertSelective(convert(goodsCategory)) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    public String updateCategory(GoodsCategoryRequest goodsCategory) {
        GoodsCategoryDTO temp = mallCategoryService.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategoryDTO temp2 = mallCategoryService.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (mallCategoryService.updateByPrimaryKeySelective(convert(goodsCategory)) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();

    }


    public static GoodsCategoryDTO convert(GoodsCategoryRequest request) {
        if (request == null) {
            return null;
        }
        GoodsCategoryDTO goodsCategoryDTO = new GoodsCategoryDTO();
        goodsCategoryDTO.setId(request.getCategoryId());
        goodsCategoryDTO.setCategoryLevel(0xFF & request.getCategoryLevel());
        goodsCategoryDTO.setParentId(request.getParentId());
        goodsCategoryDTO.setCategoryName(request.getCategoryName());
        goodsCategoryDTO.setCategoryRank(request.getCategoryRank());
        goodsCategoryDTO.setDeleted(0);
        return goodsCategoryDTO;
    }


}
