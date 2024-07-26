package com.innovatelu.test_task_innovatelu;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentStorage = new ConcurrentHashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }
        documentStorage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentStorage.values().stream()
                .filter(doc -> matches(doc, request))
                .collect(Collectors.toList());
    }

    private boolean matches(Document document, SearchRequest request) {
        if (request == null) return true;

        boolean matchesTitlePrefixes = request.getTitlePrefixes() == null ||
                request.getTitlePrefixes().stream().anyMatch(prefix -> document.getTitle().startsWith(prefix));

        boolean matchesContainsContents = request.getContainsContents() == null ||
                request.getContainsContents().stream().anyMatch(content -> document.getContent().contains(content));

        boolean matchesAuthorIds = request.getAuthorIds() == null ||
                request.getAuthorIds().contains(document.getAuthor().getId());

        boolean matchesCreatedFrom = request.getCreatedFrom() == null ||
                document.getCreated().isAfter(request.getCreatedFrom());

        boolean matchesCreatedTo = request.getCreatedTo() == null ||
                document.getCreated().isBefore(request.getCreatedTo());

        return matchesTitlePrefixes && matchesContainsContents && matchesAuthorIds && matchesCreatedFrom && matchesCreatedTo;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}