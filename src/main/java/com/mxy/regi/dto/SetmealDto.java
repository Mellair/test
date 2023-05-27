package com.mxy.regi.dto;


import com.mxy.regi.entity.Setmeal;
import com.mxy.regi.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}