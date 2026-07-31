package com.aiplatform.sentinel.specification;

import com.aiplatform.sentinel.entity.Incident;
import com.aiplatform.sentinel.enums.Severity;
import com.aiplatform.sentinel.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class IncidentSpecification {

    private IncidentSpecification() {
    }

    public static Specification<Incident> hasSeverity(Severity severity) {

        return (root, query, cb) -> severity == null
                ? null
                : cb.equal(root.get("severity"), severity);
    }

    public static Specification<Incident> hasStatus(Status status) {

        return (root, query, cb) -> status == null
                ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Incident> hasKeyword(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern));
        };
    }
}