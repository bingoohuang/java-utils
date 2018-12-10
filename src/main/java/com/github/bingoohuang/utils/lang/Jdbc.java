package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Properties;

public class Jdbc {
    static String url, user, password;

    static {
        init();
    }

    @SneakyThrows
    private static void init() {
        //load a properties file from class path, inside static method
        val is = ClzPath.toInputStream("jdbc.properties");

        if (is != null) {
            Properties jdbcProps = new Properties();
            jdbcProps.load(is);
            config(jdbcProps);
        }

    }

    public static void config(Properties jdbcProps) {
        url = jdbcProps.getProperty("url");
        user = jdbcProps.getProperty("user");
        password = jdbcProps.getProperty("password");
    }

    public static Connection getConn() {
        if (StringUtils.isEmpty(url))
            throw new RuntimeException("Jdbc is not properly configured!");

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw Fucks.fuck(e);
        }
    }

    public static boolean exec(String sql, Object... placeholders) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            bindPlaceholders(ps, placeholders);
            return ps.execute();
        } catch (Exception e) {
            throw Fucks.fuck(e);
        } finally {
            Closer.closeQuietly(ps, conn);
        }
    }

    public interface BeanMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    public static <T> T exec(BeanMapper<T> beanMapper, String sql, Object... placeholders) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            conn = getConn();
            ps = conn.prepareStatement(sql);
            bindPlaceholders(ps, placeholders);
            resultSet = ps.executeQuery();
            return resultSet.next() ? beanMapper.map(resultSet) : null;
        } catch (SQLException e) {
            throw Fucks.fuck(e);
        } finally {
            Closer.closeQuietly(resultSet, ps, conn);
        }
    }

    public static <T> T exec(Class<T> beanClass, String sql, Object... placeholders) {
        DefaultBeanMapper<T> beanMapper = new DefaultBeanMapper<T>(beanClass);
        return exec(beanMapper, sql, placeholders);
    }

    private static void bindPlaceholders(PreparedStatement ps, Object[] placeholders) throws SQLException {
        int i = 0;
        for (Object placeholder : placeholders) {
            if (placeholder instanceof String) {
                ps.setString(++i, (String) placeholder);
            } else {
                ps.setObject(++i, placeholder);
            }
        }
    }

}
