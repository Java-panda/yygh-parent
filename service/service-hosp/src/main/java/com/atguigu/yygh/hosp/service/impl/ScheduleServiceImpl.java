package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.utils.DateTimeUtil;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;
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

    @Override
    public Map<String, Object> getSchedulePageByHoscodeAndDepcode(String hoscode, String depcode, Integer page, Integer limit) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        AggregationResults<BookingScheduleRuleVo> aggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        Aggregation totalAggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.DESC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalResults = mongoTemplate.aggregate(totalAggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregationResults.getMappedResults();
        for (BookingScheduleRuleVo mappedResult : mappedResults) {
            Date workDate = mappedResult.getWorkDate();
            //设置星期
            mappedResult.setDayOfWeek(DateTimeUtil.getDayOfWeekByDate(workDate));
        }

        Map<String, Object> pages = new HashMap<>();
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        pages.put("content",mappedResults);
        pages.put("total", totalResults.getMappedResults().size());
        pages.put("hosname",hospital!=null?hospital.getHosname():null);
        return pages;
    }

    @Override
    public List<Schedule> getScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, String workDate) {
        List<Schedule> schedules = scheduleRepository.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, DateTime.parse(workDate).toDate());
        for (Schedule schedule : schedules) {
            schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(hoscode).getHosname());
            schedule.getParam().put("depname",departmentService.getDepartmentByHoscodeAndDepcode(hoscode, depcode).getDepname());
            schedule.getParam().put("workDay",DateTimeUtil.getDayOfWeekByDate(schedule.getWorkDate()));
        }
        return schedules;
    }
}
