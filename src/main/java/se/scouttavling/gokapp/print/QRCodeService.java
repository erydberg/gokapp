package se.scouttavling.gokapp.print;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QRCodeService {

    public byte[] generateQRCode(String data, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public String generateQRCodeDataUrl(Integer patrolId) {
        // Generate QR with just the patrol ID
        byte[] qrCode = null;
        try {
            qrCode = generateQRCode(patrolId.toString(), 150, 150);
            String base64 = Base64.getEncoder().encodeToString(qrCode);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            return null;
        }
    }

}
