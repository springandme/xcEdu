package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName TestIndex
 * @Description TODO
 * @Author liushi
 * @Date 2020/8/2 17:40
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

    //搜索请求对象
    private static SearchRequest searchRequest;
    //搜索源构建对象
    private static SearchSourceBuilder searchSourceBuilder;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestClient restClient;

    @BeforeClass
    public static void beforeClass() throws Exception {
        //创建搜索请求对象
        searchRequest = new SearchRequest("xc_test");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        searchSourceBuilder = new SearchSourceBuilder();
    }

    //搜索全部记录
    @Test
    public void testSearchAll() throws IOException {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_test");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索方式  matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        SearchHit[] searchHits = hits.getHits();
        //得到匹配度高的文档
        for (SearchHit searchHit : searchHits) {
            //文档的主键
            String id = searchHit.getId();
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");

            System.out.println(name);
            System.out.println(studymodel);
        }
    }


    //分页查询
    @Test
    public void testSearchPage() throws IOException {
        //设置分页参数
        //页码
        int page = 1;
        //每页记录数
        int size = 1;
        //计算出记录起始下标
        int from = (page - 1) * size;
        // from:起始记录下标,从0开始  size:每页显示的记录数
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //搜索方式  matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }

    //Term Query 精确查询  搜索的时候不分词
    @Test
    public void testTermQuery() throws IOException {
        //搜索方式   termQuery精确匹配
        searchSourceBuilder.query(QueryBuilders.termQuery("studymodel", "201001"));
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }

    //根据id查询
    @Test
    public void testTermQueryByIds() throws IOException {
        //根据id查询
        //定义id
        String[] ids = {"1", "2"};
        //搜索方式  termsQuery精确匹配->根据id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", ids));
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }


    //MatchQuery(匹配单个字段)) 全文检索,它的搜索方式是先将搜索字符串分词,再使用各个词语从索引中搜索
    @Test
    public void testMatchQuery() throws IOException {
        //搜索方式  MatchQuery 全文检索  minimumShouldMatch("80%") ->设置匹配占比
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发框架").minimumShouldMatch("70%"));
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }

    //MultiQuery 匹配多个字段
    @Test
    public void testMultiQuery() throws IOException {
        //搜索方式  MultiQuery 匹配多个字段  minimumShouldMatch("80%") ->设置匹配占比
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10));
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }


    //BoolQuery
    @Test
    public void testBoolQuery() throws IOException {
        //先定义一个multiMatchQuery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        //再定义一个termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //定义一个boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);

        //搜索方式  QueryBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }

    //filter过滤器
    @Test
    public void testFilter() throws IOException {
        //先定义一个multiMatchQuery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        //定义一个boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        //定义一个Filter过滤器,过滤是针对搜索的结果进行过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        //设置price金额在60-100之间
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        //搜索方式  QueryBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }


    //Sort排序
    @Test
    public void testSort() throws IOException {
        //定义一个boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //定义一个Filter过滤器,过滤是针对搜索的结果进行过滤
        //设置price金额在60-100之间
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        //搜索方式  QueryBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //添加排序
        searchSourceBuilder.sort("studymodel", SortOrder.DESC);
        searchSourceBuilder.sort("price", SortOrder.ASC);

        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //打出输出结果
        this.printlnSearchHits(searchResponse);
    }

    //关键字高亮显示
    @Test
    public void testHighLight() throws IOException {
        //先定义一个multiMatchQuery
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery("开发", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        //定义一个boolQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        //定义一个Filter过滤器,过滤是针对搜索的结果进行过滤
        //设置price金额在60-100之间
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        //搜索方式  QueryBuilder
        searchSourceBuilder.query(boolQueryBuilder);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        //设置源字段过滤   第一个参数结果包括哪些字段,第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name", "description", "studymodel"}, new String[]{""});
        //向搜索请求对象设置搜索源
        searchRequest.source(searchSourceBuilder);
        //执行搜索 ,向ES发起http请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        SearchHit[] searchHits = hits.getHits();
        //得到匹配度高的文档
        for (SearchHit searchHit : searchHits) {
            //文档的主键
            String id = searchHit.getId();
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null) {
                    Text[] fragments = nameField.getFragments();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Text fragment : fragments) {
                        stringBuilder.append(fragment);
                    }
                    name = stringBuilder.toString();
                }
            }
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    //打出输出结果
    public void printlnSearchHits(SearchResponse searchResponse) {
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        SearchHit[] searchHits = hits.getHits();
        //得到匹配度高的文档
        for (SearchHit searchHit : searchHits) {
            //文档的主键
            String id = searchHit.getId();
            //源文档的内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");

            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
}
