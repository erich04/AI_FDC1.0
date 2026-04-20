package com.smartarchive.common.exception;

import com.smartarchive.common.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static String safeUserMessage(Throwable ex, String fallbackIfBlank) {
        Throwable cur = ex;
        for (int i = 0; i < 8 && cur != null; i++) {
            String m = cur.getMessage();
            if (StringUtils.hasText(m)) {
                return m.trim();
            }
            cur = cur.getCause();
        }
        if (StringUtils.hasText(fallbackIfBlank)) {
            return fallbackIfBlank.trim();
        }
        return ex.getClass().getSimpleName();
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.failure(4001, safeUserMessage(ex, "Business rule violation"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream().findFirst()
            .map(error -> error.getField() + " " + error.getDefaultMessage())
            .orElse("Validation failed");
        return ApiResponse.failure(4000, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleUnreadableBody(HttpMessageNotReadableException ex) {
        Throwable root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause() : ex;
        String detail = safeUserMessage(root, "请求体 JSON 格式错误或字段类型不匹配");
        log.warn("Unreadable HTTP message: {}", detail);
        return ApiResponse.failure(4000, detail);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponse.failure(5000, safeUserMessage(ex, null));
    }
}
