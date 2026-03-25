package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.entities.LoaiPhong;
import model.entities.Phong;
import model.enums.TenLoaiPhong;
import model.enums.TrangThaiPhong;
import dao.PhongDAO;

public class PhongDialog extends JDialog {
    private JTextField txtMaPhong, txtSoTang;
    private JComboBox<TenLoaiPhong> cbLoaiPhong;
    private JComboBox<String> cbTrangThai;
    private JButton btnSave, btnCancel;
    private PhongDAO phongDAO = new PhongDAO();
    private QuanLyPhongPanel parentPanel;
    
    // Biến cờ hiệu để biết đang ở chế độ Thêm hay Sửa
    private boolean isEditMode = false;

    // Constructor 1: Dùng khi bấm nút "+ Thêm phòng"
    public PhongDialog(QuanLyPhongPanel parentPanel) {
        this.parentPanel = parentPanel;
        initUI();
        setTitle("Thêm Phòng Mới");
    }

    // Constructor 2: Dùng khi click chuột phải chọn "Sửa"
    public PhongDialog(QuanLyPhongPanel parentPanel, String maPhong, String loaiPhong, String trangThai, int soTang) {
        this.parentPanel = parentPanel;
        initUI();
        this.isEditMode = true; // Đổi cờ hiệu sang chế độ Sửa
        setTitle("Cập Nhật Thông Tin Phòng");
        
        // Đổ dữ liệu cũ lên form
        txtMaPhong.setText(maPhong);
        txtMaPhong.setEditable(false); // KHÔNG CHO SỬA MÃ PHÒNG VÌ LÀ KHÓA CHÍNH
        txtMaPhong.setBackground(new Color(230, 230, 230)); 
        
        txtSoTang.setText(String.valueOf(soTang));
        cbLoaiPhong.setSelectedItem(TenLoaiPhong.valueOf(loaiPhong));
        cbTrangThai.setSelectedItem(trangThai);
    }

    private void initUI() {
        setSize(400, 350);
        setLocationRelativeTo(null); // Giữa màn hình
        setModal(true); // Khóa màn hình nền khi đang mở
        setLayout(new BorderLayout());
        
        JPanel pnlForm = new JPanel(new GridLayout(4, 2, 10, 20));
        pnlForm.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        pnlForm.add(new JLabel("Mã phòng:"));
        txtMaPhong = new JTextField();
        pnlForm.add(txtMaPhong);
        
        pnlForm.add(new JLabel("Loại phòng:"));
        cbLoaiPhong = new JComboBox<>(TenLoaiPhong.values());
        pnlForm.add(cbLoaiPhong);
        
        pnlForm.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Còn trống", "Đã có khách", "Đang bảo trì"});
        pnlForm.add(cbTrangThai);
        
        pnlForm.add(new JLabel("Số tầng:"));
        txtSoTang = new JTextField();
        pnlForm.add(txtSoTang);
        
        add(pnlForm, BorderLayout.CENTER);
        
        // Nút chức năng
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setBackground(new Color(60, 40, 35));
        btnSave.setForeground(Color.WHITE);
btnCancel = new JButton("Hủy");
        
        pnlBottom.add(btnSave);
        pnlBottom.add(btnCancel);
        add(pnlBottom, BorderLayout.SOUTH);
        
        // Sự kiện nút bấm
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePhong());
    }

    private void savePhong() {
        try {
            String ma = txtMaPhong.getText().trim();
            int tang = Integer.parseInt(txtSoTang.getText().trim());
            TenLoaiPhong loai = (TenLoaiPhong) cbLoaiPhong.getSelectedItem();
            
            String ttStr = cbTrangThai.getSelectedItem().toString();
            TrangThaiPhong tt = ttStr.equals("Còn trống") ? TrangThaiPhong.CONTRONG :
                                (ttStr.equals("Đã có khách") ? TrangThaiPhong.DACOKHACH : TrangThaiPhong.BAN);
            
            if(ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!");
                return;
            }

            Phong p = new Phong(ma, new LoaiPhong(loai), tt, tang);
            
            // Xử lý lưu dựa theo chế độ Thêm hay Sửa
            if (isEditMode) {
                if (phongDAO.update(p)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    parentPanel.initData(); // Gọi lại hàm load bảng của QuanLyPhongPanel để cập nhật
                    dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại!");
                }
            } else {
                if (phongDAO.insert(p)) {
                    JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
                    parentPanel.initData(); 
                    dispose(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại. Có thể trùng mã phòng!");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số tầng phải là chữ số!");
        }
    }
}