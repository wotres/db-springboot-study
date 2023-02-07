# JDBC study
## Summary
### JDBC - 데이터베이스 마다 다른 접속 방식을 표준화  
* Java Database Connectivity
* 이름 그대로 자바에서 데이터베이스 접속을 위한 자바 API
  * ex) MySQL JDBC / Oracle JDBC
* 표준 인터페이스에 정의된 대표적 3가지 기능
  * Connection - 연결
  * Statement - SQL 내용
  * ResultSet - SQL 응답
* 장점: 서버의 코드 변경 없이 원하는 JDBC 구현 라이브러리만 변경하면 됨
* 한계: ANSI SQL 이라는 표준이 있지만, 각 데이터베이스마다 SQL, 데이터타입등이 조금싹 다름
  * JPA(Java Persistence API) 로 많은 부분 해결 가능

### JDBC 사용 기술
* SQL Mapper
  * 장점: SQL 응답결과를 객체로 변환 / 반복 코드 제거
  * 단점: 직접 SQL 작성해야함
  * 스프링 JdbcTemplate, MyBatis 
* ORM
  * 객체를 관계형 데이터베이스 테이블과 매핑해주는 기술
  * ORM 기술이 개발자 대신 SQL 을 동적으로 만들어 실행
  * JPA(ORM 표준 인터페이스)
  * 하이버네이트 / 이클립스 링크, 둘다 JPA 구현
* SQL Mapper vs ORM
  * SQL Mapper
    * SQL 만 직접 작성하면 나머지는 해결
  * ORM
    * SQL 작성을 하지않아 개발 생산성이 높아짐
    * 편리하지만 깊이가 필요함

### Connection
* DB Connection
  * 어플리케이션 로직에서 DB 드라이버를 통해 커넥션 조회
  * DB 드라이버와 DB 는 TCP/IP 커넥션 연결
  * 연결 후 ID/PW 와 기타 부가정보 DB 에 전달
  * ID/PW 인증 후 인증 후 DB 세션 생성
  * DB 커넥션 생성 완료 응답
  * DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환
* Connection Pool 을 사용하면
  * 미리 커넥션 생성 후 사용 (보통 10개)
  * 요청시 커넥션 풀중 하나를 할당
  * 커넥션 사용후 종료가 아닌 반환 => 재사용
  * 서버당 최대 커넥션 수를 제한하여 DB 보호도 가능
  * hikariCP 를 Connection pool 로 사용
* javax.sql.DataSource 인터페이스로 커넥션 획득하는 방법 추상화
  * DBCP2 / HikariCP 커넥션 풀 등 원하는 구현체로 갈아끼울 수 있음
  * DataManager는 DataSource 를 사용하지않아 DriverManagerDataSource 클래스를 사용
    * DriverManager 는 Connection 획득할떄마다 파라미터 전달해야함
    * DataSource 는 객체 생성시에 파라미터 선언 후 커넥션 획득할떄는 호출만 일어남 => 설정과 사용의 분리
  * 애플리케이션 로직은 DataSource 인터페이스에만 의존
  * 별도의 쓰레드를 사용해 애플리케이션 성능에 영향 주는것을 줄인다.
* DataManger 보다는 DataSource 를 사용하자.
  * 애플리케이션 코드 변경 없이 DriverManagerDataSource -> HikariDataSource 가능
  * 즉, 애플리케이션 코드는 DataSource 인터페이스만 의존 (DI + OCP)

#### 트랜잭션
* 하나의 거래를 안전하게 처리하도록 보장해주는것
  * 모든 작업이 성공해서 데이터베이스에 정상 반영하는것을 커밋(Commit)
  * 작업 중 하나라도 실패해서 거래 이전으로 되돌리는 것을 롤백(Rollback)
* ACID 보장
  * Atomicity(원자성)
    * 트랜잭션 내 실행 작업들은 하나의 작업처럼 모두 성공하거나 실패해야한다.
    * 실패해도 하나의 작업을 되돌리는것처럼 처리(롤백)
    * 오토 커밋모드여서 쿼리 하나 실행 마다 커밋되면 실패시 심각한문제 => 수동 커밋모드로 전환 = 트랜잭션 시작
    * 서로 다른 세션이 같은 데이터를 수정하면 트랜잭션의 원자성이 꺠짐 => 락
  * Consistency(일관성)
    * 일관성 있는 데이터베이스 상태를 유지할것. 무결성 제약조건을 항상 만족할것
  * Isolation(격리성)
    * 동시에 실행되는 트랜잭션들이 서로 영향을 미치지않게 격리
    * 동시에 데이터를 수정하지 못하도록해야함
    * 동시성과 관련된 성능이슈로 격리수준 선택가능(isolation level)
  * Durability(지속성)
    * 트랜잭션 끝나면 그 결과가 항상 기록되어야 함
    * 중간에 문제가 생겨도 데이터베이스 로그등을 사용하여 내용을 복구
* Isolation level 
  * READ UNCOMMITED(커밋되지 않은 읽기)
  * READ COMMITTED(커밋된 읽기)
  * REPEATABLE READ(반복 가능한 읽기)
  * SERIALIZABLE(직렬화 가능)
* 데이터 베이스 연결구조
  * 사용자 가 클라이언트를 사용해서 데이터베이스 서버에 접근 -> 서버는 내부에 세션 생성 -> 세션이 SQL 실행
  -> 세션이 트랜잭션 시작 후 종료 -> 이후 새로운 트랜잭션 시작 가능 -> 사용자가 커넥션 닫거나 DBA가 세션 강제 종료시 세션 종료
  * 커넥션 풀이 10개의 커넥션을 생성 -> 세션 10개 생성
