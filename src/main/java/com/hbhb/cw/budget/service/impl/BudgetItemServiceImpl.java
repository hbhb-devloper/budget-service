package com.hbhb.cw.budget.service.impl;


import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetItemMapper;
import com.hbhb.cw.budget.model.BudgetItem;
import com.hbhb.cw.budget.service.BudgetItemService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.web.vo.BudgetItemVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author yzc
 */
@Service
public class BudgetItemServiceImpl implements BudgetItemService {

    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetItemMapper budgetItemMapper;

    @Override
    public BudgetItemVO getBudgetItemById(Long budgetItemId) {
        budgetItemId = budgetItemId >> 10;
        BudgetItemVO budgetItemVO = new BudgetItemVO();
        BudgetItem budgetItem = budgetItemMapper.selectByPrimaryKey(budgetItemId);
        BeanUtils.copyProperties(budgetItem, budgetItemVO);
        return budgetItemVO;
    }

    @Override
    public void addBudgetItem(BudgetItemVO budgetItemVO) {
        BudgetItem budgetItem = new BudgetItem();
        BeanUtils.copyProperties(budgetItemVO, budgetItem);
        Long id = budgetItemMapper.selectByItemName(budgetItemVO.getItemName());
        if (id == null) {
            budgetItemMapper.insertSelective(budgetItem);
        } else {
            budgetItem.setId(id);
            budgetItem.setUpdateTime(new Date());
            budgetItemMapper.updateByPrimaryKeySelective(budgetItem);
        }
    }

    @Override
    public void deleteByItemId(Long budgetItemId) {
        budgetItemId = budgetItemId >> 10;
        Integer budgetCount = budgetService.getBudgetCount(budgetItemId);
        if (budgetCount != 0) {
            throw new BudgetException(BudgetErrorCode.THE_ITEM_IS_BUDGET);
        }
        budgetItemMapper.deleteByPrimaryKey(budgetItemId);
    }

    @Override
    public void updateByBudgetItemVO(BudgetItemVO budgetItemVO) {
        budgetItemVO.setId(budgetItemVO.getId() >> 10);
        BudgetItem budgetItem = new BudgetItem();
        BeanUtils.copyProperties(budgetItemVO, budgetItem);
        budgetItemMapper.updateByPrimaryKeySelective(budgetItem);
    }

    @Override
    public List<SelectVO> getBudgetItemList() {
        List<SelectVO> voList = budgetItemMapper.selectAllItems();
        for (SelectVO selectVO : voList) {
            selectVO.setId(selectVO.getId() << 10);
        }
        return voList;
    }

}