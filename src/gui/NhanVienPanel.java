package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;

import dao.NhanVienDAO;
import model.entities.NhanVien;
import model.enums.ChucVu;

public class NhanVienPanel extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private DefaultTableModel dtmNhanVien;
    private JTable tblNhanVien;
    private JLabel lblSubTitle;
    private final boolean isAdmin;

    public NhanVienPanel(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 241, 234));
        setBorder(new EmptyBorder(10, 30, 10, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);

        initData();
    }

    private JPanel createHeader() {
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Staff List");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 32));
        lblTitle.setForeground(new Color(60, 40, 30));

        lblSubTitle = new JLabel("0 nhân viên");
        lblSubTitle.setForeground(Color.GRAY);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubTitle);

        topPanel.add(titlePanel, BorderLayout.WEST);

        if (isAdmin) {
            JButton btnAdd = new JButton("+ Thêm nhân viên");
            btnAdd.setBackground(new Color(60, 40, 35));
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setFocusPainted(false);
            btnAdd.addActionListener(e -> showFormDialog(null));
            topPanel.add(btnAdd, BorderLayout.EAST);
        }

        pnlMain.add(topPanel);
        pnlMain.add(Box.createVerticalStrut(20));
        return pnlMain;
    }

    private JScrollPane createTable() {
        String[] cols = isAdmin 
            ? new String[]{"STT", "Mã NV", "Họ tên", "SĐT", "Ngày vào", "Hệ số", "Chức vụ", "Thao tác"}
            : new String[]{"STT", "Mã NV", "Họ tên", "SĐT", "Ngày vào", "Hệ số", "Chức vụ"};

        dtmNhanVien = new DefaultTableModel(null, cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return isAdmin && c == (isAdmin ? 7 : -1); 
            }
        };

        tblNhanVien = new JTable(dtmNhanVien);
        tblNhanVien.setRowHeight(45);

        if (isAdmin) {
            TableColumn actionCol = tblNhanVien.getColumnModel().getColumn(7);
            actionCol.setPreferredWidth(160);
            actionCol.setCellRenderer(new ActionButtonRenderer());
            // Khởi tạo Editor
            actionCol.setCellEditor(new ActionButtonEditor(this));
        }

        JTableHeader header = tblNhanVien.getTableHeader();
        header.setBackground(new Color(90, 55, 45));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        return new JScrollPane(tblNhanVien);
    }

    public void initData() {
        dtmNhanVien.setRowCount(0);
        List<NhanVien> ds = nhanVienDAO.getAll();
        if (ds == null) return; 

        int stt = 1;
        for (NhanVien nv : ds) {
            String ngay = nv.getNgayVaoLam() == null ? "" : nv.getNgayVaoLam().format(FMT);
            String cvStr = (nv.getChucVu() == ChucVu.QUAN_LY) ? "Quản lý" : "Nhân viên";

            Object[] row = isAdmin 
                ? new Object[]{stt++, nv.getMaNhanVien(), nv.getHoTen(), nv.getSoDienThoai(), ngay, nv.getHeSoLuong(), cvStr, "action"}
                : new Object[]{stt++, nv.getMaNhanVien(), nv.getHoTen(), nv.getSoDienThoai(), ngay, nv.getHeSoLuong(), cvStr};
            dtmNhanVien.addRow(row);
        }
        lblSubTitle.setText(ds.size() + " nhân viên");
    }

    public void showFormDialog(NhanVien nvEdit) {
        JOptionPane.showMessageDialog(this, "Chức năng đang mở cho: " + (nvEdit != null ? nvEdit.getHoTen() : "Thêm mới"));
    }

    public void confirmDelete(String ma, String ten) {
        int c = JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + ten + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            nhanVienDAO.delete(ma);
            initData();
        }
    }

    // --- RENDERER ---
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit = new JButton("Sửa");
        private final JButton btnDel = new JButton("Xóa");

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            add(btnEdit);
            add(btnDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Đảm bảo màu nền thay đổi khi chọn dòng
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    // --- EDITOR (Đã dọn dẹp lỗi thừa) ---
    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        private final JButton btnEdit = new JButton("Sửa");
        private final JButton btnDel = new JButton("Xóa");
        private final NhanVienPanel parent;
        private String currentMa;
        private String currentTen;

        public ActionButtonEditor(NhanVienPanel parent) {
            this.parent = parent;
            container.setOpaque(true); // Đảm bảo màu nền hiển thị đúng
            container.add(btnEdit);
            container.add(btnDel);

            btnEdit.addActionListener(e -> {
                // Lấy mã từ biến tạm đã lưu khi click
                NhanVien nv = parent.nhanVienDAO.getById(currentMa);
                fireEditingStopped(); 
                parent.showFormDialog(nv);
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                parent.confirmDelete(currentMa, currentTen);
            });
        }

        // Đây là hàm quan trọng nhất của Editor
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Lấy dữ liệu ẩn từ model trước khi hiển thị Editor
            currentMa = table.getValueAt(row, 1).toString();
            currentTen = table.getValueAt(row, 2).toString();
            
            container.setBackground(table.getSelectionBackground());
            return container;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}