package com.deveyk.jobmatch.search.infrastructure.elasticsearch.filter;

import org.springframework.data.elasticsearch.core.query.Query;

public interface JmSearchFilter<T> {

    Query toQuery();

}
