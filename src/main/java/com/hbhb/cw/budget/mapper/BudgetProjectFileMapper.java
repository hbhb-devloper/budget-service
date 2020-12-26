package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetProjectFile;
import com.hbhb.cw.web.vo.BudgetProjectFileVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectFileMapper extends BaseMapper<BudgetProjectFile, Long> {

    void insertBudgetProjectFiles(List<BudgetProjectFile> budgetProjectFile);

    void updateBudgetProjectFiles(List<BudgetProjectFileVO> budgetProjectFileVOS);

    void deleteByFileId(@Param("fileId") Long fileId);

    int selectCountByFileId(@Param("fileId") Long fileId);

    BudgetProjectFile selectProjectFileByFileId(@Param("fileId") Long fileId);

    List<Long> selectIdListByProjectId(@Param("projectId") Long projectId);
}