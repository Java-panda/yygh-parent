package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> objectMap) {
        String s = JSONObject.toJSONString(objectMap);
        Schedule schedule = JSONObject.parseObject(s, Schedule.class);
        Schedule tempSchedule = scheduleRepository.getScheduleByHoscodeAndDepcodeAndHosScheduleId(schedule.getHoscode(),schedule.getDepcode(),schedule.getHosScheduleId());
        if (tempSchedule!=null){
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setId(tempSchedule.getId());
            schedule.setCreateTime(tempSchedule.getCreateTime());
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }else {
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> schedulePageList(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        Pageable pageRequest = PageRequest.of(page - 1, limit, sort);
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo,schedule);
        schedule.setIsDeleted(0);
        ExampleMatcher metcher = ExampleMatcher.matching().withIgnoreCase(true).withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Schedule> example = Example.of(schedule, metcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageRequest);
        return all;
    }

    @Override
    public void deleteScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId) {
        scheduleRepository.deleteScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
    }
}
