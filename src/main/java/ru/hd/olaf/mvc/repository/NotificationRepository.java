package ru.hd.olaf.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hd.olaf.entities.Notification;
import ru.hd.olaf.entities.User;

import java.util.List;

/**
 * Created by Olaf on 08.05.2017.
 */
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserId(User user);
}
