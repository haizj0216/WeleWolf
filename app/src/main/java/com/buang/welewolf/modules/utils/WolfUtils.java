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
    public static final int GAME_ROLE_WITCH = 6;

    public static final int TIME_MY_ROLE = 5;
    public static final int TIME_WOLF_SKILL = 20;

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

    /**
     * 角色名称
     * @param role
     * @return
     */
    public static String getRoleName(int role) {
        String skill = "村民";
        switch (role) {
            case GAME_ROLE_HUNTER:
                skill = "猎人";
                break;
            case GAME_ROLE_WOLF:
                skill = "狼人";
                break;
            case GAME_ROLE_IDIOT:
                skill = "天使";
                break;
            case GAME_ROLE_WITCH:
                skill = "女巫";
                break;
            case GAME_ROLE_VILLAGE:
                skill = "村民";
                break;
        }
        return skill;
    }


    /**
     * 各角色图片
     *
     * @param role
     * @return
     */
    public static int getRoleId(int role) {
        int id = R.drawable.icon_role_village;
        switch (role) {
            case GAME_ROLE_HUNTER:
                id = R.drawable.icon_role_hunter;
                break;
            case GAME_ROLE_WOLF:
                id = R.drawable.icon_role_wolf;
                break;
            case GAME_ROLE_IDIOT:
                id = R.drawable.icon_role_idiot;
                break;
            case GAME_ROLE_WITCH:
                id = R.drawable.icon_role_witch;
                break;
            case GAME_ROLE_VILLAGE:
                id = R.drawable.icon_role_village;
                break;
        }
        return id;
    }

    /**
     * 角色技能
     *
     * @param role
     * @return
     */
    public static String getRoleSkill(int role) {
        String skill = "你还没有特殊技能";
        switch (role) {
            case GAME_ROLE_HUNTER:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_WOLF:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_IDIOT:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_WITCH:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_VILLAGE:
                skill = "你还没有特殊技能";
                break;
        }
        return skill;
    }

    /**
     * 角色描述
     *
     * @param role
     * @return
     */
    public static String getRoleDesc(int role) {
        String skill = "你还没有特殊技能";
        switch (role) {
            case GAME_ROLE_HUNTER:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_WOLF:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_IDIOT:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_WITCH:
                skill = "你还没有特殊技能";
                break;
            case GAME_ROLE_VILLAGE:
                skill = "你还没有特殊技能";
                break;
        }
        return skill;
    }
}
