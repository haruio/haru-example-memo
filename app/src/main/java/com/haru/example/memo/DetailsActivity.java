package com.haru.example.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haru.Entity;
import com.haru.Haru;
import com.haru.HaruException;
import com.haru.Query;
import com.haru.User;
import com.haru.callback.DeleteCallback;
import com.haru.callback.SaveCallback;
import com.haru.ui.PagedEntityAdapter;

import java.text.SimpleDateFormat;

public class DetailsActivity extends ActionBarActivity {

    private Entity mMemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // 파라미터 받아오기
        mMemo = getIntent().getParcelableExtra("memo");
        if (mMemo == null) throw new IllegalArgumentException("memo must be given.");

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 백 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    /**
     * 뷰들을 초기화한다.
     */
    private void initView() {
        // 뷰 설정
        TextView title = (TextView) findViewById(R.id.memo_title),
                date = (TextView) findViewById(R.id.memo_date),
                content = (TextView) findViewById(R.id.memo_content);

        title.setText(mMemo.getString("title"));
        content.setText(mMemo.getString("body"));
        date.setText(new SimpleDateFormat().format(mMemo.getUpdatedAt()));
    }

    /**
     * 이 메모를 삭제한다.
     */
    private void deleteMemo() {
        mMemo.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(HaruException e) {
                finish();
            }
        });
    }

    /**
     * 이 메모를 수정하는 팝업을 띄우고, 수정할 수 있게 한다.
     */
    private void editMemo() {
        // 수정 팝업 띄우기
        View editLayout = LayoutInflater.from(this)
                .inflate(R.layout.popup_write_memo, null);

        final EditText title = (EditText) editLayout.findViewById(R.id.title),
                body = (EditText) editLayout.findViewById(R.id.body);

        title.setText(mMemo.getString("title"));
        body.setText(mMemo.getString("body"));

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_memo)
                .setView(editLayout)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.menu_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mMemo.put("title", title.getText().toString());
                        mMemo.put("body", body.getText().toString());
                        mMemo.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(HaruException e) {
                                initView();
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_edit:
                editMemo();
                return true;

            case R.id.action_delete:
                deleteMemo();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
