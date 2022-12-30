package com.mole.community.controller;

import com.mole.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @Auther: ys
 * @Date: 2022/12/26 - 12 - 26 - 16:56
 */
@Controller
public class DataController {

    @Autowired
    private DataService dataService;

    //统计页面
    @RequestMapping(value = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    //统计网站UV
    //告诉服务器日期的格式，使得返回的字符串可以自动装配,加上这个注解
    @RequestMapping(value = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        //forward表示现在这个方法只能处理一半请求，交给另外一个平级方法继续处理
        return "forward:/data";
    }

    //统计活跃用户
    @RequestMapping(value = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        //forward表示现在这个方法只能处理一半请求，交给另外一个平级方法继续处理
        return "forward:/data";
    }
}
