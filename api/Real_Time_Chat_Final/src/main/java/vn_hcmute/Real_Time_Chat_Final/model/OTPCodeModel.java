package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPCodeModel {
    private String email;
    private String code;
}
