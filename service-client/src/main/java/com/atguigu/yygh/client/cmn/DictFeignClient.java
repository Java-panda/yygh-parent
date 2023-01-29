package com.atguigu.yygh.client.cmn;

import com.atguigu.yygh.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Component
public interface DictFeignClient {

    @GetMapping("/admin/cmn/dict/getDictByValue/{value}")
    public Result getDictByValue(@PathVariable("value") String value);
    @GetMapping("/admin/cmn/dict/getDictByDictCodeAndvalue/{dictCode}/{value}")
    public Result getDictByDictCodeAndvalue(@PathVariable("dictCode") String dictCode,@PathVariable("value") String value);
}
