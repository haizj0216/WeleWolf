package com.buang.welewolf.modules.services;

import com.buang.welewolf.base.bean.OnlineGuildInfo;

/**
 * Created by weilei on 17/6/7.
 */

public class GuildServiceImp implements GuildService {

    private OnlineGuildInfo guildInfo;

    @Override
    public void releaseAll() {

    }

    @Override
    public void setGuildInfo(OnlineGuildInfo guildInfo) {
        this.guildInfo = guildInfo;
    }

    @Override
    public OnlineGuildInfo getGuildInfo() {
        return guildInfo;
    }
}
