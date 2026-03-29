package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.BangGiaDichVu;
import model.entities.BangGiaDichVu_ChiTiet;
import model.entities.DichVu;

public class BangGiaDichVuDAO {

    // 1. Lấy danh sách tất cả bảng giá
    public List<BangGiaDichVu> getAllBangGia() {
        List<BangGiaDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM BangGiaDichVu_ThongTin";
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBangGia(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy chi tiết của một bảng giá
    public List<BangGiaDichVu_ChiTiet> getChiTietByMa(String maBangGia) {
        List<BangGiaDichVu_ChiTiet> list = new ArrayList<>();
        String sql = "SELECT * FROM BangGiaDichVu_ChiTiet WHERE maBangGia = ?";
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maBangGia);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapChiTiet(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Thêm mới bảng giá thông tin
    public boolean insertBangGia(BangGiaDichVu bg) {
        String sql = "INSERT INTO BangGiaDichVu_ThongTin VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bg.getMaBangGia());
            ps.setString(2, bg.getTenBangGia());
            ps.setDate(3, new java.sql.Date(bg.getNgayApDung().getTime()));
            ps.setDate(4, new java.sql.Date(bg.getNgayHetHieuLuc().getTime()));
            ps.setInt(5, bg.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 4. Thêm chi tiết bảng giá (Ví dụ: Thêm dịch vụ vào bảng giá)
    public boolean insertChiTiet(BangGiaDichVu_ChiTiet ct) {
        String sql = "INSERT INTO BangGiaDichVu_ChiTiet VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaChiTietBangGia());
            ps.setString(2, ct.getMaBangGia().getMaBangGia());
            ps.setString(3, ct.getMaDichVu().getMaDichVu());
            ps.setDouble(4, ct.getGiaDichVu());
            ps.setString(5, ct.getDonViTinh());
            ps.setInt(6, ct.getSoLuong());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 5. Cập nhật trạng thái bảng giá (Ví dụ: Kích hoạt/Hủy kích hoạt)
    public boolean updateTrangThai(String maBangGia, int status) {
        String sql = "UPDATE BangGiaDichVu_ThongTin SET trangThai = ? WHERE maBangGia = ?";
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setString(2, maBangGia);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 6. Xóa bảng giá (Lưu ý: Thường phải xóa chi tiết trước do ràng buộc FK)
    public boolean deleteBangGia(String maBangGia) {
        String sqlChiTiet = "DELETE FROM BangGiaDichVu_ChiTiet WHERE maBangGia = ?";
        String sqlThongTin = "DELETE FROM BangGiaDichVu_ThongTin WHERE maBangGia = ?";
        try (Connection conn = ConnectDatabase.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction
            try (PreparedStatement ps1 = conn.prepareStatement(sqlChiTiet);
                 PreparedStatement ps2 = conn.prepareStatement(sqlThongTin)) {
                ps1.setString(1, maBangGia);
                ps1.executeUpdate();
                
                ps2.setString(1, maBangGia);
                ps2.executeUpdate();
                
                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                return false;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Helper: Map dữ liệu từ ResultSet sang Object
    private BangGiaDichVu mapBangGia(ResultSet rs) throws SQLException {
        BangGiaDichVu bg = new BangGiaDichVu();
        bg.setMaBangGia(rs.getString("maBangGia"));
        bg.setTenBangGia(rs.getString("tenBangGia"));
        bg.setNgayApDung(rs.getDate("ngayApDung"));
        bg.setNgayHetHieuLuc(rs.getDate("ngayHetHieuLuc"));
        bg.setTrangThai(rs.getInt("trangThai"));
        return bg;
    }

    private BangGiaDichVu_ChiTiet mapChiTiet(ResultSet rs) throws SQLException {
        BangGiaDichVu_ChiTiet ct = new  BangGiaDichVu_ChiTiet();
        ct.setMaChiTietBangGia(rs.getString("maChiTietBangGia"));
        ct.setMaBangGia(new BangGiaDichVu(rs.getString("maBangGia")));
        ct.setMaDichVu(new DichVu(rs.getString("maDichVu")));
        ct.setGiaDichVu(rs.getDouble("giaDichVu"));
        ct.setDonViTinh(rs.getString("donViTinh"));
        ct.setSoLuong(rs.getInt("soLuong"));
        return ct;
    }
    
    public boolean insertFullBangGia(BangGiaDichVu thongTin, List<BangGiaDichVu_ChiTiet> dsChiTiet) {
        Connection con = null;
        try {
            con = ConnectDatabase.getInstance().getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Lưu vào bảng ThongTin
            String sqlThongTin = "INSERT INTO BangGiaDichVu_ThongTin (maBangGia, tenBangGia, ngayApDung, ngayHetHieuLuc, trangThai) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps1 = con.prepareStatement(sqlThongTin);
            ps1.setString(1, thongTin.getMaBangGia());
            ps1.setString(2, thongTin.getTenBangGia());
            ps1.setDate(3, new java.sql.Date(thongTin.getNgayApDung().getTime()));
            ps1.setDate(4, new java.sql.Date(thongTin.getNgayHetHieuLuc().getTime()));
            ps1.setInt(5, thongTin.getTrangThai());
            ps1.executeUpdate();

            // 2. Lưu vào bảng ChiTiet
            String sqlChiTiet = "INSERT INTO BangGiaDichVu_ChiTiet (maChiTietBangGia, maBangGia, maDichVu, giaDichVu, donViTinh, soLuong) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps2 = con.prepareStatement(sqlChiTiet);
            
            int count = 1;
            for (BangGiaDichVu_ChiTiet ct : dsChiTiet) {
                ps2.setString(1, thongTin.getMaBangGia() + "-CT" + (count++));
                ps2.setString(2, thongTin.getMaBangGia());
                ps2.setString(3, ct.getMaDichVu().getMaDichVu()); // Lấy mã từ Object DichVu
                ps2.setDouble(4, ct.getGiaDichVu());
                ps2.setString(5, ct.getDonViTinh());
                ps2.setInt(6, 1); // Mặc định số lượng là 1
                ps2.addBatch(); // Dùng Batch để lưu nhanh nhiều dòng
            }
            ps2.executeBatch();

            con.commit(); // Thành công hết thì mới lưu thật sự
            return true;
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateFullBangGia(BangGiaDichVu bg, List<BangGiaDichVu_ChiTiet> dsChiTiet) {
        Connection con = null;
        PreparedStatement psUpdateBG = null;
        PreparedStatement psDeleteDetails = null;
        PreparedStatement psInsertDetails = null;

        try {
            con = ConnectDatabase.getInstance().getConnection();
            con.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            String sqlUpdateBG = "UPDATE BangGiaDichVu SET TenBangGia = ?, NgayApDung = ?, NgayHetHieuLuc = ? WHERE MaBangGia = ?";
            psUpdateBG = con.prepareStatement(sqlUpdateBG);
            psUpdateBG.setString(1, bg.getTenBangGia());
            psUpdateBG.setDate(2, new java.sql.Date(bg.getNgayApDung().getTime()));
            psUpdateBG.setDate(3, new java.sql.Date(bg.getNgayHetHieuLuc().getTime()));
            psUpdateBG.setString(4, bg.getMaBangGia());
            psUpdateBG.executeUpdate();

            String sqlDeleteDetails = "DELETE FROM BangGiaDichVu_ChiTiet WHERE MaBangGia = ?";
            psDeleteDetails = con.prepareStatement(sqlDeleteDetails);
            psDeleteDetails.setString(1, bg.getMaBangGia());
            psDeleteDetails.executeUpdate();

            String sqlInsertDetails = "INSERT INTO BangGiaDichVu_ChiTiet (MaBangGia, MaDichVu, GiaDichVu, DonViTinh) VALUES (?, ?, ?, ?)";
            psInsertDetails = con.prepareStatement(sqlInsertDetails);
            
            for (BangGiaDichVu_ChiTiet ct : dsChiTiet) {
                psInsertDetails.setString(1, bg.getMaBangGia());
                psInsertDetails.setString(2, ct.getMaDichVu().getMaDichVu());
                psInsertDetails.setDouble(3, ct.getGiaDichVu());
                psInsertDetails.setString(4, ct.getDonViTinh());
                psInsertDetails.addBatch();
            }
            psInsertDetails.executeBatch();

            con.commit();
            return true;

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback(); // CÓ LỖI - Hoàn tác toàn bộ thay đổi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng tất cả resource
            try {
                if (psUpdateBG != null) psUpdateBG.close();
                if (psDeleteDetails != null) psDeleteDetails.close();
                if (psInsertDetails != null) psInsertDetails.close();
                // Không đóng con ở đây nếu bạn dùng Singleton Connection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public BangGiaDichVu getBangGiaByMa(String maBG) {
        BangGiaDichVu bg = null;
        // Lưu ý: Kiểm tra tên bảng là BangGiaDichVu hay BangGiaDichVu_ThongTin
        String sql = "SELECT * FROM BangGiaDichVu_ThongTin WHERE maBangGia = ?";
        
        try (Connection conn = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maBG);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tái sử dụng hàm helper mapBangGia bạn đã viết ở dưới
                    bg = mapBangGia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bg;
    }
}
