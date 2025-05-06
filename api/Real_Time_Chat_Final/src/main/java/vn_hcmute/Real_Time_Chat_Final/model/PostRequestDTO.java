package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {
    private Long userId;
    private String content;
    private Post.Privacy privacy;
    private List<String> imageUrls;
}
