package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Reimbursement;

import java.util.List;

public interface ReimbursementService extends IService<Reimbursement> {
    
    IPage<Reimbursement> getReimbursementList(Integer page, Integer pageSize, String keyword, List<Long> accessibleEmployeeIds);

    Reimbursement managerApprove(Long id, String approver, String remark);

    Reimbursement managerReject(Long id, String approver, String remark);

    Reimbursement financeApprove(Long id, String approver, String remark);

    Reimbursement financeReject(Long id, String approver, String remark);
}
