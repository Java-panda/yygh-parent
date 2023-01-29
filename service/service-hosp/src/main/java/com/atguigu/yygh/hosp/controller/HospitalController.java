package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/listPageHospital/{page}/{limit}")
    public Result listPageHospital(@PathVariable Integer page, @PathVariable Integer limit, @RequestBody(required = false) HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.listPageHospital(page, limit, hospitalQueryVo);
        return Result.ok(hospitalPage);
    }
    @PostMapping("/updateHospitalStatusByIdAndStatus/{id}/{status}")
    public Result updateHospitalStatusByIdAndStatus(@PathVariable String id, @PathVariable Integer status){
        hospitalService.updateHospitalStatusByIdAndStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/getHospitalById/{id}")
    public Result getHospitalById(@PathVariable String id){
        Hospital hospital = hospitalService.getHospitalById(id);
        return Result.ok(hospital);
    }
    @GetMapping("/getHospitalByHosname/{hosname}")
    public Result getHospitalByHosname(@PathVariable String hosname){
        List<Hospital> hospitals = hospitalService.getHospitalByHosname(hosname);
        return Result.ok(hospitals);
    }
}
