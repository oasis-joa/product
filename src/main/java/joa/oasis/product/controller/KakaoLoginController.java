package joa.oasis.product.controller;

import joa.oasis.product.model.User;
import joa.oasis.product.repository.UserRepository;
import joa.oasis.product.security.JwtTokenProvider;
import joa.oasis.product.service.KakaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class KakaoLoginController {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public KakaoLoginController(KakaoService kakaoService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.kakaoService = kakaoService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String oauthLogin() {
        String url = "https://kauth.kakao.com/oauth/authorize?";
        url += "response_type=code&";
        url += "redirect_uri=" + redirectUri + "&";
        url += "client_id=" + clientId;
        return "redirect:" + url;
    }

    @GetMapping("/")
    public RedirectView callback(@RequestParam(name = "code") String code, RedirectAttributes redirectAttributes) throws Exception {
        try {
            String token = kakaoService.login(code);
            User kakaoUser = kakaoService.getKakaoUserInfo(token);

            // DB에서 사용자 조회
            User existingUser = userRepository.findByEmail(kakaoUser.getEmail()).orElse(null);
            if (existingUser == null) {
                // 회원가입 페이지로 리다이렉트
                redirectAttributes.addAttribute("email", kakaoUser.getEmail());
                return new RedirectView("/register");
            }

            // 사용자 존재 시 home 페이지로 리다이렉트
            String jwtToken = jwtTokenProvider.generateToken(kakaoUser.getEmail());
            redirectAttributes.addAttribute("token", jwtToken);
            return new RedirectView("/home");

        } catch (Exception e) {
            // 예외 발생 시 에러 로그 출력
            e.printStackTrace();
            return new RedirectView("/error"); // 오류 페이지로 리다이렉트
        }
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "home";
    }

    @GetMapping("/register")
    public String showRegisterForm(@RequestParam(name = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "register";
    }

    @PostMapping("/register")
    public RedirectView registerUser(@RequestParam(name = "email") String email,
                                     @RequestParam(name = "username") String username,
                                     @RequestParam(name = "statusMessage") String statusMessage,
                                     @RequestParam(name = "emergencyContact") String emergencyContact) {
        User newUser = new User(email, username, statusMessage, emergencyContact);
        userRepository.save(newUser);
        return new RedirectView("/home");
    }
}