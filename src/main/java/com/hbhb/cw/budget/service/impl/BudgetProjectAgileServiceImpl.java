package com.hbhb.cw.budget.service.impl;

import com.github.pagehelper.PageHelper;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetBelongMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectAgileFileMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectAgileMapper;
import com.hbhb.cw.budget.model.BudgetData;
import com.hbhb.cw.budget.model.BudgetProjectAgile;
import com.hbhb.cw.budget.model.BudgetProjectAgileFile;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.rpc.FileApiExp;
import com.hbhb.cw.budget.rpc.UnitApiExp;
import com.hbhb.cw.budget.service.BudgetDataService;
import com.hbhb.cw.budget.service.BudgetProgressService;
import com.hbhb.cw.budget.service.BudgetProjectAgileService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.web.vo.BudgetAgileAddVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressDeclareVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileExportWordVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileFileVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAmountVO;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.util.FileUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yzc
 * @since 2020-09-23
 */
@Service
public class BudgetProjectAgileServiceImpl implements BudgetProjectAgileService {

    @Resource
    private BudgetBelongMapper budgetBelongMapper;
    @Resource
    private BudgetProjectAgileMapper budgetProjectAgileMapper;
    @Resource
    private BudgetProjectAgileFileMapper budgetProjectAgileFileMapper;
    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetDataService budgetDataService;
    @Resource
    private BudgetProgressService budgetProgressService;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private FileApiExp fileApi;

