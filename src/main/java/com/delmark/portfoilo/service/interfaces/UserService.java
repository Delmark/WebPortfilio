package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.Chat;
import com.delmark.portfoilo.models.DTO.UserDto;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    void registration(UserDto registrationDto);
    User getUserByAuth(Authentication authentication);
    Page<User> getAllUsers(int page);
    List<User> getUsersWithPortfolio();
    Portfolio getPortfolioByUser(String username);
    Set<Chat> getUserChats(String username);
    User grantAuthority(String authority, String username);
    User revokeAuthority(String authority, String username);
}
