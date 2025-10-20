package com.cdwater.petmall.controller.manager;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.entity.Notice;
import com.cdwater.petmall.service.NoticeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerNoticeController")
@RequestMapping("/manager/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        noticeService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        noticeService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody Notice notice) {
        noticeService.edit(notice);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        Notice notice = noticeService.query(id);
        return Result.success(notice);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(Notice notice,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Notice> pageInfo = noticeService.page(notice, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}
