package com.bear.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author bear
 * @version 1.0
 * @className JDBCTest
 * @description TODO
 * @date 2020/9/13 10:18
 */
public class JDBCTest {

    private static final String url = "jdbc:mysql://47.106.174.252:3306/orm";

    private static final String user = "root";

    private static final String password = "123456";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = DriverManager.getConnection(url, user, password);

        String sql = "SELECT * FROM tb_student";

        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet resultSet = ps.executeQuery();

        ResultSetMetaData metaData = resultSet.getMetaData();

        while (resultSet.next()) {

        }
    }
}
