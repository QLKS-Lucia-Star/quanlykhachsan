package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import dao.PhongDAO;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import model.entities.Phong;
import model.enums.TrangThaiPhong;

public class QuanLyPhongPanel extends JPanel {
    private PhongDAO phongDAO = new PhongDAO();
    private DefaultTableModel dtmPhong;
    private JTable tblPhong;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public QuanLyPhongPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 241, 234));
        setBorder(new EmptyBorder(10, 30, 10, 20));

        JScrollPane scrollTable = createTable();
        add(createHeader(), BorderLayout.NORTH);
        add(scrollTable, BorderLayout.CENTER);
        
        initData(); 
    }

    public JPanel createHeader() {
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Room Management");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 32));
        lblTitle.setForeground(new Color(60, 40, 30));

        JLabel lblSubTitle = new JLabel("Quản lý danh sách phòng");
        lblSubTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubTitle.setForeground(Color.GRAY);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubTitle);

        JButton btnAdd = new JButton("+ Thêm phòng");
        // --- ĐÃ ĐỔI MÀU NỀN SANG XANH LÁ (Success Green) ---
        btnAdd.setBackground(new Color(40, 167, 69)); 
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(160, 20)); 
        
        btnAdd.addActionListener(e -> {
            new PhongDialog(QuanLyPhongPanel.this).setVisible(true);
        });

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);

        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardPanel.setOpaque(false);
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        cardPanel.add(createStatCard("TỔNG SỐ PHÒNG", "20", Color.BLACK));
        cardPanel.add(createStatCard("PHÒNG TRỐNG", "15", new Color(40, 100, 80)));
        cardPanel.add(createStatCard("ĐANG CÓ KHÁCH", "3", new Color(180, 150, 100)));
cardPanel.add(createStatCard("ĐANG BẢO TRÌ", "2", new Color(150, 50, 50)));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        String placeholderText = "Tìm kiếm bằng mã phòng, loại phòng...";
        JTextField txtSearch = new JTextField(placeholderText);
        txtSearch.setBorder(null);
        txtSearch.setFont(new Font("SansSerif", Font.ITALIC, 14));
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals(placeholderText)) {
                    txtSearch.setText("");
                    txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setFont(new Font("SansSerif", Font.ITALIC, 14));
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(placeholderText);
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtSearch.getText();
                if (text.equals(placeholderText) || text.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text.trim(), 1, 2));
                }
            }
        });

        JLabel lblSearchIcon = new JLabel("🔍 ");
        searchPanel.add(lblSearchIcon, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        pnlMain.add(topPanel);
        pnlMain.add(Box.createVerticalStrut(20)); 
        pnlMain.add(cardPanel);
        pnlMain.add(Box.createVerticalStrut(20)); 
        pnlMain.add(searchPanel);
        pnlMain.add(Box.createVerticalStrut(25)); 

        return pnlMain;
    }

    private JPanel createStatCard(String title, String value, Color valueColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(Color.LIGHT_GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblValue.setForeground(valueColor);

        card.add(lblTitle);
        card.add(lblValue);
        return card;
    }

    private JScrollPane createTable() {
        String[] columns = {"Số thứ tự", "Mã phòng", "Loại phòng", "Trạng thái", "Số tầng"};
        dtmPhong = new DefaultTableModel(null, columns);
        tblPhong = new JTable(dtmPhong);
        
        rowSorter = new TableRowSorter<>(dtmPhong);
        tblPhong.setRowSorter(rowSorter);

        tblPhong.setRowHeight(40);
        tblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tblPhong.setGridColor(new Color(240, 240, 240));
        tblPhong.setShowVerticalLines(false);
        tblPhong.setSelectionBackground(new Color(220, 210, 200));

        JTableHeader header = tblPhong.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(90, 55, 45));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));

        DefaultTableCellRenderer dftcr = new DefaultTableCellRenderer();
        dftcr.setHorizontalAlignment(SwingConstants.CENTER);
        tblPhong.setDefaultRenderer(Object.class, dftcr);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnuSua = new JMenuItem("✏️ Cập nhật thông tin");
        JMenuItem mnuXoa = new JMenuItem("🗑️ Xóa phòng này");
        
        // --- ĐỔI MÀU VÀ IN ĐẬM CHỮ CHO MENU CHUỘT PHẢI ---
        mnuSua.setForeground(new Color(0, 102, 204)); // Màu Xanh dương
        mnuSua.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        mnuXoa.setForeground(new Color(220, 53, 69)); // Màu Đỏ
        mnuXoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // --------------------------------------------------
        
        popupMenu.add(mnuSua);
        popupMenu.addSeparator(); 
        popupMenu.add(mnuXoa);

        tblPhong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tblPhong.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tblPhong.getRowCount()) {
                        tblPhong.setRowSelectionInterval(row, row);
                    } else {
                        tblPhong.clearSelection();
                    }

                    int rowIndex = tblPhong.getSelectedRow();
                    if (rowIndex < 0) return; 

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        mnuXoa.addActionListener(e -> {
int viewRow = tblPhong.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = tblPhong.convertRowIndexToModel(viewRow);
                String maPhong = dtmPhong.getValueAt(modelRow, 1).toString();
                
                int confirm = JOptionPane.showConfirmDialog(QuanLyPhongPanel.this,
                        "Bạn có chắc chắn muốn XÓA phòng [" + maPhong + "] không?\nDữ liệu không thể khôi phục!", 
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (phongDAO.delete(maPhong)) {
                        JOptionPane.showMessageDialog(QuanLyPhongPanel.this, "Xóa thành công!");
                        initData(); 
                    } else {
                        JOptionPane.showMessageDialog(QuanLyPhongPanel.this, "Xóa thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        mnuSua.addActionListener(e -> {
            int viewRow = tblPhong.getSelectedRow();
            if (viewRow != -1) {
                int modelRow = tblPhong.convertRowIndexToModel(viewRow);
                String maPhong = dtmPhong.getValueAt(modelRow, 1).toString();
                String loaiPhong = dtmPhong.getValueAt(modelRow, 2).toString();
                String trangThai = dtmPhong.getValueAt(modelRow, 3).toString();
                int soTang = Integer.parseInt(dtmPhong.getValueAt(modelRow, 4).toString());

                new PhongDialog(QuanLyPhongPanel.this, maPhong, loaiPhong, trangThai, soTang).setVisible(true);
            }
        });

        JScrollPane scroll = new JScrollPane(tblPhong);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    public void initData() {
        dtmPhong.setRowCount(0);
        List<Phong> dsPhong = phongDAO.getAll();

        int stt = 1; 
        for (Phong p : dsPhong) {
            String trangThai = p.getTrangThai() == TrangThaiPhong.CONTRONG ? "Còn trống" :
                              (p.getTrangThai() == TrangThaiPhong.DACOKHACH ? "Đã có khách" : "Đang bảo trì");
                              
            Object[] row = {
                stt++, 
                p.getMaPhong(), 
                p.getLoaiPhong().getTenLoaiPhong().toString(), 
                trangThai, 
                p.getSoTang()
            };
            dtmPhong.addRow(row);
        }
    }
}