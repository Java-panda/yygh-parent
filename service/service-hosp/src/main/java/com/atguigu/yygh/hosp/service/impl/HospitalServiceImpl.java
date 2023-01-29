package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.client.cmn.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void saveHospital(Map<String, Object> objectMap) {
        String s = JSONObject.toJSONString(objectMap);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);
        Hospital tempHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        if (tempHospital!=null){
            hospital.setStatus(tempHospital.getStatus());
            hospital.setIsDeleted(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setId(tempHospital.getId());
        }else {
            hospital.setStatus(0);
            hospital.setIsDeleted(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
        }
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> listPageHospital(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Pageable pageable = PageRequest.of(page-1,limit, Sort.by(Sort.Direction.DESC,"createTime"));
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase(true).withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);
        all.getContent().stream().forEach(hosp -> {
            String dictHostype = (String)dictFeignClient.getDictByDictCodeAndvalue("Hostype", hosp.getHostype()).getData();
            String dictProvinceCode = (String)dictFeignClient.getDictByValue(hosp.getProvinceCode()).getData();
            String dictCityCode = (String)dictFeignClient.getDictByValue(hosp.getCityCode()).getData();
            String dictDistrictCode = (String)dictFeignClient.getDictByValue(hosp.getDistrictCode()).getData();
            hosp.getParam().put("hostype", dictHostype);
            hosp.getParam().put("address", dictProvinceCode);
            hosp.getParam().put("cityCode", dictCityCode);
            hosp.getParam().put("districtCode", dictDistrictCode);
        });
        return all;
    }

    @Override
    public void updateHospitalStatusByIdAndStatus(String id, Integer status) {
        Optional<Hospital> optionalHospital = hospitalRepository.findById(id);
        Hospital hospital = optionalHospital.get();
        hospital.setStatus(status);
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospitalById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        return hospital;
    }

    @Override
    public List<Hospital> getHospitalByHosname(String hosname) {
        List<Hospital>  hospitals = hospitalRepository.getHospitalByHosnameLike(hosname);
        return hospitals;
    }
}
