package pers.lbw.seckill.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pers.lbw.seckill.dao.GoodsDao;
import pers.lbw.seckill.domain.Goods;
import pers.lbw.seckill.domain.SeckillGoods;
import pers.lbw.seckill.vo.GoodsVo;

@Service
public class GoodsService {
	
	@Autowired
	GoodsDao gd;
	
	public List<GoodsVo> getGoodsVoList(){
		return gd.getGoodsVoList();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return gd.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(Goods goods) {
		SeckillGoods g=new SeckillGoods();
		g.setGoodsId(goods.getId());
		int c = gd.reduceStock(g);
		return c>0;
	}
}
