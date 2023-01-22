package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> objectMap) {
        String s = JSONObject.toJSONString(objectMap);
        Department department = JSONObject.parseObject(s, Department.class);
        Department tempDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        if (tempDepartment!=null){
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            department.setId(tempDepartment.getId());
            department.setCreateTime(tempDepartment.getCreateTime());
            departmentRepository.save(department);
        }else {
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> departmentPageList(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        Pageable pageRequest = PageRequest.of(page - 1, limit, sort);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        ExampleMatcher metcher = ExampleMatcher.matching().withIgnoreCase(true).withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Department> example = Example.of(department, metcher);
        Page<Department> all = departmentRepository.findAll(example, pageRequest);
        return all;
    }

    @Override
    public void deleteDepartmentByHoscodeAndDepcode(String hoscode, String depcode) {
        departmentRepository.deleteDepartmentByHoscodeAndDepcode(hoscode,depcode);
    }

}
