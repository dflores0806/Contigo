package es.unex.spilab.contigo.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
public class UserRole {
    private Long id;
    private String name;
    private Set<UserLogin> users;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "roles")
    public Set<UserLogin> getUsers() {
        return users;
    }

    public void setUsers(Set<UserLogin> users) {
        this.users = users;
    }
}
