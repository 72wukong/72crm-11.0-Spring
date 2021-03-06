package com.kakarote.crm.service;

import com.kakarote.core.servlet.BaseService;
import com.kakarote.crm.common.CrmModel;
import com.kakarote.crm.entity.PO.CrmReceivablesPlanData;
import com.kakarote.crm.entity.VO.CrmModelFiledVO;

import java.util.List;

/**
 * <p>
 * 回款计划自定义字段存值表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-05-21
 */
public interface ICrmReceivablesPlanDataService extends BaseService<CrmReceivablesPlanData> {

    /**
     * 设置用户数据
     * @param crmModel crmModel
     */
    public void setDataByBatchId(CrmModel crmModel);

    /**
     * 保存自定义字段数据
     * @param array data
     * @param batchId batchId
     */
    public void saveData(List<CrmModelFiledVO> array, String batchId);

    /**
     * 通过batchId删除数据
     * @param batchId data
     */
    public void deleteByBatchId(List<String> batchId);
}
