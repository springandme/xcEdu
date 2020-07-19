package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MongoConfig
 * @Description TODO
 * @Author liushi
 * @Date 2020/7/19 22:09
 * @Version V1.0
 **/
@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String db;

    /**
     * @return com.mongodb.client.gridfs.GridFSBucket
     * @Author liushi
     * @Description GridSBucket用于打开下载流对象
     * @Date 2020-07-19 22:11
     * @Param [mongoClient]
     */
    @Bean
    public GridFSBucket getGridFSBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(db);
        GridFSBucket bucket = GridFSBuckets.create(database);
        return bucket;
    }

}
