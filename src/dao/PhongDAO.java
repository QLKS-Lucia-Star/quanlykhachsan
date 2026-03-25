package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.LoaiPhong;
import model.entities.Phong;
import model.enums.TenLoaiPhong;
import model.enums.TrangThaiPhong;

public class PhongDAO {

    // --- HÀM HỖ TRỢ CHUYỂN ĐỔI ENUM ---
    private TrangThaiPhong findEnumByString(String text) {
        if (text == null) return TrangThaiPhong.CONTRONG;
        for (TrangThaiPhong ttp : TrangThaiPhong.values()) {
            if (ttp.toString().equalsIgnoreCase(text.trim())) {
                return ttp;
            }
        }
        return TrangThaiPhong.CONTRONG;
    }

    // --- HÀM LẤY PHÒNG TRỐNG (Dùng cho TaoDonDatPhongPanel - GIẢI QUYẾT LỖI CỦA BẠN) ---
    public List<Phong> getDanhSachPhongTrong(String tenLoai, Date checkIn, Date checkOut) {
        List<Phong> list = new ArrayList<>();
        // Query kiểm tra những phòng thuộc loại đó và KHÔNG nằm trong các chi tiết đặt phòng có thời gian trùng lặp
        String sql = "SELECT maPhong, tenLoaiPhong, tinhTrang, soTang FROM Phong " +
                     "WHERE tenLoaiPhong = ? AND maPhong NOT IN (" +
                     "    SELECT dp.maPhong FROM ChiTietDatPhong dp " +
                     "    JOIN DatPhong d ON dp.maDatPhong = d.maDatPhong " +
                     "    WHERE (d.ngayCheckInDuKien < ? AND d.ngayCheckOutDuKien > ?))";

        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, tenLoai);
            ps.setDate(2, checkOut); 
            ps.setDate(3, checkIn);  

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maP = rs.getString("maPhong");
                TenLoaiPhong tlp = TenLoaiPhong.valueOf(rs.getString("tenLoaiPhong").trim());
                TrangThaiPhong tt = findEnumByString(rs.getString("tinhTrang"));
                int tang = rs.getInt("soTang");

                list.add(new Phong(maP, new LoaiPhong(tlp), tt, tang));
            }
        } catch (Exception e) {
            System.err.println("Lỗi getDanhSachPhongTrong: " + e.getMessage());
        }
        return list;
    }

    // --- CÁC HÀM CƠ BẢN KHÁC ---
    public List<Phong> getAll() {
        List<Phong> ds = new ArrayList<>();
        String sql = "SELECT maPhong, tenLoaiPhong, tinhTrang, soTang FROM Phong";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                TenLoaiPhong tlp = TenLoaiPhong.valueOf(rs.getString("tenLoaiPhong").trim());
                TrangThaiPhong ttp = findEnumByString(rs.getString("tinhTrang"));
                ds.add(new Phong(rs.getString("maPhong"), new LoaiPhong(tlp), ttp, rs.getInt("soTang")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<Phong> getByTrangThai(String trangThaiTiengViet) {
        List<Phong> ds = new ArrayList<>();
        String sql = "SELECT maPhong, tenLoaiPhong, tinhTrang, soTang FROM Phong WHERE tinhTrang = ?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setNString(1, trangThaiTiengViet);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                TenLoaiPhong tlp = TenLoaiPhong.valueOf(rs.getString("tenLoaiPhong").trim());
                TrangThaiPhong ttp = findEnumByString(rs.getString("tinhTrang"));
                ds.add(new Phong(rs.getString("maPhong"), new LoaiPhong(tlp), ttp, rs.getInt("soTang")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }

    public boolean insert(Phong p) {
        String sql = "INSERT INTO Phong(maPhong, tenLoaiPhong, tinhTrang, soTang) VALUES (?,?,?,?)";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, p.getMaPhong());
            pstmt.setString(2, p.getLoaiPhong().getTenLoaiPhong().toString());
            pstmt.setString(3, p.getTrangThai().toString()); 
            pstmt.setInt(4, p.getSoTang());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean update(Phong p) {
        String sql = "UPDATE Phong SET tenLoaiPhong=?, tinhTrang=?, soTang=? WHERE maPhong=?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, p.getLoaiPhong().getTenLoaiPhong().toString());
            pstmt.setString(2, p.getTrangThai().toString());
            pstmt.setInt(3, p.getSoTang());
            pstmt.setString(4, p.getMaPhong());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean delete(String maPhong) {
        String sql = "DELETE FROM Phong WHERE maPhong=?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, maPhong);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}