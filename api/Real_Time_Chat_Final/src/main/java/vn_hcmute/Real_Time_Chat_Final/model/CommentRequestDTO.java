package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequestDTO {
    private Long postId;
    private Long userId;
    private String content;
    private Long parentCommentId;

}
