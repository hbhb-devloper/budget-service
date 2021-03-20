package com.hbhb.cw.budget.mapper;


import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.budget.model.BudgetItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetItemMapper extends BaseMapper<BudgetItem, Long> {

    List<SelectVO> selectAllItems();

    Long selectByItemName(@Param("itemName") String itemName);

    int insertBatch(List<BudgetItem> list);
}