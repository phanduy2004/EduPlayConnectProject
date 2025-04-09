package vn_hcmute.Real_Time_Chat_Final.model;

public class StartGameRequest {
    private String roomId;
    private String userId;

    public StartGameRequest() {
    }

    public StartGameRequest(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
