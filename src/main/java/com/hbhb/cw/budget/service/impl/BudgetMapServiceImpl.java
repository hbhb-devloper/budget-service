package com.hbhb.cw.budget.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.budget.mapper.BudgetMapMapper;
import com.hbhb.cw.budget.mapper.BudgetMonthCheckMapper;
import com.hbhb.cw.budget.model.BudgetMap;
import com.hbhb.cw.budget.model.BudgetMonthCheck;
import com.hbhb.cw.budget.service.BudgetMapService;
import com.hbhb.cw.budget.web.vo.BudgetMapVO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
@Service
public class BudgetMapServiceImpl implements BudgetMapService {

    @Resource
    private BudgetMapMapper budgetMapMapper;
    @Resource
    private BudgetMonthCheckMapper budgetMonthCheckMapper;
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<BudgetMapVO> getBudgetMapList() {
        List<BudgetMap> list = budgetMapMapper.findAll();
        return BeanConverter.copyBeanList(list, BudgetMapVO.class);
    }

    @Override
    public void saveBudgetMap(List<BudgetMap> list) {
        mongoTemplate.insert(list, BudgetMap.class);
    }

    @Override
    public List<BudgetMonthCheck> getMonthCheckList() {
        return budgetMonthCheckMapper.findAll();
    }

    @Override
    public void saveMonthCheck(List<BudgetMonthCheck> list) {
        mongoTemplate.insert(list, BudgetMonthCheck.class);
    }

}