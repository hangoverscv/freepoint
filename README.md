### 실행방법
* H2 DB 접속 => ./h2.sh (접속 정보 : application.properties 참고)
* FreepointApplication 실행
* Swagger 접속 => http://localhost:8080/swagger-ui/index.html
* API 테스트 순서 => 사용자 생성 -> 주문 생성 -> 포인트 적립/사용/적립취소/사용취소
  * ex.) 
  1) user : /api/users
  2) order : /api/order/create
  3) point 
     - 적립 : /api/points/deposit
     - 사용 : /api/points/user
     - 적립 취소 : /api/points/deposit/cancel
     - 사용 취소 : /api/points/use/cencel

### API 
* User : 사용자 생성, 조회, 삭제
* Order : 주문 생성
* Point : 포인트 적립/사용/적립취소/사용취소

### 엔티티
* Order : 주문
* User : 사용자
* Points : 포인트
* PointTrack : 포인트 추적/관리

# ERD
* /resources/static/ERD.png

### 참고
* 실제 서비스라면 주문 시에 포인트 사용해서 차감 처리 해야 하지만, 개발 코드는 사용자 및 주문 먼저 생성 후 포인트 사용하도록 구현 
* 포인트 적립의 경우도 사용자 먼저 생성 후 포인트 적립 가능
* 1회 최대 적립가능 포인트는 application.properties에 설정 (point.max.amount=100000)
* 사용자별 최대 보유 포인트는 사용자 생성 시 최초 설정 후 API 통해 변경 가능 => PUT: ~/api/users/{userId}/max-points (단, 변경 시 현재 이용 가능한 포인트 보다 적은 포인트로 변경은 불가하도록 예외처리)
* 수기지급의 경우 POINTS.IS_MANUAL로 구분
* service는 인터페이스를 두어서 구현체 클래스를 별도로 만든 구조(ex. xxxxServiceV1 ) => 추후 요구 사항 변경에 의한 유지보수 및 의존 관계 고려
* users 테이블에 사용자별 가용포인트 컬럼은 추가 하려했으나, 결국 기간 만료가 된 포인트 체크하여 가용 포인트 차감해야하는 로직이 필요할 것 같아서 컬럼 추가하지 않음(현재 코드 상 미구현)
  => points 테이블의 user_id로 현재 가용 포인트 확인 가능
  => 만료기간이 지난 포인트 체크하여 사용 불가 처리 및 가용 포인트 차감하는 api로 구현해도 되고,
  => 실제 서비스라면 만료기간이 지난 포인트에 대해서는 배치로 구현해서 적절한 시간대에 사용 불가 처리하는 방향으로 구현
* 사용 취소 시 적립 된 포인트 중 만료되어 신규 포인트 적립 시에는 만료일 기본값으로 설정(365일)
