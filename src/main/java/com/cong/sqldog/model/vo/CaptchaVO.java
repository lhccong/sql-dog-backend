package com.cong.sqldog.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 验证码 VO
 *
 * @author cong
 * @date 2024/07/26
 */
@Data
@Accessors(chain = true)
public class CaptchaVO {
    private String key;
    private String code;
}
