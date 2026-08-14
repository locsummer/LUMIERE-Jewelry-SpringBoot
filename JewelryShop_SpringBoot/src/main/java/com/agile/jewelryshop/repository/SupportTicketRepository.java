package com.agile.jewelryshop.repository;

import com.agile.jewelryshop.model.SupportStatus;
import com.agile.jewelryshop.model.SupportTicket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    List<SupportTicket> findByCustomerIdOrderByUpdatedAtDesc(Long customerId);

    @EntityGraph(attributePaths = {"messages", "messages.sender", "customer", "assignedTo", "order"})
    Optional<SupportTicket> findByIdAndCustomerId(Long id, Long customerId);

    @EntityGraph(attributePaths = {"messages", "messages.sender", "customer", "assignedTo", "order"})
    @Query("select t from SupportTicket t where t.id = :id")
    Optional<SupportTicket> findDetailedById(@Param("id") Long id);

    List<SupportTicket> findAllByOrderByUpdatedAtDesc();
    long countByStatus(SupportStatus status);
}
