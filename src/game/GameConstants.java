package src.game;

import java.awt.Color;

/**
 * 遊戲常數定義類別
 * 集中管理所有遊戲中的常數值，避免硬編碼
 */
public class GameConstants {
    
    // ========== 玩家初始屬性 ==========
    public static final int INITIAL_PLAYER_HEALTH = 100;
    public static final int INITIAL_PLAYER_MAX_HEALTH = 100;
    public static final int INITIAL_PLAYER_ATTACK_POWER = 30;
    public static final int INITIAL_PLAYER_ATTACK_SPEED = 2000; // 毫秒
    public static final int INITIAL_PLAYER_COINS = 100;
    public static final double INITIAL_PLAYER_CRITICAL_RATE = 0.0;
    public static final double INITIAL_PLAYER_CRITICAL_DAMAGE_MULTIPLIER = 2.0;
    
    // ========== 遊戲設定 ==========
    public static final int MAX_LEVEL = 8;
    public static final int MIN_ATTACK_SPEED = 600; // 最小攻擊間隔（毫秒）
    
    // ========== Buff 數值 ==========
    public static final int SPEEDUP_BUFF_VALUE = 100;
    public static final int ATTACK_BUFF_VALUE = 8;
    public static final int HEALTH_BUFF_VALUE = 30;
    public static final int POISON_DAMAGE_PER_SECOND = 3;
    public static final double TRIPLE_CRIT_MULTIPLIER = 3.0;
    public static final double DEFENSE_REDUCTION = 0.1; // 10%
    public static final double REGENERATION_PERCENT = 0.01; // 1% 每秒
    public static final double VAMPIRE_LIFE_STEAL = 0.1; // 10%
    
    // ========== 魔法卡片 ==========
    public static final int MAGIC_CARD_ATTACK_SPEED_COST = 50;
    public static final int MAGIC_CARD_ATTACK_SPEED_VALUE = 100;
    public static final int MAGIC_CARD_ATTACK_POWER_COST = 30;
    public static final int MAGIC_CARD_ATTACK_POWER_VALUE = 5;
    public static final int MAGIC_CARD_HEALTH_COST = 20;
    public static final int MAGIC_CARD_HEALTH_VALUE = 20;
    public static final int MAGIC_CARD_CRITICAL_RATE_COST = 40;
    public static final double MAGIC_CARD_CRITICAL_RATE_VALUE = 0.1;
    
    // ========== 終極技能 ==========
    public static final int ULTIMATE_DAMAGE = 9999999; // 秒殺傷害
    public static final int ULTIMATE_ANIMATION_DELAY = 4750; // 毫秒
    
    // ========== UI 顏色 ==========
    public static final Color UI_BACKGROUND_DARK = new Color(30, 30, 30);
    public static final Color UI_BACKGROUND_MEDIUM = new Color(40, 40, 40);
    public static final Color UI_BACKGROUND_LIGHT = new Color(60, 60, 60);
    public static final Color UI_TEXT_PRIMARY = Color.WHITE;
    public static final Color UI_TEXT_SECONDARY = Color.LIGHT_GRAY;
    public static final Color UI_ACCENT_COINS = Color.YELLOW;
    public static final Color UI_HEALTH_BAR = new Color(46, 204, 113);
    public static final Color UI_MONSTER_HEALTH_BAR = new Color(231, 76, 60);
    
    // ========== UI 尺寸 ==========
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    
    // ========== 路徑 ==========
    public static final String ASSETS_PATH = "src/assets/";
    public static final String IMAGES_PATH = ASSETS_PATH + "images/";
    public static final String SOUND_PATH = ASSETS_PATH + "sound/";
    public static final String SAVE_DIR = "saves/";
    public static final String SAVE_FILE = "savegame.dat";
    
    // ========== 怪物掉落 ==========
    public static final int BASE_COIN_DROP = 10;
    
    // ========== 動畫時間 ==========
    public static final int ATTACK_ANIMATION_DURATION = 500; // 毫秒
    public static final int SLASH_EFFECT_DURATION = 1000; // 毫秒
    public static final int DAMAGE_NUMBER_DURATION = 1000; // 毫秒
    public static final int DAMAGE_NUMBER_FLOAT_DISTANCE = 50;
}

