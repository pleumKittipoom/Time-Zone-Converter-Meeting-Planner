package app;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SlotTableModel extends AbstractTableModel {
    private final List<MeetingPlanner.Slot> slots;
    private final List<City> cities;
    private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dfTime = DateTimeFormatter.ofPattern("HH:mm");

    public SlotTableModel(List<MeetingPlanner.Slot> slots, List<City> cities) {
        this.slots = slots;
        this.cities = cities;
    }

    @Override public int getRowCount() { return slots.size(); }
    @Override public int getColumnCount() { return 2 + cities.size(); }

    @Override public String getColumnName(int col) {
        if (col == 0) return "UTC-Date";
        if (col == 1) return "UTC";
        City c = cities.get(col - 2);
        return c.code() + " (" + c.zone().getId() + ")";
    }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        var slot = slots.get(rowIndex);
        if (columnIndex == 0) return dfDate.format(slot.startUtc);
        if (columnIndex == 1) return dfTime.format(slot.startUtc);
        City c = cities.get(columnIndex - 2);
        var zdt = slot.localTimes.get(c);
        return dfTime.format(zdt);
    }
}
