package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.NhanVien;
import model.enums.ChucVu;

public class NhanVienDAO {

    // ── Helper: map ResultSet → NhanVien ─────────────────────────────────────
    // Tên cột khớp đúng với DB thực tế:
    //   maNhanVien | hoTen | soDienThoai | diaChi | ngayVaoLam
    //   trinhDo    | heSoLuong | luongCoBan | matKhau | vaiTro | maQuanLy
    private NhanVien mapRow(ResultSet rs) throws Exception {
        // ngayVaoLam trong DB là kiểu DATE → dùng getDate rồi chuyển sang LocalDateTime
        Date d = rs.getDate("ngayVaoLam");
        LocalDateTime nvl = (d != null) ? d.toLocalDate().atStartOfDay() : null;

        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                nvl,
                rs.getFloat("heSoLuong"),
                rs.getString("matKhau"),
                parseVaiTro(rs.getString("vaiTro")));   // ← cột đúng là "vaiTro"
    }

    /**
     * Chuyển giá trị cột "vaiTro" trong DB sang enum ChucVu.
     *   "QUANLY"   → ChucVu.QUAN_LY
     *   "NHANVIEN" → ChucVu.NHAN_VIEN
     */
    private ChucVu parseVaiTro(String value) {
        if (value == null) return ChucVu.NHAN_VIEN;
        switch (value.trim().toUpperCase()) {
            case "QUANLY":
            case "QUAN_LY":
                return ChucVu.QUAN_LY;
            case "NHANVIEN":
            case "NHAN_VIEN":
            default:
                return ChucVu.NHAN_VIEN;
        }
    }

    /** Ngược lại: enum → chuỗi lưu vào DB */
    private String toVaiTroString(ChucVu cv) {
        if (cv == null) return "NHANVIEN";
        return cv == ChucVu.QUAN_LY ? "QUANLY" : "NHANVIEN";
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────
    public List<NhanVien> getAll() {
        List<NhanVien> ds = new ArrayList<>();
        try {
            Connection con = ConnectDatabase.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs   = stmt.executeQuery("SELECT * FROM NhanVien");
            while (rs.next()) ds.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
public NhanVien getById(String maNhanVien) {
        try {
            Connection con       = ConnectDatabase.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM NhanVien WHERE maNhanVien = ?");
            ps.setString(1, maNhanVien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien "
                   + "(maNhanVien, hoTen, soDienThoai, ngayVaoLam, heSoLuong, matKhau, vaiTro) "
                   + "VALUES (?,?,?,?,?,?,?)";
        try {
            Connection con       = ConnectDatabase.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSoDienThoai());
            ps.setDate(4, nv.getNgayVaoLam() != null
                    ? Date.valueOf(nv.getNgayVaoLam().toLocalDate()) : null);
            ps.setFloat(5, nv.getHeSoLuong());
            ps.setString(6, nv.getMatKhau());
            ps.setString(7, toVaiTroString(nv.getChucVu()));
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET "
                   + "hoTen=?, soDienThoai=?, ngayVaoLam=?, "
                   + "heSoLuong=?, matKhau=?, vaiTro=? "
                   + "WHERE maNhanVien=?";
        try {
            Connection con       = ConnectDatabase.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSoDienThoai());
            ps.setDate(3, nv.getNgayVaoLam() != null
                    ? Date.valueOf(nv.getNgayVaoLam().toLocalDate()) : null);
            ps.setFloat(4, nv.getHeSoLuong());
            ps.setString(5, nv.getMatKhau());
            ps.setString(6, toVaiTroString(nv.getChucVu()));
            ps.setString(7, nv.getMaNhanVien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
public boolean delete(String maNhanVien) {
        try {
            Connection con       = ConnectDatabase.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM NhanVien WHERE maNhanVien=?");
            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── AUTHENTICATE ──────────────────────────────────────────────────────────
    public boolean authenticate(String staffID, String password) {
        try {
            Connection con       = ConnectDatabase.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT matKhau FROM NhanVien WHERE maNhanVien=?");
            ps.setString(1, staffID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return password.equals(rs.getString("matKhau"));
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}