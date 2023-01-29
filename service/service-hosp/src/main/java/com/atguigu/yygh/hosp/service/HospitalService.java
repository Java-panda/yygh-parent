package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> objectMap);

    Hospital getHospitalByHoscode(String hoscode);

    Page<Hospital> listPageHospital(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateHospitalStatusByIdAndStatus(String id, Integer status);

    Hospital getHospitalById(String id);

    List<Hospital> getHospitalByHosname(String hosname);
}
