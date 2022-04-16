package com.example.reviewer.filter;

import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

public class PostQueryFilter implements Filter {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        if (request.getMethod().equals("POST")) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                Optional<User> userFromDb = userRepository.getByLogin(user.getLogin());
                if (userFromDb.isPresent() && !userFromDb.get().getBlocked()) {
                    session.setAttribute("user", userFromDb.get());
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    HttpServletResponse response = (HttpServletResponse) servletResponse;
                    response.sendRedirect("/login");
                }
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
