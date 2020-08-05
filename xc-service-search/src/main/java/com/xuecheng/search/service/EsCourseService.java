package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName EsCourseService
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/5 9:54
 * @Version V1.0
 **/
@Service
public class EsCourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsCourseService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${xuecheng.course.index}")
    private String index;

    @Value("${xuecheng.course.type}")
    private String type;

    //需要查询的字段
    @Value("${xuecheng.course.source_field}")
    private String source_field;

    /**
     * 搜索课程信息
     *
     * @param page              页码
     * @param size              每页显示记录
     * @param courseSearchParam 查询条件
     * @return QueryResponseResult<CoursePub>
     */
    public QueryResponseResult<CoursePub> findList(int page, int size, CourseSearchParam courseSearchParam) {
        if (courseSearchParam == null) {
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索类型
        searchRequest.types(type);
        //创建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //源字段过滤
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array, new String[]{});
        //创建boolQuery查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //设置搜索条件
        //根据关键字keyWord来搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            String keyword = courseSearchParam.getKeyword();
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "name",
                    "description", "teachplan")
                    .minimumShouldMatch("70%")
                    .field("name", 10);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //根据分类,使用过滤器filter实现
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            //根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            //根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }
        //设置分页参数
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 12;
        }
        //计算搜索起始位置
        int start = (page - 1) * size;
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(size);

        //配置高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //配置高亮信息
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        //设置boolQueryBuilder到searchSourceBuilder中
        searchSourceBuilder.query(boolQueryBuilder);
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            //执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            //搜索异常处理
            e.printStackTrace();
            LOGGER.error("search error ...{}", e.getMessage());
            return new QueryResponseResult<>(CommonCode.FAIL, null);
        }
        //获取响应结果
        SearchHits hits = searchResponse.getHits();
        //匹配总记录数
        long totalHits = hits.totalHits;
        //获得匹配度高的结果
        SearchHit[] searchHits = hits.getHits();
        //数据列表
        List<CoursePub> coursePubList = new ArrayList<>();
        //添加数据
        for (SearchHit hit : searchHits) {
            CoursePub coursePub = new CoursePub();
            // 源文档
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            // 取出id
            String id = (String) sourceAsMap.get("id");
            coursePub.setId(id);
            // 取出name
            String name = (String) sourceAsMap.get("name");

            //取出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.get("name") != null) {
                HighlightField field = highlightFields.get("name");
                Text[] fragments = field.getFragments();
                StringBuffer stringBuffer = new StringBuffer();
                for (Text text : fragments) {
                    stringBuffer.append(text);
                }
                name = stringBuffer.toString();
            }
            coursePub.setName(name);
            // 取出图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);
            //优惠后的价格
            Float price = null;
            try {
                if (sourceAsMap.get("price") != null) {
                    //String.format("%.3f", sourceAsMap.get("price")) ??用意何在?
                    price = Float.parseFloat(String.format("%.3f", sourceAsMap.get("price")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice(price);
            //优惠前的价格
            Float priceOld = null;
            try {
                if (sourceAsMap.get("price_old") != null) {
                    priceOld = Float.parseFloat(String.format("%.3f", sourceAsMap.get("price_old")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice_old(priceOld);
            //将coursePub对象放入list
            coursePubList.add(coursePub);
        }
        QueryResult<CoursePub> result = new QueryResult<>();
        result.setTotal(totalHits);
        result.setList(coursePubList);
        //响应结果集
        return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS, result);
    }
}
