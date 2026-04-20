package com.smartarchive.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /** 成功为 0；须始终序列化，避免部分 Jackson 配置省略 0 导致前端误判失败 */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "OK", data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        String safe = StringUtils.hasText(message) ? message.trim() : "Operation failed";
        return new ApiResponse<>(1, safe, null);
    }

    public static <T> ApiResponse<T> failure(Integer code, String message) {
        String safe = StringUtils.hasText(message) ? message.trim() : "Operation failed";
        return new ApiResponse<>(code, safe, null);
    }
}
