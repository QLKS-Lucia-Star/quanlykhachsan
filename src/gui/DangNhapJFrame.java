package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import dao.NhanVienDAO;
import model.entities.NhanVien;
import model.enums.ChucVu;

import java.awt.*;
import java.awt.event.*;

public class DangNhapJFrame extends JFrame implements ActionListener {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private NhanVienDAO    nvDAO = new NhanVienDAO();

    private final Color PRIMARY_BROWN    = new Color(78, 52, 46);
    private final Color BG_COLOR         = new Color(245, 241, 234);
    private final Color TEXT_DARK        = new Color(50, 30, 20);
    private final Color BTN_GOLD_DEFAULT = new Color(190, 150, 80);
    private final Color BTN_GOLD_HOVER   = new Color(210, 170, 100);
    private final Color BTN_TEXT_COLOR   = new Color(70, 45, 10);

    public DangNhapJFrame() {
        setTitle("Lucia Star - Đăng nhập");
        setSize(850, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel pnlMain = new JPanel(new GridLayout(1, 2));

        // --- Branding trái ---
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(PRIMARY_BROWN);
        JLabel lblHotelName = new JLabel("LUCIA STAR");
        lblHotelName.setFont(new Font("Serif", Font.BOLD, 36));
        lblHotelName.setForeground(new Color(230, 210, 160));
        pnlLeft.add(lblHotelName);

        // --- Form phải ---
        JPanel pnlRight = new JPanel();
        pnlRight.setBackground(BG_COLOR);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(new EmptyBorder(60, 60, 60, 60));

        JLabel lblLoginTitle = new JLabel("ĐĂNG NHẬP");
        lblLoginTitle.setFont(new Font("Serif", Font.BOLD, 30));
        lblLoginTitle.setForeground(TEXT_DARK);
        lblLoginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pnlUserField = createCustomInput("Tên đăng nhập:", txtUsername = new JTextField());
        ((javax.swing.text.AbstractDocument) txtUsername.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) 
                    throws javax.swing.text.BadLocationException {
                
                // 1. Kiểm tra xem văn bản mới nhập vào có phải là số không
                if (text != null && !text.matches("\\d*")) {
                    return; // Nếu không phải số thì không làm gì cả (chặn lại)
                }

                // 2. Kiểm tra độ dài tổng cộng sau khi thêm text mới
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length() - length) - 8; // 8 là giới hạn ký tự
                
                if (overLimit <= 0) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    // Nếu vượt quá 8 ký tự, chỉ lấy phần vừa đủ 8
                    if (text.length() > overLimit) {
                        String subText = text.substring(0, text.length() - overLimit);
                        super.replace(fb, offset, length, subText, attrs);
                    }
                }
            }
        });
        JPanel pnlPassField = createCustomInput("Mật khẩu:",      txtPassword = new JPasswordField());

        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBackground(BTN_GOLD_DEFAULT);
        btnLogin.setForeground(BTN_TEXT_COLOR);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnLogin.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(BTN_GOLD_HOVER); }
            public void mouseExited(MouseEvent e)  { btnLogin.setBackground(BTN_GOLD_DEFAULT); }
});
        btnLogin.addActionListener(this);

        // Enter để đăng nhập
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

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nvDAO.authenticate(user, pass)) {
            // Lấy thông tin NhanVien đầy đủ (bao gồm vaiTro)
            NhanVien staff = nvDAO.getById(user);

            JOptionPane.showMessageDialog(this,
                    "Đăng nhập thành công!\nXin chào: " + (staff != null ? staff.getHoTen() : user),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                new MainFrame(staff).setVisible(true);
                this.dispose();
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tài khoản hoặc mật khẩu! Vui lòng kiểm tra lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}