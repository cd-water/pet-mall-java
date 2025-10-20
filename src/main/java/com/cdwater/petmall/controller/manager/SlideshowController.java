package com.cdwater.petmall.controller.manager;

import com.cdwater.petmall.common.Result;
import com.cdwater.petmall.model.dto.SlideshowPageDTO;
import com.cdwater.petmall.entity.Slideshow;
import com.cdwater.petmall.model.vo.SlideshowPageVO;
import com.cdwater.petmall.model.vo.SlideshowQueryVO;
import com.cdwater.petmall.service.SlideshowService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("managerSlideshowController")
@RequestMapping("/manager/slideshow")
public class SlideshowController {

    @Resource
    private SlideshowService slideshowService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Slideshow slideshow) {
        slideshowService.add(slideshow);
        return Result.success();
    }

    /**
     * 单个删除
     */
    @DeleteMapping("/remove/{id}")
    public Result removeOne(@PathVariable Integer id) {
        slideshowService.removeOne(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/remove/batch")
    public Result removeBatch(@RequestBody List<Integer> ids) {
        slideshowService.removeBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/edit")
    public Result edit(@RequestBody Slideshow slideshow) {
        slideshowService.edit(slideshow);
        return Result.success();
    }

    /**
     * id查询
     */
    @GetMapping("/query/{id}")
    public Result query(@PathVariable Integer id) {
        SlideshowQueryVO slideshowQueryVO = slideshowService.query(id);
        return Result.success(slideshowQueryVO);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result page(SlideshowPageDTO slideshowPageDTO) {
        PageInfo<SlideshowPageVO> pageInfo = slideshowService.page(slideshowPageDTO);
        return Result.success(pageInfo);
    }
}
