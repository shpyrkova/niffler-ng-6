package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;

public interface AuthorityDao {

    List<AuthorityEntity> create(AuthorityEntity... authorityEntities);

    void delete(AuthorityEntity authority);

    List<AuthorityEntity> findAll();

}