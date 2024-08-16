package joa.oasis.product.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import joa.oasis.product.model.User;
import joa.oasis.product.repository.UserRepository;
import joa.oasis.product.security.KakaoTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class KakaoService {

    private static final Logger LOGGER = Logger.getLogger(KakaoService.class.getName());

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final KakaoTokenProvider kakaoTokenProvider;

    @Autowired
    public KakaoService(KakaoTokenProvider kakaoTokenProvider, UserRepository userRepository) {
        this.kakaoTokenProvider = kakaoTokenProvider;
        this.userRepository = userRepository;
    }

    @Transactional
    public String login(String code) throws Exception {
        LOGGER.info("Logging in with code: " + code);
        String token = kakaoTokenProvider.getToken(code);
        LOGGER.info("Obtained token: " + token);
        User kakaoUser = getKakaoUserInfo(token);
        LOGGER.info("Kakao user info: " + kakaoUser.getEmail());

        User user = userRepository.findByEmail(kakaoUser.getEmail()).orElseGet(() -> userRepository.save(kakaoUser));
        user.setKakaoAccessToken(token);
        userRepository.save(user);
        LOGGER.info("Saved token for user: " + user.getEmail());

        return token;
    }

    public User getKakaoUserInfo(String accessToken) throws Exception {
        var url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(headers), String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        String email = jsonNode.path("kakao_account").path("email").asText();
        return new User(email);
    }
}