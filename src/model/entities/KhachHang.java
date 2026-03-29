package model.entities;

import java.time.LocalDate;

public class KhachHang {
    private String maKhachHang;
    private String hoTen;
    private String CCCD;            // ánh xạ cột soCanCuocCongDan trong DB
    private String soDienThoai;
    private LocalDate ngaySinh;

    // ─── Constructors ────────────────────────────────────────────────────────
    public KhachHang() {}

    public KhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public KhachHang(String maKhachHang, String hoTen, String CCCD, String soDienThoai) {
        this.maKhachHang = maKhachHang;
        this.hoTen       = hoTen;
        this.CCCD        = CCCD;
        this.soDienThoai = soDienThoai;
    }

    public KhachHang(String maKhachHang, String hoTen, String CCCD,
                     String soDienThoai, LocalDate ngaySinh) {
        this(maKhachHang, hoTen, CCCD, soDienThoai);
        this.ngaySinh = ngaySinh;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    /** Hôm nay có phải sinh nhật không */
    public boolean isBirthdayToday() {
        if (ngaySinh == null) return false;
        LocalDate today = LocalDate.now();
        return ngaySinh.getDayOfMonth() == today.getDayOfMonth()
            && ngaySinh.getMonthValue()  == today.getMonthValue();
    }

    /** Sinh nhật có trong tháng hiện tại không */
    public boolean isBirthdayThisMonth() {
        if (ngaySinh == null) return false;
        return ngaySinh.getMonthValue() == LocalDate.now().getMonthValue();
    }

    // ─── Getters / Setters ───────────────────────────────────────────────────
    public String getMaKhachHang()             { return maKhachHang; }
    public void   setMaKhachHang(String v)     { this.maKhachHang = v; }

    public String getHoTen()                   { return hoTen; }
    public void   setHoTen(String v)           { this.hoTen = v; }

    public String getCCCD()                    { return CCCD; }
    public void   setCCCD(String v)            { this.CCCD = v; }

    public String getSoDienThoai()             { return soDienThoai; }
    public void   setSoDienThoai(String v)     { this.soDienThoai = v; }

    public LocalDate getNgaySinh()             { return ngaySinh; }
    public void       setNgaySinh(LocalDate v) { this.ngaySinh = v; }
}