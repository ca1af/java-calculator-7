# 요구사항 1 - 쉼표와 숫자가 포함된 문자열이 제공되면, 쉼표를 기준으로 숫자를 분리하여 더한 값을 리턴한다.

> 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.

# 요구사항 2 - 쉼표나, 콜론과 같은 구분자가 포함되면서 숫자가 포함된 문자열이 제공되면, 구분자를 기준으로 숫자를 분리하여 더한 값을 리턴한다.

> 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.

# 요구사항 3 - 커스텀 구분자로 숫자를 구분 가능하다. 커스텀 구분자는 // 로 시작해서 \n 으로 끝난다.

> 예를 들어 "//;\n1;2;3"과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.

## 요구사항 3.1 - 설계를 변경하기

기존에는 Calculator 객체 하나가 모든 계산을 처리하므로, calculate() 매서드 안에서 구분자들을 판별하고, 구분자별 예외 처리를 하고...모든 일이 여기서 일어났다.

크지 않은 프로젝트라서, 물론 위와 같이 구성해도 큰 문제는 없겠지만 좀 더 객체지향적으로, 변경이 있거나 확장이 있을 때에 더 괜찮은 구현이 무엇일까 고민했다.

이에 따라, 내가 개념적으로 나누어 둔 "정규 구분자", "커스텀 구분자" 를 새로 객체로 만들었다. 그리고 이 녀석들이 하는 공통 동작을 하나의 인터페이스로 분리했다.

추가적으로, 요구사항에 명시는 되어 있지 않으나, 구현 예시 등을 통해서 암시적으로 알 수 있는 구현 세부사항들 (ex - " " 이 들어오면 0이 리턴된다) 은 DefaultDelimiter 라는 이름의 구현체로
구성했다.

이로 인해서

1. 코드를 읽는 사람이 개념적인 차이를 바로 캐치 할 수 있다.
2. 공통 동작은 호출하는 Calculator 가 수행하고, 구분자 별로 달리 동작되어야 하는 것들은 각각 구현체들이 수행한다.
3. Factory 클래스를 활용해서, 구분자 정책이 추가되더라도 Calculator 세부 구현이 변경 될 필요가 없다.

---

### 해체하기

1. 문자열이 입력된다.
2. 문자열은 구분자에 의해서 분리된다.
3. 분리된 숫자 값들을 더하기 하여 결과를 리턴한다.
4. 구분자는 개념적으로 두 개로 나눈다. {",", ":"} 는 정규 구분자, // 로 시작하는 녀석들은 커스텀 구분자로 명칭하여 관리한다.
5. 커스텀 구분자는 // 로 시작하고, \n 으로 끝난다.

### 명시되지 않은 요구사항

1. 빈 문자열이 제공되면 예외를 발생시키지 않고, 0 으로 리턴한다.
    - 따라서 명시되어있지는 않지만, 문자열 공백을 없애준다.
2. 구분하는 숫자의 갯수는 몇 개가 들어오든 무방하다.
3. 커스텀 구분자가 시작하고, 닫히지 않았다면 예외이다.
4. 커스텀 구분자는 반드시 문자열 앞쪽에 위치해야 한다.

### 잘못된 값?

1. 구분자를 제외하고, 숫자가 아닌 입력이 들어오면 예외가 발생한다.
2. 음수가 주어지면 예외가 발생한다.