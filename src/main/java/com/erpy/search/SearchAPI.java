package com.erpy.search;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.metrics.percentiles.InternalPercentileRanks;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.swing.text.html.HTMLDocument;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.*;

/**
 * Created by baeonejune on 15. 1. 2..
 */
public class SearchAPI {
    /**
     * ElasticSearch 칼럼정보
     *
     *  del_yn : 삭제 여부 칼럼 ( not_analyzed , string )
     *  reg_dt : 등록일 ( not_analyzed, date )
     *  title : 제목 ( analyzed - korean_analyzer , string )
     *  concept : 컨셉 ( analyzed - korean_analyzer , string )
     * @param DataMap int
     * @return void
     * @throws Exception
     * @author OJS
     */
    public void search() throws Exception {
        Node node = nodeBuilder().node();
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(
                "nike 뮤트",                    // Text you are looking for
                "brand_name", "product_name"     // Fields you query on
        );

        SearchResponse response = client.prepareSearch("shop")
                //.setTypes("twitter", "type2")
                .setTypes("okmall")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                //.setQuery(QueryBuilders.termQuery("brand_name", "nike"))             // Query
                //.setPostFilter(FilterBuilders.rangeFilter("age").from(0).to(10))   // Filter
                .setQuery(queryBuilder)
                .setFrom(0).setSize(10).setExplain(true)
                .execute()
                .actionGet();



//        SearchHit[] searchHits = response.getHits().getHits();
//        StringBuilder builder = new StringBuilder();
//        int length = searchHits.length;
//        builder.append("[");
//        for (int i = 0; i < length; i++) {
//            if (i == length - 1) {
//                builder.append(searchHits[i].getSourceAsString());
//            } else {
//                builder.append(searchHits[i].getSourceAsString());
//                builder.append(",");
//            }
//        }
//
//        builder.append("]");
//        String result= builder.toString();


        System.out.println(response.toString());
        System.out.println("------------------");

        client.close();
        node.close();

    }


    public void search2() throws Exception {
        Node node = nodeBuilder().node();
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));

        //searchResponse = client().prepareSearch().setQuery(QueryBuilders.multiMatchQuery("the quick brown", "field1", "field2").cutoffFrequency(3).operator(MatchQueryBuilder.Operator.AND)).execute().actionGet();

        /*
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(
                "nike 뮤트",                    // Text you are looking for
                "brand_name",     // Fields you query on
                "product_name"
        ).operator(MatchQueryBuilder.Operator.AND);
        */

        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(
                "nike 뮤트",                    // Text you are looking for
                "brand_name",     // Fields you query on
                "product_name"
        );

        SearchResponse response = client.prepareSearch("shop")
                //.setTypes("twitter", "type2")
                .setTypes("okmall")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        //.setQuery(QueryBuilders.termQuery("brand_name", "nike"))             // Query
                        //.setPostFilter(FilterBuilders.rangeFilter("age").from(0).to(10))   // Filter
                .addSort(new FieldSortBuilder("sale_price").order(SortOrder.ASC))
                .setQuery(queryBuilder)
                .setFrom(0).setSize(10).setExplain(true)
                .execute()
                .actionGet();

        int count = 0;
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit hit : searchHits) {
            Map<String, Object> listMap = hit.getSource();
            System.out.println(String.format("[%d] %s:%s:%s|%d",
                    count,
                    listMap.get("cp"),
                    listMap.get("brand_name"),
                    listMap.get("product_name"),
                    listMap.get("sale_price")));
            //System.out.println(listMap.toString());
            System.out.println("------------------");
            count++;
        }

        System.out.println("Current results: " + searchHits.length);

        client.close();
        node.close();
    }
}
