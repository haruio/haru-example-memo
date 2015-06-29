package com.haru.example.memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ListActivity extends ActionBarActivity {

    private PagedEntityAdapter mAdapter;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // 로그인되어있는지 체크
        if (!User.isLogined()) throw new IllegalStateException("Not Logined!");
        mUser = User.getCurrentUser();

        // 리스트 초기화
        ListView listView = (ListView) findViewById(R.id.listView);

        //
        Query filter =
                Entity.where("Memo")
                    .equalTo("owner", mUser.getId());

        mAdapter = new PagedEntityAdapter(this, filter, R.layout.memo_list_row);
        mAdapter.setOnViewRenderListener(new PagedEntityAdapter.OnViewRenderListener() {
            @Override
            public void onViewRender(int index, Entity article, View view) {

                TextView listTitle = (TextView) view.findViewById(R.id.listTitle),
                        updatedAt = (TextView) view.findViewById(R.id.listUpdatedAt);

                listTitle.setText(article.getString("title"));

                updatedAt.setText(DateUtils.getRelativeTimeSpanString(
                        article.getUpdatedAt().getTime(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE));
            }
        });
        listView.setAdapter(mAdapter);

        // 누르면 자세히
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Entity selected = mAdapter.getItem(index);

                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                intent.putExtra("memo", selected);
                startActivity(intent);
            }
        });

        // 길게 눌렀을 시 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
                deleteItem(mAdapter.getItem(index));
                return true;
            }
        });

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * 리스트의 해당 아이템을 삭제한다.
     * @param entity 엔티티
     */
    private void deleteItem(final Entity entity) {
        final ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new String[]{"삭제"});

        AlertDialog dialog = new AlertDialog.Builder(ListActivity.this)
                .setAdapter(menuAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 삭제
                        entity.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(HaruException exception) {
                                if (exception != null) {
                                    exception.printStackTrace();
                                    toast(exception.getMessage());
                                }
                                mAdapter.notifyDataSetChanged();
                                toast("삭제되었습니다.");
                            }
                        });
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mAdapter.refreshInBackground();
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_write) {

            // 글 쓰기 팝업 띄우기
            View writeLayout = LayoutInflater.from(this)
                    .inflate(R.layout.popup_write_memo, null);

            final EditText title = (EditText) writeLayout.findViewById(R.id.title),
                    body = (EditText) writeLayout.findViewById(R.id.body);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.write_memo)
                    .setView(writeLayout)
                    .setPositiveButton(R.string.menu_write, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doCreateEntity(title.getText().toString(),
                                    body.getText().toString());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 실질적으로 메모를 생성하는 함수이다.
     * @param title 메모 제목
     * @param content 메모 내용
     */
    private void doCreateEntity(String title, String content) {
        // 엔티티를 만들어 저장한다.
        Entity entity = new Entity("Memo");
        entity.put("title", title);
        entity.put("body", content);
        entity.put("owner", mUser.getId());

        entity.saveInBackground(new SaveCallback() {
            @Override
            public void done(HaruException error) {
                if (error != null) {
                    Haru.stackTrace(error);
                    return;
                }

                // 어댑터 새로고침
                mAdapter.refreshInBackground();
            }
        });
    }
}
