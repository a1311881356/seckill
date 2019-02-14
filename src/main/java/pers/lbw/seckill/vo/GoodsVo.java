package pers.lbw.seckill.vo;

import java.util.Date;

import pers.lbw.seckill.domain.Goods;

public class GoodsVo extends Goods{
	private Integer stockCount;
	private double miaoshaPrice;
	public double getMiaoshaPrice() {
		return miaoshaPrice;
	}
	public void setMiaoshaPrice(double miaoshaPrice) {
		this.miaoshaPrice = miaoshaPrice;
	}
	private Date startDate;
	private Date endDate;
	public Integer getStockCount() {
		return stockCount;
	}
	public void setStockCount(Integer stockCount) {
		this.stockCount = stockCount;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
