package gui;

import javax.swing.*;

import dao.KhachHangDAO;
import model.entities.KhachHang;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SuaThongTinKhachHangFrame extends JFrame {

    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JTextField txtCCCD;

    private JButton btnCapNhat;
    private JButton btnDong;

    private KhachHangDAO khachHangDAO = new KhachHangDAO();

    public SuaThongTinKhachHangFrame(KhachHang kh) {

        setTitle("Sửa thông tin khách hàng");
        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5,2,10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel lblMaKH = new JLabel("Mã KH");
        JLabel lblTenKH = new JLabel("Tên KH");
        JLabel lblSDT = new JLabel("CCCD");
        JLabel lblCCCD = new JLabel("Số điện thoại");

        txtMaKH = new JTextField();
        txtTenKH = new JTextField();
        txtSDT = new JTextField();
        txtCCCD = new JTextField();

        txtMaKH.setEditable(false); // không cho sửa mã

        btnCapNhat = new JButton("Cập nhật");
        btnDong = new JButton("Đóng");

        panel.add(lblMaKH);
        panel.add(txtMaKH);

        panel.add(lblTenKH);
        panel.add(txtTenKH);

        panel.add(lblSDT);
        panel.add(txtSDT);

        panel.add(lblCCCD);
        panel.add(txtCCCD);

        panel.add(btnCapNhat);
        panel.add(btnDong);

        add(panel);

        // HIỂN THỊ THÔNG TIN KHÁCH HÀNG
        txtMaKH.setText(kh.getMaKhachHang());
        txtTenKH.setText(kh.getHoTen());
        txtSDT.setText(kh.getSoDienThoai());
        txtCCCD.setText(kh.getCCCD());

        // NÚT CẬP NHẬT
        btnCapNhat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                KhachHang khUpdate = new KhachHang();

                khUpdate.setMaKhachHang(txtMaKH.getText());
                khUpdate.setHoTen(txtTenKH.getText());
                khUpdate.setSoDienThoai(txtSDT.getText());
                khUpdate.setCCCD(txtCCCD.getText());

                boolean kq = khachHangDAO.update(khUpdate);

                if(kq){
                    JOptionPane.showMessageDialog(null,"Cập nhật thành công");
                    dispose();
                }else{
                    JOptionPane.showMessageDialog(null,"Cập nhật thất bại");
                }
            }
        });

        btnDong.addActionListener(e -> dispose());
    }
}
