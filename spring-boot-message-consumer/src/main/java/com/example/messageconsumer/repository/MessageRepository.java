package com.example.messageconsumer.repository;

import com.example.messageconsumer.dto.MessageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageInfo, Long> {
}
