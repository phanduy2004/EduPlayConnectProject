package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn_hcmute.Real_Time_Chat_Final.entity.Category;
import vn_hcmute.Real_Time_Chat_Final.entity.Question;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId")
    List<Question> findQuestionsByCategoryId(long categoryId);

}
