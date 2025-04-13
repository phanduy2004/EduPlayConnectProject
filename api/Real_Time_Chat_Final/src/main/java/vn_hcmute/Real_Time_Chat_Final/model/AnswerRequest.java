package vn_hcmute.Real_Time_Chat_Final.model;

public class AnswerRequest {
    private String roomId;
    private String questionId;
    private String Answer;
    private String userId;

    public AnswerRequest(String roomId, String questionId, String answer) {
        this.roomId = roomId;
        this.questionId = questionId;
        Answer = answer;
    }

    public String getAnswer() {
        return Answer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
