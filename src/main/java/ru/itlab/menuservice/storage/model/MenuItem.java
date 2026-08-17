package ru.itlab.menuservice.storage.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itlab.menuservice.storage.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.itlab.menuservice.storage.util.HibernateUtil.getEffectiveClass;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "menu_items")
@Entity
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(name = "time_to_cook", nullable = false)
    private long timeToCook;
    @Column(name = "weight", nullable = false)
    private double weight;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private LocalDateTime updatedAt;
    @Type(JsonBinaryType.class)
    @Column(name = "ingredient_collection", columnDefinition = "jsonb")
    private IngredientCollection ingredientCollection;

    @Override
    public final boolean equals(Object o)  {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        MenuItem menuItem = (MenuItem) o;
        return getId() != null && Objects.equals(getId(), menuItem.getId());
    }

    @Override
    public final int hashCode() {
        return getEffectiveClass(this).hashCode();
    }
}
