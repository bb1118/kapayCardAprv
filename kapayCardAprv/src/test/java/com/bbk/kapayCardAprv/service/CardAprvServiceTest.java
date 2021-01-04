package com.bbk.kapayCardAprv.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.bbk.kapayCardAprv.domain.CardAprvVO;
import com.bbk.kapayCardAprv.domain.CardAprvRepository;
//import com.bbk.kapayCardAprv.web.CardAprvWebRestController.CardAprvPaymnetRequestDto;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardAprvServiceTest {
	
	@Autowired
	private CardAprvService cardAprvService;
	
	@Autowired
	private CardAprvRepository cardAprvRepository;
	
	@After
    public void cleanup () {
		cardAprvRepository.deleteAll();
	}
	
	@Test
	public int countCardAprv() throws Exception {
		int iMaxId = 0;
		iMaxId = cardAprvService.countCardAprv();
		return iMaxId;
	}
	
	
//	
//	@Test
//	public void insertDtoData() {
//		//given
//		CardAprvPaymnetRequestDto dto = CardAprvPaymnetRequestDto.builder()
//				.strAprvNo("210002")
//                .strRelAprvNo("210001")
//                .strAprvType("P")
//                .strEncCardInfo("asdqwezxc")
//                .strMonInst("03")
//                .strAprvAmt("35000")
//                .strAprvVat("350")
//                .strAprvMsg("TestMsg")
//                .build();
//		
//		//when
//		cardAprvService.save(dto);
//		
//		//then
//		CardAprv cardAprv = cardAprvRepository.findAll().get(0);
//		assertThat(cardAprv.getStrAprvNo()).isEqualTo(dto.getStrAprvNo());
//		assertThat(cardAprv.getStrRelAprvNo()).isEqualTo(dto.getStrRelAprvNo());
//		assertThat(cardAprv.getStrAprvType()).isEqualTo(dto.getStrAprvType());
//		assertThat(cardAprv.getStrEncCardInfo()).isEqualTo(dto.getStrEncCardInfo());
//		assertThat(cardAprv.getStrMonInst()).isEqualTo(dto.getStrMonInst());
//		assertThat(cardAprv.getStrAprvAmt()).isEqualTo(dto.getStrAprvAmt());
//		assertThat(cardAprv.getStrAprvVat()).isEqualTo(dto.getStrAprvVat());
//		assertThat(cardAprv.getStrAprvMsg()).isEqualTo(dto.getStrAprvMsg());
//	}
	

}
