package com.mydata.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mydata.domain.IdVerification;
import com.mydata.domain.IdentityMaster;
import com.mydata.dto.IdVerificationRequest;
import com.mydata.dto.IdVerificationResponse;
import com.mydata.global.util.CiValueGenerator;
import com.mydata.repository.IdVerificationRepository;
import com.mydata.repository.IdentityMasterRepository;

@Service
public class IdVerificationService {

    private final IdVerificationRepository idVerificationRepository;
    private final IdentityMasterRepository identityMasterRepository;
    private final CiValueGenerator ciValueGenerator;

    public IdVerificationService(IdVerificationRepository idVerificationRepository,
                                 IdentityMasterRepository identityMasterRepository,
                                 CiValueGenerator ciValueGenerator) {
        this.idVerificationRepository = idVerificationRepository;
        this.identityMasterRepository = identityMasterRepository;
        this.ciValueGenerator = ciValueGenerator;
    }

    @Transactional
    public IdVerificationResponse verifyIdentity(IdVerificationRequest request) {
        validateRequest(request);

        String residentNo = extractResidentKey(request.getIdResidentNo());
        String residentFront = residentNo.substring(0, 6);
        String genderCode = residentNo.substring(6, 7);
        String issueDate = normalizeIssueDate(request.getIdIssueDate());

        String generatedCiValue = ciValueGenerator.generate(
                request.getIdName(),
                residentFront,
                genderCode,
                request.getIdAddress()
        );
        
        // TODO: 임시 처리 — MYDATA_IDENTITY_MASTER 연동 전까지 무조건 Y 반환
        IdVerification entity = new IdVerification(
                request.getAppId(),
                normalize(request.getIdType()),
                normalize(request.getIdName()),
                residentNo,
                normalizeAddress(request.getIdAddress()),
                normalizeIssueDate(request.getIdIssueDate()),
                generatedCiValue,
                null,
                "Y",
                null
        );
        idVerificationRepository.save(entity);

        return new IdVerificationResponse("Y", generatedCiValue);

//        Optional<IdentityMaster> matchedIdentity = identityMasterRepository
//                .findFirstByCiValueAndIdTypeAndIdIssueDateAndStatus(
//                        generatedCiValue,
//                        normalize(request.getIdType()),
//                        issueDate,
//                        "ACTIVE"
//                );
//
//        String verifiedYn = matchedIdentity.isPresent() ? "Y" : "N";
//
//        Long matchedIdentityId = matchedIdentity
//                .map(IdentityMaster::getIdentityId)
//                .orElse(null);
//
//        String failReason = matchedIdentity.isPresent()
//                ? null
//                : "신원 원장 정보와 일치하지 않습니다.";
//
//        IdVerification entity = new IdVerification(
//                request.getAppId(),
//                normalize(request.getIdType()),
//                normalize(request.getIdName()),
//                residentNo,
//                normalizeAddress(request.getIdAddress()),
//                issueDate,
//                generatedCiValue,
//                matchedIdentityId,
//                verifiedYn,
//                failReason
//        );
//
//        idVerificationRepository.save(entity);
//
//        String responseCiValue = matchedIdentity.isPresent()
//                ? generatedCiValue
//                : null;
//
//        return new IdVerificationResponse(
//                verifiedYn,
//                responseCiValue
//        );
    }

    private void validateRequest(IdVerificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("본인확인 요청값이 없습니다.");
        }

        if (request.getAppId() == null) {
            throw new IllegalArgumentException("신청 ID가 없습니다.");
        }

        if (!hasText(request.getIdType())) {
            throw new IllegalArgumentException("신분증 종류가 없습니다.");
        }

        if (!hasText(request.getIdName())) {
            throw new IllegalArgumentException("이름이 없습니다.");
        }

        if (!hasText(request.getIdResidentNo())) {
            throw new IllegalArgumentException("주민등록번호 정보가 없습니다.");
        }

        if (!hasText(request.getIdAddress())) {
            throw new IllegalArgumentException("주소가 없습니다.");
        }

        if (!hasText(request.getIdIssueDate())) {
            throw new IllegalArgumentException("발급일자가 없습니다.");
        }

        String residentNo = extractResidentKey(request.getIdResidentNo());

        if (!residentNo.matches("\\d{7}")) {
            throw new IllegalArgumentException("주민등록번호 정보는 앞 6자리와 성별코드 1자리를 포함해야 합니다.");
        }

        String genderCode = residentNo.substring(6, 7);

        if (!genderCode.matches("[1-4789]")) {
            throw new IllegalArgumentException("성별코드가 올바르지 않습니다.");
        }

        String issueDate = normalizeIssueDate(request.getIdIssueDate());

        if (!issueDate.matches("\\d{8}")) {
            throw new IllegalArgumentException("발급일자는 yyyyMMdd 또는 yyyy-MM-dd 형식이어야 합니다.");
        }
    }

    private String extractResidentKey(String value) {
        if (value == null) {
            return null;
        }

        String digits = value.replaceAll("[^0-9]", "");

        if (digits.length() == 13) {
            return digits.substring(0, 7);
        }

        if (digits.length() == 7) {
            return digits;
        }

        throw new IllegalArgumentException("주민등록번호 정보는 7자리 또는 13자리 형식이어야 합니다.");
    }

    private String normalizeIssueDate(String value) {
        return value == null ? null : value.replaceAll("[^0-9]", "");
    }

    private String normalizeAddress(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}