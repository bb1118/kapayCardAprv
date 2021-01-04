package com.bbk.kapayCardAprv.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CardAprvRepositoryTest {
	
//	private final String strAprvNo 		= "21000000000000000002";
//	private final String strRelAprvNo	= "21000000000000000001";
//	private final String strAprvType 	= "C";
//	private final String strEncCardInfo	= "암호화된카드정보테스트";
//	private final String strMonInst		= "00";
//	private final int	 iAprvAmt 		= 250000;
//	private final int	 iAprvVat 		= 250;
//	private final String strAprvMsg		= "테스트전문";
	
	@Autowired
	CardAprvRepository cardAprvRepository;
	
	@After
    public void cleanup() {
        /** 
        이후 테스트 코드에 영향을 끼치지 않기 위해 
        테스트 메소드가 끝날때 마다 respository 전체 비우는 코드
        **/
		cardAprvRepository.deleteAll();
    }
	
	@Test
	public void searchAllCardAprv() {
		List<CardAprvVO> cardAprvoVOList = cardAprvRepository.findAll();
		CardAprvVO cardAprvVO = cardAprvoVOList.get(0);
//        assertThat(cardAprvVO.getStrAprvNo(), is("21000000000000000002"));
//        assertThat(cardAprvVO.getStrAprvMsg(), is("테스트전문"));
        return;
	} 
	
//	/**
//	 * DB의 모든 데이터 출력
//	 */
//	@Test
//    public void searchCardAprvHisTest1() {
//        //given
//		cardAprvRepository.save(CardAprv.builder()
//				.strAprvNo("21000000000000000002")
//				.strRelAprvNo("21000000000000000001")
//				.strAprvType("P")
//				.strEncCardInfo("cardInfo")
//				.strMonInst("03")
//				.strAprvAmt("250000")
//				.strAprvVat("250")
//                .strAprvMsg("테스트전문")
//                .tInputDt(LocalDateTime.now())
//                .build());
//		
//		List<CardAprv> cardAprvList = cardAprvRepository.findAll();
//		CardAprv cardAprv = cardAprvList.get(0);
//		
//        assertThat(cardAprv.getStrAprvNo(), is("21000000000000000002"));
//        assertThat(cardAprv.getStrAprvMsg(), is("테스트전문"));
//    }
	
//	/**
//	 * strAprvNo 를 조건으로 조회
//	 */
//	@Before
//	public void init() {
//		CardAprvHis cardAprvHis = cardAprvHisRepository.save(CardAprvHis.builder()
//				.strAprvNo(strAprvNo)
//				.strRelAprvNo(strRelAprvNo)
//				.strAprvType(strAprvType)
//				.strEncCardInfo(strEncCardInfo)
//				.strMonInst(strMonInst)
//				.iAprvAmt(iAprvAmt)
//				.iAprvVat(iAprvVat)
//				.strAprvMsg(strAprvMsg)
//				.tInputDt(LocalDateTime.now())
//				.build());
//	}
//	
//	@Test
//    public void searchCardAprvHisTest2() {
//        printout..;
//		CardAprvHis cardAprvHis = cardAprvHisRepository.findByAprvNo(strAprvNo);
//        assertThat(cardAprvHis.getStrAprvNo(), is(strAprvNo));
//        assertThat(cardAprvHis.getStrAprvMsg(), is("strAprvMsg"));
//    }

}
