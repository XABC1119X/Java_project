package src.game;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class DamageNumber extends JLabel {
    private int x, y;
    private final int FLOAT_DISTANCE = 50; // 數字上浮的距離
    private final int DURATION = 1000; // 動畫持續時間(毫秒)
    private Timer timer;
    private long startTime;
    
    // 新增構造函數重載，添加類型參數
    public DamageNumber(int number, NumberType type, Component parent) {
        super(formatNumber(number, type));
        
        // 根據類型設置不同的視覺效果
        switch (type) {
            case DAMAGE:
                setForeground(Color.YELLOW);
                setFont(new Font("Arial", Font.BOLD, 20));
                break;
            case CRITICAL:
                setForeground(Color.RED);
                setFont(new Font("Arial", Font.BOLD, 24));
                break;
            case HEALING:
                setForeground(Color.GREEN);
                setFont(new Font("Arial", Font.BOLD, 20));
                break;
        }
        
        // 確保標籤大小足夠顯示文字
        setSize(getPreferredSize());
        
        timer = new Timer();
        startTime = System.currentTimeMillis();
        
        // 開始動畫
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                float progress = (System.currentTimeMillis() - startTime) / (float) DURATION;
                if (progress >= 1.0f) {
                    timer.cancel();
                    SwingUtilities.invokeLater(() -> ((Container) parent).remove(DamageNumber.this));
                    parent.repaint();
                    return;
                }
                
                // 更新位置和透明度
                float alpha = 1.0f - progress;
                float currentY = y - (FLOAT_DISTANCE * progress);
                
                SwingUtilities.invokeLater(() -> {
                    setLocation(x, (int) currentY);
                    setForeground(new Color(
                        getForeground().getRed(),
                        getForeground().getGreen(),
                        getForeground().getBlue(),
                        (int) (alpha * 255)
                    ));
                });
            }
        }, 0, 16);
    }
    
    // 為了保持向後兼容性，保留原有的構造函數
    public DamageNumber(int damage, boolean isCritical, Component parent) {
        this(damage, isCritical ? NumberType.CRITICAL : NumberType.DAMAGE, parent);
    }
    
    // 數字類型枚舉
    public enum NumberType {
        DAMAGE,
        CRITICAL,
        HEALING
    }
    
    // 格式化顯示的數字
    private static String formatNumber(int number, NumberType type) {
        switch (type) {
            case DAMAGE:
                return String.valueOf(number);
            case CRITICAL:
                return number + " CRIT!";
            case HEALING:
                return "+" + number;
            default:
                return String.valueOf(number);
        }
    }
    
    public void setStartPosition(int x, int y) {
        this.x = x;
        this.y = y;
        setLocation(x, y);
    }
}