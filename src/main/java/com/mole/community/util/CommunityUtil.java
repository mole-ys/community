package com.mole.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @Auther: ys
 * @Date: 2022/12/8 - 12 - 08 - 15:41
 */
public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    //密码 + 随机字符串
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        //spring自带工具：把结果加密成16进制字符串返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    /**
     * @param code 编码
     * @param msg 即时信息
     * @param map 业务数据
     * @return JSON格式字符串
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        //把map打散，把每一个键值对都放进JSON对象
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    //重载方法，便于调用
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
