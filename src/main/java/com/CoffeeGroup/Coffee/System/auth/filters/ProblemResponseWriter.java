package com.CoffeeGroup.Coffee.System.auth.filters;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ProblemResponseWriter {

    private static final String BODY =
            "{\"type\":\"about:blank\",\"title\":\"%s\",\"status\":%d,\"detail\":\"%s\",\"instance\":\"%s\"}";

    public void writeUnauthorized(HttpServletRequest request, HttpServletResponse response, String detail)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(BODY, "Unauthorized", HttpServletResponse.SC_UNAUTHORIZED,
                escape(detail), escape(request.getRequestURI())));
        response.getWriter().flush();
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
