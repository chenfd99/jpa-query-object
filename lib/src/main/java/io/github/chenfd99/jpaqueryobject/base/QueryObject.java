package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 基础查询类
 */
public abstract class QueryObject<T> implements Specification<T> {


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = toSpecWithLogicType(root, criteriaBuilder);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }


    /**
     * 获取所有的字段
     */
    protected List<Field> getAllFields() {
        List<Field> fieldList = new ArrayList<>();
        Class<?> searchType = getClass();
        while (searchType != null) {
            fieldList.addAll(Arrays.asList(searchType.getDeclaredFields()));
            searchType = searchType.getSuperclass();
        }
        return fieldList;
    }


    protected List<Predicate> toSpecWithLogicType(Root<T> root, CriteriaBuilder cb) {
        List<Field> fields = getAllFields();
        List<Predicate> predicates = new ArrayList<>();
        for (Field field : fields) {
            QFiled qf = field.getAnnotation(QFiled.class);
            QGroup qg = field.getAnnotation(QGroup.class);
            if (qf == null && qg == null) {
                continue;
            }

            if (qf != null) {
                Optional.ofNullable(createPredicate(root, cb, field, qf)).ifPresent(predicates::add);
            }

            if (qg != null && qg.type() != null && qg.value() != null && qg.value().length != 0) {
                List<Predicate> groupPredicates = Arrays.stream(qg.value())
                        .map(qFiled -> createPredicate(root, cb, field, qFiled))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (groupPredicates.isEmpty()) {
                    continue;
                }
                if (qg.type() == QGroup.Type.OR) {
                    predicates.add(cb.or(groupPredicates.toArray(new Predicate[0])));
                } else if (qg.type() == QGroup.Type.AND) {
                    predicates.addAll(groupPredicates);
                }
            }
        }

        return predicates.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * 创建查询条件
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Predicate createPredicate(Root<T> root, CriteriaBuilder cb, Field field, QFiled qf) {
        String column = qf.name();
        if (column == null || column.equals("")) column = field.getName();

        field.setAccessible(true);

        try {
            Object value = field.get(this);
            if (value == null) {
                return null;
            }

            if (String.class.isAssignableFrom(value.getClass()) && value.equals("")) {
                return null;
            }

            //是否是连接查询条件
            Join join = qf.joinName() != null && qf.joinName().length() > 0 && qf.joinType() != null
                    ? root.join(qf.joinName(), qf.joinType()) : null;

            Path path = join != null ? join.get(column) : root.get(column);
            switch (qf.value()) {
                case EQUAL:
                    return cb.equal(path, value);
                case EQUAL_OR_NULL:
                    return cb.or(cb.equal(path, value), cb.isNull(path));
                case NOT_EQUAL_OR_NULL:
                    return cb.or(cb.notEqual(path, value), cb.isNull(path));
                case LIKE:
                    return cb.like(path, "%" + value + "%");
                case LIKE_START:
                    return cb.like(path, "%" + value);
                case LIKE_END:
                    return cb.like(path, value + "%");
                case GT:
                    assert value instanceof Number;
                    return cb.gt(path, (Number) value);
                case LT:
                    assert value instanceof Number;
                    return cb.lt(path, (Number) value);
                case GE:
                    assert value instanceof Number;
                    return cb.ge(path, (Number) value);
                case LE:
                    assert value instanceof Number;
                    return cb.le(path, (Number) value);
                case NOT_EQUAL:
                    return cb.notEqual(path, value);
                case NOT_LIKE:
                    return cb.notLike(path, "%" + value + "%");
                case GREATER_THAN:
                    return cb.greaterThan(path, (Comparable) value);
                case GREATER_THAN_OR_EQUAL:
                    return cb.greaterThanOrEqualTo(path, (Comparable) value);
                case LESS_THAN:
                    return cb.lessThan(path, (Comparable) value);
                case LESS_THAN_OR_EQUAL:
                    return cb.lessThanOrEqualTo(path, (Comparable) value);

                case NOT_IN:
                case IN: {
                    if (value instanceof Object[] && ((Object[]) value).length != 0) {
                        CriteriaBuilder.In in = cb.in(path);
                        Arrays.stream(((Object[]) value)).forEach(in::value);
                        return qf.value() == QType.IN ? cb.in(path) : cb.not(cb.in(path));
                    }

                    if (value instanceof Collection && ((Collection) value).size() != 0) {
                        CriteriaBuilder.In in = cb.in(path);
                        ((Collection) value).forEach(in::value);
                        return qf.value() == QType.IN ? cb.in(path) : cb.not(cb.in(path));
                    }
                }

                case IS_NULL:
                    return cb.isNull(path);
                case NOT_NULL:
                    return cb.isNotNull(path);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
