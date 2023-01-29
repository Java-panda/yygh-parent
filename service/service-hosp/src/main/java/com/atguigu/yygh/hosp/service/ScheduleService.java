package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    void saveSchedule(Map<String, Object> objectMap);

    Page<Schedule> schedulePageList(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    void deleteScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    Map<String, Object> getSchedulePageByHoscodeAndDepcode(String hoscode, String depcode, Integer page, Integer limit);

    List<Schedule> getScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, String workDate);
}
