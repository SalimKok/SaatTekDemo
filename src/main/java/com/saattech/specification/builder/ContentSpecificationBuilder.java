package com.saattech.specification.builder;

import com.saattech.entity.Content;
import com.saattech.enums.EntityStatus;
import com.saattech.specification.dto.ContentFilterDto;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ContentSpecificationBuilder {

    public static Specification<Content> build(ContentFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("status"), EntityStatus.ACTIVE));
            }

            if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"));
            }

            if (filter.getContentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("contentType"), filter.getContentType()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}