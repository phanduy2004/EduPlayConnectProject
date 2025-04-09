package vn_hcmute.Real_Time_Chat_Final.model;

public class CreateRoomRequest {
    private String userId;
    private String categoryId;
    private String categoryName;

    public CreateRoomRequest() {
    }

    public CreateRoomRequest(String userId, String categoryId, String categoryName) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
