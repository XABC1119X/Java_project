package src.game;

import javax.swing.ImageIcon;

/**
 * 技能介面
 * 定義所有技能必須實現的方法
 */
public interface Skill {
    String getId();
    String getName();
    String getDescription();
    int getCooldown(); // 冷卻時間（秒）
    ImageIcon getIcon();
    void execute(Player player, Monster monster, GameLogic gameLogic);
    boolean isOnCooldown();
    int getRemainingCooldown();
}

