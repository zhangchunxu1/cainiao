package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Contract;

public interface ContractService extends IService<Contract> {

    IPage<Contract> getContractList(Integer page, Integer pageSize, String keyword, String status, String contractType);
}