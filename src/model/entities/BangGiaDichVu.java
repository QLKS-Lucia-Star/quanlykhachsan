package model.entities;

public class BangGiaDichVu {
	private String maBangGia;
	private double giaDichVu;
	public BangGiaDichVu(String maBangGia, double giaDichVu) {
		super();
		this.maBangGia = maBangGia;
		this.giaDichVu = giaDichVu;
	}
	public String getMaBangGia() {
		return maBangGia;
	}
	public void setMaBangGia(String maBangGia) {
		this.maBangGia = maBangGia;
	}
	public double getGiaDichVu() {
		return giaDichVu;
	}
	public void setGiaDichVu(double giaDichVu) {
		this.giaDichVu = giaDichVu;
	}
	
	
}	
