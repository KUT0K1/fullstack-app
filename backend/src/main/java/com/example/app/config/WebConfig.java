package com.example.app.config;

import com.example.app.repository.UserRepository;
import com.example.app.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final UserRepository userRepository;

  @Override
  public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(
        new HandlerMethodArgumentResolver() {
          @Override
          public boolean supportsParameter(@NonNull MethodParameter parameter) {
            return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(Long.class);
          }

          @Override
          @Nullable
          public Object resolveArgument(
              @NonNull MethodParameter parameter,
              @Nullable ModelAndViewContainer mavContainer,
              @NonNull NativeWebRequest webRequest,
              @Nullable WebDataBinderFactory binderFactory) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
              UserDetails userDetails = (UserDetails) authentication.getPrincipal();
              return userRepository
                  .findByUsername(userDetails.getUsername())
                  .map(user -> user.getId())
                  .orElseThrow(() -> new RuntimeException("User not found"));
            }
            throw new RuntimeException("User not authenticated");
          }
        });
  }
}

