package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectAgileFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectAgileFileMapper extends BaseMapper<BudgetProjectAgileFile, Long> {


    int insertBatch(@Param("list") List<BudgetProjectAgileFile> list);

    BudgetProjectAgileFile selectAgileFileByFileId(@Param("fileId") Long fileId);

    int deleteByFileId(@Param("fileId") Long fileId);
}