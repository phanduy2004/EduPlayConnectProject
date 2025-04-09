package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn_hcmute.Real_Time_Chat_Final.entity.Category;
import vn_hcmute.Real_Time_Chat_Final.entity.Question;
import vn_hcmute.Real_Time_Chat_Final.repository.QuestionRepository;
import vn_hcmute.Real_Time_Chat_Final.service.impl.CategoryService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final QuestionRepository questionRepository;
    @GetMapping()
    public ResponseEntity<List<Category>> getCategory() {
        return ResponseEntity.ok(categoryService.findAll());
    }
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestionsByCategoryId(@RequestParam Long categoryId) {
        List<Question> questions = questionRepository.findQuestionsByCategoryId(categoryId);
        return ResponseEntity.ok(questions);
    }
}
