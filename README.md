## 개발 환경<br>

- SpringToolSuite4<br>
- SpringBoot 2.4.1<br>
- Maven<br>
- myBatis<Br>
- java 1.8<br>
- H2 DB<br>
- JUnit4<br>
- Postman<br>
<br>

## 테이블설계<br>

| No |   TABLE   | TABLE NAME (ENG) | TABLE NAME (KOR) |     COLUMN    |   COLUMN NAME (ENG)  | COLUMN NAME (KOR) |    DATATYPE   | PK | FK | NULL OPTION |                                       DEFINITION                                      |
|:--:|:---------:|:----------------:|:----------------:|:-------------:|:--------------------:|:-----------------:|:-------------:|:--:|:--:|:-----------:|:-------------------------------------------------------------------------------------:|
| 1  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | ID            | ID                   | SEQ               | NUMBER(8)     |  Y | 　 | NOT NULL    | 　                                                                                    |
| 2  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | APRV_NO       | APPROVAL NO          | 관리번호          | VARCHAR(20)   | 　 | 　 | NOT NULL    | YY(4)+일련번호(16)                                                                    |
| 3  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | REL_APRV_NO   | RELATED APPROVAL NO  | 원거래관리번호    | VARCHAR(20)   | 　 | 　 | 　          | -   기능구분 P인 경우: 공백      - 기능구분 C인 경우: 취소 대상인 결제이력의 관리번호 |
| 4  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | APRV_TYPE     | APPROVAL TYPE        | 기능구분          | VARCHAR(1)    | 　 | 　 | 　          | -   P : 결제(PAYMENT)      - C : 취소(CANCEL)                                         |
| 5  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | ENC_CARD_INFO | ENCRYPTED CARD INFO  | 암호화된 카드정보 | VARCHAR(300)  | 　 | 　 | 　          | 암호화된 카드번호+유효기간+CVC 정보                                                   |
| 6  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | MON_INST      | MONTHLY INSTALLMENTS | 할부개월          | VARCHAR(2)    | 　 | 　 | 　          | -   기능구분 P인 경우: 00, 02~12      - 기능구분 C인 경우: 00(일시불) default         |
| 7  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | APRV_AMT      | APPROVAL AMOUNT      | 승인 금액         | VARCHAR(11)   | 　 | 　 | 　          | -   기능구분 P인 경우: 결제 금액      - 기능구분 C인 경우: 취소 금액                  |
| 8  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | APRV_VAT      | APPROVAL VAT         | 승인 부가가치세   | VARCHAR(11)   | 　 | 　 | 　          | 　                                                                                    |
| 9  | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | APRV_MSG      | APPROVAL MESSAGE     | 승인 요청 전문    | VARCHAR2(450) | 　 | 　 | 　          | 카드사에 전송한 전체 String   데이터                                                  |
| 10 | CARD_APRV | CARD APPROVAL    | 카드 승인 이력   | INPUT_DT      | INPUT DATE TIME      | 승인일시          | TIMESTAMP     | 　 | 　 | 　          | 　                                                                                    |
<br>

