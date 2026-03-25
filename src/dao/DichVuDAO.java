package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.DichVu;

public class DichVuDAO {

    /**
     * Lấy tất cả dịch vụ từ bảng DichVu
     */
    public List<DichVu> getAll() {
        List<DichVu> ds = new ArrayList<>();
        // Truy vấn trực tiếp từ bảng DichVu theo cấu trúc mới
        String sql = "SELECT maDichVu, tenDichVu, giaDichVu, loaiDichVu, mieuTa FROM DichVu";

        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getDouble("giaDichVu"),
                    rs.getString("loaiDichVu"),
                    rs.getString("mieuTa")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy dịch vụ theo loại (NUOCUONG, THUCAN, MAYGIAT...) cho giao diện Tab
     * Kết nối với BangGiaDichVu_ChiTiet để lấy giá đang áp dụng (TrangThai = 1)
     */
    public List<DichVu> getByType(String type) {
        List<DichVu> ds = new ArrayList<>();
        // JOIN với bảng giá để lấy đơn giá mới nhất đang có hiệu lực (trangThai = 1)
        String sql = "SELECT dv.maDichVu, dv.tenDichVu, bgct.giaDichVu, dv.loaiDichVu, dv.mieuTa " +
                     "FROM DichVu dv " +
                     "JOIN BangGiaDichVu_ChiTiet bgct ON dv.maDichVu = bgct.maDichVu " +
                     "JOIN BangGiaDichVu_ThongTin bgtt ON bgct.maBangGia = bgtt.maBangGia " +
                     "WHERE dv.loaiDichVu = ? AND bgtt.trangThai = 1"; 

        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ds.add(new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getDouble("giaDichVu"),
                    rs.getString("loaiDichVu"),
                    rs.getString("mieuTa")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy dịch vụ theo loại: " + type);
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Cập nhật thông tin dịch vụ
     */
    public boolean update(DichVu dv) {
        String sql = "UPDATE DichVu SET tenDichVu = ?, giaDichVu = ?, loaiDichVu = ?, mieuTa = ? WHERE maDichVu = ?";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, dv.getTenDichVu());
            pstmt.setDouble(2, dv.getGiaDichVu());
            pstmt.setString(3, dv.getLoaiDichVu());
            pstmt.setString(4, dv.getMieuTa());
            pstmt.setString(5, dv.getMaDichVu());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm mới dịch vụ
     */
    public boolean insert(DichVu dv) {
        String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, giaDichVu, loaiDichVu, mieuTa) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, dv.getMaDichVu());
            pstmt.setString(2, dv.getTenDichVu());
            pstmt.setDouble(3, dv.getGiaDichVu());
            pstmt.setString(4, dv.getLoaiDichVu());
            pstmt.setString(5, dv.getMieuTa());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}