package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connectDatabase.ConnectDatabase;
import model.entities.DatPhong;
import model.entities.DichVu;
import model.entities.KhachHang;

public class DatPhongDAO {
    
    // 1. Tìm khách hàng dựa trên mã đặt phòng (Code cũ của bạn)
    public KhachHang findKhachHangByIdDatPhong(String ma) {
        try {
            Connection con = ConnectDatabase.getInstance().getConnection();
            PreparedStatement pstmt = con.prepareStatement("SELECT maKhachHang FROM DatPhong WHERE maDatPhong=?");
            pstmt.setString(1, ma);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                KhachHangDAO khDAO = new KhachHangDAO();
                // Lấy mã khách hàng và tìm thông tin chi tiết
                return khDAO.findKhachHangByID(rs.getString("maKhachHang"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 2. Lấy danh sách mã đặt phòng cần Check-in trong hôm nay (MỚI - Cho phần Gợi ý)
    public List<String> getMaDatPhongCheckInHomNay() {
        List<String> dsMa = new ArrayList<>();
        // Truy vấn dựa trên ngày dự kiến và chưa có ngày check-in thực tế
        String sql = "SELECT maDatPhong FROM DatPhong " +
                     "WHERE CAST(ngayCheckInDuKien AS DATE) = CAST(GETDATE() AS DATE) " +
                     "AND ngayCheckInThucTe IS NULL";
        try {
            Connection con = ConnectDatabase.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dsMa.add(rs.getString("maDatPhong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsMa;
    }

    // 3. Tìm thông tin tổng hợp để hiển thị lên giao diện CheckInPanel (MỚI)
    // Trả về một đối tượng chứa cả thông tin Đặt phòng và Khách hàng
    public DatPhong findDatPhongDetail(String keyword) {
        // SQL JOIN để lấy thông tin đặt phòng và khách hàng
        String sql = "SELECT dp.*, kh.hoTen, kh.soDienThoai, kh.soCanCuocCongDan " +
                     "FROM DatPhong dp JOIN KhachHang kh ON dp.maKhachHang = kh.maKhachHang " +
                     "WHERE dp.maDatPhong = ? OR kh.soDienThoai = ?";
        
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, keyword);
            pstmt.setString(2, keyword);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // 1. Lấy dữ liệu thời gian (sử dụng getTimestamp để đổi sang LocalDateTime)
                LocalDateTime ngayDat = rs.getTimestamp("ngayDatPhong") != null ? rs.getTimestamp("ngayDatPhong").toLocalDateTime() : null;
                LocalDateTime checkInDK = rs.getTimestamp("ngayCheckInDuKien") != null ? rs.getTimestamp("ngayCheckInDuKien").toLocalDateTime() : null;
                LocalDateTime checkOutDK = rs.getTimestamp("ngayCheckOutDuKien") != null ? rs.getTimestamp("ngayCheckOutDuKien").toLocalDateTime() : null;
                LocalDateTime checkInTT = rs.getTimestamp("ngayCheckInThucTe") != null ? rs.getTimestamp("ngayCheckInThucTe").toLocalDateTime() : null;
                
                // 2. Tạo đối tượng Khách hàng
                KhachHang kh = new KhachHang(
                    rs.getString("maKhachHang"),
                    rs.getString("hoTen"),
                    rs.getString("soCanCuocCongDan"),
                    rs.getString("soDienThoai")
                );

                // 3. Khởi tạo Object DatPhong (Sửa lỗi truyền nhầm biến 'sql' của bạn)
                DatPhong dp = new DatPhong(
                    rs.getString("maDatPhong"), 
                    ngayDat, 
                    checkInDK, 
                    checkOutDK, 
                    checkInTT, 
                    null,
                    kh
                );
                
                return dp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm lấy gợi ý các đơn cần check-in hôm nay
    public List<String> getMaDatPhongGoiY() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT maDatPhong FROM DatPhong " +
                     "WHERE CAST(ngayCheckInDuKien AS DATE) = CAST(GETDATE() AS DATE) " +
                     "AND ngayCheckInThucTe IS NULL";
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("maDatPhong"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 4. Hàm insert (Code cũ của bạn)
    public boolean insert(DatPhong dp) {
        int n = 0;
        try {
            Connection con = ConnectDatabase.getInstance().getConnection();
            PreparedStatement pstmt = con.prepareStatement(
                "INSERT INTO DatPhong(maDatPhong, ngayDat, maKhachHang, ngayCheckIn, ngayCheckInDuKien, ngayCheckOut, ngayCheckOutDuKien) VALUES (?,?,?,?,?,?,?)"
            );
            pstmt.setString(1, dp.getMaDatPhong());
            pstmt.setDate(2, java.sql.Date.valueOf(dp.getNgayDat().toLocalDate()));
            pstmt.setString(3, dp.getKhachHang().getMaKhachHang());
            pstmt.setDate(4, dp.getNgayCheckIn() != null ? java.sql.Date.valueOf(dp.getNgayCheckIn().toLocalDate()) : null);
            pstmt.setDate(5, java.sql.Date.valueOf(dp.getNgayCheckInDuKien().toLocalDate()));
            pstmt.setDate(6, dp.getNgayCheckOut() != null ? java.sql.Date.valueOf(dp.getNgayCheckOut().toLocalDate()) : null);
            pstmt.setDate(7, java.sql.Date.valueOf(dp.getNgayCheckOutDuKien().toLocalDate()));

            n = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n > 0;
    }
    /**
     * Lưu danh sách dịch vụ khách đã chọn vào Database
     * @param maPhong Mã phòng đang chọn
     * @param cart Giỏ hàng chứa đối tượng DichVu và số lượng tương ứng
     * @return true nếu lưu thành công toàn bộ
     */
    public boolean saveServiceOrder(String maPhong, Map<DichVu, Integer> cart) {
        Connection con = ConnectDatabase.getInstance().getConnection();
        PreparedStatement psGetMaDatPhong = null;
        PreparedStatement psInsertService = null;
        
        try {
            // 1. Bắt đầu Transaction để đảm bảo an toàn dữ liệu
            con.setAutoCommit(false);

            // 2. Lấy maDatPhong của phiếu thuê đang "Chưa thanh toán" tại phòng này
            // Tên bảng và cột có thể thay đổi tùy theo DB của bạn (ví dụ: PhieuThuePhong, HoaDon)
            String sqlMaDP = "SELECT maDatPhong FROM DatPhong_ThongTin " +
                            "WHERE maPhong = ? AND trangThai = N'Đang ở'";
            
            psGetMaDatPhong = con.prepareStatement(sqlMaDP);
            psGetMaDatPhong.setString(1, maPhong);
            ResultSet rs = psGetMaDatPhong.executeQuery();
            
            String maDatPhong = "";
            if (rs.next()) {
                maDatPhong = rs.getString("maDatPhong");
            } else {
                throw new SQLException("Không tìm thấy phiếu thuê cho phòng này!");
            }

            // 3. Insert từng dòng dịch vụ vào bảng sử dụng (DichVuDaSuDung)
            String sqlInsert = "INSERT INTO DichVuDaSuDung (maDatPhong, maDichVu, soLuong, ngaySuDung, giaTaiThiemDiem) " +
                               "VALUES (?, ?, ?, GETDATE(), ?)";
            psInsertService = con.prepareStatement(sqlInsert);

            for (Map.Entry<DichVu, Integer> entry : cart.entrySet()) {
                DichVu dv = entry.getKey();
                int soLuong = entry.getValue();
                
                psInsertService.setString(1, maDatPhong);
                psInsertService.setString(2, dv.getMaDichVu());
                psInsertService.setInt(3, soLuong);
                // Lưu lại giá tại thời điểm sử dụng để tránh bảng giá thay đổi sau này
                psInsertService.setDouble(4, dv.getGiaDichVu()); 
                
                psInsertService.addBatch(); // Dùng Batch để tối ưu hiệu suất
            }

            psInsertService.executeBatch();

            // 4. Commit nếu mọi thứ ok
            con.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (con != null) con.rollback(); // Hoàn tác nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Đóng connection và statement
            try {
                if (psGetMaDatPhong != null) psGetMaDatPhong.close();
                if (psInsertService != null) psInsertService.close();
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Lấy tên khách hàng đang ở trong phòng để hiển thị lên UI
     */
    public String getTenKhachHienTai(String maPhong) {
        String tenKhach = "";
        String sql = "SELECT kh.tenKhachHang FROM KhachHang kh " +
                     "JOIN DatPhong_ThongTin dp ON kh.maKhachHang = dp.maKhachHang " +
                     "WHERE dp.maPhong = ? AND dp.trangThai = N'Đang ở'";
        
        try (Connection con = ConnectDatabase.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tenKhach = rs.getString("tenKhachHang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tenKhach;
    }
}