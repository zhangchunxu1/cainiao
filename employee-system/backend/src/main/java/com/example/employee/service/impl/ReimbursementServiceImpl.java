package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Reimbursement;
import com.example.employee.mapper.ReimbursementMapper;
import com.example.employee.service.ReimbursementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReimbursementServiceImpl extends ServiceImpl<ReimbursementMapper, Reimbursement> implements ReimbursementService {

    @Override
    public IPage<Reimbursement> getReimbursementList(Integer page, Integer pageSize, String keyword, List<Long> accessibleEmployeeIds) {
        Page<Reimbursement> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Reimbursement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reimbursement::getDeleted, 0);

        if (accessibleEmployeeIds != null) {
            if (accessibleEmployeeIds.isEmpty()) {
                queryWrapper.eq(Reimbursement::getEmployeeId, -1L);
            } else {
                queryWrapper.in(Reimbursement::getEmployeeId, accessibleEmployeeIds);
            }
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Reimbursement::getEmployeeName, keyword)
                .or()
                .like(Reimbursement::getDepartment, keyword)
                .or()
                .like(Reimbursement::getReimbursementNo, keyword));
        }
        
        queryWrapper.orderByDesc(Reimbursement::getId);
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public Reimbursement managerApprove(Long id, String approver, String remark) {
        Reimbursement reimbursement = getById(id);
        if (reimbursement == null) {
            throw new RuntimeException("报销不存在");
        }
        if (!"待审批".equals(reimbursement.getStatus())) {
            throw new RuntimeException("当前状态不允许部门经理审批");
        }
        
        reimbursement.setStatus("待财务审批");
        reimbursement.setManagerApprover(approver);
        reimbursement.setManagerRemark(remark);
        reimbursement.setManagerApproveDate(LocalDate.now().toString());
        
        updateById(reimbursement);
        return reimbursement;
    }

    @Override
    public Reimbursement managerReject(Long id, String approver, String remark) {
        Reimbursement reimbursement = getById(id);
        if (reimbursement == null) {
            throw new RuntimeException("报销不存在");
        }
        if (!"待审批".equals(reimbursement.getStatus())) {
            throw new RuntimeException("当前状态不允许部门经理驳回");
        }
        
        reimbursement.setStatus("已驳回");
        reimbursement.setManagerApprover(approver);
        reimbursement.setManagerRemark(remark);
        reimbursement.setManagerApproveDate(LocalDate.now().toString());
        
        updateById(reimbursement);
        return reimbursement;
    }

    @Override
    public Reimbursement financeApprove(Long id, String approver, String remark) {
        Reimbursement reimbursement = getById(id);
        if (reimbursement == null) {
            throw new RuntimeException("报销不存在");
        }
        if (!"待财务审批".equals(reimbursement.getStatus())) {
            throw new RuntimeException("当前状态不允许财务审批");
        }
        
        reimbursement.setStatus("已审批");
        reimbursement.setFinanceApprover(approver);
        reimbursement.setFinanceRemark(remark);
        reimbursement.setFinanceApproveDate(LocalDate.now().toString());
        
        updateById(reimbursement);
        return reimbursement;
    }

    @Override
    public Reimbursement financeReject(Long id, String approver, String remark) {
        Reimbursement reimbursement = getById(id);
        if (reimbursement == null) {
            throw new RuntimeException("报销不存在");
        }
        if (!"待财务审批".equals(reimbursement.getStatus())) {
            throw new RuntimeException("当前状态不允许财务驳回");
        }
        
        reimbursement.setStatus("已驳回");
        reimbursement.setFinanceApprover(approver);
        reimbursement.setFinanceRemark(remark);
        reimbursement.setFinanceApproveDate(LocalDate.now().toString());
        
        updateById(reimbursement);
        return reimbursement;
    }
}
