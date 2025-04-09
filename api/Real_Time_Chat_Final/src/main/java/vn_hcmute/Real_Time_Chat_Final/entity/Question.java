package vn_hcmute.Real_Time_Chat_Final.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;  // Add this import

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference  // Add this annotation
    private Category category;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    @Column(nullable = false)
    private String correctAnswer;

    @Column
    private String image;

    public Question() {}

    public Question(Category category, String question, String optionA, String optionB, String optionC, String optionD, String correctAnswer, String image) {
        this.category = category;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.image = image;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
