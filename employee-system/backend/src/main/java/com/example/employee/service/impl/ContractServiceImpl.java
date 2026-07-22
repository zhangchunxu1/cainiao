package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Contract;
import com.example.employee.mapper.ContractMapper;
import com.example.employee.service.ContractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    @Override
    public IPage<Contract> getContractList(Integer page, Integer pageSize, String keyword, String status, String contractType) {
        Page<Contract> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(Contract::getStatus, status);
        }

        if (StringUtils.isNotBlank(contractType)) {
            wrapper.eq(Contract::getContractType, contractType);
        }

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Contract::getContractNo, keyword)
                    .or()
                    .like(Contract::getContractName, keyword)
                    .or()
                    .like(Contract::getPartyA, keyword)
                    .or()
                    .like(Contract::getPartyB, keyword)
                    .or()
                    .like(Contract::getEmployeeName, keyword)
            );
        }

        wrapper.orderByDesc(Contract::getCreatedTime);
        return this.page(pageParam, wrapper);
    }
}