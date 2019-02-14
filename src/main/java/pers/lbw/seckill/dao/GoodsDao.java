package pers.lbw.seckill.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pers.lbw.seckill.domain.Goods;
import pers.lbw.seckill.domain.SeckillGoods;
import pers.lbw.seckill.vo.GoodsVo;

@Mapper
public interface GoodsDao {
	
	@Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.miaosha_price from seckill_goods sg left join goods g on g.id=sg.goods_id")
	public List<GoodsVo> getGoodsVoList();

	@Select("select g.*,sg.stock_count,sg.start_date,sg.end_date,sg.miaosha_price from seckill_goods sg left join goods g on g.id=sg.goods_id where g.id=#{goodsId}")
	public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);

	//在数据库设置stock_count>0防止超发，通过行锁实现
	@Update("update seckill_goods set stock_count = stock_count-1 where goods_id=#{goodsId} and stock_count>0")
	public int reduceStock(SeckillGoods g);
	
}
