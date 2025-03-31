package org.example;


import org.example.util.DBUtil;
import org.example.util.SecSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        } else if (cmd.equals("article list")) {
            System.out.println("==목록==");

            List<Article> articles = new ArrayList<>();

            SecSql sql = new SecSql();
            sql.append("SELECT * ");
            sql.append("FROM article");
            sql.append("ORDER BY id DESC");

            List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);

            for(Map<String, Object> articleMap : articleListMap) {
                articles.add(new Article(articleMap)); // 생성자 실행
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

            SecSql sql = new SecSql();
            sql.append("SELECT * ");
            sql.append("FROM article");
            sql.append("WHERE id = ?;", id);

            Map<String, Object> articleMap = DBUtil.selectRow(conn, sql); // 하나일때

            if (articleMap.isEmpty()) {
                System.out.println(id + "번 글은 없음");
                return 0;
                // 두액션을 끝내겠다는 뜻
            }

            System.out.println("==글 수정==");
            System.out.print("새 제목 : ");
            String title = sc.nextLine().trim();
            System.out.print("새 내용 : ");
            String body = sc.nextLine().trim();

            sql = new SecSql();
            sql.append("UPDATE article");
            sql.append("SET updateDate = NOW()");
            if(title.length() > 0) {
                sql.append(", title = ?", title);
            }
            if(body.length() > 0) {
                sql.append(", `body` = ?", body);
            }
            sql.append("where id = ?;", id);

            DBUtil.update(conn, sql);
            //몇열에 적용되나가 리턴


            System.out.println(id + "번 글이 수정되었습니다");

        } else if (cmd.startsWith("article detail")) {

            int id = 0;

            try {
                id = Integer.parseInt(cmd.split(" ")[2]);
            } catch (Exception e) {
                System.out.println("정수 입력");
                return 0;
            }

            // 있는지 없는지?

            SecSql sql = new SecSql();
            sql.append("SELECT * ");
            sql.append("FROM article");
            sql.append("WHERE id = ?;", id);

            Map<String, Object> articleMap = DBUtil.selectRow(conn, sql); // 하나일때

            if (articleMap.isEmpty()) {
                System.out.println(id + "번 글은 없음");
                return 0;
                // 두액션을 끝내겠다는 뜻
            }

            Article article = new Article(articleMap);

            System.out.println("번호 : " + article.getId());
            System.out.println("작성 날짜 : " + article.getRegDate());
            System.out.println("수정 날짜 : " + article.getUpdateDate());
            System.out.println("제목 : " + article.getTitle());
            System.out.println("내용 : " + article.getBody());

        } else if (cmd.startsWith("article delete")) {

            int id = 0;

            try {
                id = Integer.parseInt(cmd.split(" ")[2]);
            } catch (Exception e) {
                System.out.println("정수 입력");
                return 0;
            }

            // 있는지 없는지?

            SecSql sql = new SecSql();
            sql.append("SELECT * ");
            sql.append("FROM article");
            sql.append("WHERE id = ?;", id);

            Map<String, Object> articleMap = DBUtil.selectRow(conn, sql); // 하나일때

            if (articleMap.isEmpty()) {
                System.out.println(id + "번 글은 없음");
                return 0;
                // 두액션을 끝내겠다는 뜻
            }

            System.out.println("==삭제==");
            sql = new SecSql();
            sql.append("DELETE FROM article");
            sql.append("WHERE id = ?;", id);

            DBUtil.delete(conn, sql); //업데이트와 내용이 같기에 업데이트로 돌아간다

            System.out.println(id + "번 글이 삭제되었습니다.");
        }


        return 0;
    }

}


