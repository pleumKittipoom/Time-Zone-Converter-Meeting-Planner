// PlanPanel.java
package app;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanPanel extends JPanel {
    private final CityIndex index;

    // เปลี่ยนจาก JList<String> เป็น JList<City>
    private final JList<City> cityList;
    private final JTextField startDateField; // "yyyy-MM-dd"
    private final JSpinner durationMin;
    private final JSpinner stepMin;
    private final JSpinner days;
    private final JSpinner maxResults;

    private final JTable table;
    private List<City> selectedCities = new ArrayList<>();

    public PlanPanel(CityIndex index) {
        this.index = index;
        setLayout(new BorderLayout(10, 10));

        // ใช้ City objects ตรง ๆ แล้วเรนเดอร์ให้เห็น "CODE (Name)"
        cityList = new JList<>(index.all().toArray(new City[0]));
        cityList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        cityList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof City c) {
                    setText(c.code() + " (" + c.name() + ")");
                }
                return this;
            }
        });

        startDateField = new JTextField(10);
        startDateField.setText(LocalDate.now().toString());
        durationMin = new JSpinner(new SpinnerNumberModel(60, 15, 480, 15));
        stepMin = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
        days = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        maxResults = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));

        JButton findBtn = new JButton("ค้นหาเวลาประชุม");
        findBtn.addActionListener(e -> doFind());

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(new JLabel("เลือกเมือง (Ctrl/Shift เพื่อเลือกหลายเมือง):"));
        left.add(new JScrollPane(cityList));
        left.add(Box.createVerticalStrut(10));
        left.add(Ui.row(new JLabel("วันเริ่มค้นหา (YYYY-MM-DD): "), startDateField));
        left.add(Ui.row(new JLabel("ความยาว (นาที): "), durationMin));
        left.add(Ui.row(new JLabel("ก้าวเวลา step (นาที): "), stepMin));
        left.add(Ui.row(new JLabel("ค้นหากี่วันถัดไป: "), days));
        left.add(Ui.row(new JLabel("จำนวนผลลัพธ์สูงสุด: "), maxResults));
        left.add(Box.createVerticalStrut(10));
        left.add(findBtn);

        table = new JTable();
        table.setAutoCreateRowSorter(true);

        add(left, BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void doFind() {
        try {
            var selected = cityList.getSelectedValuesList(); // ได้ List<City> แล้ว
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกอย่างน้อย 2 เมือง", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            selectedCities = new ArrayList<>(selected);
            if (selectedCities.size() < 2) {
                JOptionPane.showMessageDialog(this, "ต้องมีอย่างน้อย 2 เมือง", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            Duration duration = Duration.ofMinutes((Integer) durationMin.getValue());
            Duration step = Duration.ofMinutes((Integer) stepMin.getValue());
            int d = (Integer) days.getValue();
            int max = (Integer) maxResults.getValue();

            MeetingPlanner planner = new MeetingPlanner();
            var slots = planner.plan(selectedCities, start, duration, step, d, max);

            table.setModel(new SlotTableModel(slots, selectedCities));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "อินพุตไม่ถูกต้อง: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshCities() {
        cityList.setListData(index.all().toArray(new City[0]));
    }

}
