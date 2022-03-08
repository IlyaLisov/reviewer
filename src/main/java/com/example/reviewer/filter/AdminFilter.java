package com.example.reviewer.filter;

import com.example.reviewer.model.user.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        User user = (User) servletRequest.getAttribute("user");

        if (user == null || !user.isAdmin()) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendRedirect("/account");
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
