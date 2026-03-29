package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.BangGiaDichVuDAO;
import model.entities.BangGiaDichVu;
import model.entities.BangGiaDichVu_ChiTiet;
import java.util.List;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BangGiaDichVuPanel extends JPanel {
    private JTable tblBangGia;
    private DefaultTableModel model;
    private JPopupMenu popupMenu;

    // Tông màu chủ đạo
    private final Color PRIMARY_BROWN = new Color(74, 45, 42);
    private final Color SECONDARY_BROWN = new Color(110, 85, 80);
    private final Color SELECTION_COLOR = new Color(245, 235, 220);

    public BangGiaDichVuPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 1. Header: Tiêu đề và các nút chức năng ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblHeader = new JLabel("QUẢN LÝ BẢNG GIÁ DỊCH VỤ");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(PRIMARY_BROWN);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pnlActions.setBackground(Color.WHITE);

        JButton btnThemDV = new JButton("Thêm Dịch Vụ");
        styleHeaderButton(btnThemDV, SECONDARY_BROWN);
        btnThemDV.addActionListener(e -> {
            ThemDichVuDialog dialog = new ThemDichVuDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
        });

        // Nút Thêm Bảng Giá (Tạo đợt giá mới)
        JButton btnThemBG = new JButton("Thêm Bảng Giá");
        styleHeaderButton(btnThemBG, PRIMARY_BROWN);
        btnThemBG.addActionListener(e -> {
            ThemBangGiaDialog dialog = new ThemBangGiaDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            loadDataFromDatabase(); 
        });

        pnlActions.add(btnThemDV);
        pnlActions.add(btnThemBG);

        pnlHeader.add(lblHeader, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. Center: Bảng dữ liệu ---
        String[] columns = {"Mã Bảng Giá", "Tên Bảng Giá", "Ngày Áp Dụng", "Ngày Hết Hạn", "Trạng Thái"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblBangGia = new JTable(model);
        setupTableAppearance();
        loadDataFromDatabase();

        // Thêm Popup Menu và sự kiện Mouse
        createPopupMenu();
        tblBangGia.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = tblBangGia.getSelectedRow();
                    if (row != -1) handleEdit(row); // Double click để sửa
                }
            }
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tblBangGia.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        tblBangGia.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBangGia);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleHeaderButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        btn.setBorder(BorderFactory.createEmptyBorder());
    }

    private void setupTableAppearance() {
        tblBangGia.setRowHeight(40);
        tblBangGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblBangGia.setSelectionBackground(SELECTION_COLOR);
        tblBangGia.setSelectionForeground(PRIMARY_BROWN);
        tblBangGia.setShowGrid(false);
        tblBangGia.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tblBangGia.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_BROWN);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblBangGia.getColumnCount(); i++) {
            tblBangGia.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void loadDataFromDatabase() {
        model.setRowCount(0);
        BangGiaDichVuDAO dao = new BangGiaDichVuDAO();
        List<BangGiaDichVu> list = dao.getAllBangGia();

        if (list == null) return;

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (BangGiaDichVu bg : list) {
            model.addRow(new Object[]{
                bg.getMaBangGia(),
                bg.getTenBangGia(),
                sdf.format(bg.getNgayApDung()),
                sdf.format(bg.getNgayHetHieuLuc()),
                bg.getTrangThai() == 1 ? "Đang áp dụng" : "Ngưng áp dụng"
            });
        }
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JMenuItem itemEdit = new JMenuItem(" Chỉnh sửa chi tiết");
        itemEdit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemEdit.addActionListener(e -> handleEdit(tblBangGia.getSelectedRow()));

        JMenuItem itemDelete = new JMenuItem(" Xóa bảng giá");
        itemDelete.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemDelete.setForeground(Color.RED);
        itemDelete.addActionListener(e -> handleDelete(tblBangGia.getSelectedRow()));

        popupMenu.add(itemEdit);
        popupMenu.addSeparator();
        popupMenu.add(itemDelete);
    }

    private void handleEdit(int row) {
        if (row == -1) return;
        String maBG = model.getValueAt(row, 0).toString();
        BangGiaDichVuDAO dao = new BangGiaDichVuDAO();
        BangGiaDichVu bg = dao.getBangGiaByMa(maBG);
        List<BangGiaDichVu_ChiTiet> dsChiTiet = dao.getChiTietByMa(maBG);

        if (bg != null) {
            SuaBangGiaDialog dialog = new SuaBangGiaDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), bg, dsChiTiet
            );
            dialog.setVisible(true);
            loadDataFromDatabase();
        }
    }

    private void handleDelete(int row) {
        if (row == -1) return;
        String ma = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa bảng giá: " + ma + "?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // BangGiaDichVuDAO dao = new BangGiaDichVuDAO();
            // if(dao.delete(ma)) { ... }
            model.removeRow(row);
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        }
    }
}