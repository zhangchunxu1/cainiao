package com.example.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.employee.common.Result;
import com.example.employee.entity.Announcement;
import com.example.employee.service.AnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "公告管理")
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @ApiOperation("获取公告列表")
    @GetMapping
    public Result<IPage<Announcement>> getAnnouncementList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        IPage<Announcement> result = announcementService.getAnnouncementList(page, pageSize, keyword);
        return Result.success(result);
    }

    @ApiOperation("根据ID获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcement);
    }

    @ApiOperation("添加公告")
    @PostMapping
    public Result<Announcement> addAnnouncement(@Valid @RequestBody Announcement announcement) {
        boolean success = announcementService.save(announcement);
        if (success) {
            return Result.success(announcement);
        }
        return Result.error("添加失败");
    }

    @ApiOperation("更新公告")
    @PutMapping("/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody Announcement announcement) {
        announcement.setId(id);
        boolean success = announcementService.updateById(announcement);
        if (success) {
            return Result.success(announcement);
        }
        return Result.error("更新失败");
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAnnouncement(@PathVariable Long id) {
        boolean success = announcementService.removeById(id);
        if (success) {
            return Result.success(true);
        }
        return Result.error("删除失败");
    }
}