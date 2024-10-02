package guru.qa.niffler.data.dao.impl.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class AuthorityDaoSpringJdbc implements AuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthorityEntity[] create(AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
        return authority;
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM authority",
                AuthorityEntityRowMapper.instance
        );
    }

    @Override
    public void delete(AuthorityEntity authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(
                "DELETE FROM authority WHERE user_id = ?",
                authority.getUserId().getId()
        );
    }

}