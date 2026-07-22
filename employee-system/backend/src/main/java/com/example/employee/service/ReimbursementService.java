package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Reimbursement;

public interface ReimbursementService extends IService<Reimbursement> {

    IPage<Reimbursement> getReimbursementList(Integer page, Integer pageSize, String keyword);

    Reimbursement managerApprove(Long id, String approver, String remark);

    Reimbursement managerReject(Long id, String approver, String remark);

    Reimbursement financeApprove(Long id, String approver, String remark);

    Reimbursement financeReject(Long id, String approver, String remark);
}