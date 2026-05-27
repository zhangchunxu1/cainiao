package com.example.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.employee.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {
    
    IPage<Announcement> getAnnouncementList(Integer page, Integer pageSize, String keyword);
}