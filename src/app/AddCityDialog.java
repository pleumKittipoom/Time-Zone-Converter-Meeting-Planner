package app;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.ZoneId;

public class AddCityDialog extends JDialog {
    private final JTextField codeField = new JTextField(10);
    private final JTextField nameField = new JTextField(20);
    private final JTextField zoneField = new JTextField(20);
    private final JTextField startField = new JTextField(5); // HH:mm
    private final JTextField endField = new JTextField(5); // HH:mm

    private final CityIndex index;
    private boolean saved = false;

    public AddCityDialog(Frame owner, CityIndex index) {
        super(owner, "Add City (Admin)", true);
        this.index = index;

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        int r = 0;
        gc.gridx = 0;
        gc.gridy = r;
        form.add(new JLabel("รหัสเมือง (เช่น BKK):"), gc);
        gc.gridx = 1;
        form.add(codeField, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        form.add(new JLabel("ชื่อเมืองเต็ม (Bangkok):"), gc);
        gc.gridx = 1;
        form.add(nameField, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        form.add(new JLabel("ZoneId (เช่น Asia/Bangkok):"), gc);
        gc.gridx = 1;
        form.add(zoneField, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        form.add(new JLabel("เวลางานเริ่ม (HH:mm):"), gc);
        gc.gridx = 1;
        form.add(startField, gc);
        r++;

        gc.gridx = 0;
        gc.gridy = r;
        form.add(new JLabel("เวลางานเลิก (HH:mm):"), gc);
        gc.gridx = 1;
        form.add(endField, gc);
        r++;

        JButton ok = new JButton("บันทึก");
        JButton cancel = new JButton("ยกเลิก");

        ok.addActionListener(e -> save());
        cancel.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(ok);
        btns.add(cancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(btns, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    private void save() {
        try {
            String code = codeField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            String zoneId = zoneField.getText().trim();
            String ws = startField.getText().trim();
            String we = endField.getText().trim();

            if (code.isEmpty() || name.isEmpty() || zoneId.isEmpty() || ws.isEmpty() || we.isEmpty())
                throw new IllegalArgumentException("กรุณากรอกข้อมูลให้ครบ");

            // ตรวจ ZoneId/เวลา
            ZoneId zone = ZoneId.of(zoneId);
            LocalTime start = LocalTime.parse(ws);
            LocalTime end = LocalTime.parse(we);

            // รหัส: A–Z/0–9 ยาว 2–5 ตัว, ห้ามมีเครื่องหมายจุลภาค
            if (!code.matches("[A-Z0-9]{2,5}"))
                throw new IllegalArgumentException("รหัสเมืองต้องเป็น A–Z/0–9 ยาว 2–5 ตัว");
            if (code.contains(","))
                throw new IllegalArgumentException("ห้ามมีเครื่องหมายจุลภาคในรหัสเมือง");

            // ZoneId ต้องอยู่ในชุดที่ Java รู้จัก
            if (!java.time.ZoneId.getAvailableZoneIds().contains(zoneId))
                throw new IllegalArgumentException("ZoneId ไม่ถูกต้อง (ตัวอย่าง: Asia/Bangkok, Europe/London)");

            // ถ้ามีโค้ดเดิมอยู่แล้ว ถามยืนยัน
            if (index.exists(code)) {
                int c = JOptionPane.showConfirmDialog(this,
                        "มีรหัสเมืองนี้อยู่แล้ว ต้องการแก้ไข (overwrite) หรือไม่?",
                        "ยืนยัน", JOptionPane.YES_NO_OPTION);
                if (c != JOptionPane.YES_OPTION)
                    return;
            }

            City city = new City(code, name, zone, new WorkHours(start, end));
            index.upsert(city); // ใส่ลงแคชในหน่วยความจำ
            index.saveToCsv(); // เขียนกลับไฟล์ cities.csv

            saved = true;
            setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "ข้อมูลไม่ถูกต้อง: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
