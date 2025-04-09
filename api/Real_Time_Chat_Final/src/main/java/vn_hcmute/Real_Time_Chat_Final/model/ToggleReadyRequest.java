// JoinRoomRequest.java
package vn_hcmute.Real_Time_Chat_Final.model;


public class ToggleReadyRequest {
    private String userId;
    private String roomId;
    private boolean ready;

    public ToggleReadyRequest() {}

    public ToggleReadyRequest(String userId, String roomId, boolean ready) {
        this.userId = userId;
        this.roomId = roomId;
        this.ready = ready;
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}