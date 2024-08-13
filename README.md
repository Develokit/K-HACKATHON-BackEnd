## ERD


<img src="img.png" width="500" height = "600"/>




## 작업
### FeignClient에서 WebClient로 일부 요청 이전
- 답변을 SSE 스트림으로 받기 위해 FeignClient에서 처리했던 메시지 생성 요청과 런 생성 요청을 WebClient로 이전
- 메시지 생성과 답변 생성을 체인으로 같이 묶어서 메시지 생성 후 런을 실행해 답변 추출 가능한 상태가 되면 스트림으로 반환
- 답변을 오디오로 반환하는 기능도 동일하게 진행


## 기록
### @SuperBuilder
- @Builder는 단일 클래스에 대한 빌더를 생성
- 공통 속성을 뽑아 User라는 추상 클래스로 만든 뒤, 상속으로 구성한 경우 부모 클래스 필드에는 빌더 패턴이 적용이 안된다
- 상속받은 부모 클래스의 필드까지 적용하기 위해서는 @SuperBuilder를 사용 -> 생성자에서 상속받은 필드도 빌더에서 사용 가능하게 된다
```text
The @SuperBuilder annotation produces complex builder APIs for your classes. 
In contrast to @Builder, @SuperBuilder also works with fields from superclasses. 
However, it only works for types. Most importantly, it requires that all superclasses also have the @SuperBuilder annotation.
```
- 부모와 자식 양쪽에 달아줘야 한다

### JSON 데이터 카멜 케이스로 받기
- @JsonProperty로 필드에 지정해줘도 되지만 필드가 너무 많을 경우,
- @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class) 사용해서 클래스 레벨에 걸어주기

### 에러 1: InternalAuthenticationServiceException
- JSON 데이터를 객체로 역직렬화 할 때 기본 생성자나 명시적으로 정의된 생성자(@JsonProperty)가 없을 때 발생
- ReqDto에 꼭 기본 생성자 부여하자

### 에러 2: InvalidDefinitionException
- Jakson이 직렬화할 수 있는 접근 가능한 필드나 게터가 없기 때문에 발생
- DTO의 private 필드에 대해서 Getter를 열어줘야 한다