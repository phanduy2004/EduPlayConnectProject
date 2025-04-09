package vn_hcmute.Real_Time_Chat_Final.service;

import vn_hcmute.Real_Time_Chat_Final.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserService {
    List<User> getAllUsers();

    void saveUser(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<Map<String, Object>> findChatContacts(int userId);

    void disconnect(User user);

    public List<User> findConnectedUsers();
    Optional<User> findByUserId(long userId);
}
