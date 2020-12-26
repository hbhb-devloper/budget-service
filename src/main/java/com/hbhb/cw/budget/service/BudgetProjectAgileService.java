package com.hbhb.cw.budget.service;

import com.hbhb.cw.model.BudgetProjectAgile;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.cw.web.vo.BudgetAgileAddVO;
import com.hbhb.cw.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.web.vo.BudgetProgressResVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileExportVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileInfoVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileReqVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileVO;
import com.hbhb.cw.web.vo.BudgetProjectAmountVO;
import com.hbhb.springboot.web.view.Page;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yzc
 * @since 2020-09-23
 */
public interface BudgetProjectAgileService {

    /**
     * 分页查询日常性费用签报
     */
    Page<BudgetProjectAgileVO> getBudgetAgileList(Integer pageNum, Integer pageSize, BudgetProjectAgileReqVO cond);

    /**
     * 新增常用性签报
     */
    void addSaveBudgetAgile(BudgetAgileAddVO cond, UserInfo user);

    /**
     * 通过id得到常用性签报详情
     */
    BudgetProjectAgileInfoVO getBudgetAgile(Long id);

    /**
     * 导出
     */
    List<BudgetProjectAgileExportVO> getDetailsExportByCond(BudgetProjectAgileReqVO cond);

    /**
     * 通过id删除
     */
    void deleteBudgetProject(Long id, UserInfo user);

    /**
     * 删除附件
     */
    void deleteAgileFile(Long fileId, UserInfo user);

    /**
     * 日常性签报费用统计
     */
    List<BudgetProjectAmountVO> getProgressByBudget(@Param("unitId") Integer unitId,
                                                    @Param(value = "budgetId") Long budgetId,
                                                    @Param(value = "year") String year);

    /**
     * 日常费用签报统计
     */
    BudgetProgressResVO getProgressByState(BudgetProgressReqVO cond);

    /**
     * 通过编号获得详情
     */
    BudgetProjectAgile getInfoByNum(String projectNum);

    /**
     * 日常性word导出
     */
    void export2Word(HttpServletResponse response, BudgetProjectAgileInfoVO vo);
}
