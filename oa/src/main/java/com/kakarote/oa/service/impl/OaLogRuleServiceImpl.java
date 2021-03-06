package com.kakarote.oa.service.impl;

import com.kakarote.core.feign.admin.entity.SimpleUser;
import com.kakarote.core.servlet.BaseServiceImpl;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.core.utils.UserCacheUtil;
import com.kakarote.oa.entity.PO.OaLogRule;
import com.kakarote.oa.mapper.OaLogRuleMapper;
import com.kakarote.oa.service.IOaLogRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyq
 * @since 2020-05-15
 */
@Service
public class OaLogRuleServiceImpl extends BaseServiceImpl<OaLogRuleMapper, OaLogRule> implements IOaLogRuleService {

    @Override
    public List<OaLogRule> queryOaLogRuleList() {
        List<OaLogRule> list = list();
        for (OaLogRule oaLogRule : list) {
            String memberUserId = oaLogRule.getMemberUserId();
            List<SimpleUser> data = UserCacheUtil.getSimpleUsers(TagUtil.toLongSet(memberUserId));
            oaLogRule.setMemberUser(data);
        }
        return list;
    }

    @Override
    public void setOaLogRule(List<OaLogRule> ruleList) {
        updateBatchById(ruleList);
    }
}
