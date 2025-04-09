package vn_hcmute.Real_Time_Chat_Final.model;

public class LeaveRoomRequest {
    private String userId;
    private String roomId;

    public LeaveRoomRequest() {
    }

    public LeaveRoomRequest(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
