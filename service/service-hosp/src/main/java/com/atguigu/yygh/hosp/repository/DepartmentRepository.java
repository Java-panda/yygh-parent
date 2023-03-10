package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    void deleteDepartmentByHoscodeAndDepcode(String hoscode, String depcode);

    List<Department> getDepartmentByHoscode();
}
