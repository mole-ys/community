package com.mole.community.controller;

import com.mole.community.entity.Event;
import com.mole.community.event.EventProducer;
import com.mole.community.util.CommunityConstant;
import com.mole.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: ys
 * @Date: 2022/12/27 - 12 - 27 - 20:48
 */
@Controller
public class ShareController implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareController.class);

    //因为是异步事件，使用kafka
    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl){
        // 生成文件名
        String fileName = CommunityUtil.generateUUID();

        //异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        //返回访问路径
        Map<String, Object> map = new HashMap<>();
        //map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);//域名 + 项目名 + 自定义路径
        map.put("shareUrl", shareBucketUrl + "/" + fileName);

        return CommunityUtil.getJSONString(0, null, map);
    }

    //废弃
    // 获取长图
    @RequestMapping(value = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShareImage (@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空！");
        }

        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("获取长图失败：" + e.getMessage());
        }
    }
}
