package com.buang.welewolf.modules.utils;

import com.buang.welewolf.R;

/**
 * Created by weilei on 17/6/13.
 */

public class WolfUtils {
    public static final int JOB_HUIZHANG = 1;
    public static final int JOB_FUHUIZHANG = 2;
    public static final int JOB_JINGYING = 3;
    public static final int JOB_XIAOLUOLUO = 4;

    public static final int GAME_STATUS_DEAAD = 1;
    public static final int GAME_STATUS_HANDUP = 2;
    public static final int GAME_STATUS_SPEAKING = 3;
    public static final int GAME_STATUS_READY = 4;

    public static final int GAME_ROLE_WOLF = 1;
    public static final int GAME_ROLE_HUNTER = 2;
    public static final int GAME_ROLE_IDIOT = 3;
    public static final int GAME_ROLE_PROPHET = 4;
    public static final int GAME_ROLE_VILLAGE = 5;
    public static final int GAME_ROLE_WITVH = 6;

    public static int getGuildLevel(int level) {
        int id = R.drawable.icon_guild_level_1;
        switch (level) {
            case 1:
                id = R.drawable.icon_guild_level_1;
                break;
            case 2:
                id = R.drawable.icon_guild_level_2;
                break;
            case 3:
                id = R.drawable.icon_guild_level_3;
                break;
            case 4:
                id = R.drawable.icon_guild_level_4;
                break;
            case 5:
                id = R.drawable.icon_guild_level_5;
                break;
            case 6:
                id = R.drawable.icon_guild_level_6;
                break;
            case 7:
                id = R.drawable.icon_guild_level_7;
                break;
            case 8:
                id = R.drawable.icon_guild_level_8;
                break;
            case 9:
                id = R.drawable.icon_guild_level_9;
                break;
            case 10:
                id = R.drawable.icon_guild_level_10;
                break;
            case 11:
                id = R.drawable.icon_guild_level_11;
                break;
            case 12:
                id = R.drawable.icon_guild_level_12;
                break;
            case 13:
                id = R.drawable.icon_guild_level_13;
                break;
            case 14:
                id = R.drawable.icon_guild_level_14;
                break;
            case 15:
                id = R.drawable.icon_guild_level_15;
                break;
            case 16:
                id = R.drawable.icon_guild_level_16;
                break;
        }
        return id;
    }

    public static String getGuildJob(int type) {
        String job = "小喽喽";
        switch (type) {
            case JOB_HUIZHANG:
                job = "会长";
                break;
            case JOB_FUHUIZHANG:
                job = "副会长";
                break;
            case JOB_JINGYING:
                job = "精英";
                break;
            case JOB_XIAOLUOLUO:
                job = "小喽喽";
                break;
        }
        return job;
    }
}
