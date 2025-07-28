package com.loopers.domain.brand;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "brands")
public class Brand extends BaseEntity {

    String name;
    String description;
    String imageUrl;

}
