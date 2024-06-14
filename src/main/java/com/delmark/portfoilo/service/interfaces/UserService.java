package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.UserRegDTO;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

public interface UserService {

    // Авторизация
    void registration(UserRegDTO registrationDto);
    User getUserByAuth(Authentication authentication);

    // Получение общей информации
    User getUserById(Long id);
    User getUserByUsername(String username);
    Page<User> getAllUsers(int page);
    List<User> getUsersWithPortfolio();
    Portfolio getPortfolioByUser(String username);

    // Управление ролями
    User grantAuthority(String authority, String username);
    User revokeAuthority(String authority, String username);

    // Управление пользователем
    void deleteUser(String username);
    void setUserAvatar(Long userId, byte[] imageData);
}
