package io.github.chenfd99.jpaqueryobjecttest.base;

import io.github.chenfd99.jpaqueryobjecttest.utils.TableUtils;
import org.springframework.boot.test.system.CapturedOutput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.chenfd99.jpaqueryobjecttest.utils.TableUtils.getTableName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface BaseJoinTest {
    String LEFT_JOIN_PATTERN = "left\\s+(?:outer\\s+)?join\\s+%s";
    String INNER_JOIN_PATTERN = "inner\\s+join\\s+%s";
    String ALIAS_NAME = "alias";
    String QUERY_TABLE_PATTERN = "(?:inner|left)\\s+(?:outer\\s)?join\\s+%s\\s+(?<alias>\\S+)";

    /**
     * 验证是否由 left join
     *
     * @param out 输出
     */
    default void assertLeftJoinTable(String out, final Class<?> clazz) {
        assertNotNull(clazz);
        System.out.println("assertLeftJoinTable: " + clazz.getSimpleName());
        assertJoinTable(out, getTableName(clazz), LEFT_JOIN_PATTERN);
    }

    /**
     * 验证是否由 inner join
     *
     * @param out 输出
     */
    default void assertInnerJoinTable(String out, final Class<?> clazz) {
        assertNotNull(clazz);
        System.out.println("assertInnerJoinTable: " + clazz.getSimpleName());
        String table = getTableName(clazz);
        assertJoinTable(out, table, INNER_JOIN_PATTERN);
    }


    private static void assertJoinTable(String out, String table, String pattern) {
        System.out.println("tableName = " + table);

        assertTrue(out != null && !out.isBlank());
        assertTrue(table != null && !table.isBlank());

        String regex = pattern.formatted(table);
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(out);

        boolean math = matcher.find();

        System.out.println("math = " + math);
        assertTrue(math);
        System.out.println();
    }


    /**
     * 验证是否有查询条件
     *
     * @param out 输出
     */
    default void assertQueryCondition(CapturedOutput out, Class<?> clazz, String filedName) {
        assertQueryCondition(out.getOut(), clazz, filedName);
    }

    default void assertQueryCondition(String out, Class<?> clazz, String filedName) {
        System.out.println("assertQueryCondition: class = " + clazz.getTypeName() + ", filedName = " + filedName);
        assertNotNull(clazz);

        String name = getTableName(clazz);
        System.out.println("tableName = " + name);
        assertNotNull(name);

        String regex = QUERY_TABLE_PATTERN.formatted(name);
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(out);

        boolean math = matcher.find();
        System.out.println("math = " + math);
        assertTrue(math);

        String group = matcher.group(ALIAS_NAME);
        System.out.println("alias = " + group);
        assertNotNull(group);

        String columnName = TableUtils.getColumnName(clazz, filedName);
        System.out.println("columnName = " + columnName);
        assertNotNull(columnName);

        int indexOf = out.indexOf(group + "." + columnName);
        System.out.println("indexOf = " + indexOf);
        assertTrue(indexOf != -1);
        System.out.println();
    }
}
