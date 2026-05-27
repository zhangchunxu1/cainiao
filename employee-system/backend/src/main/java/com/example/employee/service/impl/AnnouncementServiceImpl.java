package com.example.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.employee.entity.Announcement;
import com.example.employee.mapper.AnnouncementMapper;
import com.example.employee.service.AnnouncementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public IPage<Announcement> getAnnouncementList(Integer page, Integer pageSize, String keyword) {
        Page<Announcement> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                .like(Announcement::getTitle, keyword)
                .or()
                .like(Announcement::getPublisher, keyword)
            );
        }
        
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}