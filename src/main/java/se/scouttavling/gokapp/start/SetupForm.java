package se.scouttavling.gokapp.start;

import lombok.Data;

@Data
public class SetupForm {
    private String username = "";
    private String password = "";
    private String confirmPassword = "";
    private String systemName = "";
    private String phone = "";
    private boolean allowPublicResult = false;
    private boolean useQr = true;
}
