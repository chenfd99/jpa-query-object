package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.annotation.QOrderBy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 基础查询类
 */
@Slf4j
@Getter
@Setter
@ToString
public abstract class QueryObject<T> implements Specification<T> {
    protected int pageNo = 0;
    protected int pageSize = 10;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    private List<Field> fieldList;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = toSpecWithLogicType(root, criteriaQuery, criteriaBuilder);
        return criteriaQuery.where(predicates.toArray(new Predicate[0])).getRestriction();
    }


    /**
     * 获取分页
     */
    public Pageable getPageable() {
        return PageRequest.of(pageNo, pageSize, getSort());
    }


    /**
     * 获取排序
     */
    public Sort getSort() {
        List<Sort.Order> orders = getSortOrder();

        if (orders == null || orders.isEmpty()) {
            return Sort.unsorted();
        }

        return Sort.by(orders);
    }


    /**
     * 获取排序字段
     */
    protected List<Sort.Order> getSortOrder() {
        List<Sort.Order> orders = new ArrayList<>();

        getAllFields().forEach(field -> {
            QOrderBy[] orderByArray = field.getAnnotationsByType(QOrderBy.class);
            if (orderByArray == null || orderByArray.length == 0) {
                return;
            }
            if (!QOrder.class.isAssignableFrom(field.getType())) {
                return;
            }

            try {
                field.setAccessible(true);
                QOrder qOrder = (QOrder) field.get(this);
                if (qOrder == null) {
                    return;
                }
                Arrays.stream(orderByArray)
                        .sorted(Comparator.comparing(QOrderBy::order))
                        .forEach(orderBy -> {
                            if (orderBy.value() == null || orderBy.value().trim().isEmpty()) {
                                return;
                            }

                            Optional<Sort.Direction> direction = Sort.Direction.fromOptionalString(qOrder.name());
                            if (!direction.isPresent()) {
                                return;
                            }

                            orders.add(new Sort.Order(direction.get(), orderBy.value()));
                        });
            } catch (IllegalAccessException e) {
                log.error("getSortOrder error", e);
            }
        });

        return orders.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    /**
     * 获取所有的属性
     */
    protected List<Field> getAllFields() {
        if (fieldList != null) {
            return fieldList;
        }

        fieldList = new ArrayList<>();
        Class<?> searchType = getClass();
        while (searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            searchType = searchType.getSuperclass();
        }

        return fieldList;
    }


    protected List<Predicate> toSpecWithLogicType(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        List<Field> fields = getAllFields();
        List<Predicate> predicates = new ArrayList<>();
        for (Field field : fields) {
            if (QOrder.class.isAssignableFrom(field.getType())) {
                continue;
            }

            QFiled qf = field.getAnnotation(QFiled.class);
            QFiled.QGroup qg = field.getAnnotation(QFiled.QGroup.class);
            if (qf == null && qg == null) {
                continue;
            }

            if (qf != null) {
                predicates.add(createPredicate(root, cb, field, qf));
            }

            if (qg != null && qg.type() != null && qg.value() != null && qg.value().length != 0) {
                List<Predicate> groupPredicates = new ArrayList<>();
                for (QFiled qFiled : qg.value()) {
                    groupPredicates.add(createPredicate(root, cb, field, qFiled));
                }

                groupPredicates = groupPredicates.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (groupPredicates.isEmpty()) {
                    continue;
                }
                if (qg.type() == QFiled.QGroup.Type.OR) {
                    predicates.add(cb.or(groupPredicates.toArray(new Predicate[0])));
                } else if (qg.type() == QFiled.QGroup.Type.AND) {
                    predicates.addAll(groupPredicates);
                }
            }
        }

        return predicates.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Predicate createPredicate(Root<T> root, CriteriaBuilder cb, Field field, QFiled qf) {
        if (QOrder.class.isAssignableFrom(field.getType())) {
            return null;
        }
        String column = qf.name();
        if (column == null || column.equals("")) column = field.getName();

        field.setAccessible(true);

        try {
            Object value = field.get(this);
            if (value == null) return null;

            if (String.class.isAssignableFrom(value.getClass()) && value.equals("")) {
                return null;
            }

            Path path = root.get(column);
            switch (qf.value()) {
                case EQUAL:
                    return cb.equal(path, value);
                case EQUAL_OR_NULL:
                    return cb.or(cb.equal(path, value), cb.isNull(path));
                case NOT_EQUAL_OR_NULL:
                    return cb.or(cb.notEqual(path, value), cb.isNull(path));
                case LIKE_ANYWHERE:
                    return cb.like(path, "%" + value + "%");
                case LIKE_START:
                    return cb.like(path, "%" + value);
                case LIKE_END:
                    return cb.like(path, value + "%");
                case GT:
                    return cb.gt(path, (Number) value);
                case LT:
                    return cb.lt(path, (Number) value);
                case GE:
                    return cb.ge(path, (Number) value);
                case LE:
                    return cb.le(path, (Number) value);
                case NOT_EQUAL:
                    return cb.notEqual(path, value);
                case NOT_LIKE:
                    return cb.notLike(path, "%" + value + "%");
                case GREATER_THAN:
                    return cb.greaterThan(path, (Comparable) value);
                case GREATER_THAN_OR_EQUAL_TO:
                    return cb.greaterThanOrEqualTo(path, (Comparable) value);
                case LESS_THAN:
                    return cb.lessThan(path, (Comparable) value);
                case LESS_THAN_OR_EQUAL_TO:
                    return cb.lessThanOrEqualTo(path, (Comparable) value);
                case IN: {
                    if (field.getType().isArray() && ((Object[]) value).length != 0) {
                        CriteriaBuilder.In in = cb.in(path);
                        Arrays.stream(((Object[]) value)).forEach(in::value);
                        return in;
                    }

                    if (Collection.class.isAssignableFrom(field.getType()) && ((Collection) value).size() != 0) {
                        CriteriaBuilder.In in = cb.in(path);
                        ((Collection) value).forEach(in::value);
                        return in;
                    }
                }
            }
        } catch (Exception e) {
            log.error("createPredicate error", e);
        }
        return null;
    }
}
