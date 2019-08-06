package jp.co.pscsrv.exceptionHandler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * controllerを横断して例外処理する場合
 *
 * 例外処理クラスに@ControllerAdviceアノテーションを付ける
 * （メソッドの定義はcontroller毎の例外処理と同じ）
 */
@ControllerAdvice
public class ExceptionHandlerClass {

	//Exceptionが発生した場合共通エラー画面へ飛ばす
    @ExceptionHandler({Exception.class})
    public String ExceptionHandler() {
        return "/common/ERR101";
    }
}
