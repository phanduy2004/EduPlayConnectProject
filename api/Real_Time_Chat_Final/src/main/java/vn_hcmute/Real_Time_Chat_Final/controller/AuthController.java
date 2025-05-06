package vn_hcmute.Real_Time_Chat_Final.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.OTPCodeModel;
import vn_hcmute.Real_Time_Chat_Final.service.IUserService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.UserServiceImpl;
import vn_hcmute.Real_Time_Chat_Final.utils.Email;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final Email email;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> signUpPostForm(@RequestBody @Valid User userReq, BindingResult result) {
        Map<String, String> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("message", "Invalid input data");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.findByEmail(userReq.getEmail()).isPresent()) {
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = User.builder()
                .isActive(false)
                .email(userReq.getEmail())
                .password(userReq.getPassword())
                .isReady(false)
                .username(userReq.getUsername())
                .build();

        String randomCode = email.getRandom();
        user.setOtp(randomCode);
        email.sendEmail(user);
        userService.saveUser(user);

        response.put("message", "User registered successfully. Please verify OTP.");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");

        Map<String, String> response = new HashMap<>();

        // Kiểm tra user có tồn tại không
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            response.put("message", "invalid_email");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra mã OTP
        if (code.equals(user.getOtp())) {
            user.setActive(true);
            userService.saveUser(user);
            response.put("message", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> sendOTPForgotPassword(@RequestParam String user_email) {
        Map<String, String> response = new HashMap<>();

        // Kiểm tra nếu email không tìm thấy
        if (userService.findByEmail(user_email).isEmpty()) {
            response.put("message", "Email not exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByEmail(user_email).get();
        String randomCode = email.getRandom();
        user.setOtp(randomCode);
        email.sendEmail(user);
        userService.saveUser(user);

        response.put("message", "OTP sent. Please verify.");
        response.put("userid", String.valueOf(user.getId()));
        return ResponseEntity.ok(response);
    }



}
