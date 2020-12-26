package com.hbhb.cw.budget.enums;

import lombok.Getter;

@Getter
public enum BudgetErrorCode {
    BUDGET_IMPORT_DATE_ERROR("60000", "budget.import.date.error"),
    BUDGET_DATA_IMPORT_ERROR("60001", "budget.data.import.error"),
    BUDGET_DATA_NAME_ERROR("60061", "budget.data.name.error"),
    BUDGET_DATA_PARSE_ERROR("60002", "budget.data.parse.error"),
    BUDGET_MAP_DATA_IMPORT_ERROR("60003", "budget.map.data.import.error"),
    BUDGET_DATA_NOT_NEGATIVE("60004", "budget.data.not.negative"),
    BUDGET_PROJECT_NOT_AT_RISK("60050", "budget.project.not.at.risk"),
    BUDGET_PROJECT_SPLIT_YEAR_NOT_NULL("60051", "budget.project.split.year.not.null"),
    BUDGET_PROJECT_SPLIT_EXCESS("60052", "budget.project.split.excess"),
    BUDGET_PROJECT_SPLIT_COST_ERROR("60055", "budget.project.split.cost.error"),
    BUDGET_PROJECT_FILE_DELETE_SUCCESSFUL("60060", "budget.project.file.delete.successful"),
    BUDGET_NO_DATA("60062", "budget.no.data"),
    BUDGET_PROJECT_SPLIT_YEAR_ERROR("60053", "budget.project.split.year.error"),
    BUDGET_PROJECT_INITIATOR_ERROR("60063", "budget.project.initiator.error"),
    BUDGET_DATA_HAVE_PROJECT("60054", "budget.data.have.project"),
    BUDGET_PROJECT_TIME_ERROR("60064", "budget.project.time.error"),
    INVOICE_DATA_IMPORT_ERROR("600065", "invoice.data.import.error"),
    LOCK_OF_INVOICE_ROLE("600066", "lock.of.invoice.role"),

    THE_PROJECT_IS_CONNECTED("50000", "the.project.is.connected"),
    THE_ITEM_IS_BUDGET("50001", "the.item.is.budget"),
    THE_ITEM_IS_REPEAT("50002", "the.item.is.repeat"),
    CATEGORY_BUDGET_NOT_WRITE("50003", "category.budget.not.write"),
    COST_EXCEED_SURPLUS("50004", "cost.exceed.surplus"),
    THE_PROJECT_NOT_BUDGET("50005", "the.project.not.budget"),
    BUDGET_NOT_ADD_PROJECT("50050", "budget.not.add.project"),


    THE_ROLE_IS_CONNECTED("40000", "the.role.is.connected"),
    NOT_EXIST_FLOW("40001", "not.exist.flow"),
    EXCEED_LIMIT_FLOW("40002", "exceed.limit.flow"),
    LACK_OF_FLOW("40003", "lack.of.flow"),
    LACK_OF_NODE_PROP("40004", "lack.of.node.prop"),
    NOT_REACHED_AMOUNT("40011", "not.reached.amount"),
    LACK_OF_FLOW_ROLE("40005", "lack.of.flow.role"),
    LOCK_OF_APPROVAL_ROLE("40006", "lock.of.approval.role"),
    NOT_ALL_APPROVERS_ASSIGNED("40007", "not.all.approvers.assigned"),
    NEXT_NODE_NO_USER("40008", "next.node.no.user"),
    LACK_OF_REVERT("40009", "lack.of.revert"),
    LACK_OF_REVERT_ROLE("40010", "lack.of.revert.role"),
    ;


    private String code;

    private String message;

    BudgetErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
