package com.mydata.repository;

import com.mydata.domain.HometaxIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HometaxIncomeRepository extends JpaRepository<HometaxIncome, Long> {

    // ciValue 기준 최신 귀속연도 SUCCESS 소득 목록 조회
    @Query("SELECT h FROM HometaxIncome h " +
           "WHERE h.ciValue = :ciValue " +
           "AND h.taxYear = (" +
           "    SELECT MAX(h2.taxYear) FROM HometaxIncome h2 " +
           "    WHERE h2.ciValue = :ciValue AND h2.queryStatus = 'SUCCESS'" +
           ") " +
           "AND h.queryStatus = 'SUCCESS'")
    List<HometaxIncome> findLatestYearByCiValue(@Param("ciValue") String ciValue);

    // creditAppId 기준 NO_DATA/FAILED 레코드 조회 (서류 심사 통과 시 업데이트 대상)
    @Query("SELECT h FROM HometaxIncome h " +
           "WHERE h.creditAppId = :creditAppId " +
           "AND h.queryStatus IN ('NO_DATA', 'FAILED')")
    List<HometaxIncome> findUnverifiedByCreditAppId(@Param("creditAppId") Long creditAppId);

    // 시드 더미(credit_app_id=0) 정리용 — 재시드 멱등성 확보
    void deleteByCreditAppId(Long creditAppId);
}