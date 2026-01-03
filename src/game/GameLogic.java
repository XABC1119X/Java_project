package src.game;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;
import javax.swing.*;

public class GameLogic {

    private Player player;
    private Monster monster;
    private BuffManager buffManager;
    private GameInterface gameInterface;

    private Timer playerAttackTimer;
    private Timer monsterAttackTimer;

    private boolean isGameOver = false;
    private boolean isPaused = false;
    

    private int level = 1;
    private final int maxLevel = 8;
    public GameLogic() {};
    public GameLogic(GameInterface gameInterface, Player player, Monster monster) {
        this.gameInterface = gameInterface;
        this.player = player;
        this.monster = monster;
        
    }

    public void setBuffManager(BuffManager buffManager) {
        this.buffManager = buffManager;
    }
    public Monster getCurrentTarget() {
        return monster;
    }
    public void startGame() {
        gameInterface.updateUI();
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startTimers();
            }
        }, 500);
    }

    private void endGame(String imagePath, boolean isWin) {
        if (isGameOver) return;
        isGameOver = true;
        stopTimers();
        
        // 延遲1秒顯示結果
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                JFrame frame = gameInterface.getFrame();
                frame.getContentPane().removeAll();
                
                JPanel mainPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // 設置黑色背景
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        
                        // 繪製圖片
                        ImageIcon icon = new ImageIcon(imagePath);
                        Image img = icon.getImage();
                        
                        double panelRatio = (double) getWidth() / getHeight();
                        double imageRatio = (double) icon.getIconWidth() / icon.getIconHeight();
                        
                        int drawWidth, drawHeight;
                        int x = 0, y = 0;
                        
                        if (panelRatio > imageRatio) {
                            drawHeight = getHeight();
                            drawWidth = (int)(drawHeight * imageRatio);
                            x = (getWidth() - drawWidth) / 2;
                        } else {
                            drawWidth = getWidth();
                            drawHeight = (int)(drawWidth / imageRatio);
                            y = (getHeight() - drawHeight) / 2;
                        }
                        
                        g2d.drawImage(img, x, y, drawWidth, drawHeight, this);
                        
                        // 繪製文字
                        String text = isWin ? "成功" : "失敗";
                        g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
                        
                        // 創建漸變色文字
                        GradientPaint gradient = isWin ? 
                            new GradientPaint(0, 0, Color.YELLOW, 0, 50, Color.ORANGE) :
                            new GradientPaint(0, 0, Color.RED, 0, 50, new Color(139, 0, 0));
                        g2d.setPaint(gradient);
                        
                        int textX = 50;
                        int textY = 50; // 在圖片下方50像素處
                        
                        // 添加文字陰影效果
                        g2d.setColor(new Color(0, 0, 0, 128));
                        g2d.drawString(text, textX + 2, textY + 2);
                        
                        // 繪製主文字
                        g2d.setPaint(gradient);
                        g2d.drawString(text, textX, textY);
                    }
                };
                
                mainPanel.setBackground(Color.BLACK);
                mainPanel.setOpaque(true);
                
                mainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        System.exit(0);
                    }
                });
                
                frame.setContentPane(mainPanel);
                frame.revalidate();
                frame.repaint();
            }
        }, 1000); // 延遲1秒
    }


    private void startTimers() {
        if (isPaused || isGameOver) return;
        
        playerAttackTimer = new Timer();
        monsterAttackTimer = new Timer();
        scheduleAttacks();
    }

    private void stopTimers() {
        if (playerAttackTimer != null) {
            playerAttackTimer.cancel();
            playerAttackTimer = null;
        }
        if (monsterAttackTimer != null) {
            monsterAttackTimer.cancel();
            monsterAttackTimer = null;
        }
    }
    

    public void pauseGame() {
        if (!isPaused) {
            isPaused = true;
            stopTimers();
        }
    }
    public void resumeGame() {
        if (isPaused && !isGameOver) {
            isPaused = false;
            startTimers();
        }
    }


    private void scheduleAttacks() {
        playerAttackTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isGameOver && !isPaused) {
                    int d = player.getCriticalAttackPower();
                    player.showAttackAnimation();
                    player.lifeSteal(d);
                    monster.takeDamage(d, player.getAttackPower());   
                    gameInterface.updateUI();
                    checkMonsterHealth();
                }
            }
        }, 0, (long) player.getAttackSpeed());

        monsterAttackTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isGameOver && !isPaused) {
                    monster.showAttackAnimation();
                    player.takeDamage(monster.getAttackPower());
                    gameInterface.updateUI();
                    checkPlayerHealth();
                }
            }
        }, 0, monster.getAttackSpeed());
    }

    public void handleUltimateAttack() {
        File sound = new File("src/assets/sound/Ult.wav"); 
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start(); 
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        // 計算大招傷害(基礎攻擊力的3倍)
        int ultimateDamage = 9999999;
        
        // 暫停普通攻擊
        pauseGame();
        
        // 延遲傷害，等待動畫播放
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                monster.takeDamage(ultimateDamage, ultimateDamage);
                gameInterface.updateUI();
                checkMonsterHealth();
                
            }
        }, 4750); // 等待1秒讓動畫播放
    }

    private void nextLevel() {
        pauseGame();
        level++;
    
        // Get the game's JFrame
        JFrame frame = gameInterface.getFrame();
        BuffSelPanel[] buffPanel = new BuffSelPanel[1];
    
        buffPanel[0] = new BuffSelPanel(buffManager, buffId -> {
            // Apply the selected buff
            buffManager.applyBuff(buffId);

            // Remove buff selection panel
            Container contentPane = frame.getContentPane();
            contentPane.remove(buffPanel[0]);
            
            // Upgrade monster and continue game
            monster.upgradeForNewLevel(level);
            gameInterface.updateUI();
            
            // Delay before restarting timers
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    resumeGame();
                }
            }, player.getAttackSpeed());
            
            frame.revalidate();
            frame.repaint();
        });
    
        // Add buff selection panel to game interface
        Container contentPane = frame.getContentPane();
        contentPane.add(buffPanel[0], JLayeredPane.POPUP_LAYER);
        contentPane.setComponentZOrder(buffPanel[0], 0);
    
        // Set panel size to cover entire JFrame
        buffPanel[0].setBounds(0, 0, frame.getWidth(), frame.getHeight());
    
        // Update JFrame view
        frame.revalidate();
        frame.repaint();
    }
    

    private void checkMonsterHealth() {
        if (monster.getHealth() <= 0) {
            
            monster.showDefeatAnimation();
            player.addCoins(monster.getDropCoins());
            pauseGame();
            
            if (level < maxLevel) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        nextLevel();
                    }
                }, 1000);
            } else {
                endGame("src\\assets\\images\\player\\win.png", true);
            }
        }
    }

    private void checkPlayerHealth() {
        if (player.getHealth() <= 0) {
            pauseGame();
            player.showDefeatAnimation();
            endGame("src\\assets\\images\\player\\lose.png", false);
        }
    }


    public void handleMagicCard(String cardText) {
        int cost = 0;
        boolean success = false;
        
        if (cardText.contains("攻速")) {
            cost = 50;
            if (player.spendCoins(cost)) {
                player.increaseAttackSpeed(100);
                success = true;
            }
        } else if (cardText.contains("攻擊力")) {
            cost = 30;
            if (player.spendCoins(cost)) {
                player.increaseAttackPower(5);
                success = true;
            }
        } else if (cardText.contains("生命值")) {
            cost = 20;
            if (player.spendCoins(cost)) {
                player.increaseMaxHealth(20);
                success = true;
            }
        } else if (cardText.contains("暴擊率")) {
            cost = 40;
            if (player.spendCoins(cost)) {
                player.increaseCriticalRate(0.1);
                success = true;
            }
        }
        
        if (!success) {
            JOptionPane.showMessageDialog(gameInterface.getFrame(), "金幣不足！");
        }
        
        gameInterface.regenerateMagicCards();
        gameInterface.updateUI();
    }
}