package se.scouttavling.gokapp.patrol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    private Integer track;

    @NotBlank(message = "Kontaktperson krävs")
    private String leaderContact;

    @NotEmpty(message = "E-postadress krävs")
    @Email(message = "Se till att e-postadressen är korrekt")
    private String leaderContactMail;

    @NotBlank(message = "Telefonnummer till kontaktpersonen krävs")
    private String leaderContactPhone;

}
