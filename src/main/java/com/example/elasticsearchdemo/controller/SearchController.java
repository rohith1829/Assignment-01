package com.example.elasticsearchdemo.controller;

import com.example.elasticsearchdemo.document.CourseDocument;
import com.example.elasticsearchdemo.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*") // Allow requests from any UI
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    // Get all courses (paginated)
    @GetMapping("/all")
    public Page<CourseDocument> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return searchService.findAll(PageRequest.of(page, size));
    }

    // Get course by ID
    @GetMapping("/{id}")
    public CourseDocument getCourseById(@PathVariable String id) {
        return searchService.findById(id);
    }

    // Save or update a course
    @PutMapping("/update")
    public ResponseEntity<CourseDocument> updateCourse(@RequestBody CourseDocument updatedCourse) {
        if (updatedCourse.getId() == null || updatedCourse.getId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        CourseDocument saved = searchService.saveOrUpdateCourse(updatedCourse);
        return ResponseEntity.ok(saved);
    }

    // Search courses with filters (pagination)
    @GetMapping
    public Page<CourseDocument> searchCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return searchService.search(category, type, minAge, maxAge, minPrice, maxPrice, page, size, sortBy, sortDir);
    }
}
