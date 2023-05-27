package com.mxy.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.entity.Category;

import com.mxy.regi.service.CateegoryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CateegoryService cateegoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public JsonResult<String> save(@RequestBody Category category) {
        log.info("category:{}", category);
        cateegoryService.save(category);
        return JsonResult.success("新增成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public JsonResult<Page> page(int page, int pageSize) {
        log.info("page = {} ,pageSize = {} , name = {}", page, pageSize);
        //构造分页查询器
        Page<Category> pageInfo = new Page(page, pageSize);

        //构造条件查询器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //排序条件
        queryWrapper.orderByDesc(Category::getSort);

        cateegoryService.page(pageInfo, queryWrapper);

        return JsonResult.success(pageInfo);
    }

    /**
     * 根据ID删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> delete(Long ids) {
        log.info("删除分类：{}", ids);
        cateegoryService.remove(ids);
        return JsonResult.success("删除成功");
    }

    /**
     * 根据ID修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public JsonResult<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);
        cateegoryService.updateById(category);
        return JsonResult.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public JsonResult<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType() != null , Category::getType ,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        List<Category> list = cateegoryService.list(lambdaQueryWrapper);
        return JsonResult.success(list);
    }
}
