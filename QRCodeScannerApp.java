import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;

public class QRCodeScannerApp extends JFrame {
    private JTextArea resultArea;
    private boolean scanning = true;
    private Webcam webcam;

    public QRCodeScannerApp() {
        setTitle("📷 QR Code Scanner App");
        setSize(640, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        
        webcam = Webcam.getDefault();
        webcam.setViewSize(new java.awt.Dimension(320, 240));
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setMirrored(true);
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.NORTH);

        
        resultArea = new JTextArea(5, 20);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setBackground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createTitledBorder("Scanned Result"));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        
        JButton resetButton = new JButton("Reset Scanner");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setBackground(new Color(220, 220, 220));
        resetButton.addActionListener(e -> {
            resultArea.setText("");
            scanning = true;
        });
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(resetButton);
        add(bottomPanel, BorderLayout.SOUTH);

        
        new Thread(this::scanLoop).start();

        setVisible(true);
    }

    private void scanLoop() {
        while (true) {
            if (!scanning) continue;

            BufferedImage image = webcam.getImage();
            if (image != null) {
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    String text = result.getText();
                    resultArea.setText("Scanned: " + text);
                    if (text.startsWith("http")) {
                        Desktop.getDesktop().browse(new URI(text));
                    }
                    scanning = false; 
                } catch (Exception e) {
                   
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRCodeScannerApp::new);
    }
}
