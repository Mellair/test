package com.mxy.regi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.regi.dto.DishDto;
import com.mxy.regi.entity.Dish;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getbyIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
