package service.client.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ApiSpecification<T> implements Specification<T> {
    private SearchCriteria searchCriteria;
    private SpecificationAttrConverter converter;

    public ApiSpecification(SearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public ApiSpecification(SearchCriteria searchCriteria, SpecificationAttrConverter converter) {
        this.searchCriteria = searchCriteria;
        this.converter = converter;
    }

    /**
     * Handling different operators.
     *
     * @param root
     * @param query
     * @param criteriaBuilder
     * @return
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        String key = searchCriteria.getKey();
        Object value = convertIfPossible(searchCriteria.getKey(), searchCriteria.getValue());

        switch (searchCriteria.getOperation()) {
            case EQUALS:
                return criteriaBuilder.equal(root.get(key), value);
            case GREATER_THAN_OR_EQUAL_TO:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (Comparable) value);
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(key), (Comparable) value);
            case LESS_THAN_OR_EQUAL_TO:
                return criteriaBuilder.lessThanOrEqualTo(root.get(key), (Comparable) value);
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(key), (Comparable) value);
        }
        return null;
    }

    /**
     * Convert the the operation value if converter is provided.
     *
     * @param key
     * @param value
     * @return
     */
    private Object convertIfPossible(String key, Object value) {
        return converter == null ? value : converter.processAttr(key, value);
    }
}
