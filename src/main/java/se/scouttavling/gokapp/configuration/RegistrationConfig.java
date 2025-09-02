package se.scouttavling.gokapp.configuration;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "configregistration")
@Getter
@Setter
@NoArgsConstructor
public class RegistrationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "allowpublicregistration", nullable = false)
    private Boolean allowPublicRegistration = false;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "firstregisterday")
    private LocalDate firstRegisterDay;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "lastregisterday")
    private LocalDate lastRegisterDay;

    @Column(name = "registerinfo", length = 900)
    private String registerInfo;

    @Column(name = "maxpatrols")
    private Integer maxPatrols;

    @Column(name = "confirmmessage", length = 900)
    private String confirmMessage;

    @Column(name = "regnotopenmsg")
    private String registrationNotOpen;
}