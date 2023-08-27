package com.book.onlinestore.repository;

import com.book.onlinestore.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order,Long> {

    List<Order> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
