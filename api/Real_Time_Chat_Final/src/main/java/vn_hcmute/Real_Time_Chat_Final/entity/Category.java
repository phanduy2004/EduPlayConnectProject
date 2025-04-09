package vn_hcmute.Real_Time_Chat_Final.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonManagedReference;  // Add this import

import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image; // Có thể lưu URL hoặc base64

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference  // Add this annotation
    private List<Question> questions;

    public Category() {}

    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
