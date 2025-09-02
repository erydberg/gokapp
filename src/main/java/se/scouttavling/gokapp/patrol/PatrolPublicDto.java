package se.scouttavling.gokapp.patrol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatrolPublicDto {

    @NotBlank(message = "Patrullens namn är obligatoriskt")
    @Size(max = 255)
    private String patrolName;

    @NotBlank(message = "Scoutkår är obligatoriskt")
    private String troop;

    private Integer track; // You can bind just the ID for selection

    @NotBlank(message = "Kontaktperson krävs")
    private String leaderContact;

    @Email(message = "Ogiltig e-postadress")
    private String leaderContactMail;

    private String leaderContactPhone;

}
