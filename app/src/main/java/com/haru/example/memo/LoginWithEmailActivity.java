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
 * @author VISTA
 */
public class LoginWithEmailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        final EditText usernameInput = (EditText) findViewById(R.id.usernameInput),
                pwInput = (EditText) findViewById(R.id.passwordInput);

        Button loginButton = (Button) findViewById(R.id.logInButton),
                signInButton = (Button) findViewById(R.id.signInButton);

        // Login Button Handler
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = usernameInput.getText().toString();
                String password = pwInput.getText().toString();
                doLogin(userName, password);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginWithEmailActivity.this, SignUpActivity.class));
            }
        });
    }

    /**
     * 실질적인 서버로의 로그인 처리를 담당한다.
     * @param username 유저 ID
     * @param pw 패스워드
     */
    private void doLogin(String username, String pw) {

        // Haru 서버에 로그인한다.
        User.logInInBackground(username, pw, new LoginCallback() {
            @Override
            public void done(User user, HaruException error) {

                // 에러 처리
                if (error != null) {
                    // 존재하지 않는 사용자 계정인가?
                    if (error.getErrorCode() == HaruException.USERNAME_MISSING)
                        toast(R.string.username_missing);

                    // 로그 후 리턴
                    Haru.stackTrace(error);
                    return;
                }

                // 로그인 성공! 메모 리스트로 이동
                Intent intent = new Intent(LoginWithEmailActivity.this, ListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