    @Override
    public Page<BudgetProjectAgileVO> getBudgetAgileList(Integer pageNum, Integer pageSize, BudgetProjectAgileReqVO cond) {
        List<BudgetProjectAgileVO> list;
        int count;

        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        if (cond.getDate()==null){
            cond.setDate(cond.getImportDate());
        }
        if (UnitEnum.isBenbu(cond.getUnitId())) {
            List<Integer> unitIdByParentId = unitApi.getSubUnit(UnitEnum.BENBU.value());
            PageHelper.startPage(pageNum, pageSize);
            list = budgetProjectAgileMapper.selectParentAgileList(cond, unitIdByParentId);
            count = budgetProjectAgileMapper.countParentAgileList(cond, unitIdByParentId);
        } else {
            PageHelper.startPage(pageNum, pageSize);
            list = budgetProjectAgileMapper.selectAgileList(cond);
            count = budgetProjectAgileMapper.countAgileList(cond);
        }
        for (int i = 0; i < list.size(); i++) {
            int h = (pageNum - 1) * pageSize;
            list.get(i).setLineNumber(String.valueOf(h + i + 1));
        }
        return new Page<>(list, (long) count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSaveBudgetAgile(BudgetAgileAddVO cond, UserInfo user) {
        // ??????budgetNum?????????????????????budgetId
        Long budgetId = budgetService.getIdByNum(cond.getBudgetType(), cond.getImportDate());
        // ????????????????????????????????????????????????
        BudgetData budgetData = budgetDataService.getDataByUnitIdAndBudgetId(user.getUnitId(), budgetId);
        if (budgetData == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NOT_ADD_PROJECT);
        }
        // ??????????????????????????????????????????id
        Integer underUnitId = budgetBelongMapper.selectUnderUnitId(budgetId, user.getUnitId());
        String newUnitId;
        if (underUnitId == null) {
            throw new BudgetException(BudgetErrorCode.COST_EXCEED_SURPLUS);
        }
        // ?????????????????????????????????
        if (underUnitId != Math.toIntExact(user.getUnitId())) {
            // ????????????????????????????????????????????????
            newUnitId = underUnitId.toString();
        } else {
            newUnitId = user.getUnitId().toString();
        }
        BudgetProgressDeclareVO progressByState = budgetProgressService
                .getProgressByState(BudgetProgressReqVO.builder()
                        .budgetId(budgetId)
                        .unitId(Integer.valueOf(newUnitId))
                        .year(cond.getImportDate())
                        .build());
        // ?????????????????????0
        progressByState.setSurplus(progressByState.getSurplus() == null ? new BigDecimal("0.0") : progressByState.getSurplus());
        // ???????????????????????????
        if (cond.getCost().compareTo(progressByState.getSurplus()) > 0) {
            throw new BudgetException(BudgetErrorCode.COST_EXCEED_SURPLUS);
        }
        // ????????????=????????????+????????????+????????????+4???????????????
        String budgetNum = cond.getBudgetType();
        // ????????????
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        String abbr = unit.getAbbr();
        // ????????????
        String date = cond.getImportDate();
        // ??????????????????
        Integer count = budgetProjectAgileMapper.selectTypeCountByBudgetId(
                budgetId, user.getUnitId(), DateUtil.dateToStringY(new Date()));
        BudgetProjectAgile budgetProjectAgile = new BudgetProjectAgile();
        budgetProjectAgile.setBudgetId(budgetId);
        budgetProjectAgile.setCost(cond.getCost());
        budgetProjectAgile.setCreateBy(user.getNickName());
        budgetProjectAgile.setCreateTime(new Date());
        budgetProjectAgile.setProjectName(cond.getProjectName());
        budgetProjectAgile.setUnitId(user.getUnitId());
        budgetProjectAgile.setVatRate(cond.getVatRate());
        if (count == null) {
            count = 0;
        }
        String counts = String.format("%0" + 4 + "d", (count + 1));
        budgetProjectAgile.setProjectNum(budgetNum + abbr + date + counts);
        budgetProjectAgile.setTaxIncludeAmount(cond.getTaxIncludeAmount());
        budgetProjectAgileMapper.insertSelective(budgetProjectAgile);
        List<BudgetProjectAgileFileVO> files = cond.getFiles();
        if (files != null && files.size() != 0) {
            ArrayList<BudgetProjectAgileFile> agileFileList = new ArrayList<>();
            for (BudgetProjectAgileFileVO file : files) {
                agileFileList.add(BudgetProjectAgileFile.builder()
                        .author(user.getNickName())
                        .createTime(new Date())
                        .required(file.getRequired())
                        .projectAgileId(budgetProjectAgile.getId())
                        .fileId(file.getFileId())
                        .build());
            }
            budgetProjectAgileFileMapper.insertBatch(agileFileList);
        }
    }

    @Override
    public BudgetProjectAgileInfoVO getBudgetAgile(Long id) {
        return budgetProjectAgileMapper.selectAgileInfoById(id);
    }

    @Override
    public List<BudgetProjectAgileExportVO> getDetailsExportByCond(BudgetProjectAgileReqVO cond) {
        List<BudgetProjectAgileVO> list;
        if (UnitEnum.isHangzhou(cond.getUnitId())) {
            cond.setUnitId(null);
        }
        if (UnitEnum.isBenbu(cond.getUnitId())) {
            List<Integer> unitIdByParentId = unitApi.getSubUnit(UnitEnum.BENBU.value());
            list = budgetProjectAgileMapper.selectParentAgileList(cond, unitIdByParentId);
        } else {
            list = budgetProjectAgileMapper.selectAgileList(cond);
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setLineNumber(String.valueOf(i + 1));
        }
        return BeanConverter.copyBeanList(list, BudgetProjectAgileExportVO.class);
    }

    @Override
    public void deleteBudgetProject(Long id, UserInfo user) {
        BudgetProjectAgile budgetProjectAgile = budgetProjectAgileMapper.selectByPrimaryKey(id);
        if (!budgetProjectAgile.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        budgetProjectAgileMapper.updateDeleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAgileFile(Long fileId, UserInfo user) {
        BudgetProjectAgileFile budgetProjectAgileFile = budgetProjectAgileFileMapper.selectAgileFileByFileId(fileId);
        if (budgetProjectAgileFile == null) {
            fileApi.deleteFile(fileId);
        }
        if (budgetProjectAgileFile != null && !budgetProjectAgileFile.getAuthor().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        budgetProjectAgileFileMapper.deleteByFileId(fileId);
    }

    @Override
    public List<BudgetProjectAmountVO> getProgressByBudget(Integer unitId, Long budgetId, String year) {
        return budgetProjectAgileMapper.selectProgressByBudget(unitId, budgetId, year);
    }

    @Override
    public BudgetProgressResVO getProgressByState(BudgetProgressReqVO cond) {
        return budgetProjectAgileMapper.selectProgressByState(cond);
    }

    @Override
    public BudgetProjectAgile getInfoByNum(String projectNum) {
        return budgetProjectAgileMapper.selectInfoByNum(projectNum);
    }

    @Override
    public void export2Word(HttpServletResponse response, BudgetProjectAgileInfoVO vo) {
        BudgetProjectAgileExportWordVO result = new BudgetProjectAgileExportWordVO();
        BeanUtils.copyProperties(vo, result);
        result.setVatRate(String.valueOf(new BigDecimal(result.getVatRate()).multiply(new BigDecimal("100"))));

        String path = fileApi.getPath() + File.separator + result.getProjectName() + ".doc";
        // ??????????????????
        fileApi.fillTemplate(result, "???????????????????????????.ftl", path);
        // ????????????
        FileUtil.download(response, path, true);
    }
}

