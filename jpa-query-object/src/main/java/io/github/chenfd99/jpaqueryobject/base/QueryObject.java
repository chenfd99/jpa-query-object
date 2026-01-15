package io.github.chenfd99.jpaqueryobject.base;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
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

        List<Predicate> fieldPredicates = toSpecWithLogicType(root, cq, cb);
        if (fieldPredicates != null && !fieldPredicates.isEmpty()) {
            predicates.addAll(fieldPredicates);
        }

        if (predicates.isEmpty()) {
            return null;
        }

        if (predicates.size() == 1) {
            return predicates.getFirst();
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
        List<Predicate> condition = new ArrayList<>();
        Map<String, List<Predicate>> groupMap = null;

        List<Field> fields = getAllFields();
        for (Field field : fields) {

            Predicate fieldPredicate = handleQField(root, cq, cb, field);
            if (fieldPredicate == null) {
                continue;
            }

            QGroup queryGroup = field.getAnnotation(QGroup.class);

            //QGroup为空,组合进之前得条件里
            if (queryGroup == null) {
                condition.add(fieldPredicate);
                continue;
            }

            //QGroup不为空, 放进groupMap里等循环结束后处理
            String groupName = queryGroup.value() == null || queryGroup.value().trim().isEmpty()
                    ? "default" : queryGroup.value();
            if (groupMap == null) {
                groupMap = new HashMap<>();
            }
            List<Predicate> groupConditions = groupMap.get(groupName);
            if (groupConditions == null) {
                groupConditions = new ArrayList<>();
                groupConditions.add(fieldPredicate);
                groupMap.put(groupName, groupConditions);
            } else {
                groupConditions.add(fieldPredicate);
            }
        }

        if (groupMap == null) {
            return condition;
        }

        //把 QGroup 字段组合成 or 条件添加到 predicates里
        for (List<Predicate> gp : groupMap.values()) {
            if (gp == null || gp.isEmpty()) {
                continue;
            }

            condition.add(gp.size() == 1 ? gp.getFirst() : cb.or(gp.toArray(new Predicate[0])));
        }

        return condition;
    }


    protected Predicate handleQField(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field) {

        boolean fieldAccessible = field.canAccess(this);
        //如果是不可访问,修改可访问的属性
        if (!fieldAccessible) {
            field.setAccessible(true);
        }

        Predicate fieldPredicate = handleQFieldAnno(root, cq, cb, field);

        //如果之前是不可访问,恢复之前的属性值
        if (!fieldAccessible) {
            field.setAccessible(false);
        }

        return fieldPredicate;
    }

    protected Predicate handleQFieldAnno(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb, Field field) {
        Predicate fieldPredicate = null;

        Object fieldValue = getFieldValue(field);

        if (fieldValue == null) {
            return null;
        }

        //处理单个 QField
        QField qf = field.getAnnotation(QField.class);
        if (qf != null) {
            fieldPredicate = createPredicate(root, cq, cb, field, qf, fieldValue);
        }

        //处理 QFields
        QFields qg = field.getAnnotation(QFields.class);
        if (qg == null || qg.type() == null) {
            return fieldPredicate;
        }

        List<Predicate> groupPredicateList = Arrays.stream(qg.value())
                .map(qFiled -> createPredicate(root, cq, cb, field, qFiled, fieldValue))
                .filter(Objects::nonNull)
                .toList();

        if (groupPredicateList.isEmpty()) {
            return fieldPredicate;
        }

        Predicate groupPredicate = qg.type() == QFields.Type.OR
                ? cb.or(groupPredicateList.toArray(new Predicate[0]))
                : cb.and(groupPredicateList.toArray(new Predicate[0]));


        return fieldPredicate == null ? groupPredicate : cb.and(fieldPredicate, groupPredicate);
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


        String column = qf.name() == null || qf.name().trim().isBlank() ? field.getName() : qf.name();
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
