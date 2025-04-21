package vn_hcmute.Real_Time_Chat_Final.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IUserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository repository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Transactional
    @Override
    public Optional<User> findByEmail(String email){
        return repository.findByEmail(email);
    }
    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    public Optional<User> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public List<Map<String, Object>> findChatContacts(int userId) {
        return repository.findChatContacts(userId);
    }
    @Override
    public void saveUser(User user) {
        user.setStatus(true);
        repository.save(user);
    }
    @Override
    public void disconnect(User user) {
        var storedUser = repository.findById((long) user.getId()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(false);
            repository.save(storedUser);
        }
    }
    @Override
    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(false);
    }

    @Override
    public Optional<User> findByUserId(long userId) {
        return repository.findById(userId);
    }

    public User findByUserId1(long userId) {
        return repository.findById(userId).orElse(null);
    }
    @Transactional
    public User updateUser(int id, User updatedUser) {
        // Lấy user hiện tại từ database
        User existingUser = findByUserId1(id);

        // Kiểm tra dữ liệu đầu vào
        if (updatedUser.getUsername() == null || updatedUser.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Kiểm tra định dạng email

        // Kiểm tra username và email có trùng với user khác không
        Optional<User> userWithSameUsername = repository.findByUsername(updatedUser.getUsername());
        if (userWithSameUsername.isPresent() && userWithSameUsername.get().getId() != id) {
            throw new IllegalArgumentException("Username already exists");
        }

        Optional<User> userWithSameEmail = repository.findByEmail(updatedUser.getEmail());
        if (userWithSameEmail.isPresent() && userWithSameEmail.get().getId() != id) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Cập nhật các trường cho phép
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        // Chỉ cập nhật avatarUrl nếu không null
        if (updatedUser.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
        }

        // Không cho phép cập nhật các trường sau qua API này
        // - password: Nên có endpoint riêng để đổi mật khẩu
        // - isActive: Chỉ admin nên được phép thay đổi
        // - createdAt: Không nên thay đổi

        // Lưu user vào database
        return repository.save(existingUser);
    }

    @Transactional
    public User updateAvatar(long userId, MultipartFile file) throws Exception {
        User user = findByUserId1(userId);
        if (user == null) {
            throw new IllegalArgumentException("Không tìm thấy user");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        String avatarUrl = "/uploads/" + fileName;
        user.setAvatarUrl(avatarUrl);
        return repository.save(user);
    }
}
