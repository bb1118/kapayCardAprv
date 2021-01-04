package com.bbk.kapayCardAprv.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bbk.kapayCardAprv.domain.CardAprvVO;

@Repository("com.bbk.kapayCardAprv.mapper.CardAprvMapper")
public interface CardAprvMapper {
	
	List<CardAprvVO> searchAllCardAprv();
	
	// 데이터조회
	public CardAprvVO searchCardAprv(String strAprvNo) throws Exception;

	// MAX ID
	public int countCardAprv() throws Exception;
	
	// 결제요청
	public int insertCardAprv(CardAprvVO cardAprvVO) throws Exception;
	
	// 취소이력 조회
	public Integer searchRelAprvNo(String strAprvNo) throws Exception;

}
