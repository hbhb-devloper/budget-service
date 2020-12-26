package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetMonthCheck;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

@Document(collection = "BudgetMonthCheck")
public interface BudgetMonthCheckMapper extends MongoRepository<BudgetMonthCheck, String> {

}
