package com.postvue.feelogserver.endpoint.converter;

import jakarta.persistence.EntityManager;

import com.vaadin.hilla.crud.JpaFilterConverter;
import com.vaadin.hilla.crud.PropertyStringFilterSpecification;
import com.vaadin.hilla.crud.filter.AndFilter;
import com.vaadin.hilla.crud.filter.Filter;
import com.vaadin.hilla.crud.filter.OrFilter;
import com.vaadin.hilla.crud.filter.PropertyStringFilter;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class JpaFilterCustomConverter {

    @Autowired
    private EntityManager em;
    public <T> Specification<T> toSpec(Filter rawFilter, Class<T> entity) {
        if (rawFilter == null) {
            return Specification.anyOf();
        }
        if (rawFilter instanceof AndFilter filter) {
            return Specification.allOf(filter.getChildren().stream()
                    .map(f -> toSpec(f, entity)).toList());
        } else if (rawFilter instanceof OrFilter filter) {
            return Specification.anyOf(filter.getChildren().stream()
                    .map(f -> toSpec(f, entity)).toList());
        } else if (rawFilter instanceof PropertyStringFilter filter) {
            filter.setPropertyId(convertToDotNotation(filter.getPropertyId()));
            if (filter.getPropertyId().contains(ID_PROPERTY)){
                filter.setMatcher(PropertyStringFilter.Matcher.EQUALS);
            }
            Class<?> javaType = extractPropertyJavaType(entity,
                    filter.getPropertyId());

            return new PropertyStringFilterSpecification<>(filter, javaType);
        } else {
            if (rawFilter != null) {
                throw new IllegalArgumentException("Unknown filter type "
                        + rawFilter.getClass().getName());
            }
            return Specification.anyOf();
        }
    }

    private Class<?> extractPropertyJavaType(Class<?> entity,
            String propertyId) {
        if (propertyId.contains(ID_PROPERTY)){
            return Long.class;
        } else if (propertyId.contains(".")) {
            String[] parts = propertyId.split("\\.");
            Root<?> root = em.getCriteriaBuilder().createQuery(entity)
                .from(entity);
            Path<?> path = root.get(parts[0]);
            int i = 1;
            while (i < parts.length) {
                path = path.get(parts[i]);
                i++;
            }
            return path.getJavaType();

        } else {
            return em.getMetamodel().entity(entity).getAttribute(propertyId)
                .getJavaType();
        }
    }

    private String PROPERTY_SPLIT_STRING = "_";
    private String ID_PROPERTY = "id";
    private String convertToDotNotation(String input) {
        // "_property_"가 문자열에 포함된 경우만 변환
        if (input.contains(PROPERTY_SPLIT_STRING)) {
            // "_property_"를 기준으로 분리하여 "."으로 이어붙이기
            String[] parts = input.split(PROPERTY_SPLIT_STRING);
            return parts[0] + "." + parts[1];
        }
        // "_property_"가 없으면 원래 문자열 그대로 반환
        return input;
    }
}
