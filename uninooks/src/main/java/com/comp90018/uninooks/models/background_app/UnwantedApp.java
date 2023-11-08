package com.comp90018.uninooks.models.background_app;

public enum UnwantedApp {

    FACEBOOK("facebook"),
    TWITTER("twitter"),
    INSTAGRAM("instagram"),
    YOUTUBE("youtube"),
    TIKTOK("tiktok"),
    DISCORD("discord"),
    WHATSAPP("whatsapp"),
    WECHAT("tencent.mm"),
    MESSENGER("messenger"),
    TELEGRAM("telegram"),
    SNAPCHAT("snapchat"),
    TUMBLR("tumblr"),
    TWITCH("twitch"),
    PINTEREST("pinterest"),
    AMAZON("amazon"),
    NETFLIX("netflix"),
    DOUYIN("douyin"),
    REDDIT("reddit");


    public String name;
    UnwantedApp(String name) {
        this.name = name;
    }
}
