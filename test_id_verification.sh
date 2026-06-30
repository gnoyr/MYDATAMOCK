#!/bin/bash
# MYDATAMOCK 본인확인 테스트 스크립트
# 사용법: ./test_id_verification.sh [hong|kim|hyun|all]
# 앱 없이 본인확인 API를 직접 호출해 CI 일치 여부(idVerifiedYn)를 확인한다.

MYDATA_URL="${MYDATA_URL:-http://192.168.50.30:8081}"

verify() {
  local label="$1" name="$2" resident="$3" addr="$4" phone="$5" issue="$6"
  echo "─────────────────────────────────────────"
  echo "▶ $label  (이름=$name, 주민앞7=$resident, 전화=$phone)"
  curl -s -X POST "$MYDATA_URL/api/mydata/id-verification" \
    -H "Content-Type: application/json" \
    -d "{
      \"appId\": 99999,
      \"idType\": \"RESIDENT_ID\",
      \"idName\": \"$name\",
      \"idResidentNo\": \"$resident\",
      \"idAddress\": \"$addr\",
      \"idPhone\": \"$phone\",
      \"idIssueDate\": \"$issue\"
    }"
  echo ""
}

hong() { verify "홍길동" "홍길동" "9001011" "서울특별시 강남구 테헤란로 123" "01011112222" "20200101"; }
kim()  { verify "김영희" "김영희" "9203152" "부산광역시 해운대구 해운대로 456" "01033334444" "20190601"; }
hyun() { verify "김현길" "김현길" "9501141" "부산 부산진구 서전로37번길 40 가동 503호" "01089853746" "20250902"; }

case "${1:-all}" in
  hong) hong ;;
  kim)  kim ;;
  hyun) hyun ;;
  all)  hong; kim; hyun ;;
  *) echo "사용법: $0 [hong|kim|hyun|all]"; exit 1 ;;
esac
echo "─────────────────────────────────────────"
echo "idVerifiedYn=Y → 통과 / N → CI 불일치(입력값 확인)"
