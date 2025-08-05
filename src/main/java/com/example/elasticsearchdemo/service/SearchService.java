package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.document.CourseDocument;
import com.example.elasticsearchdemo.repository.CourseRepository;
import co.elastic.clients.json.JsonData;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchService(CourseRepository courseRepository,
                         ElasticsearchOperations elasticsearchOperations) {
        this.courseRepository = courseRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    // Get all courses
    public Page<CourseDocument> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    // Find by ID
    public CourseDocument findById(String id) {
        return courseRepository.findById(id).orElse(null);
    }

    // Save or update a course
    public CourseDocument saveOrUpdateCourse(CourseDocument course) {
        return courseRepository.save(course);
    }

    // Update course with existence check
    public CourseDocument updateCourse(CourseDocument updatedCourse) {
        if (updatedCourse.getId() == null || !courseRepository.existsById(updatedCourse.getId())) {
            throw new RuntimeException("Course with given ID does not exist!");
        }
        return courseRepository.save(updatedCourse);
    }

    // Search with filters + pagination + case-insensitive matching
    public Page<CourseDocument> search(String category, String type, Integer minAge, Integer maxAge,
                                       Double minPrice, Double maxPrice, int page, int size,
                                       String sortBy, String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {

                    // Case-insensitive Category filter
                    if (category != null && !category.isEmpty()) {
                        b.must(m -> m.wildcard(w -> w
                                .field("category.keyword")
                                .wildcard("*" + category.toLowerCase() + "*")
                                .caseInsensitive(true)
                        ));
                    }

                    // Case-insensitive Type filter
                    if (type != null && !type.isEmpty()) {
                        b.must(m -> m.wildcard(w -> w
                                .field("type.keyword")
                                .wildcard("*" + type.toLowerCase() + "*")
                                .caseInsensitive(true)
                        ));
                    }

                    // Age filters
                    if (minAge != null) {
                        b.must(m -> m.range(r -> r.field("minAge").gte(JsonData.of(minAge))));
                    }
                    if (maxAge != null) {
                        b.must(m -> m.range(r -> r.field("maxAge").lte(JsonData.of(maxAge))));
                    }

                    // Price filters
                    if (minPrice != null) {
                        b.must(m -> m.range(r -> r.field("price").gte(JsonData.of(minPrice))));
                    }
                    if (maxPrice != null) {
                        b.must(m -> m.range(r -> r.field("price").lte(JsonData.of(maxPrice))));
                    }

                    return b;
                }))
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        List<CourseDocument> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }
}
