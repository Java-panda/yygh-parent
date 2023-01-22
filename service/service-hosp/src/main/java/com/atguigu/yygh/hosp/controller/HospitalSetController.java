package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/hosp/hospitalset")
@CrossOrigin
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    @GetMapping("/listAllHospitalSet")
    public Result findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }
    @PostMapping("/listPageHospitalSet/{page}/{limit}")
    public Result listPageHospitalSet(@PathVariable Integer page, @PathVariable Integer limit, @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        System.out.println(hospitalSetQueryVo);
        Page<HospitalSet> hospitalSetPage = new Page<>(page, limit);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hospitalSetQueryVo)){
            if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
                queryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
            }
            if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
                queryWrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
            }
        }
        Page<HospitalSet> pages = hospitalSetService.page(hospitalSetPage,queryWrapper);
        return Result.ok(pages);
    }

    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody(required = true) HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        return save?Result.ok():Result.fail();
    }

    @GetMapping("/getHospitalSetById/{id}")
    public Result getHospitalSetById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
//        int i = 1/0;
        if (hospitalSet!=null){
            return Result.ok(hospitalSet);
        }else {
            return Result.fail();
        }
    }

    @DeleteMapping("/deleteHospitalSetById/{id}")
    public Result deleteHospitalSetById(@PathVariable Long id){
        boolean removeRet = hospitalSetService.removeById(id);
        if (removeRet){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @DeleteMapping("/deleteHospitalSetByIds")
    public Result deleteHospitalSetByIds(@RequestBody(required = true) List<Long> ids){
        boolean removeRet = hospitalSetService.removeByIds(ids);
        if (removeRet){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    @PutMapping("/editHospitalSet")
    public Result editHospitalSet(@RequestBody(required = true) HospitalSet hospitalSet){
        boolean save = hospitalSetService.updateById(hospitalSet);
        return save?Result.ok():Result.fail();
    }

    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,@PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet!=null){
            hospitalSet.setStatus(status);
            boolean updateRet = hospitalSetService.updateById(hospitalSet);
            return updateRet?Result.ok():Result.fail();
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet!=null){
            return Result.ok(hospitalSet.getSignKey());
        }else {
            return Result.fail();
        }
    }

}
