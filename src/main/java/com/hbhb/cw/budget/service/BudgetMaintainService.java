package com.hbhb.cw.budget.service;

import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;

/**
 * @author yzc
 * @since 2021-02-19
 */
public interface BudgetMaintainService {
    /**
     * 通过项目编号分页获取签报信息
     */
    Page<BudgetProjectResVO> getPageByProjectNum(Integer pageNum, Integer pageSize, boolean pageable,
                                                 String projectNum);

    /**
     * 管理员删除
     * 删除签报(只改变其删除状态并非清除数据)
     */
    void deleteBudgetProject(Integer id);
}
