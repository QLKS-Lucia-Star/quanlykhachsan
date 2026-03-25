package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import connectDatabase.ConnectDatabase;
import model.entities.KhachHang;


public class KhachHangDAO {
	public List<KhachHang> getAll() {
		List<KhachHang> dsKhachHang = new ArrayList<KhachHang>();
		try {
			Connection con = ConnectDatabase.getInstance().getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM KhachHang");
			
			while (rs.next()) {
				KhachHang kh = new KhachHang(
						rs.getString("maKhachHang"), 
						rs.getString("hoTen"), 
						rs.getString("soCanCuocCongDan"), 
						rs.getString("soDienThoai"));
				
				dsKhachHang.add(kh);
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy tất cả danh sách khách hàng " +e.getMessage());
		}
		return dsKhachHang;
	}
	
	public boolean insert(KhachHang kh) {
		try {
			Connection con = ConnectDatabase.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement("INSERT INTO KhachHang(maKhachHang, hoTen, CCCD, soDienThoai) VALUES (?,?,?,?)");
			
			pstmt.setString(1, kh.getMaKhachHang());
			pstmt.setString(2, kh.getHoTen());
			pstmt.setString(3, kh.getCCCD());
			pstmt.setString(4, kh.getSoDienThoai());
			
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			
			System.err.println("Lỗi insert KhachHang. Vui lòng kiểm tra lại.");
			return false;
		}
	}
	
	public boolean update(KhachHang kh) {
		int n=0;
		try {
			Connection con = ConnectDatabase.getInstance().getConnection();
			PreparedStatement pstmt = con.prepareStatement("UPDATE KhachHang SET hoTen=?, soDienThoai=?, soCanCuocCOngDan=? WHERE maKhachHang=?");
			
			pstmt.setString(1, kh.getHoTen());
			pstmt.setString(2, kh.getCCCD());
			pstmt.setString(3, kh.getSoDienThoai());
			pstmt.setString(4, kh.getMaKhachHang());
			
			n = pstmt.executeUpdate();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n>0;
		
	}
	
	public KhachHang findKhachHangByID(String keyWord) {
	    KhachHang kh = null;

	    try {
	        Connection con = ConnectDatabase.getInstance().getConnection();

	        String sql = "SELECT * FROM KhachHang WHERE maKhachHang LIKE ?";
	        PreparedStatement pstmt = con.prepareStatement(sql);

	        pstmt.setString(1, "%" + keyWord + "%");

	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            kh = new KhachHang(
	                    rs.getString("maKhachHang"),
	                    rs.getString("hoTen"),
	                    rs.getString("soCanCuocCongDan"),
	                    rs.getString("soDienThoai")
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return kh;
	}
}
