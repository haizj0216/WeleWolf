package com.buang.welewolf.modules.services;

import com.buang.welewolf.base.bean.OnlineGuildInfo;
import com.hyena.framework.servcie.BaseService;

/**
 * Created by weilei on 17/6/7.
 */

public interface GuildService extends BaseService {

    public static final String SERVICE_NAME = "com.buang.welewolf_guild";

    public void setGuildInfo(OnlineGuildInfo guildInfo);

    public OnlineGuildInfo getGuildInfo();
}
