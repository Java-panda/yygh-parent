package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        //获取HospitalSet
        String hoscode = (String) objectMap.get("hoscode");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        String logoData = (String) objectMap.get("logoData");
        //Base64照片中的空格还原成+
        objectMap.put("logoData",logoData.replaceAll(" ","+"));
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                hospitalService.saveHospital(objectMap);
                return Result.ok();
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }
    @PostMapping("/hospital/show")
    public Result hospitalShow(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");

        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
                return Result.ok(hospital);
            }else {
                return Result.fail("数据不存在");
            }
        }else {
            return Result.fail("数据不存在");
        }
    }
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                departmentService.saveDepartment(objectMap);
                return Result.ok();
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/department/list")
    public Result departmentList(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        String depcode = (String) objectMap.get("depcode");
        if (StringUtils.isEmpty(hoscode)){
            return Result.fail();
        }
        Integer page = (String) objectMap.get("page")==null?1: Integer.parseInt((String) objectMap.get("page"));
        Integer limit = (String) objectMap.get("limit")==null?1:Integer.parseInt((String) objectMap.get("limit"));
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
                departmentQueryVo.setHoscode(hoscode);
                departmentQueryVo.setDepcode(depcode);
                Page<Department> pageObj = departmentService.departmentPageList( page, limit, departmentQueryVo);
                return Result.ok(pageObj);
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/department/remove")
    public Result departmentRemove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        String depcode = (String) objectMap.get("depcode");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                departmentService.deleteDepartmentByHoscodeAndDepcode(hoscode,depcode);
                return Result.ok();
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                scheduleService.saveSchedule(objectMap);
                return Result.ok();
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/schedule/list")
    public Result scheduleList(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        String depcode = (String) objectMap.get("depcode");
        if (StringUtils.isEmpty(hoscode)){
            return Result.fail();
        }
        Integer page = (String) objectMap.get("page")==null?1: Integer.parseInt((String) objectMap.get("page"));
        Integer limit = (String) objectMap.get("limit")==null?1:Integer.parseInt((String) objectMap.get("limit"));
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
                scheduleQueryVo.setHoscode(hoscode);
                scheduleQueryVo.setDepcode(depcode);
                Page<Schedule> pageObj = scheduleService.schedulePageList( page, limit, scheduleQueryVo);
                return Result.ok(pageObj);
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }

    @PostMapping("/schedule/remove")
    public Result scheduleRemove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> objectMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) objectMap.get("hoscode");
        String hosScheduleId = (String) objectMap.get("hosScheduleId");
        //获取请求中的签名(加密过)
        String sign = (String) objectMap.get("sign");
        HospitalSet hospitalSet = hospitalSetService.getHospitalByHoscode(hoscode);
        if (hospitalSet!=null){
            //获取数据库中的签名
            String signKey = hospitalSet.getSignKey();
            //加密数据库中的签名和请求中的签名对比
            String encryptSignKey = MD5.encrypt(signKey);
            if (sign.equals(encryptSignKey)){
                //一样则保存
                scheduleService.deleteScheduleByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
                return Result.ok();
            }else {
                return Result.fail();
            }
        }else {
            return Result.fail();
        }
    }
}
