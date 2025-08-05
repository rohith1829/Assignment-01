package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.document.CourseDocument;
import com.example.elasticsearchdemo.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Service
public class DataLoaderService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    public DataLoaderService(CourseRepository courseRepository,
                             ObjectMapper objectMapper,
                             ElasticsearchOperations elasticsearchOperations) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @PostConstruct // runs after Spring Boot starts
    public void loadData() {
        try {
            // 1. Delete old index if it exists
            boolean indexExists = elasticsearchOperations.indexOps(CourseDocument.class).exists();
            if (indexExists) {
                elasticsearchOperations.indexOps(CourseDocument.class).delete();
                System.out.println("🗑 Old 'courses' index deleted.");
            }

            // 2. Create a fresh index
            elasticsearchOperations.indexOps(CourseDocument.class).create();
            System.out.println("📦 New 'courses' index created.");

            // 3. Read sample JSON file from resources
            InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
            List<CourseDocument> courses = objectMapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});

            // 4. Index the data
            courseRepository.saveAll(courses);

            System.out.println("✅ Sample data indexed successfully: " + courses.size() + " courses");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
