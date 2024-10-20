package calculator.delimiter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import calculator.global.ErrorMessage;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CustomDelimiterTest {

    private CustomDelimiter customDelimiter;

    @BeforeEach
    void setUp() {
        this.customDelimiter = new CustomDelimiter();
    }

    @ParameterizedTest
    @DisplayName("커스텀 구분자에 해당하는 지 판별 할 수 있다.")
    @MethodSource("provideCustomDelimiterTestCases")
    void applicable(String input, boolean expected) {
        boolean applicable = customDelimiter.applicable(input);
        Assertions.assertThat(applicable).isEqualTo(expected);
    }

    private static Stream<Arguments> provideCustomDelimiterTestCases() {
        return Stream.of(
                // 정상적인 커스텀 구분자
                Arguments.of("//;\\n1;2", true),
                Arguments.of("//|\\n3|4", true),
                Arguments.of("//:\\n7:8", true),
                Arguments.of("// \\n", true),

                // 엣지 케이스: 빈 문자열
                Arguments.of("", false),

                // 엣지 케이스: 비정상적인 형식
                Arguments.of(";;1;2", false),
                Arguments.of("1,2", false),

                // 엣지 케이스: 시작은 맞지만 \\n 없이 종료
                Arguments.of("//;", false),
                Arguments.of("//;\\n", true)
        );
    }

    @Test
    @DisplayName("커스텀 구분자는 공백(길이가0)일 수 없다.")
    void emptyDelimiterNotAllowed() {
        String input = "//\\n123";
        String expectedMessage = ErrorMessage.EMPTY_STRING_IS_NOT_ALLOWED.getMessage();

        assertThatThrownBy(() -> customDelimiter.tokenize(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @ParameterizedTest
    @DisplayName("주어진 문자열에서 숫자를 올바르게 분리하여 리턴한다.")
    @MethodSource("provideTokenizeTestCases")
    void tokenize(String input, List<String> expectedNumbers) {

        List<String> numbers = customDelimiter.tokenize(input);
        Assertions.assertThat(numbers).isEqualTo(expectedNumbers);
    }

    private static Stream<Arguments> provideTokenizeTestCases() {
        return Stream.of(
                // 기본적인 커스텀 구분자
                Arguments.of("//;|,:\\n1;|,:2;|,:3", List.of("1", "2", "3")),
                Arguments.of("//|구분자,\\n4|구분자,5|구분자,6", List.of("4", "5", "6")),
                Arguments.of("//:\\n7:8:9:", List.of("7", "8", "9")),
                Arguments.of("//\\\\n1\\2\\3", List.of("1", "2", "3")),

                // 공백 포함 처리
                Arguments.of("//;\\n1;2;3", List.of("1", "2", "3")),

                // 단일 숫자
                Arguments.of("//;\\n1", List.of("1")),

                // 큰 숫자 처리
                Arguments.of("//;\\n1000;2000;3000", List.of("1000", "2000", "3000"))
        );
    }
}