package se.scouttavling.gokapp.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationConfigService {

    final RegistrationConfigRepository repository;

    public RegistrationConfig getCurrentConfig() {
        RegistrationConfig registrationConfig = repository.findTopByOrderByIdDesc();
        if (registrationConfig == null) {
            registrationConfig = new RegistrationConfig();
        }
        return registrationConfig;
    }

    public RegistrationConfig save(RegistrationConfig registrationConfig) {
        return repository.save(registrationConfig);
    }


}
