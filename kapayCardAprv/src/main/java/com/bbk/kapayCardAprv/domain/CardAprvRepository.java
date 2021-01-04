package com.bbk.kapayCardAprv.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardAprvRepository extends JpaRepository<CardAprvVO, Long>{
	
}
