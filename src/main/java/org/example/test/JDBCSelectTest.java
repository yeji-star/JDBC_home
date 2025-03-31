package org.example.test;

import org.example.Article;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSelectTest {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Article> list = new ArrayList<>();

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://127.0.0.1:3306/AM_DB_25_03?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul";
            conn = DriverManager.getConnection(url, "root", "");
            System.out.println("연결 성공!");

            String sql = "SELECT *";
            sql += " FROM article";
            sql += " ORDER BY id DESC";

            System.out.println(sql);

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery(sql);

            while (rs.next()) {

                int id = rs.getInt("id");
                String regDate = rs.getString("regDate");
                String updateDate = rs.getString("updateDate");
                String title = rs.getString("title");
                String body = rs.getString("body");

                Article article = new Article(id, regDate, updateDate, title, body);

                list.add(article);
            }

            for (int i = 0; i < list.size(); i++) {
                System.out.println("번호:" + list.get(i).getId());
                System.out.println("작성날짜:" + list.get(i).getRegDate());
                System.out.println("수정날짜:" + list.get(i).getUpdateDate());
                System.out.println("제목:" + list.get(i).getTitle());
                System.out.println("내용:" + list.get(i).getBody());
            }


        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패" + e);
        } catch (SQLException e) {
            System.out.println("에러 : " + e);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pstmt != null && !pstmt.isClosed()) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}