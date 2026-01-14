package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Optional.ofNullable;


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
        ofNullable(customJoin(root, cq, cb)).ifPresent(predicates::addAll);

        ofNullable(distinct()).ifPresent(cq::distinct);

        ofNullable(customPredicate(root, cq, cb)).ifPresent(predicates::addAll);

        predicates.addAll(toSpecWithLogicType(root, cq, cb));

        if (predicates.isEmpty()) {
            return null;
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }


    /**
     * 自定义join
     */
    protected List<Predicate> customJoin(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        return Collections.emptyList();
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
        List<Field> fields = new ArrayList<>();

        for (Class<?> searchType = this.getClass(); searchType != QueryObject.class; searchType = searchType.getSuperclass()) {
            fields.addAll(Arrays.asList(searchType.getDeclaredFields()));
        }

        return fields.stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
                .toList();
    }


    protected List<Predicate> toSpecWithLogicType(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        List<Field> fields = getAllFields();
        for (Field field : fields) {

            List<Predicate> fieldPredicates = handleQField(root, cq, cb, field);
            if (fieldPredicates == null) {
                continue;
            }

            predicates.addAll(fieldPredicates);
        }

        return predicates.stream().filter(Objects::nonNull).toList();
    }


    protected List<Predicate> handleQField(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field) {

        boolean fieldAccessible = field.canAccess(this);
        //如果是不可访问,修改可访问的属性
        if (!fieldAccessible) {
            field.setAccessible(true);
        }

        List<Predicate> fieldPredicates = handleQFieldAnno(root, cq, cb, field);

        //如果之前是不可访问,恢复之前的属性值
        if (!fieldAccessible) {
            field.setAccessible(false);
        }

        return fieldPredicates;
    }

    protected List<Predicate> handleQFieldAnno(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field) {
        List<Predicate> predicates = new ArrayList<>();

        Object fieldValue = getFieldValue(field);

        if (fieldValue == null) {
            return null;
        }

        //处理单个 QField
        QField qf = field.getAnnotation(QField.class);
        if (qf != null) {
            Predicate predicate = createPredicate(root, cq, cb, field, qf, fieldValue);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        //处理 QFields
        QFields qg = field.getAnnotation(QFields.class);
        if (qg == null || qg.type() == null) {
            return predicates;
        }

        List<Predicate> groupPredicates = Arrays.stream(qg.value())
                .map(qFiled -> createPredicate(root, cq, cb, field, qFiled, fieldValue))
                .filter(Objects::nonNull)
                .toList();

        if (groupPredicates.isEmpty()) {
            return predicates;
        }

        if (qg.type() == QFields.Type.OR) {
            predicates.add(cb.or(groupPredicates));
        } else if (qg.type() == QFields.Type.AND) {
            predicates.addAll(groupPredicates);
        }

        return predicates;
    }


    protected Join<T, ?> createJoin(Root<T> root, QField qf) {
        return createJoin(root, qf.joinName(), qf.joinType());
    }

    protected Join<T, ?> createJoin(Root<T> root, String joinName) {
        return createJoin(root, joinName, JoinType.INNER);
    }

    protected Join<T, ?> createJoin(Root<T> root, String joinName, JoinType type) {
        if (joinName == null || joinName.trim().isBlank() || type == null) {
            return null;
        }
        //已经 join 了
        Join<T, ?> join = getJoin(root, joinName, type);
        if (join != null) {
            return join;
        }

        return root.join(joinName, type);
    }

    protected Join<T, ?> getJoin(Root<T> root, String joinName, JoinType joinType) {
        if (joinName == null || joinName.trim().isBlank() || joinType == null) {
            return null;
        }
        return root.getJoins().stream()
                .filter(tJoin -> tJoin.getAttribute().getName().equals(joinName))
                .filter(tJoin -> joinType.equals(tJoin.getJoinType()))
                .findFirst()
                .orElse(null);
    }

    protected Join<T, ?> getJoin(Root<T> root, String joinName) {
        return getJoin(root, joinName, JoinType.INNER);
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
    protected Predicate createPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field, QField qf, Object fieldValue) {
        if (fieldValue == null) {
            return null;
        }

        Join<T, ?> join = null;
        //join 那么不为空 并且 强制join 或者有值 执行join操作
        if (!qf.joinName().trim().isBlank()) {
            join = createJoin(root, qf);
        }


        //不查询String空条件
        if (fieldValue instanceof String && ((String) fieldValue).trim().isBlank()) {
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
