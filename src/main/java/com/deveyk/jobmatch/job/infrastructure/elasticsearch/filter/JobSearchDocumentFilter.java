package com.deveyk.jobmatch.job.infrastructure.elasticsearch.filter;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.job.infrastructure.elasticsearch.JobDocument;
import com.deveyk.jobmatch.search.infrastructure.elasticsearch.filter.JmSearchFilter;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class JobSearchDocumentFilter implements JmSearchFilter<JobDocument> {

    private final String q;
    private final Seniority seniority;
    private final EmploymentType employmentType;
    private final WorkplaceType workplaceType;
    private final String locationCountry;
    private final String locationCity;
    private final BigDecimal salaryMin;
    private final Set<Long> skillIds;

    @Override
    public org.springframework.data.elasticsearch.core.query.Query toQuery() {

        final Query boolQuery = Query.of(qb -> qb.bool(b -> {

            b.filter(this.buildFilterClauses());

            if (this.q != null && !this.q.isBlank()) {
                b.must(this.buildMultiMatchQuery());
            }

            return b;
        }));

        final FunctionScoreQuery functionScoreQuery = FunctionScoreQuery.of(fsq -> fsq
                .query(boolQuery)
                .functions(this.buildFreshnessFunctions())
                .boostMode(FunctionBoostMode.Sum));

        final Query scoredQuery = Query.of(qb -> qb.functionScore(functionScoreQuery));

        return NativeQuery.builder()
                .withQuery(scoredQuery)
                .build();

    }

    private List<Query> buildFilterClauses() {

        final List<Query> filters = new ArrayList<>();

        filters.add(Query.of(qb -> qb.term(t -> t.field("status").value(JobStatusType.PUBLISHED.name()))));

        if (this.seniority != null) {
            filters.add(Query.of(qb -> qb.term(t -> t.field("seniority").value(this.seniority.name()))));
        }

        if (this.employmentType != null) {
            filters.add(Query.of(qb -> qb.term(t -> t.field("employmentType").value(this.employmentType.name()))));
        }

        if (this.workplaceType != null) {
            filters.add(Query.of(qb -> qb.term(t -> t.field("workplaceType").value(this.workplaceType.name()))));
        }

        if (this.locationCountry != null && !this.locationCountry.isBlank()) {
            filters.add(Query.of(qb -> qb.term(t -> t.field("locationCountry").value(this.locationCountry))));
        }

        if (this.locationCity != null && !this.locationCity.isBlank()) {
            filters.add(Query.of(qb -> qb.term(t -> t.field("locationCity").value(this.locationCity))));
        }

        if (this.salaryMin != null) {
            filters.add(Query.of(qb -> qb.range(r -> r.number(n -> n
                    .field("salaryMaxAmount")
                    .gte(this.salaryMin.doubleValue())))));
        }

        if (this.skillIds != null && !this.skillIds.isEmpty()) {

            final List<FieldValue> skillIdValues = this.skillIds.stream().map(FieldValue::of).toList();

            filters.add(Query.of(qb -> qb.bool(b -> b
                    .should(sh -> sh.terms(t -> t.field("requiredSkillIds").terms(tf -> tf.value(skillIdValues))))
                    .should(sh -> sh.terms(t -> t.field("preferredSkillIds").terms(tf -> tf.value(skillIdValues)))))));
        }

        return filters;

    }

    private Query buildMultiMatchQuery() {

        return Query.of(qb -> qb.multiMatch(mm -> mm
                .query(this.q)
                .fields("title^5", "description^1")
                .type(TextQueryType.BestFields)
                .fuzziness("AUTO")));

    }

    private List<FunctionScore> buildFreshnessFunctions() {

        return List.of(FunctionScore.of(fs -> fs
                .exp(e -> e.date(d -> d
                        .field("publishedAt")
                        .placement(p -> p
                                .origin("now")
                                .scale(Time.of(t -> t.time("30d")))
                                .decay(0.5))))));

    }

}
