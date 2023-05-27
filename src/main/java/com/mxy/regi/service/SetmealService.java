package com.mxy.regi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.regi.dto.SetmealDto;
import com.mxy.regi.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐的同时保存套餐与菜品的关联
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐的同时删除相关菜品
     */
    public void removeWithDish(List<Long> ids);
}
