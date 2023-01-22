package com.atguigu.yygh.cmn.controller;

import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atguigu.yygh.common.result.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {
    @Autowired
    DictService dictService;

    @PostMapping ("/uploadDictExcelList")
    public void uploadDictExcelList(MultipartFile file){
        dictService.uploadDictExcelList(file);
    }
    @GetMapping("/downloadDictExcelList")
    public void downloadDictExcelList(HttpServletResponse response){
        dictService.downloadDictExcelList(response);
    }
    @GetMapping("/getDictChildrenByParentId/{id}")
    public Result getDictChildrenByParentId(@PathVariable Long id){
        List<Dict> dicts= dictService.getDictChildrenByParentId(id);

        return Result.ok(dicts);
    }
    @GetMapping("/getDictByDictCode/{dictCode}")
    public Result getDictByDictCode(@PathVariable String dictCode){
        Dict dict= dictService.getDictByDictCode(dictCode);
        if (dict!=null){
            return Result.ok(dict);
        }else {
            return Result.fail("数据不存在");
        }
    }
    @GetMapping("/getDictByDictCodeAndvalue/{dictCode}/{value}")
    public Result getDictByDictCodeAndvalue(@PathVariable String dictCode,@PathVariable String value){
        Dict dict= dictService.getDictByDictCodeAndvalue(dictCode,value);
        if (dict!=null){
            return Result.ok(dict);
        }else {
            return Result.fail("数据不存在");
        }
    }
}
