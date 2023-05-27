package com.mxy.regi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mxy.regi.entity.Category;

public interface CateegoryService extends IService<Category> {
    public void remove(Long id);
}
