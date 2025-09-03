package com.ecommerce.sb_ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min=3, message="Product name must contain atleast 3 characters")
    private String productName;

    @NotBlank
    @Size(min=3, message="Product description must contain atleast 3 characters")
    private String description;
    private Integer quantity;
    private String image;
    private Double price;
    private Double specialPrice;
    private Double discount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
