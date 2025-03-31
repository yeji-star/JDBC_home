package org.example;


import org.example.util.DBUtil;
import org.example.util.SecSql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public void run() {
        System.out.println("==프로그램 시작==");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("명령어 > ");
            String cmd = sc.nextLine().trim();

            Connection conn = null;

            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            String url = "jdbc:mariadb://127.0.0.1:3306/AM_DB_25_03?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul";

            try {
                conn = DriverManager.getConnection(url, "root", "");

                int actionResult = doAction(conn, sc, cmd);

                if (actionResult == -1) {
                    System.out.println("==프로그램 종료==");
                    sc.close();
                    break;
                }
            } catch (SQLException e) {
                System.out.println("에러 1 : " + e);
            } finally {
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

    private int doAction(Connection conn, Scanner sc, String cmd) {
        if (cmd.equals("exit")) {
            return -1;
        }

        if (cmd.equals("article write")) {
            System.out.println("==글쓰기==");
            System.out.print("제목 : ");
            String title = sc.nextLine().trim();
            System.out.print("내용 : ");
            String body = sc.nextLine().trim();


            SecSql sql = new SecSql();

            sql.append("INSERT INTO article"); // 공백을 알아서 넣어주기 때문에 넣지 않아도 됨
            sql.append("SET regDate = NOW(),");
            sql.append("updateDate = NOW(),");
            sql.append("title = ?,", title); //printf처럼 ?로 대체할 수 있음
            sql.append("`body` = ?;", body); //가변인자 처리로 하지 않으면 리스트나, 인덱스, 배열로 처리해야한다

           int id =  DBUtil.insert(conn, sql); //리턴을 id로 하기에 이렇게 가능

            System.out.println(id + "번 글이 생성됨");
            
            //주석처리한 곳은 결국 데이터 보내는 곳

//            PreparedStatement pstmt = null; //필요한 자원 준비

//            try {
//                String sql = "insert into article";
//                sql += " set regDate = now(),";
//                sql += "updateDate = now(),";
//                sql += "title = '" + title + "',";
//                sql += "`body` = '" + body + "';";
//
//                System.out.println(sql);
//
//                pstmt = conn.prepareStatement(sql);
//
//                int affectedRows = pstmt.executeUpdate();
//
//                System.out.println(affectedRows + "열에 적용됨");
//
//            } catch (SQLException e) {
//                System.out.println("에러2 : " + e);
//            } finally {
//                try {
//                    if (pstmt != null && !pstmt.isClosed()) {
//                        pstmt.close();
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }

        } else if (cmd.equals("article list")) {
            System.out.println("==목록==");

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            List<Article> articles = new ArrayList<>();

            try {
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

                    articles.add(article);
                }


            } catch (SQLException e) {
                System.out.println("에러3 : " + e);
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
            }

            if (articles.size() == 0) {
                System.out.println("게시글 없음");
                return 0;
            }

            System.out.println("   번호    /    제목    ");
            for (Article article : articles) {
                System.out.printf("    %d      /   %s    \n", article.getId(), article.getTitle());
            }
        } else if (cmd.startsWith("article modify")) {

            int id = 0;

            try {
                id = Integer.parseInt(cmd.split(" ")[2]);
            } catch (Exception e) {
                System.out.println("정수 입력");
                return 0;
            }

            // 있는지 없는지?

            System.out.println("==글 수정==");
            System.out.print("새 제목 : ");
            String title = sc.nextLine().trim();
            System.out.print("새 내용 : ");
            String body = sc.nextLine().trim();

            PreparedStatement pstmt = null;

            try {
                // 있는지 없는지?

                String sql = "UPDATE article";
                sql += " SET updateDate = NOW()";
                if (title.length() > 0) {
                    sql += ", title = '" + title + "'";
                }
                if (body.length() > 0) {
                    sql += ", body = '" + body + "'";
                }
                sql += " WHERE id = " + id + ";";

                System.out.println(sql);

                pstmt = conn.prepareStatement(sql);

                pstmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("에러4 : " + e);
            } finally {

                try {
                    if (pstmt != null && !pstmt.isClosed()) {
                        pstmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            System.out.println(id + "번 글이 수정되었습니다");

        }
        return 0;
    }

}


