package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<DepartmentVo> getDepartmentByHoscode(String hoscode) {
        List<DepartmentVo> departmentVos = new ArrayList<>();
        Department department = new Department();
        department.setHoscode(hoscode);
        List<Department> departments = departmentRepository.findAll(Example.of(department));
        Map<String, List<Department>> mapList = departments.stream().collect(Collectors.groupingBy(Department::getBigcode));
        for (Map.Entry<String, List<Department>> entry : mapList.entrySet()) {
            String key = entry.getKey();
            List<Department> departmentList = entry.getValue();
            List<DepartmentVo> collects = departmentList.stream().map(dep -> {
                DepartmentVo vo = new DepartmentVo();
                vo.setDepname(dep.getDepname());
                vo.setDepcode(dep.getDepcode());
                return vo;
            }).collect(Collectors.toList());
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(departmentList.get(0).getBigcode());
            departmentVo.setDepname(departmentList.get(0).getBigname());
            departmentVo.setChildren(collects);
            departmentVos.add(departmentVo);
        }
        return departmentVos;
    }

    @Override
    public Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return department;
    }

}
