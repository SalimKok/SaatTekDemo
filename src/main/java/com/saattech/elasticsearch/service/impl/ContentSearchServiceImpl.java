package com.saattech.elasticsearch.service.impl;

import com.saattech.elasticsearch.ContentIndex;
import com.saattech.elasticsearch.repository.ContentElasticsearchRepository;
import com.saattech.elasticsearch.service.ContentSearchService;
import com.saattech.entity.Content;
import com.saattech.entity.Metadata;
import com.saattech.enums.EntityStatus;
import com.saattech.repository.ContentRepository;
import com.saattech.specification.dto.ContentFilterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentSearchServiceImpl implements ContentSearchService {

    private final ContentElasticsearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ContentRepository contentRepository;

    @Override
    public Page<ContentIndex> search(String query, ContentFilterDto filter, Pageable pageable) {
        try {
            boolean hasCriteria = false;
            Criteria criteria = null;

            if (query != null && !query.trim().isEmpty()) {
                String trimmed = query.trim();
                Criteria textCriteria = new Criteria("title").fuzzy(trimmed)
                        .or(new Criteria("plot").contains(trimmed))
                        .or(new Criteria("genre").contains(trimmed))
                        .or(new Criteria("castNames").contains(trimmed));

                criteria = (criteria == null) ? textCriteria : criteria.and(textCriteria);
                hasCriteria = true;
            }


            if (filter != null) {
                if (filter.getContentType() != null) {
                    Criteria c = new Criteria("contentType").is(filter.getContentType().name());
                    criteria = (criteria == null) ? c : criteria.and(c);
                    hasCriteria = true;
                }

                EntityStatus targetStatus = (filter != null && filter.getStatus() != null)
                        ? filter.getStatus()
                        : EntityStatus.ACTIVE;
                Criteria statusCriteria = new Criteria("status").is(targetStatus.name());
                criteria = (criteria == null) ? statusCriteria : criteria.and(statusCriteria);
                hasCriteria = true;

                if (filter.getGenre() != null && !filter.getGenre().trim().isEmpty()) {
                    Criteria c = new Criteria("genre").contains(filter.getGenre().trim());
                    criteria = (criteria == null) ? c : criteria.and(c);
                    hasCriteria = true;
                }

                if (filter.getMinRating() != null && filter.getMinRating() > 0) {
                    Criteria c = new Criteria("imdbRating").greaterThanEqual(filter.getMinRating());
                    criteria = (criteria == null) ? c : criteria.and(c);
                    hasCriteria = true;
                }

                if (filter.getYear() != null && filter.getYear() > 0) {
                    Criteria c = new Criteria("year").is(filter.getYear());
                    criteria = (criteria == null) ? c : criteria.and(c);
                    hasCriteria = true;
                }
            }


            if (!hasCriteria) {
                return searchRepository.findAll(pageable);
            }

            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria, pageable);
            SearchHits<ContentIndex> searchHits = elasticsearchOperations.search(criteriaQuery, ContentIndex.class);

            List<ContentIndex> contents = searchHits.getSearchHits()
                    .stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            return new PageImpl<>(contents, pageable, searchHits.getTotalHits());

        } catch (NoSuchIndexException e) {
            log.warn("Elasticsearch index 'contents' does not exist yet. Syncing data...");
            syncAllContents();
            try {
                return searchRepository.findAll(pageable);
            } catch (Exception ex) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }
        } catch (Exception e) {
            log.error("Search query execution failed: {}", e.getMessage(), e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }


    @Override
    public void indexContent(Content content) {
        if (content == null) return;
        if (content.getStatus() == EntityStatus.DELETED) {
            deleteContentIndex(content.getId());
            return;
        }
        try {
            ContentIndex index = mapToContentIndex(content);
            searchRepository.save(index);
            log.info("Content indexed to Elasticsearch successfully with id: {}", content.getId());
        } catch (Exception e) {
            log.error("Failed to index content to Elasticsearch with id: {}", content.getId(), e);
        }
    }

    @Override
    public void deleteContentIndex(Long contentId) {
        try {
            searchRepository.deleteById(contentId);
            log.info("Content deleted from Elasticsearch index with id: {}", contentId);
        } catch (Exception e) {
            log.error("Failed to delete content from Elasticsearch with id: {}", contentId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void syncAllContents() {
        log.info("Starting bulk synchronization from PostgreSQL to Elasticsearch...");

        List<ContentIndex> indices = contentRepository.findAll().stream()
                .filter(c -> c.getStatus() == EntityStatus.ACTIVE)
                .map(this::mapToContentIndex)
                .collect(Collectors.toList());

        searchRepository.saveAll(indices);
        log.info("Successfully indexed {} contents into Elasticsearch!", indices.size());
    }

    private ContentIndex mapToContentIndex(Content content) {
        Metadata metadata = content.getMetadata();

        List<String> castNames = new ArrayList<>();
        if (content.getCastMembers() != null) {
            castNames = content.getCastMembers().stream()
                    .filter(c -> c.getCast() != null && c.getCast().getName() != null)
                    .map(c -> c.getCast().getName())
                    .collect(Collectors.toList());
        }

        Integer year = null;
        if (metadata != null && metadata.getReleased() != null) {
            year = metadata.getReleased().getYear();
        }

        Integer runtimeMinutes = parseRuntimeMinutes(metadata != null ? metadata.getRuntime() : null);

        return ContentIndex.builder()
                .id(content.getId())
                .contentType(content.getContentType())
                .status(content.getStatus())
                .seasonNo(content.getSeasonNo())
                .episodeNo(content.getEpisodeNo())
                .title(metadata != null ? metadata.getTitle() : null)
                .plot(metadata != null ? metadata.getPlot() : null)
                .genre(metadata != null ? metadata.getGenre() : null)
                .imdbRating(metadata != null ? metadata.getImdbRating() : null)
                .year(year)
                .poster(metadata != null ? metadata.getPoster() : null)
                .runtimeMinutes(runtimeMinutes)
                .castNames(castNames)
                .build();
    }

    private Integer parseRuntimeMinutes(String runtime) {
        if (runtime == null || runtime.trim().isEmpty()) return null;
        try {
            String digitsOnly = runtime.replaceAll("[^0-9]", "");
            return digitsOnly.isEmpty() ? null : Integer.parseInt(digitsOnly);
        } catch (Exception e) {
            return null;
        }
    }
}
