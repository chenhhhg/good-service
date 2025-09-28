package bupt.goodservice.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

/**
 * MyBatis SQL执行时间拦截器
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
//@Component
public class SqlLogInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SqlLogInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;

            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();

            // 获取SQL
            String sql = boundSql.getSql();
            // 获取参数
            Object parameterObject = boundSql.getParameterObject();
            // 格式化SQL
            String formattedSql = formatSql(sql, parameterObject);

            logger.info("SQL执行耗时: {} ms", sqlCost);
            logger.info("执行SQL: {}", formattedSql);
            logger.info("SQL参数: {}", parameterObject);
        }
    }

    /**
     * 格式化SQL，将参数替换到SQL中
     */
    private String formatSql(String sql, Object parameterObject) {
        if (parameterObject == null) {
            return sql;
        }

        // 简单的参数替换（实际使用中可能需要更复杂的逻辑）
        String formattedSql = sql;
        if (parameterObject instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
            for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                if (value != null) {
                    formattedSql = formattedSql.replace("#{" + key + "}", "'" + value.toString() + "'");
                    formattedSql = formattedSql.replace("${" + key + "}", value.toString());
                }
            }
        }

        return formattedSql.replaceAll("\\s+", " ").trim();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以读取配置属性
    }
}
