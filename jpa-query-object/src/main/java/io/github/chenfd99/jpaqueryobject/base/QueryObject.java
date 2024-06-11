package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 基础查询类
 */
public abstract class QueryObject<T> implements Specification<T> {

    /**
     * 去重
     */
    protected Boolean distinct() {
        return null;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        customJoin(root, cq, cb);

        Boolean distinct = distinct();
        if (distinct != null) {
            cq.distinct(distinct);
        }

        List<Predicate> customPredicate = customPredicate(root, cq, cb);
        if (customPredicate != null && !customPredicate.isEmpty()) {
            predicates.addAll(customPredicate);
        }

        predicates.addAll(toSpecWithLogicType(root, cq, cb));

        if (predicates.isEmpty()) {
            return null;
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }


    /**
     * 自定义join
     */
    protected void customJoin(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
    }

    /**
     * 添加特定条件
     */
    protected List<Predicate> customPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        return Collections.emptyList();
    }


    /**
     * 获取所有的字段
     */
    protected List<Field> getAllFields() {
        List<Field> fieldList = new ArrayList<>();
        Class<?> searchType = getClass();
        while (searchType != QueryObject.class) {
            fieldList.addAll(Arrays.asList(searchType.getDeclaredFields()));
            searchType = searchType.getSuperclass();
        }
        return fieldList;
    }


    protected List<Predicate> toSpecWithLogicType(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Field> fields = getAllFields();
        List<Predicate> predicates = new ArrayList<>();
        for (Field field : fields) {

            boolean fieldAccessible = field.isAccessible();
            if (!fieldAccessible) {
                field.setAccessible(true);
            }

            List<Predicate> fieldPredicates = handleQFieldAnno(root, cq, cb, field);
            if (fieldPredicates == null || fieldPredicates.isEmpty()) {
                if (!fieldAccessible) {
                    field.setAccessible(false);
                }
                continue;
            }
            predicates.addAll(fieldPredicates);

            if (!fieldAccessible) {
                field.setAccessible(false);
            }
        }

        return predicates.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    protected List<Predicate> handleQFieldAnno(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field) {
        List<Predicate> predicates = new ArrayList<>();

        QField qf = field.getAnnotation(QField.class);
        QFields qg = field.getAnnotation(QFields.class);
        if (qf == null && qg == null) {
            return null;
        }

        if (qf != null) {
            Predicate predicate = createPredicate(root, cq, cb, field, qf);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        if (qg == null || qg.type() == null) {
            return predicates;
        }

        Predicate[] groupPredicates = Arrays.stream(qg.value())
                .map(qFiled -> createPredicate(root, cq, cb, field, qFiled))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);

        if (groupPredicates.length == 0) {
            return predicates;
        }

        Predicate predicate = qg.type() == QFields.Type.OR ? cb.or(groupPredicates) : cb.and(groupPredicates);

        predicates.add(predicate);
        return predicates;
    }

    protected Join<T, ?> createJoin(Root<T> root, QField qf, Field field) {
        String joinName = qf.joinName();

        //已经 join 了
        Join<T, ?> join = getJoin(root, joinName, qf.joinType());
        if (join != null) {
            return join;
        }

        return root.join(joinName, qf.joinType());
    }

    protected Join<T, ?> getJoin(Root<T> root, String joinName, JoinType joinType) {
        if (joinName == null || joinName.trim().isEmpty() || joinType == null) {
            return null;
        }
        return root.getJoins().stream()
                .filter(tJoin -> tJoin.getAttribute().getName().equals(joinName))
                .filter(tJoin -> joinType.equals(tJoin.getJoinType()))
                .findFirst()
                .orElse(null);
    }


    protected Object getFieldValue(Field field) {
        try {
            return field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成查询条件
     *
     * @param field 字段
     * @param qf    字段注解
     */
    @SuppressWarnings({"rawtypes"})
    protected Predicate createPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field, QField qf) {
        Object fieldValue = getFieldValue(field);

        Join<T, ?> join = null;
        //join 那么不为空 并且 强制join 或者有值 执行join操作
        if (!qf.joinName().isEmpty() && (qf.forceJoin() || fieldValue != null)) {
            join = createJoin(root, qf, field);
        }

        if (fieldValue == null) {
            return null;
        }

        //不查询String空条件
        if (String.class.isAssignableFrom(fieldValue.getClass()) && ((String) fieldValue).trim().isEmpty()) {
            return null;
        }


        String column = qf.name() == null || qf.name().isEmpty() ? field.getName() : qf.name();
        Path path = (join == null ? root : join).get(column);
        return getPredicateWithType(cb, qf.value(), path, fieldValue);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Predicate getPredicateWithType(CriteriaBuilder cb, QType qType, Path path, Object fieldValue) {
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
