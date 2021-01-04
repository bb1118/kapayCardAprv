package com.bbk.kapayCardAprv.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="CARD_APRV")
@SequenceGenerator(
		  name = "CARD_APRV_SEQ_GENERATOR", 
		  sequenceName = "CARD_APRV_SEQ", // 매핑할 데이터베이스 시퀀스 이름 
		  initialValue = 1,
		  allocationSize = 1)
public class CardAprvVO {
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
            		generator = "CARD_APRV_SEQ_GENERATOR")
	private Long id;
	
	@Column(length = 20, nullable = false)
	private String aprv_no;
	
	@Column(length = 20)
	private String rel_aprv_no;
	
	@Column(length = 1)
	private String aprv_type;
	
	@Column(length = 300)
	private String enc_card_info;
	
	@Column(length = 2)
	private String mon_inst;
	
	@Column(length = 11)
	private String aprv_amt;
	
	@Column(length = 11)
	private String aprv_vat;
	
	@Column(length = 450)
	private String aprv_msg;
	
	@Column
	private LocalDateTime input_dt;
	
}
