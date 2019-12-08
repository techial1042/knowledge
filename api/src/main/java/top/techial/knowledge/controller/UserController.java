package top.techial.knowledge.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import top.techial.beans.ResultBean;
import top.techial.knowledge.domain.User;
import top.techial.knowledge.security.SessionService;
import top.techial.knowledge.security.UserPrincipal;
import top.techial.knowledge.service.UserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author techial
 */

@RestController
@RequestMapping("/api/user")
@Log4j2
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final SessionService sessionService;

    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/me")
    public ResultBean<Map<String, Object>> me(@AuthenticationPrincipal Object object, CsrfToken csrfToken) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("user", object);
        if (object instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) object;
            User user = userService.findById(userPrincipal.getId()).orElseThrow(NullPointerException::new);
            log.debug(sessionService.findAll());
            map.put("me", user);
        }
        map.put("_csrf", csrfToken);
        return new ResultBean<>(map);
    }

    @GetMapping("/{id}")
    public ResultBean<User> findById(@PathVariable String id) {
        return new ResultBean<>(userService.findById(id).orElse(new User()));
    }

    @PatchMapping("/{id}/password")
    public ResultBean<User> updatePassword(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String id, String srcPassword, String password) {
        if (userPrincipal.getId().equals(id)) {
            User user = userService.findById(id).orElseThrow(NullPointerException::new);
            if (!passwordEncoder.matches(srcPassword, user.getPassword())) {
                throw new IllegalArgumentException();
            }
        }

        User user = userService.updatePassword(id, password);
        sessionService.flushId(userPrincipal.getId());
        return new ResultBean<>(user);
    }


    @DeleteMapping("/{id}")
    public ResultBean<Object> deleteById(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable String id) {
        sessionService.flushId(id);
        if (userPrincipal.getId().equals(id)) {
            userService.deleteById(userPrincipal.getId());
            return new ResultBean<>("注销成功");
        }
        userService.deleteById(id);
        return new ResultBean<>("删除成功");
    }
}