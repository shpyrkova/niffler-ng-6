package guru.qa.niffler.data.dao.impl.auth;

import guru.qa.niffler.data.dao.AuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.Authority;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuthorityDaoJdbc implements AuthorityDao {

    private final Connection connection;

    public AuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity[] create(AuthorityEntity[] authorityEntities) {
        Arrays.stream(authorityEntities).forEach(ae -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO authority (user_id, authority) " +
                            "VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setObject(1, ae.getUserId().getId());
                ps.setString(2, ae.getAuthority().toString());
                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                ae.setId(generatedKey);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return authorityEntities;
    }

    @Override
    public void delete(AuthorityEntity authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, authority.getUserId().getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<AuthorityEntity> foundEntities = new ArrayList<>();
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setId(rs.getObject("user_id", UUID.class));
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUserId(aue);
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    foundEntities.add(ae);
                }
                return foundEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
