package com.mxy.regi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mxy.regi.common.CustomException;
import com.mxy.regi.dto.SetmealDto;
import com.mxy.regi.entity.Setmeal;
import com.mxy.regi.entity.SetmealDish;
import com.mxy.regi.mapper.SetmealMapper;
import com.mxy.regi.service.SetmealDishService;
import com.mxy.regi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐的同时保存套餐与菜品的关联
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息 -> setmeal ,执行insert
        this.save(setmealDto);

        
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return  item;
        }).collect(Collectors.toList());

        //保存套餐与菜品关联信息， -> setmealDish , 执行Insert
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐的同时删除相关菜品
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //首先查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        //无法删除则抛出异常
        if(count > 0){
            throw new CustomException("套餐售卖中，无法下架");
        }
        //可以删除，则删除套餐中数据 --- setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getId,ids);

        setmealDishService.remove(lambdaQueryWrapper);


    }
}
