package com.delmark.portfoilo.models.messages;

import com.delmark.portfoilo.models.userdata.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_name", nullable = false)
    private String chatName;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Message> messages;
}
