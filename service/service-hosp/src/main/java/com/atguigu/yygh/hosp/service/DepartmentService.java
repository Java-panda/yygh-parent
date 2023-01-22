package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {

    void saveDepartment(Map<String, Object> objectMap);

    Page<Department> departmentPageList(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    void deleteDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
