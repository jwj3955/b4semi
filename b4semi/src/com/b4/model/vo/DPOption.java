package com.b4.model.vo;

//상세페이지에서 옵션별로 표시하기 위한 객체
public class DPOption {
	
	private String productCode;
	private int displayListSeq;
	private String discountCode;
	private int displayOptionPrice;
	private String OptionAvailable;
	private double discountRate;
	private String productName;
	private int discountOptionPrice; //할인 가격
	private String originCountry; //개별 원산지
	
	public DPOption() {
		// TODO Auto-generated constructor stub
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public int getDisplayListSeq() {
		return displayListSeq;
	}
	public void setDisplayListSeq(int displayListSeq) {
		this.displayListSeq = displayListSeq;
	}
	public int getDisplayOptionPrice() {
		return displayOptionPrice;
	}
	public void setDisplayOptionPrice(int displayOptionPrice) {
		this.displayOptionPrice = displayOptionPrice;
	}
	public String getOptionAvailable() {
		return OptionAvailable;
	}
	public void setOptionAvailable(String optionAvailable) {
		OptionAvailable = optionAvailable;
	}
	public double getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getDiscountOptionPrice() {
		return discountOptionPrice;
	}

	public void setDiscountOptionPrice(int discountOptionPrice) {
		this.discountOptionPrice = discountOptionPrice;
	}
	public void setDiscountOptionPrice() {
		this.discountOptionPrice = (int)Math.round(displayOptionPrice*(1-discountRate)/10)*10;
	}
	public String getOriginCountry() {
		return originCountry;
	}
	public void setOriginCountry(String originCountry) {
		this.originCountry = originCountry;
	}
	public String getDiscountCode() {
		return discountCode;
	}
	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}
	
	

}
