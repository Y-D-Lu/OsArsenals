package cn.arsenals.osarsenals.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.arsenals.osarsenals.utils.Alog;
import cn.arsenals.osarsenals.utils.GenshinImpactUtil;

public class InputManager {
    private static final String TAG = "InputManager";

    private static final Map<String, Point> POINT_MAP = new HashMap<>();

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private static class SingletonHolder {
        private static final InputManager sInstance = new InputManager();
    }

    public static InputManager getInstance() {
        return InputManager.SingletonHolder.sInstance;
    }

    public void init(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        initPointMap();
    }

    private void initPointMap() {
        putStringIntoPointMap("POINT_LAST_ICON_ON_RECENT", GenshinImpactUtil.POINT_LAST_ICON_ON_RECENT);
        putStringIntoPointMap("POINT_MORE_INFO_ON_RECENT", GenshinImpactUtil.POINT_MORE_INFO_ON_RECENT);
        putStringIntoPointMap("POINT_FORCE_STOP_IN_SETTINGS", GenshinImpactUtil.POINT_FORCE_STOP_IN_SETTINGS);
        putStringIntoPointMap("POINT_SCREEN_CENTER", GenshinImpactUtil.POINT_SCREEN_CENTER);
        putStringIntoPointMap("POINT_CONFORM_FORCE_STOP_IN_SETTINGS", GenshinImpactUtil.POINT_CONFORM_FORCE_STOP_IN_SETTINGS);
        putStringIntoPointMap("GENSHIN_ON_PRIME_SCREEN", GenshinImpactUtil.POINT_GENSHIN_ON_PRIME_SCREEN);

        putStringIntoPointMap("GENSHIN_POINT_MAP", GenshinImpactUtil.GENSHIN_POINT_MAP);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SCALE_LEFT", GenshinImpactUtil.GENSHIN_POINT_MAP_SCALE_LEFT);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SCALE_RIGHT", GenshinImpactUtil.GENSHIN_POINT_MAP_SCALE_RIGHT);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SELECTOR", GenshinImpactUtil.GENSHIN_POINT_MAP_SELECTOR);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SELECTOR_MONDSTADT", GenshinImpactUtil.GENSHIN_POINT_MAP_SELECTOR_MONDSTADT);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SELECTOR_LIYUE", GenshinImpactUtil.GENSHIN_POINT_MAP_SELECTOR_LIYUE);
        putStringIntoPointMap("GENSHIN_POINT_MAP_SELECTOR_INAZUMA", GenshinImpactUtil.GENSHIN_POINT_MAP_SELECTOR_INAZUMA);
        putStringIntoPointMap("GENSHIN_POINT_MAP_TELEPORT", GenshinImpactUtil.GENSHIN_POINT_MAP_TELEPORT);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_OPERATE", GenshinImpactUtil.GENSHIN_POINT_PLAYER_OPERATE);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_LIST_SWIPE_START", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_LIST_SWIPE_START);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_FIGHT", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_FIGHT);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_SELECT_TEAM", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_SELECT_TEAM);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_LAST_SECOND_TEAM);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_SELECT_TEAM_DONE);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_SKIP_TIPS", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_SKIP_TIPS);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_EXIT", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_EXIT);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_EXIT_CONFORM", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_EXIT_CONFORM);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_EXIT_FINAL", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_EXIT_FINAL);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_GET_BONUS", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_GET_BONUS);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_USE_ORIGINAL_RESIN);
        putStringIntoPointMap("GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS", GenshinImpactUtil.GENSHIN_POINT_DOMAIN_EXIT_AFTER_GET_BONUS);
        putStringIntoPointMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1", GenshinImpactUtil.GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND1);
        putStringIntoPointMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2", GenshinImpactUtil.GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND2);
        putStringIntoPointMap("GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3", GenshinImpactUtil.GENSHIN_POINT_SWITCH_PLAYER_BACKGROUND3);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_WALK_AHEAD", GenshinImpactUtil.GENSHIN_POINT_PLAYER_WALK_AHEAD);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_WALK_BACK", GenshinImpactUtil.GENSHIN_POINT_PLAYER_WALK_BACK);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_WALK_LEFT", GenshinImpactUtil.GENSHIN_POINT_PLAYER_WALK_LEFT);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_WALK_RIGHT", GenshinImpactUtil.GENSHIN_POINT_PLAYER_WALK_RIGHT);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_JUMP", GenshinImpactUtil.GENSHIN_POINT_PLAYER_JUMP);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_SPRINT", GenshinImpactUtil.GENSHIN_POINT_PLAYER_SPRINT);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_ATTACK", GenshinImpactUtil.GENSHIN_POINT_PLAYER_ATTACK);
        putStringIntoPointMap("GENSHIN_POINT_PLAYER_E", GenshinImpactUtil.GENSHIN_POINT_PLAYER_E);
    }

    private void putStringIntoPointMap(String key, String value) {
        boolean isContains = mSharedPreferences.contains(key);
        if (isContains) {
            value = mSharedPreferences.getString(key, value);
        } else {
            mSharedPreferences.edit().putString(key, value).apply();
        }
        String[] point = value.split(",");
        Alog.info(TAG, "putStringIntoPointMap " + isContains + " " + key + " " + Arrays.toString(point));
        if (point.length != 2) {
            Alog.warn(TAG, "putStringIntoPointMap str len error " + value);
            return;
        }
        try {
            int posX = Integer.parseInt(point[0]);
            int posY = Integer.parseInt(point[1]);
            POINT_MAP.put(key, new Point(posX, posY));
        } catch (NumberFormatException e) {
            Alog.warn(TAG, "putStringIntoPointMap NumberFormatException");
        }
    }

    public void updatePointMap(String key, int posX, int posY) {
        mSharedPreferences.edit().putString(key, posX + "," + posY).apply();
        POINT_MAP.put(key, new Point(posX, posY));
    }

    public Point getPointFromMap(String key) {
        return POINT_MAP.get(key);
    }
}
