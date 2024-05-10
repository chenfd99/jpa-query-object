package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;


/**
 * 基础查询类
 */
public abstract class QueryObject<T> implements Specification<T> {

    /**
     * 去重
     */
    public boolean distinct() {
        return false;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = toSpecWithLogicType(root, criteriaBuilder);
        customPredicate(root, criteriaQuery, criteriaBuilder).ifPresent(predicates::add);

        criteriaQuery.distinct(distinct());

        if (predicates.isEmpty()) {
            return null;
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * 添加特定条件
     */
    public Optional<Predicate> customPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return Optional.empty();
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
                ofNullable(createPredicate(root, cb, field, qf)).ifPresent(predicates::add);
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


    protected Join<?, ?> createJoinFetch(Root<T> root, CriteriaBuilder cb, Field field, QFiled qf) {
        String joinName = qf.joinName();
        if (joinName == null || joinName.trim().isEmpty() || qf.joinType() == null) {
            return null;
        }

        return root.join(joinName, qf.joinType());
    }


    protected Object getFieldValue(Field field) {
        boolean fieldAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(fieldAccessible);
        }
    }

    /**
     * 生成查询条件
     *
     * @param field 字段
     * @param qf    字段注解
     */
    @SuppressWarnings({"rawtypes"})
    protected Predicate createPredicate(Root<T> root, CriteriaBuilder cb, Field field, QFiled qf) {
        Object fieldValue = getFieldValue(field);
        if (fieldValue == null) {
            return null;
        }

        //不查询String空条件
        if (String.class.isAssignableFrom(fieldValue.getClass()) && ((String) fieldValue).trim().isEmpty()) {
            return null;
        }

        //是否是连接查询条件
        Join join = createJoinFetch(root, cb, field, qf);

        String column = qf.name() == null || qf.name().isEmpty() ? field.getName() : qf.name();
        Path path = ofNullable((From) join).orElse(root).get(column);
        return getPredicateWithType(qf.value(), path, fieldValue, cb);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Predicate getPredicateWithType(QType qType, Path path, Object fieldValue, CriteriaBuilder cb) {
        switch (qType) {
            case EQUAL:
                return cb.equal(path, fieldValue);
            case EQUAL_OR_NULL:
                return cb.or(cb.equal(path, fieldValue), cb.isNull(path));
            case NOT_EQUAL_OR_NULL:
                return cb.or(cb.notEqual(path, fieldValue), cb.isNull(path));
            case LIKE:
                return cb.like((Expression<String>) path, "%" + fieldValue + "%");
            case LIKE_START:
                return cb.like((Expression<String>) path, "%" + fieldValue);
            case LIKE_END:
                return cb.like((Expression<String>) path, fieldValue + "%");
            case GT:
                assert fieldValue instanceof Number;
                return cb.gt((Expression<? extends Number>) path, (Number) fieldValue);
            case LT:
                assert fieldValue instanceof Number;
                return cb.lt((Expression<? extends Number>) path, (Number) fieldValue);
            case GE:
                assert fieldValue instanceof Number;
                return cb.ge((Expression<? extends Number>) path, (Number) fieldValue);
            case LE:
                assert fieldValue instanceof Number;
                return cb.le((Expression<? extends Number>) path, (Number) fieldValue);
            case NOT_EQUAL:
                return cb.notEqual(path, fieldValue);
            case NOT_LIKE:
                return cb.notLike((Expression<String>) path, "%" + fieldValue + "%");
            case GREATER_THAN:
                return cb.greaterThan(path, (Comparable) fieldValue);
            case GREATER_THAN_OR_EQUAL:
                return cb.greaterThanOrEqualTo(path, (Comparable) fieldValue);
            case LESS_THAN:
                return cb.lessThan(path, (Comparable) fieldValue);
            case LESS_THAN_OR_EQUAL:
                return cb.lessThanOrEqualTo(path, (Comparable) fieldValue);

            case NOT_IN:
            case IN: {
                if (fieldValue instanceof Object[] && ((Object[]) fieldValue).length != 0) {
                    CriteriaBuilder.In in = cb.in(path);
                    Arrays.stream(((Object[]) fieldValue)).forEach(in::value);
                    return qType == QType.IN ? in : cb.not(in);
                }

                if (fieldValue instanceof Collection && !((Collection) fieldValue).isEmpty()) {
                    CriteriaBuilder.In in = cb.in(path);
                    ((Collection) fieldValue).forEach(in::value);
                    return qType == QType.IN ? in : cb.not(in);
                }

                return null;
            }
            case IS_NULL:
                return cb.isNull(path);
            case NOT_NULL:
                return cb.isNotNull(path);
        }
        return null;
    }
}
