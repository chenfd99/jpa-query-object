package io.github.chenfd99.jpaqueryobjecttest.utils;

import org.junit.platform.commons.util.AnnotationUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.List;

public final class TableUtils {

    /**
     * 回去表明
     *
     * @param clazz entity类
     * @return 表名称
     */
    public static String getTableName(final Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            return null;
        }

        return table.name();
    }

    public static String getColumnName(final Class<?> clazz, final String fieldName) {
        List<Field> fields = AnnotationUtils.findAnnotatedFields(clazz, Column.class, (f) -> f.getName().equals(fieldName));

        if (fields.isEmpty()) {
            return null;
        }

        return fields.get(0).getAnnotation(Column.class).name();
    }

}
