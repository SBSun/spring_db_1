package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException{
        String sql = "INSERT INTO member(member_id, money) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            // Statement를 통해 준비된 SQL을 커넥션을 통해 DB에 전달한다.
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e){
            log.error("db error", e);
            throw e;
        } finally {
            // 사용 후 연결을 끊어줘야 한다.
            close(connection, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){

        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        // statement를 close 하는 과정에서 예외가 발생하면 커넥션을 close하는 코드가 실행되지 않기 때문에 각각 try-catch로 잡아줘야한다.
        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
