# 뉴스 기사 수집 배치(Batch)

* jsoup + 스프링 부트 배치를 이용해 뉴스 기사를 수집한다.
* 수집한 데이터는 몽고디비에 저장한다.
* 수집한 뉴스 기사는 카프카로 전송한다.
* 배치 실행 스케쥴관리는 쿼츠로 한다.

* 참고
    * 스프링 배치
        * https://cheese10yun.github.io/spring-batch-basic
        * https://jojoldu.tistory.com/328?category=635883
    * 스프링 부트 + 몽고 디비
        * https://ssoonidev.tistory.com/62
    * 스프링 배치 + 쿼츠
        * https://howtodoinjava.com/spring-batch/batch-quartz-java-config-example/
    * 스프링 + 카프카
        * https://m.blog.naver.com/talag/220930435941


# Build & Run

```bash

gradle clean build -x test

java -jar build/libs/newscrawl-0.0.1-SNAPSHOT.jar

```