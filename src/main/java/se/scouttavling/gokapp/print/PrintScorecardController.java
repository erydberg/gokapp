package se.scouttavling.gokapp.print;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import se.scouttavling.gokapp.configuration.Config;
import se.scouttavling.gokapp.configuration.ConfigService;
import se.scouttavling.gokapp.patrol.Patrol;
import se.scouttavling.gokapp.patrol.PatrolService;
import se.scouttavling.gokapp.station.Station;
import se.scouttavling.gokapp.station.StationService;

import java.util.List;

@Controller
@RequestMapping("/admin/print")
@RequiredArgsConstructor
public class PrintScorecardController {

    private final PatrolService patrolService;
    private final ConfigService configService;
    private final StationService stationService;
    private final QRCodeService qrCodeService;

    @ModelAttribute("config")
    public Config loadConfig() {
        return configService.getCurrentConfig();
    }

    @GetMapping
    public String startPrint() {

        return "print_start";
    }


    @GetMapping("/small")
    public String printSmallScoreccards(Model model) {

        List<Patrol> patrols = patrolService.getAllPatrols();
        patrols.forEach(patrol -> {
            String qrDataUrl = qrCodeService.generateQRCodeDataUrl(patrol.getPatrolId());
            patrol.setQrCodeDataUrl(qrDataUrl);
        });

        model.addAttribute("patrols", patrols);

        List<Station> stations = stationService.getAll();
        model.addAttribute("stations", stations);

        return "print_patrol_scorecards_small";
    }
}
