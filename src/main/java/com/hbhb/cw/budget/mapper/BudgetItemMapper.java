package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetItem;
import com.hbhb.cw.web.vo.SelectVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetItemMapper extends BaseMapper<BudgetItem, Long> {

    List<SelectVO> selectAllItems();

    Long selectByItemName(@Param("itemName") String itemName);

    int insertBatch(List<BudgetItem> list);
}