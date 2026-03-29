package gui;

import dao.KhachHangDAO;

import javax.swing.*;
import javax.swing.border.*;
import model.entities.NhanVien;
import model.enums.ChucVu;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private CardLayout      cardLayout;
    private JPanel          mainPanel;
    private NhanVien        staff;
    private JButton         activeMenuButton = null;
    private boolean         isAdmin          = false;

    // Giữ reference để MainFrame có thể gọi showBirthdayDialog()
    private KhachHangPanel  khachHangPanel;

    // ────────────────────────────────────────────────────────────────────────
    public MainFrame(NhanVien staff) {
        this.staff   = staff;
        this.isAdmin = (staff != null && staff.getChucVu() == ChucVu.QUAN_LY);
        init();
    }

    public MainFrame() { init(); }

    // ════════════════════════════════════════════════════════════════════════
    private void init() {
        setTitle("Khách sạn Lucia Star - " + (isAdmin ? "Quản lý" : "Nhân viên"));
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createHeader(),  BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        khachHangPanel = new KhachHangPanel();   // giữ reference

        mainPanel.add(new TrangChuPanel(),                         "dashboard");
        mainPanel.add(createScrollPane(new TaoDonDatPhongPanel()), "booking");
        mainPanel.add(createScrollPane(khachHangPanel),            "customers");
        mainPanel.add(createScrollPane(new NhanVienPanel(isAdmin)),"staff");
        mainPanel.add(new CheckInPanel(),                          "checkin");
        mainPanel.add(createScrollPane(new CheckOutPanel()),       "checkout");
        mainPanel.add(createScrollPane(new HoaDonPanel()),         "invoices");
        mainPanel.add(createScrollPane(new ThemDichVuPanel()),     "service");
        mainPanel.add(createScrollPane(new QuanLyPhongPanel()),    "rooms");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane sc = new JScrollPane(panel);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        return sc;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(new Color(50, 30, 28));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(menuItem("Trang chủ",  "dashboard"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Đặt phòng",  "booking"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Nhận phòng", "checkin"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Trả phòng",  "checkout"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Khách hàng", "customers"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Dịch vụ",    "service"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Hóa đơn",    "invoices"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(menuItem("Nhân viên",  "staff"));
        if (isAdmin) {
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(menuItem("Phòng", "rooms"));
        }

        sidebar.add(Box.createVerticalGlue());

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 55, 50));
        sep.setMaximumSize(new Dimension(220, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        JLabel lblRole = new JLabel(isAdmin ? "  Chế độ: Quản lý" : "  Chế độ: Nhân viên");
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblRole.setForeground(isAdmin ? new Color(212, 175, 55) : new Color(160, 160, 160));
        lblRole.setBorder(new EmptyBorder(0, 20, 15, 0));
        sidebar.add(lblRole);

        return sidebar;
    }

    private JButton menuItem(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(50, 30, 28));
        btn.setForeground(new Color(212, 175, 55));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 25, 10, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (text.equals("Trang chủ")) {
            setActiveStyle(btn);
            activeMenuButton = btn;
        }

        btn.addActionListener(e -> {
            if (activeMenuButton != null) setNormalStyle(activeMenuButton);
            setActiveStyle(btn);
            activeMenuButton = btn;
            cardLayout.show(mainPanel, cardName);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeMenuButton) {
                    btn.setBackground(new Color(74, 45, 42));
                    btn.setBorder(new MatteBorder(0, 5, 0, 0, new Color(212, 175, 55)));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeMenuButton) setNormalStyle(btn);
            }
        });
        return btn;
    }

    private void setActiveStyle(JButton btn) {
        btn.setBackground(new Color(92, 55, 51));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new MatteBorder(0, 8, 0, 0, new Color(212, 175, 55)));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    private void setNormalStyle(JButton btn) {
        btn.setBackground(new Color(50, 30, 28));
        btn.setForeground(new Color(212, 175, 55));
        btn.setBorder(new EmptyBorder(10, 25, 10, 10));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HEADER (có badge sinh nhật)
    // ════════════════════════════════════════════════════════════════════════
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(100, 80));
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // ── Bên trái: tên hệ thống ──────────────────────────────────────────
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(15, 25, 10, 0));

        JLabel title = new JLabel(isAdmin ? "ADMIN PANEL" : "RECEPTION DESK");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(74, 45, 42));

        JLabel sub = new JLabel("Hệ thống quản lý khách sạn Lucia Star");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(140, 140, 140));

        left.add(title);
        left.add(sub);

        // ── Bên phải: badge sinh nhật + tên user + đăng xuất ────────────────
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 20));
        right.setOpaque(false);

        // Badge sinh nhật
        int bdCount = new KhachHangDAO().getBirthdayTodayCount();
        if (bdCount > 0) {
            JLabel bdgBd = buildBirthdayBadge(bdCount);
            right.add(bdgBd);
        }

        // Tên nhân viên
        String name    = (staff != null ? staff.getHoTen() : "Admin");
        String roleTag = isAdmin ? " [Quản lý]" : " [Nhân viên]";
        JLabel user    = new JLabel("👤 " + name + roleTag);
        user.setFont(new Font("Segoe UI", Font.BOLD, 14));
        user.setForeground(isAdmin ? new Color(160, 120, 40) : new Color(74, 45, 42));

        // Nút đăng xuất
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(180, 60, 60));
        btnLogout.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 210, 200)),
                new EmptyBorder(8, 15, 8, 15)));
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Xác nhận đăng xuất?", "Thông báo",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new DangNhapJFrame().setVisible(true);
            }
        });

        right.add(user);
        right.add(btnLogout);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Tạo badge "🎂 N sinh nhật hôm nay".
     * Click → chuyển sang trang Khách hàng + mở popup danh sách.
     */
    private JLabel buildBirthdayBadge(int count) {
        JLabel badge = new JLabel("🎂  " + count + " sinh nhật hôm nay");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(new Color(180, 80, 40));
        badge.setBorder(new CompoundBorder(
                new LineBorder(new Color(140, 60, 25), 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        badge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        badge.setToolTipText("Nhấp để xem danh sách khách sinh nhật hôm nay");

        badge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Chuyển menu sang "Khách hàng"
                for (Component c : ((JPanel) getContentPane()
                        .getComponent(0)).getComponents()) {
                    // Duyệt tìm nút Khách hàng trong sidebar để set active
                }
                cardLayout.show(mainPanel, "customers");

                // 2. Mở popup sinh nhật (chạy sau khi panel hiển thị)
                SwingUtilities.invokeLater(() ->
                        khachHangPanel.showBirthdayDialog());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                badge.setBackground(new Color(200, 100, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                badge.setBackground(new Color(180, 80, 40));
            }
        });

        return badge;
    }
}