package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Category;
import vn_hcmute.Real_Time_Chat_Final.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
