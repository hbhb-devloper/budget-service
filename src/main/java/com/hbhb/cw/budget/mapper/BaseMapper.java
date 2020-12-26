package com.hbhb.cw.budget.mapper;

import java.io.Serializable;

/**
 * @author xiaokang
 * @since 2020-07-12
 */
public interface BaseMapper<Model, PK extends Serializable> {
    int deleteByPrimaryKey(PK id);

    int insert(Model record);

    int insertSelective(Model record);

    Model selectByPrimaryKey(PK id);

    int updateByPrimaryKeySelective(Model record);

    int updateByPrimaryKey(Model record);
}
