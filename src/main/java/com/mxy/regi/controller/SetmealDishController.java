package com.mxy.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.regi.common.CustomException;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.dto.SetmealDto;
import com.mxy.regi.entity.Category;
import com.mxy.regi.entity.Dish;
import com.mxy.regi.entity.Setmeal;
import com.mxy.regi.entity.SetmealDish;
import com.mxy.regi.service.CateegoryService;
import com.mxy.regi.service.SetmealDishService;
import com.mxy.regi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealDishController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CateegoryService cateegoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public JsonResult<String> save(@RequestBody  SetmealDto setmealDto){

        log.info("套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return JsonResult.success("新增成功");
    }

    @GetMapping("/page")
    public JsonResult<Page> page(int page, int pageSize , String name){
        //构造分页对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);

        queryWrapper.orderByDesc(Setmeal::getCreateTime);

        setmealService.page(pageInfo,queryWrapper);

        //拷贝pageInfo对象的records给dtoPage对象
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list =  records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //拷贝对象
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类ID
            Long categoryId = item.getCategoryId();
            //获取ID所对应的分类对象
            Category category = cateegoryService.getById(categoryId);
            if(category!=null){
                //获取套餐名
                String categoryName = category.getName();
                //保存套餐名
                setmealDto.setCategoryName(categoryName);
            }
            //回显dto对象
            return setmealDto;
                }).collect(Collectors.toList());
         dtoPage.setRecords(list);
         return JsonResult.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return JsonResult.success("删除成功！");

    }

    /**
     * 批量起售停售
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public JsonResult<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        for (Setmeal setmeal : setmeals) {
            if (setmeal!=null){
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
        return JsonResult.success("售卖状态修改成功");
    }

    /**
     * 修改套餐^ ^
     * @param setmealDto
     * @return
     */
    @PutMapping
    public JsonResult<Setmeal> updateWithDish(@RequestBody SetmealDto setmealDto) {
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        //先根据id把setmealDish表中对应套餐的数据删了
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);
        //然后在重新添加
        setmealDishes = setmealDishes.stream().map((item) ->{
            //这属性没有，需要我们手动设置一下
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        //更新套餐数据
        setmealService.updateById(setmealDto);
        //更新套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
        return JsonResult.success(setmealDto);
    }


}
