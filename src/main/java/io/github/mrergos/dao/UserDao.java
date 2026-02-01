package io.github.mrergos.dao;

import io.github.mrergos.entity.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface UserDao {
    @SqlQuery("SELECT * FROM users WHERE login = :login")
    @RegisterBeanMapper(User.class)
    User findUserByLogin(@Bind("login") String login);

    @SqlUpdate("INSERT INTO users (login, password) VALUES (:login, :password)")
    @GetGeneratedKeys
    Long save(@BindBean User user);

    @SqlQuery("SELECT EXISTS(SELECT 1 FROM users WHERE login = :login)")
    boolean existsByLogin(@Bind("login") String login);
}
