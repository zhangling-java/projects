package com.zhangling.spring.cloud.repository;

import com.zhangling.spring.cloud.model.db.UserDBModel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserDBModel, Long> {
    Optional<UserDBModel> findByAccount(String account);
}
