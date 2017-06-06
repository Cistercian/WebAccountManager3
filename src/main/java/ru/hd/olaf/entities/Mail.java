package ru.hd.olaf.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ru.hd.olaf.util.DateUtil;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by d.v.hozyashev on 24.05.2017.
 */
@Entity
@Table(name = "mail", schema = "web_account_db")
public class Mail {
    private Integer id;
    private Date date;
    private String sender;
    private String title;
    private String text;
    private Byte isRead;
    private User userId;

    public Mail() {
    }

    public Mail(String sender, String title, String text, User userId) {
        this.sender = sender;
        this.title = title;
        this.text = text;
        this.userId = userId;

        this.date = DateUtil.getDate(LocalDate.now());
        this.isRead = 0;
    }

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "sender", nullable = true, length = 40)
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Basic
    @Column(name = "title", nullable = true, length = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "text", nullable = true, length = -1)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Basic
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "is_read", nullable = true)
    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mail mail = (Mail) o;

        if (id != null ? !id.equals(mail.id) : mail.id != null) return false;
        if (date != null ? !date.equals(mail.date) : mail.date != null) return false;
        if (sender != null ? !sender.equals(mail.sender) : mail.sender != null) return false;
        if (title != null ? !title.equals(mail.title) : mail.title != null) return false;
        if (text != null ? !text.equals(mail.text) : mail.text != null) return false;
        if (isRead != null ? !isRead.equals(mail.isRead) : mail.isRead != null) return false;
        return userId != null ? userId.equals(mail.userId) : mail.userId == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (isRead != null ? isRead.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", date=" + date +
                ", sender='" + sender + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", isRead=" + isRead +
                ", userId=" + (userId != null ? userId.getUsername() : "null") +
                '}';
    }
}
