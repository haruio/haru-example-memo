package com.haru.example.memo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haru.Haru;
import com.haru.HaruException;
import com.haru.User;
import com.haru.callback.LoginCallback;

/**
 * 샘플 메모앱 using Haru
 */
public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText usernameInput = (EditText) findViewById(R.id.usernameInput),
                emailInput = (EditText) findViewById(R.id.emailInput),
                pwInput = (EditText) findViewById(R.id.passwordInput),
                pwAgainInput = (EditText) findViewById(R.id.passwordAgainInput);

        Button signInButton = (Button) findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = usernameInput.getText().toString(),
                        email = emailInput.getText().toString(),
                        pw = pwInput.getText().toString(),
                        pwRepeat = pwAgainInput.getText().toString();

                // 회원 정보의 길이 및 입력 여부 체크
                if (username.length() < 4 || email.length() < 4 ||
                        pw.length() == 0 || pwRepeat.length() == 0) {
                    showToast("필수 항목들을 전부 작성해 주세요.");
                    return;
                }

                // 비밀번호 체크
                if (!pwRepeat.equals(pw)) {
                    showToast("비밀번호와 비밀번호 확인이 일치해야 합니다.");
                    return;
                }

                doSignUp(username, email, pw);
            }
        });
    }

    /**
     * 실제 회원가입 동작을 처리한다.
     */
    private void doSignUp(String username, String email, String pw) {
        // 서버에 유저 저장
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(pw);

        user.signUpInBackground(new LoginCallback() {
            @Override
            public void done(User user, HaruException error) {
                // 에러 핸들링
                if (error != null) {
                    Haru.stackTrace(error);
                    showToast(error.getMessage());
                    return;
                }

                // 회원가입 끝!
                Intent intent = new Intent(SignUpActivity.this, ListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
