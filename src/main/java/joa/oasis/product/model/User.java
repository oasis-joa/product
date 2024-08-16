package joa.oasis.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String statusMessage;
    private String emergencyContact;
    private String kakaoAccessToken;

    public User() {
    }

    public User(String email, String username, String statusMessage, String emergencyContact) {
        this.email = email;
        this.username = username;
        this.statusMessage = statusMessage;
        this.emergencyContact = emergencyContact;
    }

    // 카카오 로그인에서 사용할 생성자
    public User(String email) {
        this.email = email;
        this.username = null;
        this.statusMessage = null;
        this.emergencyContact = null;
        this.kakaoAccessToken = null;
    }
}