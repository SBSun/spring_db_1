
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDBException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLErrorCodeSQLExceptionTranslator exTranslator;

    public MemberRepositoryV4_2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {
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
            throw exTranslator.translate("save", sql, e);
        } finally {
            // 사용 후 연결을 끊어줘야 한다.
            close(connection, pstmt, null);
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT * FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            // 무조건 한번 next 메서드를 호출해줘야 한다.
            if(rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        } catch (SQLException e){
            log.error("db error", e);
            throw exTranslator.translate("findById", sql, e);
        } finally {
            close(connection, pstmt, rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            result = pstmt.executeUpdate();
            log.info("result = {}", result);
        } catch (SQLException e){
            log.error("db error", e);
            throw exTranslator.translate("update", sql, e);
        } finally {
            close(connection, pstmt, null);
        }
    }

    @Override
    public void delete(String memberId) {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        int result = 0;

        try{
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);
            result = pstmt.executeUpdate();
        } catch (SQLException e){
            log.error("db error", e);
            throw exTranslator.translate("delete", sql, e);
        } finally {
            close(connection, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // close를 사용해서 커넥션을 닫는것이 아니라 커넥션을 유지시키기 위해 아래 메서드를 사용한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection = {}, class = {}", con, con.getClass());
        return con;
    }
}
