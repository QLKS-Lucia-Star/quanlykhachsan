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
    
    private boolean isEditMode = false;

    public PhongDialog(QuanLyPhongPanel parentPanel) {
        this.parentPanel = parentPanel;
        initUI();
        setTitle("Thêm Phòng Mới");
    }

    public PhongDialog(QuanLyPhongPanel parentPanel, String maPhong, String loaiPhong, String trangThai, int soTang) {
        this.parentPanel = parentPanel;
        initUI();
        this.isEditMode = true; 
        setTitle("Cập Nhật Thông Tin Phòng");
        
        txtMaPhong.setText(maPhong.trim());
        txtMaPhong.setEditable(false); // [cite: 31] Ràng buộc khóa chính không cho sửa mã khi cập nhật
        txtMaPhong.setBackground(new Color(230, 230, 230)); 
        
        txtSoTang.setText(String.valueOf(soTang));
        
        try {
            cbLoaiPhong.setSelectedItem(TenLoaiPhong.valueOf(loaiPhong.trim()));
        } catch (Exception e) {
            System.err.println("Không thể parse loại phòng: " + loaiPhong);
        }
        
        cbTrangThai.setSelectedItem(trangThai.trim());
    }

    private void initUI() {
        setSize(400, 350);
        setLocationRelativeTo(null); 
        setModal(true); 
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
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu dữ liệu");
        btnSave.setBackground(new Color(60, 40, 35));
        btnSave.setForeground(Color.WHITE);
        btnCancel = new JButton("Hủy");
        
        pnlBottom.add(btnSave);
        pnlBottom.add(btnCancel);
        add(pnlBottom, BorderLayout.SOUTH);
        
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePhong());
    }

    private void savePhong() {
        String ma = txtMaPhong.getText().trim();
        String soTangStr = txtSoTang.getText().trim();

        // 1. Kiểm tra rỗng và định dạng Mã phòng (Pxxx) theo nghiệp vụ 
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!");
            return;
        }
        if (!ma.matches("P\\d{3}")) {
            JOptionPane.showMessageDialog(this, "Mã phòng phải có dạng Pxxx (Ví dụ: P001, P102...)"); // 
            return;
        }

        // 2. Kiểm tra riêng trường hợp số tầng rỗng
        if (soTangStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tầng!");
            return;
        }

        try {
            // 3. Kiểm tra định dạng số tầng phải là ký số 
            int tang = Integer.parseInt(soTangStr);
            if (tang <= 0) {
                JOptionPane.showMessageDialog(this, "Số tầng phải lớn hơn 0!"); // 
                return;
            }

            TenLoaiPhong loai = (TenLoaiPhong) cbLoaiPhong.getSelectedItem();
            
            String ttStr = cbTrangThai.getSelectedItem().toString().trim();
            TrangThaiPhong tt = ttStr.equals("Còn trống") ? TrangThaiPhong.CONTRONG :
                                (ttStr.equals("Đã có khách") ? TrangThaiPhong.DACOKHACH : TrangThaiPhong.BAN);

            Phong p = new Phong(ma, new LoaiPhong(loai), tt, tang);
            
            if (isEditMode) {
                if (phongDAO.update(p)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    parentPanel.initData(); 
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
            // Chỉ hiện lỗi này khi đã nhập nhưng nhập sai định dạng ký tự 
            JOptionPane.showMessageDialog(this, "Số tầng phải là ký tự số!");
        }
    }
}