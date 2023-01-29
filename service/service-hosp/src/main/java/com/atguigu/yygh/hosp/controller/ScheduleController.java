package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/getSchedulePageByHoscodeAndDepcode/{hoscode}/{depcode}/{page}/{limit}")
    public Result getSchedulePageByHoscodeAndDepcode(@PathVariable String hoscode, @PathVariable String depcode, @PathVariable Integer page ,@PathVariable Integer limit){
        Map<String,Object> pages = scheduleService.getSchedulePageByHoscodeAndDepcode(hoscode,depcode,page,limit);
        return Result.ok(pages);
    }
    @GetMapping("/getScheduleByHoscodeAndDepcodeAndWorkDate/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleByHoscodeAndDepcodeAndWorkDate(@PathVariable String hoscode, @PathVariable String depcode, @PathVariable String workDate){
        List<Schedule> schedules= scheduleService.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,workDate);
        return Result.ok(schedules);
    }
}
