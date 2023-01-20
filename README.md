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

## Code
* External Libraries 에 설치된 라이브러리 및 버전 정보 존재
* h2 사용시 jdbc:h2:~/test 로 처음에 접속하여  
  ~/test.mv.db 파일 생성 확인하고  
  jdbc:h2:tcp://localhost/~/test 로 접속

* 아래 명령어로 테이블 생성
```
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
    * 