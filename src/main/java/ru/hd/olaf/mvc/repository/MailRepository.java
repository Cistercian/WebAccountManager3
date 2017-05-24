package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hd.olaf.entities.Mail;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
public interface MailRepository extends JpaRepository<Mail, Integer> {
    List<Mail> findByUserId(User userId);

    @Query("SELECT title FROM Mail WHERE userId = ?1 AND isRead = 0")
    List<String> getUnreadTitle(User user);
}
