package com.mxy.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.regi.common.CustomException;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.entity.Category;
import com.mxy.regi.entity.Dish;
import com.mxy.regi.entity.Setmeal;
import com.mxy.regi.mapper.CategoryMapper;
import com.mxy.regi.service.CateegoryService;
import com.mxy.regi.service.DishService;
import com.mxy.regi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CateegoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据ID删除分类
     * 删除条件 一：若分类关联其他 菜品 则无法删除
     * 删除条件 二：若分类关联其他 套餐 则无法删除
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishlambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //判断删除条件
        dishlambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishlambdaQueryWrapper);

        //计数大于零则说明有关联,需要抛出异常
        if(dishCount>0){
            throw new CustomException("当前分类下已关联相关菜品，不可删除！");
        }
        //判断删除条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int mealCount = setmealService.count(setmealLambdaQueryWrapper);
        //计数大于零则说明有关联,需要抛出异常
        if(mealCount>0){
            throw new CustomException("当前分类下已关联相关套餐，不可删除！");
        }
        super.removeById(id);
    }
}
