package com.bbk.kapayCardAprv.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bbk.kapayCardAprv.domain.CardAprvInfoVO;
import com.bbk.kapayCardAprv.domain.CardAprvVO;
import com.bbk.kapayCardAprv.service.CardAprvService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class CardAprvWebRestController {

	@Autowired
	private CardAprvService cardAprvService;
	
	
	/**
	 * 기능1. 결제API
	 * 
	 * @param cardAprvInfoVO
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(path = "/insert")
	public CardAprvVO insertCardAprv(@RequestBody CardAprvInfoVO cardAprvInfoVO) throws Exception {
		LogErr("Controller >>> insertCardAprv 시작 >>>");
		return cardAprvService.insertCardAprv(cardAprvInfoVO);
	}
	
	
	/**
	 * 기능2. 결제취소API
	 * 
	 * @param cardAprvInfoVO
	 * @return
	 * @throws Exception 
	 */
	@PostMapping(path = "/delete")
	public CardAprvVO deleteCardAprv(@RequestBody CardAprvInfoVO cardAprvInfoVO) throws Exception {
		LogErr("Controller >>> deleteCardAprv 시작 >>>");
		return cardAprvService.deleteCardAprv(cardAprvInfoVO);
	}
	
	
	/**
	 * 기능3. 데이터조회 API
	 * 
	 * @param String aprv_no
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path = "/search/{aprv_no}")
	public CardAprvInfoVO searchCardAprv(@PathVariable String aprv_no) throws Exception {
		LogErr("Controller >>> searchCardAprv 시작 >>>");
		return cardAprvService.searchCardAprv(aprv_no);
	}
	
	
	/**
	 * MAX ID 구하기
	 * 
	 * @return
	 * @throws Exception
	 */
	@GetMapping(path = "/count")
	public int countCardAprv() throws Exception {
		LogErr("Controller >>> countCardAprv 시작 >>>");
		return cardAprvService.countCardAprv();
	}
	
	
	/**
	 * DB 전체 데이터 구하기
	 */
	@GetMapping(path = "/searchAll")
	public List<CardAprvVO> searchAllCardAprv() {
		LogErr("Controller >>> searchAllCardAprv 시작 >>>");
		return cardAprvService.searchAllCardAprv();
	}
	
	
	/**
	 * Log 출력
	 * 
	 * @param s
	 */
	static void LogErr(String s){
        System.out.println(s);
    }
	
}
