package com.book.onlinestore.repository;

import com.book.onlinestore.entity.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthorityRepository extends CrudRepository<Authority,Long> {

    Optional<Authority> findAuthorityByUserId(Long userId);
}
