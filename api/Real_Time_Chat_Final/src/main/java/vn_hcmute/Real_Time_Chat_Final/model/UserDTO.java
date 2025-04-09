package vn_hcmute.Real_Time_Chat_Final.model;

public class UserDTO {
    private int id;
    private String username;
    private String email;
    private boolean isActive;
    private String createdAt;
    private String status;

    // Constructors
    public UserDTO() {}

    public UserDTO(int id, String username, String email, boolean isActive, String createdAt, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

