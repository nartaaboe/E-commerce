package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String image;
    private Double price;
    private Integer quantity;
    private Double rating;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "product")
    private List<Review> reviews;
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;
    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;
}
