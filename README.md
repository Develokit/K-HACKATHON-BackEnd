

## @SuperBuilder
- @Builder는 단일 클래스에 대한 빌더를 생성
- 공통 속성을 뽑아 User라는 추상 클래스로 만든 뒤, 상속으로 구성한 경우 부모 클래스 필드에는 빌더 패턴이 적용이 안된다
- 상속받은 부모 클래스의 필드까지 적용하기 위해서는 @SuperBuilder를 사용 -> 생성자에서 상속받은 필드도 빌더에서 사용 가능하게 된다
```text
The @SuperBuilder annotation produces complex builder APIs for your classes. 
In contrast to @Builder, @SuperBuilder also works with fields from superclasses. 
However, it only works for types. Most importantly, it requires that all superclasses also have the @SuperBuilder annotation.
```
- 부모와 자식 양쪽에 달아줘야 한다

## JSON 데이터 카멜 케이스로 받기
- @JsonProperty로 필드에 지정해줘도 되지만 필드가 너무 많을 경우,
- @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class) 사용해서 클래스 레벨에 걸어주기
