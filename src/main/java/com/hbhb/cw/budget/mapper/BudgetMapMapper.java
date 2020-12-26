package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetMap;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

@Document(collection = "budget")
public interface BudgetMapMapper extends MongoRepository<BudgetMap, String> {

}
