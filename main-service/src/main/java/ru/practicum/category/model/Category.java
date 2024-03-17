package ru.practicum.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
