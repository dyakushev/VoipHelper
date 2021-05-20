package ru.bia.voip.statistics.repo.cucm.impl;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;
import ru.bia.voip.statistics.repo.cucm.CucmCdrRepoOperations;

import java.io.IOException;
import java.time.LocalDateTime;

@Repository
public class CucmCdrRepoOperationsImpl implements CucmCdrRepoOperations {
    private RestHighLevelClient client;


    public CucmCdrRepoOperationsImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public Long countCucmCdrByFromAndTimeStampBetween(String from, LocalDateTime dateFrom, LocalDateTime dateTo) {
        CountRequest countRequest = new CountRequest();
        countRequest.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("@timestamp")
                .gte(dateFrom)
                .lte(dateTo))
                .must(QueryBuilders.termQuery("from", from)));
/*        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders
                .rangeQuery("@timestamp")
                .gte(dateFrom)
                .lte(dateTo))
                .must(QueryBuilders.termQuery("from", from)));
        countRequest.source(searchSourceBuilder);*/
        countRequest.indices("cdr-*");
        try {
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public Long countCucmCdrByToAndTimeStampBetween(String to, LocalDateTime dateFrom, LocalDateTime dateTo) {
        CountRequest countRequest = new CountRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders
                .rangeQuery("@timestamp")
                .gte(dateFrom)
                .lte(dateTo))
                .must(QueryBuilders.termQuery("to", to)));
        countRequest.source(searchSourceBuilder);
        countRequest.indices("cdr-*");
        try {
            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
