package se.scouttavling.gokapp.start;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import se.scouttavling.gokapp.security.UserRepository;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SetupCheckFilter extends OncePerRequestFilter {


    private final UserRepository userRepository;

    private volatile boolean setupDone = false;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!setupDone) {
            setupDone = userRepository.count() > 0;
        }

        if (!setupDone && !path.startsWith("/setup")) {
            response.sendRedirect(request.getContextPath() + "/setup");
            return;
        }

        if (setupDone && path.startsWith("/setup")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExcluded(String path) {
        return path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/favicon")
                || path.startsWith("/error")
                || path.startsWith("/h2-console");
    }
}
