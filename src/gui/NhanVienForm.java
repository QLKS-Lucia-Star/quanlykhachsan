package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;
import model.entities.NhanVien;
import dao.NhanVienDAO;
import model.enums.ChucVu;

public class NhanVienForm extends JDialog {
    private JTextField txtTen, txtSDT, txtHeSo;
    private JComboBox<String> cbChucVu;
    private JComboBox<NhanVien> cbNguoiQuanLy; 
    private JLabel lblErrTen, lblErrSDT, lblErrHeSo, lblNQL;
    private NhanVienDAO dao = new NhanVienDAO();
    
    private final Color BROWN_DARK = new Color(70, 45, 40); 
    private final Color ERR_RED = new Color(220, 53, 69);
    private final Color BORDER_GRAY = new Color(210, 210, 210);

    public NhanVienForm(JFrame parent, NhanVien nvEdit) {
        super(parent, true);
        setSize(550, 560); 
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));

        JPanel mainCard = new RoundedPanel(30, Color.WHITE);
        mainCard.setLayout(new BorderLayout());
        setContentPane(mainCard);

        // --- HEADER ---
        JPanel header = new RoundedPanel(30, BROWN_DARK);
        header.setPreferredSize(new Dimension(0, 60));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 25, 0, 20));
        JLabel lblTitle = new JLabel(nvEdit == null ? "THÊM NHÂN VIÊN MỚI" : "CẬP NHẬT NHÂN VIÊN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);
        
        JButton btnClose = new JButton("✕");
        btnClose.setForeground(Color.WHITE);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);
        mainCard.add(header, BorderLayout.NORTH);

        // --- BODY ---
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow("Họ tên:", txtTen = createTextField(), lblErrTen = createErrLabel(), body, gbc, 0);
        addFormRow("Số điện thoại:", txtSDT = createTextField(), lblErrSDT = createErrLabel(), body, gbc, 2);
        addFormRow("Hệ số lương:", txtHeSo = createTextField(), lblErrHeSo = createErrLabel(), body, gbc, 4);

        // Chức vụ
        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(5, 0, 15, 10);
        body.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.insets = new Insets(5, 0, 15, 0);
        cbChucVu = new JComboBox<>(new String[]{"Quản lý", "Nhân viên"});
        styleComboBox(cbChucVu);
        body.add(cbChucVu, gbc);

        // Người quản lý (Fix giao diện bằng tắp lự)
        gbc.gridy = 7; gbc.gridx = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(0, 0, 15, 10);
        lblNQL = new JLabel("Người quản lý:");
        body.add(lblNQL, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 0, 15, 0);
        cbNguoiQuanLy = new JComboBox<>();
        styleComboBox(cbNguoiQuanLy);
        loadManagersToComboBox();
        body.add(cbNguoiQuanLy, gbc);

        lblNQL.setVisible(false);
        cbNguoiQuanLy.setVisible(false);
        mainCard.add(body, BorderLayout.CENTER);

        // --- FOOTER ---
        RoundedButton btnSave = new RoundedButton("LƯU DỮ LIỆU", 15);
        btnSave.setBackground(BROWN_DARK);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(0, 50));
        btnSave.addActionListener(e -> handleSave(nvEdit));
        
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 40, 30, 40));
        footer.add(btnSave, BorderLayout.SOUTH);
        mainCard.add(footer, BorderLayout.SOUTH);

        cbChucVu.addActionListener(e -> {
            boolean isStaff = cbChucVu.getSelectedIndex() == 1;
            lblNQL.setVisible(isStaff);
            cbNguoiQuanLy.setVisible(isStaff);
            revalidate(); repaint();
        });

        setupFocusValidation();

        if (nvEdit != null) {
            txtTen.setText(nvEdit.getHoTen());
            txtSDT.setText(nvEdit.getSoDT());
            txtHeSo.setText(String.valueOf(nvEdit.getHeSoLuong()));
            cbChucVu.setSelectedIndex(nvEdit.getRole() == ChucVu.QUAN_LY ? 0 : 1);
            if (nvEdit.getRole() == ChucVu.NHAN_VIEN) {
                lblNQL.setVisible(true);
                cbNguoiQuanLy.setVisible(true);
                for (int i = 0; i < cbNguoiQuanLy.getItemCount(); i++) {
                    if (Objects.equals(cbNguoiQuanLy.getItemAt(i).getMaNV(), nvEdit.getMaQL())) {
                        cbNguoiQuanLy.setSelectedIndex(i); break;
                    }
                }
            }
        }
    }

    private void addFormRow(String labelText, JTextField field, JLabel errLabel, JPanel parent, GridBagConstraints gbc, int y) {
        gbc.gridy = y; gbc.gridx = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(5, 0, 0, 10);
        parent.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.insets = new Insets(5, 0, 0, 0);
        parent.add(field, gbc);
        gbc.gridy = y + 1; gbc.gridx = 1;
        gbc.insets = new Insets(2, 0, 8, 0);
        parent.add(errLabel, gbc);
    }

    private JTextField createTextField() {
        JTextField t = new JTextField();
        t.setPreferredSize(new Dimension(0, 38));
        t.setBorder(new RoundedBorder(10, BORDER_GRAY));
        return t;
    }

    private JLabel createErrLabel() {
        JLabel l = new JLabel(" "); 
        l.setForeground(ERR_RED);
        l.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 11));
        l.setPreferredSize(new Dimension(0, 15)); 
        return l;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(Color.WHITE);
        cb.setBorder(new RoundedBorder(10, BORDER_GRAY));
        cb.setPreferredSize(new Dimension(0, 38));
    }

    private void setupFocusValidation() {
        FocusAdapter fa = new FocusAdapter() { @Override public void focusLost(FocusEvent e) { validateInputs(); } };
        txtTen.addFocusListener(fa); txtSDT.addFocusListener(fa); txtHeSo.addFocusListener(fa);
    }

    private void validateInputs() {
        try { lblErrTen.setText(" "); txtTen.setBorder(new RoundedBorder(10, BORDER_GRAY)); if(txtTen.getText().trim().isEmpty()) throw new Exception("Tên trống"); } 
        catch (Exception e) { lblErrTen.setText(e.getMessage()); txtTen.setBorder(new RoundedBorder(10, ERR_RED)); }
        
        try { lblErrSDT.setText(" "); txtSDT.setBorder(new RoundedBorder(10, BORDER_GRAY)); if(!txtSDT.getText().matches("0\\d{9}")) throw new Exception("SĐT 10 số, đầu 0"); } 
        catch (Exception e) { lblErrSDT.setText(e.getMessage()); txtSDT.setBorder(new RoundedBorder(10, ERR_RED)); }

        try { lblErrHeSo.setText(" "); txtHeSo.setBorder(new RoundedBorder(10, BORDER_GRAY)); Float.parseFloat(txtHeSo.getText()); } 
        catch (Exception e) { lblErrHeSo.setText("Hệ số sai"); txtHeSo.setBorder(new RoundedBorder(10, ERR_RED)); }
    }

    private void handleSave(NhanVien nvEdit) {
        validateInputs();

        // Nếu còn lỗi thì không cho lưu
        if (!lblErrTen.getText().equals(" ") || 
            !lblErrSDT.getText().equals(" ") || 
            !lblErrHeSo.getText().equals(" ")) return;

        try {
            NhanVien n = (nvEdit != null) ? nvEdit : new NhanVien();

            // =========================
            // 🚨 CHẶN CHỈ 1 QUẢN LÝ
            // =========================
            boolean isManagerSelected = cbChucVu.getSelectedIndex() == 0;

            if (isManagerSelected) {
                List<NhanVien> list = dao.getAll();

                long countQL = list.stream()
                        .filter(x -> x.getRole() == ChucVu.QUAN_LY)
                        .count();

                // ➤ THÊM MỚI
                if (nvEdit == null && countQL >= 1) {
                    JOptionPane.showMessageDialog(this, "Chỉ được phép có 1 quản lý!");
                    return;
                }

                // ➤ UPDATE (tránh block chính nó)
                if (nvEdit != null) {
                    boolean wasManager = nvEdit.getRole() == ChucVu.QUAN_LY;

                    if (!wasManager && countQL >= 1) {
                        JOptionPane.showMessageDialog(this, "Đã tồn tại quản lý!");
                        return;
                    }
                }
            }

            // =========================
            // TỰ ĐỘNG SINH MÃ
            // =========================
            if (nvEdit == null) {
                List<NhanVien> list = dao.getAll();
                int maxId = 0;

                for (NhanVien item : list) {
                    int id = Integer.parseInt(item.getMaNV().replace("LUCIA", ""));
                    if (id > maxId) maxId = id;
                }

                n.setMaNV(String.format("LUCIA%04d", maxId + 1));
            }

            // =========================
            // GÁN DỮ LIỆU
            // =========================
            n.setHoTen(txtTen.getText().trim());
            n.setSoDT(txtSDT.getText().trim());
            n.setHeSoLuong(Float.parseFloat(txtHeSo.getText().trim()));
            n.setRole(isManagerSelected ? ChucVu.QUAN_LY : ChucVu.NHAN_VIEN);

            if (n.getRole() == ChucVu.NHAN_VIEN && cbNguoiQuanLy.getSelectedItem() != null) {
                n.setMaQL(((NhanVien) cbNguoiQuanLy.getSelectedItem()).getMaNV());
            } else {
                n.setMaQL(null);
            }

            // =========================
            // INSERT / UPDATE
            // =========================
            boolean ok = (nvEdit == null) ? dao.insert(n) : dao.update(n);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Thành công!");
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }


    private void loadManagersToComboBox() {
        cbNguoiQuanLy.removeAllItems();
        List<NhanVien> all = dao.getAll();
        if (all != null) all.stream().filter(n -> n.getRole() == ChucVu.QUAN_LY).forEach(cbNguoiQuanLy::addItem);
        cbNguoiQuanLy.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof NhanVien) setText(((NhanVien)v).getHoTen());
                return this;
            }
        });
    }

    // --- UI CLASSES GIỮ NGUYÊN ---
    class RoundedPanel extends JPanel {
        private int r; private Color c;
        public RoundedPanel(int r, Color c) { this.r = r; this.c = c; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r); g2.dispose();
        }
    }
    class RoundedBorder implements Border {
        private int r; private Color c;
        public RoundedBorder(int r, Color c) { this.r = r; this.c = c; }
        public Insets getBorderInsets(Component c) { return new Insets(4, 12, 4, 12); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.c); g2.drawRoundRect(x, y, w-1, h-1, r, r); g2.dispose();
        }
    }
    class RoundedButton extends JButton {
        private int r;
        public RoundedButton(String t, int r) { super(t); this.r = r; setOpaque(false); setContentAreaFilled(false); setBorderPainted(false); setCursor(new Cursor(Cursor.HAND_CURSOR)); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            super.paintComponent(g); g2.dispose();
        }
    }
}