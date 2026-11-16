package com.CoffeeGroup.Coffee.System.features.category;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CategoryEntity extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}
