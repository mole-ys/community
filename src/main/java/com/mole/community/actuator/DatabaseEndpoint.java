package com.mole.community.actuator;

import com.mole.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Auther: ys
 * @Date: 2022/12/30 - 12 - 30 - 16:35
 */
@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEndpoint.class);

    //尝试获取连接
    //通过连接池获取
    @Autowired
    private DataSource dataSource;

    //通过get请求访问
    @ReadOperation
    public String checkConnection() {
        try (
                Connection conn = dataSource.getConnection();
                ){
            return CommunityUtil.getJSONString(0, "获取连接成功！");
        } catch (SQLException e) {
            LOGGER.error("获取连接失败：" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }
}
