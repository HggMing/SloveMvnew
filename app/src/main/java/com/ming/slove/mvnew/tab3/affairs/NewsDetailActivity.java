package com.ming.slove.mvnew.tab3.affairs;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import com.ming.slove.mvnew.R;
import com.ming.slove.mvnew.app.APPS;
import com.ming.slove.mvnew.common.base.BackActivity;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsDetailActivity extends BackActivity {

    public static String TYPE = "type_for_title";
    public static String NEWS_ID = "news_id";
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        switch (getIntent().getIntExtra(TYPE, 1)) {
            case 1:
                setToolbarTitle("新闻");
                break;
            case 2:
                setToolbarTitle("政策");
                break;
            case 3:
                setToolbarTitle("服务");
                break;
            case 4:
                setToolbarTitle("资讯");
                break;
        }

        initData();
    }

    private void initData() {
        //添加进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                    animation.setDuration(500);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    progressBar.startAnimation(animation);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        String id = getIntent().getStringExtra(NEWS_ID);
        webView.loadUrl(APPS.BASE_URL + "/news/info?id=" + id);
    }
}