* 커밋호출전까지는 임시로 데이터 저장
* 조회시에는 보통 Lock을 사용하지 않고 데이터를 조회 가능
  * 조회시에도 Lock 을 획득하고 싶다면 'select ~ for update' 구문 사용 => 변경 불가
* 서비스 계층은 특정 기술에 종속되지않게 가급적 비즈니스 로직만 구현해야함
  * throws SQLException 는 JDBC 기술에 의존 => repository 에서 해결해야 
* 트랜잭션 추상화
  * 추상화된 인터페이스에 서비스가 의존
  * 원하는 구현체를 DI 를 통해 주입
  * OCP 원칙 (서비스는 인터페이스에 의존하고 DI 사용)
  * PlatformTransactionManager
* 스프링은 트랜잭션 동기화 매니저를 제공
  * ThreadLocal 을 사용해 커넥션을 동기화
  * ThreadLocal 을 사용하여 멀티쓰레드 상황에서 안전하게 커넥션 동기화
  * 트랜잭션 동기화 매니저에 보관된 커넥션을 꺼내 사용하여 파라미터로 커넥션을 전달하지 않아도 됨
* @Transactional 사용
  * 스프링이 AOP를 사용해서 트랜잭션을 편리하게 처리해줌
* 선언적 트랜잭션 관리
  * @Transactional 애노테이션을 적용
* 프로그래밍 방식 트랜잭션 관리
* 스프링 부트는 DataSource 를 스프링빈에 자동으로 등록
  * 이때 application.properties 에 있는 속성으로 DataSource 생성 후 등록
  * @TestConfiguration 으로 ComponentScan 가능하게 함 (3_4 test)

## Code
* External Libraries 에 설치된 라이브러리 및 버전 정보 존재
* h2 사용시 jdbc:h2:~/test 로 처음에 접속하여  
  ~/test.mv.db 파일 생성 확인하고  
  jdbc:h2:tcp://localhost/~/test 로 접속

* 아래 명령어로 테이블 생성
  * 해당 테이블과 의존성 관계가 있는 모든 객체들도 함께 삭제
```sql
     drop table member if exists cascade;
     create table member (
         member_id varchar(10),
         money integer not null default 0,
         primary key (member_id)
     );
    insert into member(member_id, money) values ('hi1',10000);
```
* Connection: 데이터베이스 커넥션 획득
* PrepareStatement: 파라미터가 있는 SQL 구문을 실행하는 역할
  * executeUpdate(): 데이터 변경시 사용 => 영향 받은 row 수 return
  * executeQuery(): 데이터 조회시 사용 / 결과를 ResultSet 으로 반환 
* ResultSet: 조회 결과가 순서대로 들어감
  * ResultSet 내부의 cursor 를 이동해 다음 데이터 조회 가능
  * rs.next() 를 호출해 커서를 다음으로 이동
    * 최초 한번은 호출해야 데이터 조회가능
    * true 면 이동 결과 데이터가 있다는 의미
    * false 면 더이상 커서가 가리키는 데이터가 없다는 의미

* 자동 커밋 모드 설정
```sql
    set autocommit true;
    insert into member(member_id, money) values ('data1', 10000);
    insert into member(member_id, money) values ('data2', 10000);
```
* 수동 커밋 모드 설정
```sql
    set autocommit false;
    insert into member(member_id, money) values ('data3', 10000);
    insert into member(member_id, money) values ('data4', 10000);
    commit;
```
* 데이터 초기화
```sql
    set autocommit true;
    delete from member;
```
* update
```sql
    update member set money=10000 - 2000 where member_id = 'memberA';
```
* LOCK 시간 정하기
```sql
    set LOCK_TIMEOUT 60000; 
```

* con.close() 시에 커넥션 풀을 사용하면 커넥션이 종료되는 것이 아니라 풀에 반납됨 => 자동 커밋모드로 변경한 뒤 반납
* DataSourceUtils.getConnection()
  * 트랜잭션 동기화 매니저가 관리하는 커넥션이 있으면 -> 해당 커넥션을 반환
  * 트랜잭션 동기화 매니저가 관리하는 커넥션이 없으면 -> 새로운 커넥션을 생성해서 반환
* DataSourceUtils.releaseConnection()
  * 커넥션을 바로 닫는게 아님 (트랜잭션을 종료 할때까진 살아있어야함)
  * 트랜잭션을 사용하기 위해 동기화된 커넥션 -> 커넥션을 닫지않고 그대로 유지
  * 트랜잭션 동기화 매니저가 관리하는 커넥션이 없으면 -> 커넥션 닫음
* DataSourceTransactionManager => JDBC
* JpaTransactionManager => JPA
* TransactionTemplate 를 스프링이 제공 (템플릭 콜백 패턴)
* @SpringBootTest: 스프링 AOP 를 적용하려면 스프링 컨테이너가 필요
  * 테스트시 스프링 부트를 통해 스프링 컨테이너를 생성
  * 테스트에서 @Autowired 등을 통해 스프링 컨테이너가 관리하는 빈들을 사용 가능
* @TestConfiguration
  * 테스트 안에서 내부 설정 클래스를 만들어서 사용하면 스프링 부트가 자동으로 만들어주는 빈들에 추가로 필요한 스프링 빈들을 등록해 테스트 가능
* SpringCGLIB
  * AOP 프록시(CGLIB) 적용
  