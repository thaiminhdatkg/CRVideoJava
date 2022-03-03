package vn.crln.video.crvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {
    @Autowired
    @Qualifier("dataSourceMain")
    DataSource dataSource;

    private Map getMapFromCursorResultSet(ResultSet resultSet) throws SQLException {
        Map map = new HashMap();
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            map.put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
        }
        return map;
    }
    /**
     * query trả về 01 list map
     * @param sql
     * @param parameters
     * @return
     */
    public List<Map> queryList(String sql, Object...parameters) {
        List<Map> retList = new ArrayList<>();
        callQuery((ResultSet resultSet) -> {
            try {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        retList.add(getMapFromCursorResultSet(resultSet));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, sql, parameters);
        return retList;
    }

    public Map queryMap(String sql, Object...parameters) {
        final Map[] retMaps = new Map[1];
        retMaps[0] = null;
        callQuery((ResultSet resultSet) -> {
            try {
                if (resultSet != null && resultSet.next()) {
                    retMaps[0] = getMapFromCursorResultSet(resultSet);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, sql, parameters);
        return retMaps[0];
    }

    public <Ty extends Object> Ty query(Class<Ty> clazz, String sql, Object...parameters) {
        Object[] retTy = new Object[1];
        callQuery((ResultSet resultSet) -> {
            if (resultSet == null) {
                retTy[0] = null;
            } else {
                try {
                    if (resultSet.next()) {
                        retTy[0] = resultSet.getObject(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, sql, parameters);
        return (Ty)retTy[0];
    }

    private void createPrepareStatement(CreatePrepareStatementResult handler, String sql, Object...parameters) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            if (parameters != null) {
                for (int i = 1; i <= parameters.length; i++) {
                    preparedStatement.setObject(i, parameters[i - 1]);
                }
            }
            handler.onCreatePrepareStatementResult(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            handler.onCreatePrepareStatementResult(null);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callQuery(CallQueryResult handler, String sql, Object...parameters) {
        createPrepareStatement((PreparedStatement preparedStatement) -> {
            if (preparedStatement == null) {
                handler.onCallQueryResult(null);
            } else {
                try {
                    ResultSet resultSet = preparedStatement.executeQuery();
                    handler.onCallQueryResult(resultSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                    handler.onCallQueryResult(null);
                }
            }
        }, sql, parameters);
    }

    public int update(String sql, Object...parameters) {
        final int[] retInts = new int[1];
        retInts[0] = 0;
        createPrepareStatement((PreparedStatement preparedStatement) -> {
            if (preparedStatement != null) {
                try {
                    retInts[0] = preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, sql, parameters);
        return retInts[0];
    }

    public interface CreatePrepareStatementResult {
        void onCreatePrepareStatementResult(PreparedStatement preparedStatement);
    }
    public interface CallQueryResult {
        void onCallQueryResult(ResultSet resultSet);
    }
}
