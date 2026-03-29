package model.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import model.enums.ChucVu;
import model.enums.trinhDo;

public class NhanVien {
    private String maNV, hoTen, diaChi, soDT, matKhau;
    private ChucVu role;
    private trinhDo trinhDo;
    private LocalDate ngaySinh, ngayVaoLamDate;
    private float heSoLuong;
    private String maQL;
    private int luongCB;

    // Sử dụng static để tránh tạo mới formatter cho mỗi object, tiết kiệm bộ nhớ
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --- CONSTRUCTORS ---
    public NhanVien() {
        // Khởi tạo các giá trị mặc định hợp lệ để tránh NullPointerException
        this.maNV = "LUCIA0000";
        this.hoTen = "Chưa xác định";
        this.diaChi = "Chưa có";
        this.soDT = "0900000000";
        this.matKhau = "123";
        this.role = ChucVu.NHAN_VIEN;
        this.trinhDo = trinhDo.THCS;
        this.ngaySinh = LocalDate.now().minusYears(19); // Đảm bảo > 18 tuổi
        this.ngayVaoLamDate = LocalDate.now();
        this.heSoLuong = 1.84f;
        this.luongCB = 1000000;
    }

    public NhanVien(String maNV, String hoTen, String diaChi, trinhDo trinhDo, LocalDate ngayVaoLamDate, 
                    float heSoLuong, int luongCB, ChucVu role) {
        this.setMaNV(maNV);
        this.setHoTen(hoTen);
        this.setDiaChi(diaChi);
        this.setTrinhDo(trinhDo);
        this.setNgayVaoLamDate(ngayVaoLamDate);
        this.setHeSoLuong(heSoLuong);
        this.setLuongCB(luongCB);
        this.setRole(role);
    }
    

    public NhanVien(String maNV) {
		super();
		this.maNV = maNV;
	}

	// --- GETTERS & SETTERS ---

    public void setMaNV(String maNV) {
        if (maNV == null || !maNV.matches("LUCIA\\d{4}"))
            throw new IllegalArgumentException("Mã NV phải dạng LUCIAxxxx (VD: LUCIA0001)");
        this.maNV = maNV;
    }

    public String getMaNV() { return maNV; }

    public String getMaQL() { return maQL; }

    public void setMaQL(String maQL) {
        // Cho phép maQL null (vì Quản lý cấp cao nhất không có người quản lý)
        if (maQL != null && !maQL.matches("LUCIA\\d{4}"))
            throw new IllegalArgumentException("Mã QL phải dạng LUCIAxxxx");
        this.maQL = maQL;
    }

    public String getMatKhau() { return matKhau; }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.isBlank())
            throw new IllegalArgumentException("Mật khẩu không được rỗng");
        this.matKhau = matKhau;
    }

    public LocalDate getNgaySinh() { return ngaySinh; }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null) throw new IllegalArgumentException("Ngày sinh không được để trống");
        if (ChronoUnit.YEARS.between(ngaySinh, LocalDate.now()) < 18)
            throw new IllegalArgumentException("Nhân viên phải từ 18 tuổi trở lên");
        this.ngaySinh = ngaySinh;
    }

    public String getSoDT() { return soDT; }

    public void setSoDT(String soDT) {
        // Regex sửa lại: Bắt đầu bằng 0, theo sau là 9 chữ số (tổng 10 số)
        if (soDT != null && soDT.matches("0\\d{9}"))
        	this.soDT = soDT;
        else throw new IllegalArgumentException("SĐT phải gồm 10 chữ số và bắt đầu bằng số 0");
    }

    public String getHoTen() { return hoTen; }

    public void setHoTen(String hoTen) {
        if (hoTen == null || hoTen.isBlank())
            throw new IllegalArgumentException("Họ tên không được rỗng");
        this.hoTen = hoTen;
    }

    public String getDiaChi() { return diaChi; }

    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public trinhDo getTrinhDo() { return trinhDo; }

    public void setTrinhDo(trinhDo trinhDo) {
        if (trinhDo == null) throw new IllegalArgumentException("Trình độ không được để trống");
        this.trinhDo = trinhDo;
    }

    public ChucVu getRole() { return role; }

    public void setRole(ChucVu role) {
        if (role == null) throw new IllegalArgumentException("Chức vụ không được để trống");
        this.role = role;
    }

    public LocalDate getNgayVaoLamDate() { return ngayVaoLamDate; }

    public void setNgayVaoLamDate(LocalDate ngayVaoLamDate) {
        if (ngayVaoLamDate == null) {
            this.ngayVaoLamDate = LocalDate.now();
            return;
        }
        if (ngayVaoLamDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày vào làm không được sau ngày hiện tại");
        }
        this.ngayVaoLamDate = ngayVaoLamDate;
    }

    public float getHeSoLuong() { return heSoLuong; }

    public void setHeSoLuong(float heSoLuong) {
        if (heSoLuong < 1.0f || heSoLuong > 10.0f) // Điều chỉnh lại khoảng hợp lý nếu cần
            throw new IllegalArgumentException("Hệ số lương không hợp lệ");
        this.heSoLuong = heSoLuong;
    }

    public int getLuongCB() { return luongCB; }

    public void setLuongCB(int luongCB) {
        if (luongCB <= 0) throw new IllegalArgumentException("Lương căn bản phải lớn hơn 0");
        this.luongCB = luongCB;
    }

    // --- TO STRING (Đã sửa lỗi NullPointerException) ---
    @Override
    public String toString() {
        // Kiểm tra null cho ngày tháng trước khi format để tránh lỗi "temporal"
        String sNgaySinh = (ngaySinh != null) ? ngaySinh.format(FMT) : "Chưa có";
        String sNgayVao = (ngayVaoLamDate != null) ? ngayVaoLamDate.format(FMT) : "Chưa có";

        return String.format("NV [Mã: %s, Tên: %s, SĐT: %s, Chức vụ: %s, Ngày vào: %s, Hệ số: %.2f]", 
                maNV, hoTen, soDT, role, sNgayVao, heSoLuong);
    }
}