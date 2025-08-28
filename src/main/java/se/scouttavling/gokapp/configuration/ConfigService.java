package se.scouttavling.gokapp.configuration;


import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Service
public class ConfigService {

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Config getCurrentConfig() {
        Config config = configRepository.findTopByOrderByIdDesc();
        if (config == null) {
            config = new Config();
            config.setName("- systemet ej konfigurerat");
        }
        return config;
    }

    public Config save(Config config) {
        return configRepository.save(config);
    }

    public Optional<Config> findById(Integer id) {
        return configRepository.findById(id);
    }
}
