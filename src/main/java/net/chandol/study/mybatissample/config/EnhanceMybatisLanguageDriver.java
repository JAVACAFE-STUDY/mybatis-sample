package net.chandol.study.mybatissample.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;


import java.lang.reflect.Field;
import java.util.List;

/**
 * Mybatis에서 생성되는 SQL에 디버깅용 comment를 추가합니다. <br>
 * <b>개발편의<b/>를 위한 기능이며, 운영시에는 설정하지 않는것을 권장합니다.
 *
 * @author Sejong Park
 **/
@Slf4j
public class EnhanceMybatisLanguageDriver extends XMLLanguageDriver {
    public EnhanceMybatisLanguageDriver() {
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        addDebuggingComment(boundSql);
        return super.createParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        return super.createSqlSource(configuration, script, parameterType);
    }

    @SneakyThrows
    private void addDebuggingComment(BoundSql boundSql) {
        Field sqlField = BoundSql.class.getDeclaredField("sql");
        sqlField.setAccessible(true);

        String sql = (String) sqlField.get(boundSql);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        sql = addParameterComment(sql, parameterMappings);

        sqlField.set(boundSql, sql);
    }

    private String addParameterComment(String sql, List<ParameterMapping> parameters) {
        StringBuilder sqlInternalStringBuilder = new StringBuilder(sql);

        int paramReverseIndex = parameters.size() - 1;
        for (int idx = sql.length() - 1; idx > 0; idx--) {
            char c = sql.charAt(idx);
            if (c == '?') {
                String commentedString = toCommentString(parameters.get(paramReverseIndex).getProperty());

                sqlInternalStringBuilder.insert(idx + 1, commentedString);
                paramReverseIndex = paramReverseIndex - 1;
            }
        }

        return sqlInternalStringBuilder.toString();
    }

    private String toCommentString(String comment) {
        return " /*" + comment + "*/ ";
    }

}