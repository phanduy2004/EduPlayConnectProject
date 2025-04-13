package vn_hcmute.Real_Time_Chat_Final.service.impl;

import lombok.RequiredArgsConstructor;
import vn_hcmute.Real_Time_Chat_Final.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IUserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository repository;

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
}
