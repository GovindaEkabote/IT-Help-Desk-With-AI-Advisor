package com.help.desk.radis.filter;

import com.help.desk.exception.TooManyRequestsException;
import com.help.desk.radis.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class RateLimiterFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService rateLimiterService;

    public RateLimiterFilter(RateLimiterService rateLimiterService){
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String ip = request.getRemoteAddr();

        boolean allowed = rateLimiterService.isAllowed(ip, 10, 60);

        if(!allowed){
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again after 1 minute.");
        }

        filterChain.doFilter(request, response);
    }
}
