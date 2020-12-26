package com.hbhb.cw.budget.service.impl;

import com.hbhb.cw.mapper.BudgetMapMapper;
import com.hbhb.cw.mapper.BudgetMonthCheckMapper;
import com.hbhb.cw.model.BudgetMap;
import com.hbhb.cw.model.BudgetMonthCheck;
import com.hbhb.cw.service.BudgetMapService;
import com.hbhb.cw.utils.BeanConverter;
import com.hbhb.cw.web.vo.BudgetMapVO;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

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