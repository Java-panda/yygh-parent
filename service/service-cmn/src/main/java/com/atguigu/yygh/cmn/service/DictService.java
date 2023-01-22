package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> getDictChildrenByParentId(Long id);

    void downloadDictExcelList(HttpServletResponse response);

    void uploadDictExcelList(MultipartFile multipartFile);

    Dict getDictByDictCode(String dictCode);

    Dict getDictByDictCodeAndvalue(String dictCode, String value);
}