## 문제해결전략<br>
> [ 기능1 ] 결제 API <br>
> > 1. 함수명: insertCardAprv
> > 2. 입출력 값<br>
> > > - in : 카드번호, 유효기간, CVC, 할부개월수, 결제금액, 부가가치세<br>
> > > - out : 관리번호, 카드사에 전달한 String 데이터 
> > 3. 세부로직<br>
> > > - 입력받은 값에 대한 유효성 체크 후, 만족하지 못하면 return
> > > - 현재 해당 table의 MAX SEQ + 1 값에 0을 채워서 관리번호 채번
> > > - 카드정보 구분자로 '@' 사용하여 카드번호@유효기간@CVC 형태를 암호화
> > > - 할부개월 수가 1자리일 때는 0을 채워서 2자리로 변환
> > > - 입력받은 부가가치세가 없을 때는 결제금액/11 을 소수점 첫째자리에서 반올림한 값으로 할당
> > > - 부가가치세 계산 시에는 정확한 계산을 위해 BigDecimal 사용
> > > - 카드사 전달을 위한 String 데이터는 StringBuffer에 필요한 값들을 apend 해서 생성
> > > - 주의사항: 카드번호, 유효기간, CVC 는 개인정보 보호를 위해 DB에 저장하지 않음
> 
> [ 기능2 ] 결제취소 API <br>
> > 1. 함수명: deleteCardAprv
> > 2. 입출력 값<br>
> > > - in : 카드번호, 유효기간, CVC, 할부개월수, 결제금액, 부가가치세<br>
> > > - out : 관리번호, 카드사에 전달한 String 데이터<br> 
> > 3. 세부로직<br>
> > > - 입력받은 값에 대한 유효성 체크 후, 만족하지 못하면 return
> > > - 입력받은 관리번호에 대한 결제이력이 있는지 확인, 결제이력 없으면 return
> > > - 입력받은 관리번호에 대한 결제이력 조회 후, 암호화 되어있는 카드정보를 복호화
> > > - 입력받은 관리번호가 이미 원거래관리번호로 등록된 이력이 있는지 확인, 결제이력 있으면 return
> > >   <br>( 결제에 대한 전체취소는 1번만 가능하기 때문 )
> > > - 현재 해당 table의 MAX SEQ + 1 값에 0을 채워서 관리번호 채번
> > > - 입력받은 부가가치세가 없을 때는 원거래 부가가치세 값을 동일하게 할당
> 
> [ 기능3 ] 데이터조회 API <br>
> > 1. 함수명: searchCardAprv
> > 2. 입출력 값<br>
> > > - in : 관리번호<br>
> > > - out : 관리번호, 카드번호, 유효기간, CVC, 결제/취소 구분, 결제/취소 금액, 부가가치세<br>
> > 3. 세부로직<br>
> > > - 입력받은 관리번호에 대한 결제이력 조회 후, 암호화 되어있는 카드정보를 복호화
> 
> [ 공통 ] API공통 <br>
> > 1. boolean checkInVal() : 입력 값 유효성 체크함수
> > 2. int countCardAprv() : 현재 해당 table의 MAX SEQ 조회함수 
> > 3. String makeCardAprvFlatFile() : 카드사에 전달할 String 데이터 생성함수
> > 4. String setLrPad() : LPAD와 RPAD 설정함수
> > 5. class CardAes : 카드정보 (카드번호, 유효기간, CVC)의 암복호화 클래스 

<br>
<br>

## 빌드 및 실행방법<br>
> [과제 소스 코드 확인 URL](https://github.com/bb1118/kapayCardAprv.git) <br>
> 
> 1. Spring Boot 와 H2 DB 설치
> 2. 소스 import
> 3. Spring Boot App 실행
> > - 서버 실행 시, data-h2.sql 을 읽어서 자동으로 DB에 기본으로 4개의 임시 데이터 insert
> 4. H2 DB 설정
> > > - Saved Settings: Generic H2 (Embedded)
> > > - Driver Class : org.h2.Driver
> > > - JDBC URL : jdbc:h2:mem:testDB
> > > - User Name : sa
> > > - Password : 1234
> > - port 는 8181 로 설정해둔 상태이기 때문에 H2 DB 는 http://localhost:8181/h2-console 에서 확인 가능
> > - H2 DB 웹 콘솔에서 생성되어있는 4개의 임시 데이터 및 테이블 확인 가능
> 5. POST전송 테스트를 위한 POSTMAN 설치 ( chrome 웹 스토어에서 Postman 을 찾아서 설치하거나 [POSTMAN](https://www.postman.com/) 에서 다운로드) 
> 
> <br>
> 
> [ 기능1 ] 결제 API 테스트<br>
> 1. POSTMAN 실행
> 2. 전송방식 선택하는 select box에서 POST 선택
> 3. 전송방식 select box 옆에 http://localhost:8181/insert 입력
> 4. Body > raw > JSON(application/json) 선택
> 5. 아래와 같이 원하는 입력 값을 JSON 형식으로 작성
> ```
> {
>    "card_no": "123424578962",
>    "exp_dt": "1123",
>    "cvc": "729",
>    "mon_inst": "0",
>    "aprv_amt": "1000"
> }
> ```
> 6. Send 버튼 클릭
> 7. POSTMAN 하단에서 응답 받은 값 확인 가능 
> 
> <br>
> 
> [ 기능2 ] 결제취소 API 테스트<br>
> 1. 위의 기능1 테스트 방법과 1~4, 6~7번 동일
> 2. 위의 기능1 테스트 방법의 5번 입력 값만 다르게 작성
> ```
> {
>    "aprv_no": "00000000000000000005",
>    "aprv_amt": "250000"
> }
> ```
> 
> <br>
> 
> [ 기능3 ] 데이터조회 API 테스트<br>
> 1. 브라우저에 http://localhost:8181/search/관리번호 입력
> 2. http://localhost:8181/search/00000000000000000005 입력 시, 관리번호 00000000000000000005 에 해당하는 데이터들을 조회해서 출력
<br>
