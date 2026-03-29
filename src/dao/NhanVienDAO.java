package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.NhanVien;
import model.enums.ChucVu;
import model.enums.trinhDo;

public class NhanVienDAO {

    // ── Helper: map ResultSet → NhanVien ─────────────────────────────────────
    private NhanVien mapRow(ResultSet rs) throws Exception {
        // 1. Khởi tạo bằng constructor
        NhanVien nv = new NhanVien(
            rs.getString("maNhanVien"),
            rs.getString("hoTen"),
            rs.getString("diaChi"),
            trinhDo.THCS, 
            rs.getDate("ngayVaoLam") != null ? rs.getDate("ngayVaoLam").toLocalDate() : null,
            rs.getFloat("heSoLuong"),
            rs.getInt("luongCoBan"),
            parseVaiTro(rs.getString("vaiTro"))
        );

        // 2. Gán các trường bổ sung để hiển thị đầy đủ trên Table và Form
        nv.setSoDT(rs.getString("soDienThoai")); 
        nv.setMaQL(rs.getString("maQuanLy"));
        nv.setMatKhau(rs.getString("matKhau"));
        
        return nv;
    }

    private ChucVu parseVaiTro(String value) {
        if (value == null) return ChucVu.NHAN_VIEN;
        switch (value.trim().toUpperCase()) {
            case "QUANLY":
            case "QUAN_LY":
                return ChucVu.QUAN_LY;
            default:
                return ChucVu.NHAN_VIEN;
        }
    }

    private String toVaiTroString(ChucVu cv) {
        if (cv == null) return "NHANVIEN";
        return cv == ChucVu.QUAN_LY ? "QUANLY" : "NHANVIEN";
    }

    // ── READ ALL ──────────────────────────────────────────────────────────────
    public List<NhanVien> getAll() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) ds.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    // ── TÌM KIẾM THEO TÊN, MÃ, SĐT (HÀM BẠN YÊU CẦU) ──────────────────────────
    public List<NhanVien> findByKeyword(String keyword) {
        List<NhanVien> ds = new ArrayList<>();
        // Sử dụng LIKE với % để tìm kiếm tương đối
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien LIKE ? OR hoTen LIKE ? OR soDienThoai LIKE ?";
        
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String searchStr = "%" + keyword + "%";
            ps.setString(1, searchStr);
            ps.setString(2, searchStr);
            ps.setString(3, searchStr);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
    public NhanVien getById(String maNhanVien) {
        String sql = "SELECT * FROM NhanVien WHERE maNhanVien = ?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien "
            + "(maNhanVien, hoTen, soDienThoai, ngayVaoLam, heSoLuong, matKhau, vaiTro, diaChi, trinhDo, luongCoBan, maQuanLy) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSoDT());
            ps.setDate(4, nv.getNgayVaoLamDate() != null ? Date.valueOf(nv.getNgayVaoLamDate()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setFloat(5, nv.getHeSoLuong());
            ps.setString(6, nv.getMatKhau() != null ? nv.getMatKhau() : "123"); // Mặc định nếu chưa có pass
            ps.setString(7, toVaiTroString(nv.getRole()));
            ps.setString(8, nv.getDiaChi());
            ps.setString(9, nv.getTrinhDo() != null ? nv.getTrinhDo().name() : "THCS");
            ps.setInt(10, nv.getLuongCB());
            ps.setString(11, nv.getMaQL());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET "
            + "hoTen=?, soDienThoai=?, ngayVaoLam=?, "
            + "heSoLuong=?, vaiTro=?, maQuanLy=?, diaChi=?, luongCoBan=? "
            + "WHERE maNhanVien=?";

        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSoDT());
            ps.setDate(3, nv.getNgayVaoLamDate() != null ? Date.valueOf(nv.getNgayVaoLamDate()) : null);
            ps.setFloat(4, nv.getHeSoLuong());
            ps.setString(5, toVaiTroString(nv.getRole()));
            ps.setString(6, nv.getMaQL());
            ps.setString(7, nv.getDiaChi());
            ps.setInt(8, nv.getLuongCB());
            ps.setString(9, nv.getMaNV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public boolean delete(String maNhanVien) {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien=?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    // Count Manager
    public int countManager() {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE vaiTro='QUANLY'";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    // ── AUTHENTICATE ──────────────────────────────────────────────────────────
    public boolean authenticate(String staffID, String password) {
        String sql = "SELECT matKhau FROM NhanVien WHERE maNhanVien=?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, staffID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("matKhau").equals(password);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── GENERATE ID ───────────────────────────────────────────────────────────
    public String generateMaNV() {
        String prefix = "LUCIA";
        String sql = "SELECT MAX(maNhanVien) FROM NhanVien";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String last = rs.getString(1);
                if (last != null && last.startsWith(prefix)) {
                    int num = Integer.parseInt(last.replace(prefix, ""));
                    return prefix + String.format("%04d", num + 1);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return prefix + "0001";
    }
}