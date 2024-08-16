package joa.oasis.product.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserForm {
    private String email;
    private String username;
    private String statusMessage;
    private String emergencyContact;

}