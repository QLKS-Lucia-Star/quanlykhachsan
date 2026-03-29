package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import dao.NhanVienDAO;
import model.entities.NhanVien;

import java.awt.*;
import java.awt.event.*;

public class DangNhapJFrame extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private NhanVienDAO nvDAO = new NhanVienDAO();

    private final Color PRIMARY_BROWN = new Color(78, 52, 46);
    private final Color BG_COLOR = new Color(245, 241, 234);
    private final Color TEXT_DARK = new Color(50, 30, 20);
    private final Color BTN_GOLD_DEFAULT = new Color(190, 150, 80);
    private final Color BTN_GOLD_HOVER = new Color(210, 170, 100);
    private final Color BTN_TEXT_COLOR = new Color(70, 45, 10);

    public DangNhapJFrame() {
        setTitle("Lucia Star - Đăng nhập");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel pnlMain = new JPanel(new GridLayout(1, 2));

        // LEFT
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(PRIMARY_BROWN);
        JLabel lblHotelName = new JLabel("LUCIA STAR");
        lblHotelName.setFont(new Font("Serif", Font.BOLD, 36));
        lblHotelName.setForeground(new Color(230, 210, 160));
        pnlLeft.add(lblHotelName);

        // RIGHT
        JPanel pnlRight = new JPanel();
        pnlRight.setBackground(BG_COLOR);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(new EmptyBorder(60, 60, 60, 60));

        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP");
        lblLoginTitle.setFont(new Font("Serif", Font.BOLD, 30));
        lblLoginTitle.setForeground(TEXT_DARK);
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        // ✅ FIX INPUT USERNAME (LUCIA0001)
        ((javax.swing.text.AbstractDocument) txtUsername.getDocument())
                .setDocumentFilter(new javax.swing.text.DocumentFilter() {
                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text,
                                        javax.swing.text.AttributeSet attrs)
                            throws javax.swing.text.BadLocationException {

                        if (text == null) return;

                        // Cho phép chữ + số
                        if (!text.matches("[a-zA-Z0-9]*")) return;

                        int currentLength = fb.getDocument().getLength();
                        int newLength = currentLength + text.length() - length;

                        // max 9 ký tự: LUCIA0001
                        if (newLength <= 9) {
                            super.replace(fb, offset, length, text.toUpperCase(), attrs);
                        }
                    }
                });

        txtUsername.setText("LUCIA"); // auto prefix

        JPanel pnlUserField = createCustomInput("Mã nhân viên:", txtUsername);
        JPanel pnlPassField = createCustomInput("Mật khẩu:", txtPassword);

        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBackground(BTN_GOLD_DEFAULT);
        btnLogin.setForeground(BTN_TEXT_COLOR);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(BTN_GOLD_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(BTN_GOLD_DEFAULT);
            }
        });

        btnLogin.addActionListener(this);
        txtPassword.addActionListener(this);

        pnlRight.add(Box.createVerticalGlue());
        pnlRight.add(lblLoginTitle);
        pnlRight.add(Box.createVerticalStrut(40));
        pnlRight.add(pnlUserField);
        pnlRight.add(Box.createVerticalStrut(20));
        pnlRight.add(pnlPassField);
        pnlRight.add(Box.createVerticalStrut(35));
        pnlRight.add(btnLogin);
        pnlRight.add(Box.createVerticalGlue());

        pnlMain.add(pnlLeft);
        pnlMain.add(pnlRight);
        add(pnlMain);
    }

    private JPanel createCustomInput(String labelText, JTextField textField) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(120, 100, 90));

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        textField.setOpaque(false);
        textField.setBorder(new MatteBorder(0, 0, 1, 0, new Color(180, 160, 150)));

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(textField, BorderLayout.CENTER);
        return pnl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        // validate rỗng
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // validate format LUCIA0001
        if (!user.matches("LUCIA\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Mã NV phải dạng LUCIAxxx");
            return;
        }

        // login
        if (nvDAO.authenticate(user, pass)) {
            NhanVien staff = nvDAO.getById(user);

            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!\nXin chào: " + staff.getHoTen());

            SwingUtilities.invokeLater(() -> {
                new MainFrame(staff).setVisible(true);
                this.dispose();
            });

        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu!");

            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
