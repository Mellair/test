package com.mxy.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mxy.regi.common.JsonResult;
import com.mxy.regi.entity.Employee;
import com.mxy.regi.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 处理用户登陆业务
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public JsonResult<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        //对密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //查询用户名是否与数据库中匹配
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper.eq(Employee::getUsername,employee.getUsername());
         Employee emp = employeeService.getOne(queryWrapper);
        //用户不存在时
         if(emp == null){
             return JsonResult.error("登录失败！查无此用户");
         }
        //密码错误时
         if(emp.getPassword().equals(password)){
             return JsonResult.error("密码输入错误！");
         }
        //用户被禁用时
         if(emp.getStatus() == 0){
             return JsonResult.error("此账号已被禁用！");
         }
        //记录存储session信息
         request.getSession().setAttribute("employee", emp.getId());
        return JsonResult.success(emp);
    }

    /**
     * 处理账号登出业务
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/logout")
    public JsonResult<String> logout(HttpServletRequest request , @RequestBody Employee employee){
        request.getSession().removeAttribute("employee");
        return JsonResult.success("退出成功");
    }


    /**
     * 添加新员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public JsonResult<String> save(HttpServletRequest request , @RequestBody Employee employee){
        log.info("新增员工：具体信息{}",employee.toString());
        //设置初始密码，并对密码进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户ID
        Long empId = (Long) request.getSession().getAttribute("employee");
        //设置创建者以及更新者的ID
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);


        return JsonResult.success("操作成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public JsonResult<Page> page(int page , int pageSize,String name){
        log.info("page = {} ,pageSize = {} , name = {}",page,pageSize,name);
        //构造分页查询器
        Page pageInfo = new Page(page,pageSize);

        //构造条件查询器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //排序条件
        queryWrapper.orderByDesc(Employee::getCreateTime);

        employeeService.page(pageInfo,queryWrapper);
        return JsonResult.success(pageInfo);
    }

    /**
     * 修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public JsonResult<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info(employee.toString());
        long id  = Thread.currentThread().getId();
        log.info("线程id为{}:",id);
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return  JsonResult.success("修改成功！");
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public JsonResult<Employee> getById(@PathVariable Long id){

        Employee employee = employeeService.getById(id);
        return JsonResult.success(employee);
    }
}
