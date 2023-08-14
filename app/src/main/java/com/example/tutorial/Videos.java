package com.example.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.view.View;
import android.widget.Button;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;



public class Videos extends AppCompatActivity {

    private WebView webView;
    int k=6;
    String s="UXtbEehKa-A";
    private YouTubePlayerView youTubePlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        youTubePlayerView = findViewById(R.id.youtubeplayerid);
        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        Bundle receivedBundle = getIntent().getExtras();

        k = receivedBundle.getInt("pos");


        if(k==0) {
            webView.loadUrl("file:///android_asset/helloBu.html");
            s="2ZFBUr4vrD0";
        }
//
        else if(k==1) {
            webView.loadUrl("file:///android_asset/helloDr.html");
            s="UXtbEehKa-A";
        }

        else if(k==2) {
            webView.loadUrl("file:///android_asset/helloGr.html");
            s="1B1_FLtJ4_8";
        }
        else if(k==3) {
            webView.loadUrl("file:///android_asset/helloMo.html");
            s="5xO2nyGonlg";
        }
        else {
            webView.loadUrl("file:///android_asset/helloSp.html");
            s="jzO3uRlhGNU";
        }

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = s;
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }
}