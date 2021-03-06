package com.kakarote.crm.common.log;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.core.common.FieldEnum;
import com.kakarote.core.common.log.BehaviorEnum;
import com.kakarote.core.common.log.Content;
import com.kakarote.core.entity.UserInfo;
import com.kakarote.core.feign.admin.entity.SimpleUser;
import com.kakarote.core.servlet.ApplicationContextHolder;
import com.kakarote.core.utils.BaseUtil;
import com.kakarote.core.utils.TagUtil;
import com.kakarote.core.utils.UserCacheUtil;
import com.kakarote.core.utils.UserUtil;
import com.kakarote.crm.common.ActionRecordUtil;
import com.kakarote.crm.constant.CrmEnum;
import com.kakarote.crm.entity.PO.CrmActionRecord;
import com.kakarote.crm.entity.PO.CrmCustomer;
import com.kakarote.crm.entity.VO.CrmModelFiledVO;
import com.kakarote.crm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class SysLogUtil {

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(10, 20, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2048), new ThreadPoolExecutor.AbortPolicy());


    public static class ActionRecordTask implements Runnable {
        private static final Integer BATCH_NUMBER = 1;
        private static volatile List<CrmActionRecord> SQL_LIST = new CopyOnWriteArrayList<>();
        private UserInfo userInfo;

        public ActionRecordTask(CrmActionRecord actionRecord) {
            if (actionRecord != null) {
                SQL_LIST.add(actionRecord);
            }
            userInfo = UserUtil.getUser();
        }

        @Override
        public void run() {
            if (SQL_LIST.size() >= BATCH_NUMBER) {
                List<CrmActionRecord> list = new ArrayList<>(SQL_LIST);
                //??????????????????size???0??????????????????????????????
                try {
                    UserUtil.setUser(userInfo);
                    ApplicationContextHolder.getBean(ICrmActionRecordService.class).saveBatch(list, BATCH_NUMBER);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    UserUtil.removeUser();
                    SQL_LIST.clear();
                }
            }
        }
    }


    private List<String> textList = new ArrayList<>();

    /**
     * ????????????
     *
     * @param oldObj  ????????????
     * @param newObj  ?????????
     * @param crmEnum ??????
     */
    @SuppressWarnings("unchecked")
    public Content updateRecord(Map<String, Object> oldObj, Map<String, Object> newObj, CrmEnum crmEnum, String name) {
        try {
            searchChange(textList, oldObj, newObj, crmEnum.getType());
            return new Content(name,StrUtil.join("", textList),BehaviorEnum.UPDATE);
        } finally {
            textList.clear();
        }

    }

    public Content addRecord(CrmEnum crmEnum, String name) {
        return new Content(name,"?????????" + crmEnum.getRemarks() + "???" + name,BehaviorEnum.SAVE);
    }

    @SuppressWarnings("unchecked")
    public void updateRecord(List<CrmModelFiledVO> newFieldList, Dict kv) {
        textList.clear();
        if (newFieldList == null) {
            return;
        }
        List<CrmModelFiledVO> oldFieldList = ApplicationContextHolder.getBean(ICrmActionRecordService.class).queryFieldValue(kv);
        newFieldList.forEach(newField -> {
            for (CrmModelFiledVO oldField : oldFieldList) {
                if (oldField.getFieldId().equals(newField.getFieldId())) {
                    if (ObjectUtil.isEmpty(oldField.getValue()) && ObjectUtil.isEmpty(newField.getValue())) {
                        continue;
                    }
                    if(Objects.equals(FieldEnum.parse(oldField.getType()),FieldEnum.DETAIL_TABLE)){
                        ActionRecordUtil.parseDetailTable(oldField.getValue(),newField.getValue(),oldField.getName(),oldField.getType(),textList);
                    }else{
                        String oldFieldValue = (String) ActionRecordUtil.parseValue(oldField.getValue(),oldField.getType(),true);
                        String newFieldValue = (String) ActionRecordUtil.parseValue(newField.getValue(),newField.getType(),true);
                        if (!oldFieldValue.equals(newFieldValue)) {
                            textList.add("???" + oldField.getName() + " ???" + oldFieldValue + "?????????" + newFieldValue + "???");
                        }
                    }
                }
            }
        });
    }

    private void searchChange(List<String> textList, Map<String, Object> oldObj, Map<String, Object> newObj, Integer crmTypes) {
        for (String oldKey : oldObj.keySet()) {
            for (String newKey : newObj.keySet()) {
                if (ActionRecordUtil.propertiesMap.get(crmTypes).containsKey(oldKey)) {
                    Object oldValue = oldObj.get(oldKey);
                    Object newValue = newObj.get(newKey);
                    if (oldValue instanceof Date) {
                        oldValue = DateUtil.formatDateTime((Date) oldValue);
                    }
                    if (newValue instanceof Date) {
                        newValue = DateUtil.formatDateTime((Date) newValue);
                    }
                    if (ObjectUtil.isEmpty(oldValue) || ("address".equals(oldKey) && ",,".equals(oldValue))) {
                        oldValue = "???";
                    }
                    if (ObjectUtil.isEmpty(newValue) || ("address".equals(newKey) && ",,".equals(newValue))) {
                        newValue = "???";
                    }
                    if (oldValue instanceof BigDecimal || newValue instanceof BigDecimal) {
                        oldValue = Convert.toBigDecimal(oldValue, new BigDecimal(0)).setScale(2, BigDecimal.ROUND_UP).toString();
                        newValue = Convert.toBigDecimal(newValue, new BigDecimal(0)).setScale(2, BigDecimal.ROUND_UP).toString();
                    }
                    if (newKey.equals(oldKey) && !Objects.equals(oldValue,newValue)) {
                        switch (oldKey) {
                            case "companyUserId":
                                if (!"???".equals(newValue)) {
                                    newValue = UserCacheUtil.getUserName(Long.valueOf(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = UserCacheUtil.getUserName(Long.valueOf(oldValue.toString()));
                                }
                                break;
                            case "customerId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmCustomerService.class).getCustomerName(Integer.valueOf(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmCustomerService.class).getCustomerName(Integer.valueOf(oldValue.toString()));
                                }
                                break;
                            case "businessId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmBusinessService.class).getBusinessName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmBusinessService.class).getBusinessName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "contractId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmContractService.class).getContractName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmContractService.class).getContractName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "contactsId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmContactsService.class).getContactsName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmContactsService.class).getContactsName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "typeId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmBusinessTypeService.class).getBusinessTypeName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmBusinessTypeService.class).getBusinessTypeName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "statusId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmBusinessStatusService.class).getBusinessStatusName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmBusinessStatusService.class).getBusinessStatusName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "receivablesPlanId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmReceivablesPlanService.class).getReceivablesPlanNum(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmReceivablesPlanService.class).getReceivablesPlanNum(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "categoryId":
                                if (!"???".equals(newValue)) {
                                    newValue = ApplicationContextHolder.getBean(ICrmProductCategoryService.class).getProductCategoryName(Integer.parseInt(newValue.toString()));
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = ApplicationContextHolder.getBean(ICrmProductCategoryService.class).getProductCategoryName(Integer.parseInt(oldValue.toString()));
                                }
                                break;
                            case "crmType":
                                if (!"???".equals(newValue)) {
                                    newValue = newValue.equals(1) ? "??????" : "??????";
                                }
                                if (!"???".equals(oldValue)) {
                                    oldValue = oldValue.equals(1) ? "??????" : "??????";
                                }
                                break;
                            case "relationUserId":
                                if (!"???".equals(newValue)) {
                                    List<SimpleUser> newList = UserCacheUtil.getSimpleUsers(TagUtil.toLongSet((String) newValue));
                                    newValue = newList.stream().map(SimpleUser::getRealname).collect(Collectors.joining(","));
                                }
                                if (!"???".equals(oldValue)) {
                                    List<SimpleUser> oldList = UserCacheUtil.getSimpleUsers(TagUtil.toLongSet((String) oldValue));
                                    oldValue = oldList.stream().map(SimpleUser::getRealname).collect(Collectors.joining(","));
                                }
                                break;
                            default:
                                break;
                        }

                        if (ObjectUtil.isEmpty(oldValue)) {
                            oldValue = "???";
                        }
                        if (ObjectUtil.isEmpty(newValue)) {
                            newValue = "???";
                        }
                        textList.add("???" + ActionRecordUtil.propertiesMap.get(crmTypes).get(oldKey) + " ???" + oldValue + "?????????" + newValue + "???");
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     */
    public Content addConversionRecord(CrmEnum crmEnum, Long userId, String name) {
        String userName = UserCacheUtil.getUserName(userId);
        return new Content(name,"???" + crmEnum.getRemarks() + "???" + name + "????????????" + userName,BehaviorEnum.CHANGE_OWNER);
    }

    @Autowired
    private ICrmCustomerService crmCustomerService;

    /**
     * ??????(??????/??????)??????
     */
    public List<Content> addIsLockRecord(List<String> ids, Integer isLock) {
        List<Content> contentList = new ArrayList<>();
        for (String actionId : ids) {
            String name = crmCustomerService.lambdaQuery().select(CrmCustomer::getCustomerName).eq(CrmCustomer::getCustomerId, actionId).one().getCustomerName();
            String detail;
            if (isLock == 2) {
                detail = "????????????" + name + "??????";
                contentList.add(new Content(name,detail,BehaviorEnum.LOCK));
            } else {
                detail = "????????????" + name + "??????";
                contentList.add(new Content(name,detail,BehaviorEnum.UNLOCK));
            }
        }
        return contentList;
    }



    public Content addDeleteActionRecord(CrmEnum crmEnum, String name) {
        return new Content(name,"?????????" + crmEnum.getRemarks() + "???" + name,BehaviorEnum.DELETE);
    }

    public Content addMemberActionRecord(CrmEnum crmEnum, Integer actionId, Long userId, String name) {
        String userName = UserCacheUtil.getUserName(userId);
        return new Content(name,"???" + crmEnum.getRemarks() + "???" + name + "????????????????????????" + userName);
    }

    public Content addDeleteMemberActionRecord(CrmEnum crmEnum, Long userId, boolean isSelf, String name) {
        if (isSelf) {
            return new Content(name,"?????????" + crmEnum.getRemarks() + "???" + name + "???????????????",BehaviorEnum.EXIT_MEMBER);
        } else {
            String userName = UserCacheUtil.getUserName(userId);
            return new Content(name,"?????????" + crmEnum.getRemarks() + "???" + name + "??????????????????" + userName,BehaviorEnum.REMOVE_MEMBER);
        }
    }

    public void addOaLogSaveRecord(CrmEnum crmEnum, Integer actionId) {
        CrmActionRecord actionRecord = new CrmActionRecord();
        actionRecord.setCreateUserId(UserUtil.getUserId());
        actionRecord.setCreateTime(new Date());
        actionRecord.setIpAddress(BaseUtil.getIp());
        actionRecord.setTypes(crmEnum.getType());
        actionRecord.setBehavior(BehaviorEnum.SAVE.getType());
        actionRecord.setActionId(actionId);
        actionRecord.setDetail("?????????" + crmEnum.getRemarks() + "???" + DateUtil.formatDate(new Date()));
        actionRecord.setObject(DateUtil.formatDate(new Date()));
        ActionRecordTask actionRecordTask = new ActionRecordTask(actionRecord);
        THREAD_POOL.execute(actionRecordTask);
    }

    public void addCrmExamineActionRecord(CrmEnum crmEnum, Integer actionId, BehaviorEnum behaviorEnum, String number) {
        CrmActionRecord actionRecord = new CrmActionRecord();
        actionRecord.setCreateUserId(UserUtil.getUserId());
        actionRecord.setCreateTime(new Date());
        actionRecord.setIpAddress(BaseUtil.getIp());
        actionRecord.setTypes(crmEnum.getType());
        actionRecord.setBehavior(behaviorEnum.getType());
        actionRecord.setActionId(actionId);
        String prefix = "";
        switch (behaviorEnum) {
            case SUBMIT_EXAMINE:
                prefix = "?????????";
                break;
            case RECHECK_EXAMINE:
                prefix = "?????????";
                break;
            case PASS_EXAMINE:
                prefix = "?????????";
                break;
            case REJECT_EXAMINE:
                prefix = "?????????";
                break;
            default:
                break;
        }
        actionRecord.setDetail(prefix + crmEnum.getRemarks() + "???" + number);
        actionRecord.setObject(number);
        ActionRecordTask actionRecordTask = new ActionRecordTask(actionRecord);
        THREAD_POOL.execute(actionRecordTask);
    }

    /**
     * ??????????????????????????????????????????????????????
     *
     * @param crmEnum
     * @param actionId
     * @param behaviorEnum
     */
    public void addObjectActionRecord(CrmEnum crmEnum, Integer actionId, BehaviorEnum behaviorEnum, String name) {
        CrmActionRecord actionRecord = new CrmActionRecord();
        actionRecord.setCreateUserId(UserUtil.getUserId());
        actionRecord.setCreateTime(new Date());
        actionRecord.setIpAddress(BaseUtil.getIp());
        actionRecord.setTypes(crmEnum.getType());
        actionRecord.setBehavior(behaviorEnum.getType());
        actionRecord.setActionId(actionId);
        String detail;
        switch (behaviorEnum) {
            case CANCEL_EXAMINE:
                detail = "???" + crmEnum.getRemarks() + "???" + name + "??????";
                break;
            case FOLLOW_UP:
                detail = "???" + crmEnum.getRemarks() + "???" + name + "?????????????????????";
                break;
            default:
                detail = behaviorEnum.getName() + "???" + crmEnum.getRemarks() + "???" + name;
                break;
        }
        actionRecord.setDetail(detail);
        actionRecord.setObject(name);
        ActionRecordTask actionRecordTask = new ActionRecordTask(actionRecord);
        THREAD_POOL.execute(actionRecordTask);
    }
}
