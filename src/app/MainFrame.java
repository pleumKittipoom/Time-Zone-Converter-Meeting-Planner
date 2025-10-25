package app;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class MainFrame extends JFrame {
    private final CityIndex index;
    private final TimeZoneService tz;
    private final ConvertPanel convertPanel;
    private final PlanPanel planPanel;

    public MainFrame() {
        super("Time Zone Converter + Meeting Planner (Swing)");
        Ui.initLookAndFeel();

        index = new CityIndex(Path.of("cities.csv"));
        tz = new TimeZoneService();

        // สร้างเมนู
        JMenuBar mb = new JMenuBar();
        JMenu admin = new JMenu("Admin");
        JMenuItem addCity = new JMenuItem("Add City…");
        addCity.addActionListener(e -> openAddCity());
        admin.add(addCity);
        mb.add(admin);
        setJMenuBar(mb);

        // แท็บ
        JTabbedPane tabs = new JTabbedPane();
        convertPanel = new ConvertPanel(index, tz);
        planPanel = new PlanPanel(index);
        tabs.addTab("Convert Time", convertPanel);
        tabs.addTab("Plan Meeting", planPanel);

        setContentPane(tabs);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
    }

    private void openAddCity() {
        // เช็คสิทธิ์แบบง่าย ๆ (รหัส: admin123)
        if (!checkAdminPassword())
            return;

        AddCityDialog dlg = new AddCityDialog(this, index);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            // โหลดกลับจากไฟล์อีกรอบให้ชัวร์ แล้วรีเฟรช UI
            index.reload();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "เพิ่ม/อัปเดตเมืองเรียบร้อย");
        }
    }

    private boolean checkAdminPassword() {
        JPasswordField pf = new JPasswordField();

        // ถ้าอยากให้แน่ใจว่าอ่านไทยได้ ตั้งฟอนต์ที่รองรับไทยด้วย
        // pf.setFont(Ui.pickThaiFont(14)); // ถ้ามีเมธอดนี้ใน Ui.java ตามที่ผมให้ไว้ก่อนหน้า

        // แก้ปัญหา “สี่เหลี่ยม” โดยกำหนดตัวปิดบังเอง (เลือกอย่างใดอย่างหนึ่ง)
        // pf.setEchoChar('\u25CF'); // ● แทบทุกฟอนต์มี
        // หรือ
        pf.setEchoChar('*');

        int ok = JOptionPane.showConfirmDialog(this, pf, "รหัสผู้ดูแล",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION)
            return false;

        String pass = new String(pf.getPassword());
        if (!"admin123".equals(pass)) {
            JOptionPane.showMessageDialog(this, "รหัสผ่านไม่ถูกต้อง", "Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void refreshAllPanels() {
        convertPanel.refreshCities();
        planPanel.refreshCities();
    }
}
