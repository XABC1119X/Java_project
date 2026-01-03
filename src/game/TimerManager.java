package src.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer 管理器
 * 統一管理遊戲中所有的 Timer，避免資源洩漏
 */
public class TimerManager {
    private List<Timer> timers = new ArrayList<>();
    private static TimerManager instance;
    
    private TimerManager() {}
    
    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }
    
    /**
     * 創建一個新的 Timer 並註冊到管理器
     */
    public Timer createTimer() {
        Timer timer = new Timer();
        timers.add(timer);
        return timer;
    }
    
    /**
     * 創建一個 Timer 並立即執行任務
     */
    public Timer createTimer(TimerTask task, long delay) {
        Timer timer = createTimer();
        timer.schedule(task, delay);
        return timer;
    }
    
    /**
     * 創建一個 Timer 並定期執行任務
     */
    public Timer createTimer(TimerTask task, long delay, long period) {
        Timer timer = createTimer();
        timer.scheduleAtFixedRate(task, delay, period);
        return timer;
    }
    
    /**
     * 取消所有 Timer
     */
    public void cancelAll() {
        timers.forEach(timer -> {
            try {
                timer.cancel();
            } catch (Exception e) {
                // 忽略取消時的異常
            }
        });
        timers.clear();
    }
    
    /**
     * 取消特定的 Timer
     */
    public void cancelTimer(Timer timer) {
        if (timer != null) {
            try {
                timer.cancel();
                timers.remove(timer);
            } catch (Exception e) {
                // 忽略取消時的異常
            }
        }
    }
    
    /**
     * 獲取當前活躍的 Timer 數量
     */
    public int getActiveTimerCount() {
        return timers.size();
    }
}

