package com.example.nautix.waitlist.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropBanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    // Own the relation using drop_id (matches DB)
    @OneToOne
    @JoinColumn(name = "drop_id", nullable = false, unique = true)
    @JsonIgnore
    private Drop drop;
}
