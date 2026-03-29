package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.BangGiaDichVuDAO;
import dao.DichVuDAO;
import model.entities.BangGiaDichVu_ChiTiet;
import model.entities.DichVu;

import java.util.List;

import java.awt.*;

public class ChiTietBangGiaDialog extends JDialog {
    private DefaultTableModel model;
	private JTable table;

	public ChiTietBangGiaDialog(Frame parent, String ma, String ten, String dvt, String gia, String tinhTrang) {
        super(parent, "Chi tiết bảng giá dịch vụ", true);
        setSize(600, 500); // Tăng kích thước để chứa bảng
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- PHẦN 1: HEADER (Thông tin chung) ---
        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 15, 15),
            new TitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Thông tin nhóm dịch vụ")
        ));

        addInfoRow(headerPanel, "Mã nhóm:", ma);
        addInfoRow(headerPanel, "Tên nhóm:", ten);
        addInfoRow(headerPanel, "Trạng thái:", tinhTrang);
        // (Bạn có thể thêm các dòng thông tin khác ở đây)

        // --- PHẦN 2: DETAIL (Bảng danh sách dịch vụ chi tiết) ---
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        JLabel lblDetail = new JLabel("Danh sách dịch vụ chi tiết:");
        lblDetail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDetail.setBorder(new EmptyBorder(0, 0, 10, 0));
        detailPanel.add(lblDetail, BorderLayout.NORTH);

        // Tạo bảng chi tiết
        String[] columns = {"STT", "Mã dịch vụ", "Tên dịch vụ", "Giá", "Loại","Ghi chú"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Style cho bảng để đồng bộ với giao diện Luxury
        styleTable(table);

        // Thêm dữ liệu mẫu (Thực tế bạn sẽ truyền List dữ liệu vào đây)
        loadDataFromDatabase(ma);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        detailPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PHẦN 3: FOOTER (Nút đóng) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 245));
        
        JButton btnClose = new JButton("Đóng lại");
        btnClose.setFocusPainted(false);
        btnClose.setBackground(new Color(74, 45, 42)); // Màu nâu Luxury
        btnClose.setForeground(Color.WHITE);
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());
        btnPanel.add(btnClose);

        // Thêm các phần vào Dialog
        add(headerPanel, BorderLayout.NORTH);
        add(detailPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(lbl);
        row.add(val);
        panel.add(row);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(74, 45, 42));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(240, 230, 210));
    }
    
    private void loadDataFromDatabase(String maBangGia) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        // Gọi DAO để lấy List<BangGiaDichVu_ChiTiet>
        BangGiaDichVuDAO dao = new BangGiaDichVuDAO();
        List<BangGiaDichVu_ChiTiet> list = dao.getChiTietByMa(maBangGia);
        
        if (list == null || list.isEmpty()) {
            return;
        }

        java.text.DecimalFormat df = new java.text.DecimalFormat("#,### VNĐ");
        int stt = 1;
        DichVuDAO dvDao = new DichVuDAO();
        for (BangGiaDichVu_ChiTiet ct : list) {
        	DichVu dv = dvDao.getServiceByID(ct.getMaDichVu().getMaDichVu());
            model.addRow(new Object[]{
                stt++,
                dv.getMaDichVu(), 
                dv.getTenDichVu(),
                df.format(ct.getGiaDichVu()),     
                ct.getDonViTinh(),                 
                ct.getSoLuong()                   
            });
        }
    }
}