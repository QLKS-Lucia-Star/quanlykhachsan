package gui;

import javax.swing.*;
import javax.swing.border.*;
import dao.PhongDAO;
import model.entities.Phong;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class ChonPhongFrame extends JDialog {
    private JPanel pnlGridRooms;
    private JButton btnConfirm, btnClose;
    private List<String> tempSelectedRooms = new ArrayList<>();
    private String finalResult = "";
    private PhongDAO phongDAO = new PhongDAO();

    public ChonPhongFrame(String loaiPhong, java.util.Date dIn, java.util.Date dOut) {
        setTitle("Hệ thống chọn phòng trống - " + loaiPhong);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setModal(true); // Khóa màn hình chính khi mở dialog này
        setLayout(new BorderLayout());

        // Header
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(150, 100, 60));
        JLabel lblTitle = new JLabel("CHỌN PHÒNG TRỐNG (" + loaiPhong + ")");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // Center: Danh sách phòng dạng Grid
        pnlGridRooms = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlGridRooms.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(pnlGridRooms);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        // Footer: Nút xác nhận
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        btnConfirm = new JButton("Xác nhận chọn");
        btnClose = new JButton("Đóng");
        
        // Style nút
        btnConfirm.setBackground(new Color(110, 60, 45));
        btnConfirm.setForeground(Color.WHITE);
        
        pnlFooter.add(btnConfirm);
        pnlFooter.add(btnClose);
        add(pnlFooter, BorderLayout.SOUTH);

        // Load dữ liệu
        loadRooms(loaiPhong, dIn, dOut);

        // Sự kiện
        btnConfirm.addActionListener(e -> {
            finalResult = String.join(", ", tempSelectedRooms);
            dispose();
        });

        btnClose.addActionListener(e -> {
            finalResult = "";
            dispose();
        });
    }

    private void loadRooms(String loaiPhong, java.util.Date dIn, java.util.Date dOut) {
        java.sql.Date sqlIn = new java.sql.Date(dIn.getTime());
        java.sql.Date sqlOut = new java.sql.Date(dOut.getTime());

        List<Phong> ds = phongDAO.getDanhSachPhongTrong(loaiPhong, sqlIn, sqlOut);
        
        if (ds.isEmpty()) {
            pnlGridRooms.add(new JLabel("Không tìm thấy phòng nào còn trống!"));
        } else {
            for (Phong p : ds) {
                pnlGridRooms.add(createRoomToggleButton(p.getMaPhong()));
            }
        }
    }

    private JToggleButton createRoomToggleButton(String maPhong) {
        // Sử dụng JToggleButton để nút có trạng thái "Dính" khi nhấn vào
        JToggleButton btn = new JToggleButton("PHÒNG " + maPhong.substring(1));
        btn.setPreferredSize(new Dimension(100, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(Color.LIGHT_GRAY));

        btn.addActionListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(new Color(150, 100, 60));
                btn.setForeground(Color.WHITE);
                tempSelectedRooms.add(maPhong);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
                tempSelectedRooms.remove(maPhong);
            }
        });

        return btn;
    }

    public String getSelectedRoomList() {
        return finalResult;
    }
}