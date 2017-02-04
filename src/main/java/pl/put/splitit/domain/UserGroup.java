package pl.put.splitit.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A UserGroup.
 */
@Entity
@Table(name = "user_group")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDate creationDate;

    @ManyToOne
   /* @NotNull*/
    private User owner;

    @ManyToMany(fetch=FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "user_group_users",
        joinColumns = @JoinColumn(name = "user_groups_id", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "ID"))
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public UserGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isIsPrivate() {
        return isPrivate;
    }

    public UserGroup isPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public UserGroup creationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public UserGroup owner(User user) {
        this.owner = user;
        return this;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public Set<User> getUsers() {
        return users;
    }

    public UserGroup users(Set<User> users) {
        this.users = users;
        return this;
    }

    public UserGroup addUsers(User user) {
        users.add(user);
        return this;
    }

    public UserGroup removeUsers(User user) {
        users.remove(user);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserGroup userGroup = (UserGroup) o;
        if (userGroup.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, userGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserGroup{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", isPrivate='" + isPrivate + "'" +
            ", creationDate='" + creationDate + "'" +
            '}';
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }

}
