package com.bbk.kapayCardAprv.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bbk.kapayCardAprv.domain.CardAprvInfoVO;
import com.bbk.kapayCardAprv.domain.CardAprvVO;
import com.bbk.kapayCardAprv.mapper.CardAprvMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service("com.bbk.kapayCardAprv.service.CardAprvService")
public class CardAprvService {
	
	@Resource(name="com.bbk.kapayCardAprv.mapper.CardAprvMapper")
	CardAprvMapper mCardAprvMapper;
	
	
	/**
	 * 기능1. 결제 API 
	 * 
	 * @param cardAprvVO
	 * @return
	 * @throws Exception
	 */
	public CardAprvVO insertCardAprv(CardAprvInfoVO cardAprvInfoVO) throws Exception{
		LogErr("기능1. Service >>> insertCardAprv 시작 >>>" + cardAprvInfoVO.toString());
		int iMaxId = 0;
		
		String strAprvNo = "";		// 관리번호
		String strRelAprvNo = "";	// 원거래관리번호
		String strAprvType = "P";	// 결제유형
		String strEncCardInfo = "";	// 암호화된 카드정보
		String strMonInst = "";		// 할부개월수
		int iMonInst = 0;
		String strAprvAmt = "";		// 결제금액
		int iAprvAmt = 0;
		String strAprvVat = "";		// 부가가치세
		int iAprvVat = 0;
		BigDecimal dAprvVat = new BigDecimal("0.0");
		String strAprvMsg = "";		// 승인요청 전문
		
		String strCardNo = "";		// 카드번호
		String StrExpDt = "";		// 유효기간
		String strCvc = "";			// CVC
		
		CardAprvVO cardAprvVO = new CardAprvVO();
		CardAes cardAes = new CardAes();	// 암복호화
		
		try {
			
			strCardNo = StringUtils.defaultString(cardAprvInfoVO.getCard_no());
			StrExpDt = StringUtils.defaultString(cardAprvInfoVO.getExp_dt());
			strCvc = StringUtils.defaultString(cardAprvInfoVO.getCvc());
			strMonInst = StringUtils.defaultString(cardAprvInfoVO.getMon_inst(), "0");
			strAprvAmt = StringUtils.defaultString(cardAprvInfoVO.getAprv_amt(), "0");
			strAprvVat = StringUtils.defaultString(cardAprvInfoVO.getAprv_vat(), "0");
			iMonInst = Integer.parseInt(strMonInst);
			iAprvAmt = Integer.parseInt(strAprvAmt);
			iAprvVat = Integer.parseInt(strAprvVat);
			
			// 유효성 체크
			Boolean bCheckInVal = checkInVal(strAprvType, strCardNo, StrExpDt, strCvc
											, strMonInst, strAprvAmt, strAprvVat
											, iMonInst, iAprvAmt,  iAprvVat);
			if(!bCheckInVal) return cardAprvVO;
			
			// 관리번호 생성
			iMaxId = countCardAprv();
			strAprvNo = setLrPad(String.valueOf(iMaxId+1), 20, "0", "L");
			LogErr("기능1. 관리번호 생성 >>> " + strAprvNo);
			
			// 카드정보 암호화
			LogErr("기능1. 암호화 전 카드정보 >>> " + strCardNo+"@"+StrExpDt+"@"+strCvc);
			strEncCardInfo = cardAes.encAES(strCardNo+"@"+StrExpDt+"@"+strCvc);
			LogErr("기능1. 암호화 된 카드정보 >>> " + strEncCardInfo);
			
			// 할부개월수(2자리)
			strMonInst = setLrPad(strMonInst, 2, "0", "L");
			
			// 부가가치세
			LogErr("기능1. 현재 부가가치세 >>> [" + strAprvVat + "]"); 
			if("0".equals(strAprvVat)) {
				BigDecimal dAprvAmt = new BigDecimal(strAprvAmt);
				LogErr("기능1. 결제금액 >>> " + dAprvAmt);
				dAprvVat = dAprvAmt.divide(new BigDecimal(11), RoundingMode.HALF_UP);
				LogErr("기능1. 부가가치세 >>> " + dAprvVat);
				strAprvVat = String.valueOf(dAprvVat);
				LogErr("기능1. 최종 부가가치세 >>> " + strAprvVat);
			}
			
			// 전문 만들기
			strAprvMsg = makeCardAprvFlatFile(strAprvNo, strAprvType
											, strCardNo, strMonInst, StrExpDt, strCvc
											, strAprvAmt, strAprvVat
											, strRelAprvNo, strEncCardInfo);
			
			cardAprvVO.setId((long) (iMaxId+1));
			cardAprvVO.setAprv_no(strAprvNo);			// 관리번호
			cardAprvVO.setRel_aprv_no(strRelAprvNo);	// 원거래관리번호
			cardAprvVO.setAprv_type(strAprvType);		// 기능구분(P:결제)
			cardAprvVO.setEnc_card_info(strEncCardInfo);
			cardAprvVO.setMon_inst(strMonInst);
			cardAprvVO.setAprv_amt(strAprvAmt);
			cardAprvVO.setAprv_vat(strAprvVat);
			cardAprvVO.setAprv_msg(strAprvMsg);
			cardAprvVO.setInput_dt(LocalDateTime.now());
			
			// 암호화된 카드정보만 DB에 저장
			// 카드번호, 유효기간, CVC 는 개인정보 보호를 위해 저장금지
			mCardAprvMapper.insertCardAprv(cardAprvVO);
			
		} catch(Exception ex) {
			LogErr("err " + ex.toString());
		}
		return cardAprvVO;
	}
	
	
	/**
	 * 기능2. 결제취소 API 
	 * 
	 * @param cardAprvVO
	 * @return
	 * @throws Exception
	 */
	public CardAprvVO deleteCardAprv(CardAprvInfoVO cardAprvInfoVO) throws Exception{
		LogErr("기능2. Service >>> deleteCardAprv 시작 >>>" + cardAprvInfoVO.toString());
		int iMaxId = 0;
		int iDelHis = 0;
		
		String strAprvNo = "";		// 관리번호
		String strRelAprvNo = "";	// 원거래관리번호
		String strAprvType = "C";	// 결제유형
		String strEncCardInfo = "";	// 암호화된 카드정보
		String strMonInst = "00";	// 할부개월수
		int iMonInst = 0;
		String strAprvAmt = "";		// 결제금액
		int iAprvAmt = 0;
		String strAprvVat = "";		// 부가가치세
		int iAprvVat = 0;
		String strAprvMsg = "";		// 승인요청 전문
		
		String strCardNo = "";		// 카드번호
		String StrExpDt = "";		// 유효기간
		String strCvc = "";			// CVC
		
		CardAprvVO cardAprvVO = new CardAprvVO();
		CardAprvVO cardAprvHisVO = new CardAprvVO();
		
		CardAes cardAes = new CardAes();	// 암복호화
		
		try {
			
			strRelAprvNo = StringUtils.defaultString(cardAprvInfoVO.getAprv_no());
			strAprvAmt = StringUtils.defaultString(cardAprvInfoVO.getAprv_amt(), "0");
			strAprvVat = StringUtils.defaultString(cardAprvInfoVO.getAprv_vat(), "0");
			iAprvAmt = Integer.parseInt(strAprvAmt);
			iAprvVat = Integer.parseInt(strAprvVat);
			
			// 유효성 체크
			Boolean bCheckInVal = checkInVal(strAprvType, strCardNo, StrExpDt, strCvc
											, strMonInst, strAprvAmt, strAprvVat
											, iMonInst, iAprvAmt,  iAprvVat);
			if(!bCheckInVal) return cardAprvVO;
			
			LogErr("기능2. 입력받은 관리번호 확인 >>> " + strRelAprvNo);
			// 결제 이력 확인
			cardAprvHisVO = mCardAprvMapper.searchCardAprv(strRelAprvNo);
			if(cardAprvHisVO == null) return cardAprvVO;
			
			if(cardAprvHisVO != null) {
				// 개인정보 보호를 위해 카드번호, 유효기간, CVC는 저장하지 않았으므로 복호화 필요
				strEncCardInfo = cardAprvHisVO.getEnc_card_info();
				LogErr("기능2. 암호화 된 카드정보 >>> " + strEncCardInfo);
				String strDecCardInfo = cardAes.decAES(strEncCardInfo);
				LogErr("기능2. 복호화 된 카드정보 >>> " + strDecCardInfo);

				if(!"".equals(strDecCardInfo)) {
					String[] strCardInfo = strDecCardInfo.split("@");
					strCardNo = strCardInfo[0];
					StrExpDt = strCardInfo[1];
					strCvc = strCardInfo[2];
					LogErr("기능2. 카드정보 >>> " + strCardNo + " >>> " + StrExpDt + " >>> " + strCvc);
				}
			}
			
			// 결제취소 이력 확인(결제에 대한 전체취소는 1번만 가능)
			iDelHis = mCardAprvMapper.searchRelAprvNo(strRelAprvNo);
			LogErr("기능2. 결제취소 이력 >>> " + iDelHis);
			if(iDelHis > 0) return cardAprvVO;
			
			// 관리번호 생성
			iMaxId = countCardAprv();
			strAprvNo = setLrPad(String.valueOf(iMaxId+1), 20, "0", "L");
			LogErr("기능2. 관리번호 생성 >>> " + strAprvNo);
			
			// 부가가치세
			LogErr("기능1. 현재 부가가치세 >>> [" + strAprvVat + "]"); 
			if("0".equals(strAprvVat)) {
				strAprvVat = StringUtils.defaultString(cardAprvHisVO.getAprv_vat(), "0");
				LogErr("기능2. 부가가치세 >>> " + strAprvVat);
			}
			
			// 전문 만들기
			strAprvMsg = makeCardAprvFlatFile(strAprvNo, strAprvType
											, strCardNo, strMonInst, StrExpDt, strCvc
											, strAprvAmt, strAprvVat
											, strRelAprvNo, strEncCardInfo);
			
			cardAprvVO.setId((long) (iMaxId+1));
			cardAprvVO.setAprv_no(strAprvNo);			// 관리번호
			cardAprvVO.setRel_aprv_no(strRelAprvNo);	// 원거래관리번호
			cardAprvVO.setAprv_type(strAprvType);		// 기능구분(C:취소)
			cardAprvVO.setEnc_card_info(strEncCardInfo);
			cardAprvVO.setMon_inst(strMonInst);
			cardAprvVO.setAprv_amt(strAprvAmt);
			cardAprvVO.setAprv_vat(strAprvVat);
			cardAprvVO.setAprv_msg(strAprvMsg);
			cardAprvVO.setInput_dt(LocalDateTime.now());
			
			mCardAprvMapper.insertCardAprv(cardAprvVO);
			
		} catch(Exception ex) {
			LogErr("err " + ex.toString());
		}
		return cardAprvVO;
	}
	
	
	/**
	 * 기능3. 데이터조회 API 
	 * @param String aprv_no
	 * @return CardAprvInfoVO
	 * @throws Exception
	 */
	public CardAprvInfoVO searchCardAprv(String aprv_no) throws Exception {
		LogErr("Service >>> searchCardAprv 시작 >>>");
		CardAprvVO cardAprvVO = new CardAprvVO();
		CardAprvInfoVO cardAprvInfoVO = new CardAprvInfoVO();
		CardAes cardAes = new CardAes();	// 암복호화
		
		String strDecCardInfo = "";	// 복호화된 카드정보
		String strCardNo = "";		// 카드번호
		String StrExpDt = "";		// 유효기간
		String strCvc = "";			// CVC
		
		try {
			cardAprvVO = mCardAprvMapper.searchCardAprv(aprv_no);
			
			if(cardAprvVO != null) {
				LogErr("기능3. 암호화 된 카드정보 >>> " + cardAprvVO.getEnc_card_info());
				strDecCardInfo = cardAes.decAES(cardAprvVO.getEnc_card_info());
				LogErr("기능3. 복호화 된 카드정보 >>> " + strDecCardInfo);

				if(!"".equals(strDecCardInfo)) {
					String[] strCardInfo = strDecCardInfo.split("@");
					strCardNo = strCardInfo[0];
					strCardNo = maskingCard(strCardNo);
					StrExpDt = strCardInfo[1];
					strCvc = strCardInfo[2];
					LogErr("기능3. 카드정보 >>> " + strCardNo + " >>> " + StrExpDt + " >>> " + strCvc);
				}

				cardAprvInfoVO.setAprv_no(cardAprvVO.getAprv_no());		// 관리번호
				cardAprvInfoVO.setCard_no(strCardNo);					// 마스킹된 카드번호
				cardAprvInfoVO.setExp_dt(StrExpDt);						// 유효기간
				cardAprvInfoVO.setCvc(strCvc);							// CVS
				cardAprvInfoVO.setMon_inst(cardAprvVO.getMon_inst());	// 할부개월수
				cardAprvInfoVO.setAprv_type(cardAprvVO.getAprv_type());	// 결제/취소구분(P:결제 / C:취소)
				cardAprvInfoVO.setAprv_amt(cardAprvVO.getAprv_amt());	// 결제/취소 금액
				cardAprvInfoVO.setAprv_vat(cardAprvVO.getAprv_vat());	// 부가가치세
			}
			
		} catch(Exception ex) {
			LogErr("err " + ex.toString());
		}
		return cardAprvInfoVO;
	}
	
	
	/**
	 * 입력 받은 값 유효성 체크
	 * 
	 * @param strAprvType
	 * @param strCardNo
	 * @param StrExpDt
	 * @param strCvc
	 * @param strMonInst
	 * @param strAprvAmt
	 * @param strAprvVat
	 * @param iMonInst
	 * @param iAprvAmt
	 * @param iAprvVat
	 * @return
	 */
	private boolean checkInVal(String strAprvType, String strCardNo, String StrExpDt, String strCvc
							, String strMonInst, String strAprvAmt, String strAprvVat
							, int iMonInst, int iAprvAmt, int iAprvVat) {
		
		if("P".equals(strAprvType)) {
			if(strCardNo.length() < 10 || strCardNo.length() > 16
					|| StrExpDt.length() != 4 || strCvc.length() != 3) {
				// 카드번호가 10~16자리가 아닌 경우 return
				// 유효기간이 4자리가 아닌 경우, CVC가 3자리가 아닌 경우 return
				return false;
			}
			if(iMonInst < 0 || iMonInst > 12) {
				// 할부개월수가 0보다 작거나 12보다 큰 경우 return
				return false;
			}
			if(!isStringDouble(strCardNo)
					|| !isStringDouble(StrExpDt)
					|| !isStringDouble(strCvc)
					|| !isStringDouble(strMonInst)) {
				// 카드번호, 유효기간, CVC, 할부개월수가 숫자가 아닌 경우 return
				return false;
			}
		}
		if(!isStringDouble(strAprvAmt)
				|| !isStringDouble(strAprvVat)) {
			// 결제금액, 부가가치세가 숫자가 아닌 경우 return
			return false;
		}
		if(iAprvAmt < 100 || iAprvAmt > 1000000000) {
			// 결제금액이 100보다 작거나 10억보다 큰 경우 return
			return false;
		}
		if(iAprvVat > iAprvAmt) {
			// 부가가치세가 결제금액보다 큰 경우 return
			return false;
		}
		return true;
	}
	
	
	/**
	 * MAX ID 구하기
	 * 
	 * @return
	 * @throws Exception
	 */
	public int countCardAprv() throws Exception {
		LogErr("MAX ID 시작 >>> ");
		int iMaxId = 0;
		iMaxId = mCardAprvMapper.countCardAprv();
		LogErr("MAX ID >>> " + iMaxId);
		return iMaxId;
	}
	
	
	/**
	 * 카드사에 전달할 승인요청 전문
	 * 
	 * @param strAprvNo
	 * @param strAprvType
	 * @param strCardNo
	 * @param strMonInst
	 * @param StrExpDt
	 * @param strCvc
	 * @param strAprvAmt
	 * @param strAprvVat
	 * @param strRelAprvNo
	 * @param strEncCardInfo
	 * @return String strAprvMsg
	 */
	private String makeCardAprvFlatFile(String strAprvNo, String strAprvType
										, String strCardNo, String strMonInst, String StrExpDt, String strCvc
										, String strAprvAmt, String strAprvVat
										, String strRelAprvNo, String strEncCardInfo) {
		String flatFile = "";
		StringBuffer sbHeader = null;
		StringBuffer sbData = null;
		
		String strHdDataLen = "";
		String strAprvTp = "";
		String strFiller = "";
		
		int iHeaderDataLen = 0;
		int iDataLen = 0;
		
		if(!"".equals(strAprvType)) {
			if("P".equals(strAprvType)) {
				strAprvTp = "PAYMENT";
			} else if ("C".equals(strAprvType)) {
				strAprvTp = "CANCEL";
			}
		}
		
		// 데이터부문
		sbData = new StringBuffer();
		sbData.append(setLrPad(strCardNo		,  20, " ", "R"));
		sbData.append(setLrPad(strMonInst		,   2, "0", "L"));
		sbData.append(setLrPad(StrExpDt			,   4, " ", "R"));
		sbData.append(setLrPad(strCvc			,   3, " ", "R"));
		sbData.append(setLrPad(strAprvAmt		,  10, " ", "L"));
		sbData.append(setLrPad(strAprvVat		,  10, "0", "L"));
		sbData.append(setLrPad(strRelAprvNo		,  20, " ", "R"));
		sbData.append(setLrPad(strEncCardInfo	, 300, " ", "R"));
		sbData.append(setLrPad(strFiller		,  47, " ", "R"));
		
		// 데이터부문 길이
		iDataLen = sbData.toString().length();
		
		// 공통헤더부문의 데이터길이 항목 값
		iHeaderDataLen = iDataLen + 10 + 20;	// 10(데이터 구분), 20(관리번호)
		strHdDataLen = String.valueOf(iHeaderDataLen);
		
		// 공통헤더부문
		sbHeader = new StringBuffer();
		sbHeader.append(setLrPad(strHdDataLen	,  4, " ", "L"));
		sbHeader.append(setLrPad(strAprvTp		, 10, " ", "R"));
		sbHeader.append(setLrPad(strAprvNo		, 20, " ", "R"));
		
		// 전체 String 데이터
		flatFile = sbHeader.toString() + sbData.toString();
		LogErr("flatFile >>> [" + flatFile + "]"); 
		return flatFile;
	}
	
	
	/**
	 * DB 전체 데이터 구하기
	 */
	public List<CardAprvVO> searchAllCardAprv(){
		LogErr("Service >>> searchAllCardAprv 시작 >>>");
		return mCardAprvMapper.searchAllCardAprv();
	}
	
	
	/**
	 * 카드번호 앞6자리와 뒤 3자리를 제외한 나머지를 마스킹처리
	 * 
	 * @param strData
	 * @return
	 */
	private static String maskingCard(String strData) {
		if ("".equals(strData)) return "";
		
		StringBuilder builder = new StringBuilder();
		char[] chars = strData.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			builder.append(i >= 6 && i < strData.length()-3 ? "*" : chars[i]);
		}
		return builder.toString();
	}
	
	
	/**
	 * LPAD / RPAD
	 * 
	 * @param strContext
	 * @param iLen
	 * @param strChar
	 * @return
	 */
	private static String setLrPad(String strContext, int iLen, String strChar, String strType) {
		String strResult = "";
		StringBuilder sbAddChar = new StringBuilder();
		for(int i = strContext.length(); i < iLen; i++) {
			sbAddChar.append(strChar);
		}
		if("L".equals(strType)) {
			strResult = sbAddChar + strContext;
		} else {
			strResult = strContext + sbAddChar;
		}
		return strResult;
	}
	
	
	/**
	 * 문자열 포함 여부 확인
	 * 
	 * @param s
	 * @return boolean
	 */
	public static boolean isStringDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	
	/**
	 * Log 출력
	 * 
	 * @param s
	 */
	static void LogErr(String s) {
		System.out.println(s);
	}
	
}
