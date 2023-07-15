package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    // JDBC가 제공하는 표준 Connection
    public static Connection getConnection(){
        try {
            /**
             * DB에 연결하려면 JDBC가 제공하는 DriverManager.getConnection()를 사용하면 된다.
             * 라이브러리에 있는 DB 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다.
             * JDBC가 제공하는 DriverManager는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.
             * DriverManager는 라이브러리에 등록된 드라이버 목록을 자동으로 인식하고, 이 드라이버들에게 순서대로 정보들을 넘겨서 커넥션을 획득할 수 있는지 확인한다.
            */
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("Get Connection = {}, class = {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
