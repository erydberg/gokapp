package se.scouttavling.gokapp.configuration;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "config")
@Data
@NoArgsConstructor
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "allowpublicresult")
    private Boolean allowPublicResult;

    private String phone;

    private boolean useQr = true;
}

