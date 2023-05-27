package com.mxy.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.regi.common.CustomException;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.dto.DishDto;
import com.mxy.regi.entity.Category;
import com.mxy.regi.entity.Dish;
import com.mxy.regi.service.CateegoryService;
import com.mxy.regi.service.DishFlavorService;
import com.mxy.regi.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CateegoryService cateegoryService;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public JsonResult<String> save(@RequestBody  DishDto dishDto){

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return JsonResult.success("新增成功！");

    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public JsonResult<Page> page(int page, int pageSize,String name) {
        log.info("page = {} ,pageSize = {} , name = {}", page, pageSize);
        //构造分页查询器
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(name!=null , Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getCreateTime);

        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = cateegoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return JsonResult.success(pageInfo);
    }

    /**
     * 根据ID查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public JsonResult<DishDto> get(@PathVariable  Long id){

        DishDto dishDto = dishService.getbyIdWithFlavor(id);
        return JsonResult.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public JsonResult<String> update(@RequestBody DishDto dishDto){

       dishService.updateWithFlavor(dishDto);
        return JsonResult.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public JsonResult<List<Dish>> list(Dish dish){

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return JsonResult.success(list);
    }

//    /**
//     * 单个单品的起售与停售功能
//     * @param status
//     * @param ids
//     * @return
//     */
//    @PostMapping("/status/{status}")
//    public JsonResult<String> status(@PathVariable Integer status, Long ids) {
//        log.info("status:{},ids:{}", status, ids);
//        Dish dish = dishService.getById(ids);
//        if (dish != null) {
//            //直接用它传进来的这个status改就行
//            dish.setStatus(status);
//            dishService.updateById(dish);
//            return JsonResult.success("售卖状态修改成功");
//        }
//        return JsonResult.error("系统繁忙，请稍后再试");
//    }

    /**
     * 对菜品状态进行批量管理
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public JsonResult<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("status:{},ids:{}", status, ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, status);
        dishService.update(updateWrapper);
        return JsonResult.success("批量操作成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> delete(@RequestParam List<Long> ids) {
        log.info("删除的ids：{}", ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = dishService.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("删除列表中存在启售状态商品，无法删除");
        }
        dishService.removeByIds(ids);
        return JsonResult.success("删除成功");
    }
}
