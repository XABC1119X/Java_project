package src.game;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * 存檔管理器
 * 負責遊戲進度的保存和載入
 */
public class SaveManager {
    private static final String SAVE_DIR = GameConstants.SAVE_DIR;
    private static final String SAVE_FILE = GameConstants.SAVE_FILE;
    
    /**
     * 保存遊戲進度
     */
    public boolean saveGame(Player player, GameLogic gameLogic, BuffManager buffManager) {
        try {
            // 確保保存目錄存在
            Files.createDirectories(Paths.get(SAVE_DIR));
            
            SaveData saveData = new SaveData();
            
            // 保存玩家數據
            saveData.setLevel(gameLogic.getLevel());
            saveData.setPlayerHealth(player.getHealth());
            saveData.setPlayerMaxHealth(player.getMaxHealth());
            saveData.setPlayerAttackPower(player.getAttackPower());
            saveData.setPlayerAttackSpeed(player.getAttackSpeed());
            saveData.setPlayerCoins(player.getCoins());
            saveData.setPlayerCriticalRate(player.getCriticalRate());
            saveData.setPlayerCriticalDamageMultiplier(player.getCriticalDamageMultiplier());
            saveData.setPlayerDamageReduction(player.getDamageReduction());
            saveData.setPlayerLifeSteal(player.getLifeSteal());
            
            // 保存 Buff 列表
            saveData.getActiveBuffs().addAll(buffManager.getActiveBuffs().keySet());
            
            saveData.setSaveDate(new Date());
            
            // 寫入文件
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(SAVE_DIR + SAVE_FILE))) {
                oos.writeObject(saveData);
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JOptionPane.showMessageDialog(null, 
                "遊戲已保存！\n保存時間: " + sdf.format(saveData.getSaveDate()),
                "保存成功", 
                JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "保存失敗：" + e.getMessage(),
                "保存錯誤", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * 載入遊戲進度
     */
    public SaveData loadGame() {
        try {
            File saveFile = new File(SAVE_DIR + SAVE_FILE);
            if (!saveFile.exists()) {
                return null;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(saveFile))) {
                SaveData saveData = (SaveData) ois.readObject();
                return saveData;
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "載入失敗：" + e.getMessage(),
                "載入錯誤", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * 檢查是否有存檔
     */
    public boolean hasSaveFile() {
        File saveFile = new File(SAVE_DIR + SAVE_FILE);
        return saveFile.exists();
    }
    
    /**
     * 刪除存檔
     */
    public boolean deleteSave() {
        try {
            File saveFile = new File(SAVE_DIR + SAVE_FILE);
            if (saveFile.exists()) {
                return saveFile.delete();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 從 SaveData 恢復玩家狀態
     */
    public void restorePlayerState(SaveData saveData, Player player) {
        player.setHealth(saveData.getPlayerHealth());
        player.setMaxHealth(saveData.getPlayerMaxHealth());
        player.setAttackPower(saveData.getPlayerAttackPower());
        player.setAttackSpeed(saveData.getPlayerAttackSpeed());
        player.setCoins(saveData.getPlayerCoins());
        player.setCriticalRate(saveData.getPlayerCriticalRate());
        player.setCriticalDamageMultiplier(saveData.getPlayerCriticalDamageMultiplier());
        player.setDamageReduction(saveData.getPlayerDamageReduction());
        player.setLifeSteal(saveData.getPlayerLifeSteal());
    }
}

