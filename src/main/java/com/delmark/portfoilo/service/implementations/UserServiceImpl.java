package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.response.*;
import com.delmark.portfoilo.models.DTO.authorization.UserRegDto;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.DTO.authorization.UserAuthDto;
import com.delmark.portfoilo.models.portfoliodata.Portfolio;
import com.delmark.portfoilo.models.userdata.Role;
import com.delmark.portfoilo.models.userdata.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


// TODO: Реализовать новые методы
@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;

    private RolesRepository rolesRepository;

    private PasswordEncoder passwordEncoder;

    private PortfolioRepository portfolioRepository;

    @Override
    public void registration(UserRegDto userRegDto) {
        if (!userRepository.existsByUsername(userRegDto.getUsername())) {

            HashSet<Role> role = new HashSet<>();

            role.add(rolesRepository.findByAuthority("USER").get());

            User user = new User()
                    .setId(null)
                    .setName(userRegDto.getName())
                    .setSurname(userRegDto.getSurname())
                    .setMiddleName(userRegDto.getMiddleName())
                    .setUsername(userRegDto.getUsername())
                    .setEnabled(true)
                    .setPassword(passwordEncoder.encode(userRegDto.getPassword()))
                    .setRoles(role);
            userRepository.save(user);
        }
        else {
            throw new UsernameAlreadyExistsException();
        }
    }

    @Override
    public User getUserByAuth(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getSubject();
            return (User) loadUserByUsername(username);
        } else {
            return (User) authentication.getPrincipal();
        }
    }

    @Override
    public Page<User> getAllUsers(int page) {
        return userRepository.findAll(PageRequest.of(page, 10));
    }

    @Override
    public List<User> getUsersWithPortfolio() {
        return userRepository.getUsersWithExistingPortfolio();
    }

    @Override
    public Portfolio getPortfolioByUser(String username) {
        return portfolioRepository.findByUser((User) loadUserByUsername(username)).orElseThrow(NoSuchPortfolioException::new);
    }

    @Override
    public Set<Chat> getUserChats(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return user.getChats();
    }

    @Override
    public User grantAuthority(String authority, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Role role = rolesRepository.findByAuthority(authority).orElseThrow(NoSuchRoleException::new);

        if (user.getAuthorities().contains(role)) {
            throw new UserAlreadyHaveRoleException();
        }

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Override
    public User revokeAuthority(String authority, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Role role = rolesRepository.findByAuthority(authority).orElseThrow(NoSuchRoleException::new);

        if (!user.getAuthorities().contains(role)) {
            throw new UserDoesNotHaveRoleException();
        }

        user.getRoles().remove(role);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        User sessionUser = getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        if (!(sessionUser.getId().equals(user.getId())) || !user.getRoles().contains(rolesRepository.findByAuthority("ADMIN").get())) {
            throw new AccessDeniedException("Доступ запрещён");
        }

        userRepository.delete(user);
    }

    @Override
    public void setUserAvatar(Long userId, byte[] imageData) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        User sessionUser = getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        if (!(sessionUser.getId().equals(user.getId())) || !user.getRoles().contains(rolesRepository.findByAuthority("ADMIN").get())) {
            throw new AccessDeniedException("Доступ запрещён");
        }

        user.setAvatar(imageData);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
