package com.haru.example.memo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haru.Haru;
import com.haru.HaruException;
import com.haru.User;
import com.haru.callback.LoginCallback;
import com.haru.social.FacebookLoginUtils;
import com.haru.social.KakaoLoginUtils;

/**
 * 샘플 메모앱 using Haru
 * @author VISTA
 */
public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 로그인되어있으면 바로 메인으로
        if (User.isLogined()) {
            startActivity(new Intent(this, ListActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        final View facebookLogin = findViewById(R.id.facebookLogin),
                emailLogin = findViewById(R.id.emailLogin),
                kakaoLogin = findViewById(R.id.kakaoLogin);

        kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kakaoLogin();
            }
        });

        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });

        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // email login.
                Intent intent = new Intent(LoginActivity.this, LoginWithEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Login using kakaotalk.
     */
    private void kakaoLogin() {

        // Log in into Haru - using KakaoLoginUtils.
        // This Utility class automatically do kakao login using Facebook SDK.
        KakaoLoginUtils.logIn(this, new LoginCallback() {
            @Override
            public void done(User user, HaruException error) {
                if (error != null) {
                    Haru.stackTrace(error);
                    return;
                }

                // login succeed. go to memo list
                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Called after closing KakaoTalk Login popup.
     */
    @Override
    protected void onResume() {
        super.onResume();
        KakaoLoginUtils.onResume();
    }

    /**
     * Login using facebook.
     */
    private void facebookLogin() {

        // Log in into Haru - using FacebookLoginUtils.
        // This Utility class automatically do facebook login using Facebook SDK.
        FacebookLoginUtils.logIn(this, new LoginCallback() {
            @Override
            public void done(User user, HaruException error) {
                if (error != null) {
                    Haru.stackTrace(error);
                    return;
                }

                // login succeed. go to memo list
                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Called after closing Facebook login popup.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // We must pass the Facebook login result to FacebookLoginUtils.
        FacebookLoginUtils.onActivityResult(requestCode, resultCode, data);
    }
}
