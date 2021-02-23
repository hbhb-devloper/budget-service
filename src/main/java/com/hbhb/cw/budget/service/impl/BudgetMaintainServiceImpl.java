package com.hbhb.cw.budget.service.impl;

import com.github.pagehelper.PageHelper;
import com.hbhb.cw.budget.mapper.BudgetProjectMapper;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.service.BudgetMaintainService;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2021-02-19
 */
@Service
@Slf4j
public class BudgetMaintainServiceImpl implements BudgetMaintainService {

    @Resource
    private BudgetProjectMapper budgetProjectMapper;

    @Override
    public Page<BudgetProjectResVO> getPageByProjectNum(Integer pageNum, Integer pageSize, boolean pageable, String projectNum) {
        if (pageable) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<BudgetProjectResVO> list = budgetProjectMapper.selectListByProjectNum(projectNum);
        int count = budgetProjectMapper.countListByProjectNum(projectNum);
        return new Page<>(list, (long) count);
    }

    @Override
    public void deleteBudgetProject(Integer id) {
        budgetProjectMapper.updateDeleteFlagById(id);
    }
}
