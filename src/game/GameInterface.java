package src.game;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.TimerTask;
import javax.swing.*;

public class GameInterface {
    
    private Player player;
    private Monster monster;
    private GameLogic gameLogic;
    private ActiveBuffsPanel activeBuffsPanel;
    private SaveManager saveManager;
    private SkillManager skillManager;
    private BuffManager buffManager;

    private JFrame frame;
    private JLayeredPane mainLayeredPane;
    private JLabel heroStatsLabel, monsterStatsLabel, criticalRateLabel, ultimateWarningLabel, coinsLabel, ultimateEffectLabel;;
    
    private CustomProgressBar heroHealthBar, monsterHealthBar;
    private ModernButton[] magicCards = new ModernButton[4];
    private ModernButton showBuffsButton, ultimateButton;;
    
    public GameInterface(Player player, Monster monster, SaveManager saveManager, SkillManager skillManager) {
        this.player = player;
        this.monster = monster;
        this.saveManager = saveManager;
        this.skillManager = skillManager;
        showStartMenu();
    }
    public JFrame getTheFrame() {
        return frame;
    }
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }
    public void setActiveBuffPanel(ActiveBuffsPanel activeBuffsPanel) {
        this.activeBuffsPanel = activeBuffsPanel;
    }
    
    public void setSaveManager(SaveManager saveManager) {
        this.saveManager = saveManager;
    }
    
    public void setSkillManager(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    private void showStartMenu() {
        frame = new JFrame("地下城冒險");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
    
        NewPanel mainPanel = new NewPanel("src\\assets\\images\\background\\startbackgound.gif");
        mainPanel.setLayout(new GridBagLayout()); // 使用 GridBagLayout 來居中內容
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // 控制元件間的間距
    
        // 標題
        JLabel titleLabel = new JLabel("Welcome to the Dungeon");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        // 設定按鈕大小與樣式
        Dimension buttonSize = new Dimension(200, 50);
        ModernButton startButton = new ModernButton("開始遊戲");
        ModernButton continueButton = new ModernButton("繼續遊戲");
        ModernButton shopButton = new ModernButton("商店");
        ModernButton helpButton = new ModernButton("幫助");
        ModernButton exitButton = new ModernButton("離開");
        
        startButton.setPreferredSize(buttonSize);
        continueButton.setPreferredSize(buttonSize);
        shopButton.setPreferredSize(buttonSize);
        helpButton.setPreferredSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);
        
        // 檢查是否有存檔
        continueButton.setEnabled(saveManager != null && saveManager.hasSaveFile());
        
        startButton.addActionListener(e -> setupGameWindow());
        continueButton.addActionListener(e -> loadAndContinueGame());
        shopButton.addActionListener(e -> showShop());
        helpButton.addActionListener(e -> showHelpMenu());
        exitButton.addActionListener(e -> System.exit(0));
    
        // 添加元件到主面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
    
        gbc.gridy++;
        mainPanel.add(startButton, gbc);
        
        gbc.gridy++;
        mainPanel.add(continueButton, gbc);
        
        gbc.gridy++;
        mainPanel.add(shopButton, gbc);
        
        gbc.gridy++;
        mainPanel.add(helpButton, gbc);
        
        gbc.gridy++;
        mainPanel.add(exitButton, gbc);
    
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    
    private void setupGameWindow() {
        frame.getContentPane().removeAll();
    
    // 設置圖層管理 (包含了所有遊戲元素的創建和添加)
    setupLayerManagement();
    
    // 設置增益效果按鈕和面板
    setupBuffsButton();
    
    frame.revalidate();
    frame.repaint();
    gameLogic.startGame();
    }

    private void setupLayerManagement() {
        // 創建主圖層面板
        mainLayeredPane = new JLayeredPane();
        mainLayeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        frame.setContentPane(mainLayeredPane);

        // 背景層 (最底層)
        NewPanel gamePanel = new NewPanel("src\\assets\\images\\background\\graveyard.png");
        gamePanel.setLayout(null);
        gamePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        mainLayeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);

        // 遊戲元素層
        JPanel gameElementsLayer = new JPanel(null);
        gameElementsLayer.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        gameElementsLayer.setOpaque(false);

        // 添加遊戲基礎元素到遊戲元素層
        addGameElementsToLayer(gameElementsLayer);
        mainLayeredPane.add(gameElementsLayer, JLayeredPane.PALETTE_LAYER);

        // 魔法卡片層
        JPanel cardLayer = new JPanel(null);
        cardLayer.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        cardLayer.setOpaque(false);
        setupMagicCards(cardLayer);
        mainLayeredPane.add(cardLayer, JLayeredPane.MODAL_LAYER);

 
         // 大招特效層
         JPanel  ultimateEffectLayer = new JPanel(null);
         ultimateEffectLayer.setBounds(0, 0, frame.getWidth(), frame.getHeight());
         ultimateEffectLayer.setOpaque(false);
         setupUltimateEffect(ultimateEffectLayer);
         mainLayeredPane.add(ultimateEffectLayer, JLayeredPane.POPUP_LAYER);

        // 更新所有元素的層級
        updateComponentLayers();
    }

    private void addGameElementsToLayer(JPanel layer) {
        // 添加統計面板
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(3, 1, 5, 5));
        statsPanel.setBounds(50, 10, 300, 80);
        statsPanel.setOpaque(false);
        
        heroStatsLabel = new JLabel();
        criticalRateLabel = new JLabel();
        coinsLabel = new JLabel();
        
        styleLabel(heroStatsLabel);
        styleLabel(criticalRateLabel);
        styleLabel(coinsLabel);
        
        statsPanel.add(heroStatsLabel);
        statsPanel.add(criticalRateLabel);
        statsPanel.add(coinsLabel);
        layer.add(statsPanel);

        // 添加怪物統計標籤
        monsterStatsLabel = new JLabel();
        styleLabel(monsterStatsLabel);
        monsterStatsLabel.setBounds(530, 10, 300, 20);
        layer.add(monsterStatsLabel);

        // 添加終極警告標籤
        ultimateWarningLabel = new JLabel("", SwingConstants.CENTER);
        ultimateWarningLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ultimateWarningLabel.setForeground(new Color(255, 99, 71));
        ultimateWarningLabel.setBounds(200, 100, 400, 50);
        layer.add(ultimateWarningLabel);

        // 添加血條
        heroHealthBar = new CustomProgressBar(0, player.getMaxHealth());
        heroHealthBar.setBounds(50, 100, 250, 20);
        heroHealthBar.setForeground(new Color(46, 204, 113));
        layer.add(heroHealthBar);

        monsterHealthBar = new CustomProgressBar(0, monster.getMaxHealth());
        monsterHealthBar.setBounds(450, 100, 250, 20);
        monsterHealthBar.setForeground(new Color(231, 76, 60));
        layer.add(monsterHealthBar);

        // 添加角色頭像
        layer.add(player.getPortraitLabel());
        layer.add(monster.getPortraitLabel());

        // 添加特效
        initializeSlashEffect();
    }
    
    private void setupBuffsButton() {
        // 創建按鈕面板
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBounds(640, 420, 140, 140);
        buttonPanel.setOpaque(false);
        
        showBuffsButton = new ModernButton("查看增益效果");
        showBuffsButton.setPreferredSize(new Dimension(130, 45));
        showBuffsButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        showBuffsButton.setBackground(new Color(75, 0, 130));
        showBuffsButton.setForeground(Color.WHITE);
        
        ModernButton saveButton = new ModernButton("保存遊戲");
        saveButton.setPreferredSize(new Dimension(130, 45));
        saveButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        saveButton.setBackground(new Color(0, 100, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            if (saveManager != null && gameLogic != null) {
                // 需要從 gameLogic 獲取 buffManager
                BuffManager bm = gameLogic.getBuffManager();
                if (bm != null) {
                    saveManager.saveGame(player, gameLogic, bm);
                }
            }
        });
        
        ultimateButton = new ModernButton("終極一擊");
        ultimateButton.setPreferredSize(new Dimension(130, 45));
        ultimateButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        ultimateButton.setBackground(new Color(139, 0, 0));
        ultimateButton.setForeground(Color.WHITE);
        
        ultimateButton.addActionListener(e -> {
            gameLogic.handleUltimateAttack();
            showUltimateAnimation();
        });
        
        // 創建增益效果面板，設置在右側
        
        
        activeBuffsPanel.setBounds(
            (frame.getWidth() - 400) / 2,  // 水平居中
            (frame.getHeight() - 300) / 2,  // 垂直居中
            400,  // 固定寬度
            300   // 固定高度
        );
        activeBuffsPanel.setVisible(false);
        showBuffsButton.addActionListener(e -> {
            boolean newVisibility = !activeBuffsPanel.isVisible();
            activeBuffsPanel.setVisible(newVisibility);
            if (newVisibility) {
                mainLayeredPane.moveToFront(activeBuffsPanel);
                
            } 
        });
        
        buttonPanel.add(showBuffsButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(ultimateButton);
        mainLayeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
        
        // 確保增益面板在最上層
        mainLayeredPane.add(activeBuffsPanel, JLayeredPane.POPUP_LAYER);
        mainLayeredPane.setLayer(activeBuffsPanel, JLayeredPane.POPUP_LAYER);
    }

    private void updateComponentLayers() {
        // 確保增益效果面板始終在最上層
        if (activeBuffsPanel != null) {
            mainLayeredPane.setLayer(activeBuffsPanel, JLayeredPane.POPUP_LAYER);
        }
    }

    // 修改 setupMagicCards 方法
    private void setupMagicCards(JPanel cardLayer) {
        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardPanel.setBounds(20, 460, 600, 100);
        cardPanel.setOpaque(false);
        
        MagicCardType[] cardTypes = MagicCardType.values();
        
        magicCards = new ModernButton[cardTypes.length];
        for (int i = 0; i < magicCards.length; i++) {
            final MagicCardType cardType = cardTypes[i];
            magicCards[i] = new ModernButton(cardType.getCardText());
            magicCards[i].setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            magicCards[i].setBackground(new Color(45, 45, 45));
            magicCards[i].setForeground(Color.WHITE);
            
            magicCards[i].addActionListener(e -> {
                gameLogic.handleMagicCard(cardType.getCardText());
            });
            
            cardPanel.add(magicCards[i]);
        }
        
        cardLayer.add(cardPanel);
    }
    
    public void regenerateMagicCards() {
        MagicCardType[] cardTypes = MagicCardType.values();
        for (int i = 0; i < magicCards.length && i < cardTypes.length; i++) {
            magicCards[i].setText(cardTypes[i].getCardText());
        }
    }
    


    private void setupUltimateEffect(JPanel ultimateEffectLayer) {
        ImageIcon effectIcon = new ImageIcon("src\\assets\\images\\player\\attackcg.png");
    
        // 等比例縮放圖片
        int originalWidth = effectIcon.getIconWidth();
        int originalHeight = effectIcon.getIconHeight();
        int targetWidth = 600; // 目標寬度
        int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth); // 根據比例計算高度
    
        Image scaledImage = effectIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        effectIcon = new ImageIcon(scaledImage);
    
        ultimateEffectLabel = new JLabel(effectIcon);
        ultimateEffectLabel.setBounds(-targetWidth, (600 - targetHeight) / 2, targetWidth, targetHeight); // 初始位置居中垂直
        ultimateEffectLabel.setVisible(false);
    
        ultimateEffectLayer.add(ultimateEffectLabel);
    }

    // Slash effect
    private ImageIcon slashEffect1,slashEffect2;
    private JLabel slashLabel1,slashLabel2;

    private void initializeSlashEffect() {
        slashEffect1 = new ImageIcon("src\\assets\\images\\effect\\slashEffect1.gif");
        slashLabel1 = new JLabel(slashEffect1);
        slashLabel1.setBounds(0, 0, 600, 600);  
        slashLabel1.setVisible(false);  
        slashEffect2 = new ImageIcon("src\\assets\\images\\effect\\slashEffect2.gif");
        slashLabel2 = new JLabel(slashEffect2);
        slashLabel2.setBounds(0, 0, 600, 600);  
        slashLabel2.setVisible(false);  
    }
    
    public void addSlashEffect(int effectIndex) {
        JLabel slashLabel = (effectIndex == 1) ? slashLabel1 : slashLabel2;
        
        Point position = monster.getPortraitLabel().getLocation();
        slashLabel.setLocation(position.x - 160, position.y - 130);
    
        javax.swing.JComponent parent = (javax.swing.JComponent) monster.getPortraitLabel().getParent();
        parent.add(slashLabel);
        parent.setComponentZOrder(slashLabel, 1);
        slashLabel.setVisible(true);
        parent.revalidate();
        parent.repaint();
    
        javax.swing.Timer timer = new javax.swing.Timer(GameConstants.SLASH_EFFECT_DURATION, event -> {
            slashLabel.setVisible(false);
            parent.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // 保持向後兼容
    public void addSlashEffect() {
        addSlashEffect(1);
    }
    
    public void addSlashEffect2() {
        addSlashEffect(2);
    }
    
    public void showUltimateAnimation() {
        ultimateEffectLabel.setVisible(true);
    
        int targetWidth = ultimateEffectLabel.getWidth();
        int targetHeight = ultimateEffectLabel.getHeight();
        int centerX = (600 - targetWidth) / 2; // 螢幕中間位置
    
        // 創建動畫計時器
        Timer timer = new Timer(10, null);
        int[] currentX = {-targetWidth};
        int[] phase = {1}; // 動畫階段 (1: 快速到中間, 2: 慢速移動, 3: 快速右移)
    
        timer.addActionListener(e -> {
            switch (phase[0]) {
                case 1: // 1秒快速移動到螢幕中間
                    currentX[0] += 15; // 快速移動
                    if (currentX[0] >= centerX) {
                        currentX[0] = centerX; // 確保精準停留
                        phase[0] = 2; // 進入下一階段
                    }
                    break;
    
                case 2: // 2秒慢速水平移動
                    currentX[0] += 1; // 慢速移動
                    if (currentX[0] >= centerX + 150) { // 移動一小段距離
                        phase[0] = 3; // 進入下一階段
                    }
                    break;
    
                case 3: // 快速右移出螢幕並消失
                    currentX[0] += 15; // 快速移動
                    if (currentX[0] >= 800) { // 完全滑出螢幕
                        timer.stop();
                        ultimateEffectLabel.setVisible(false); // 消失 
                        addSlashEffect();  
                        new java.util.Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                               
                                addSlashEffect2(); 
                            }
                        }, 1500);
                    }
                    break;
            }
            ultimateEffectLabel.setBounds(currentX[0], (600 - targetHeight) / 2, targetWidth, targetHeight);
        });
        timer.start();
    }
    
    
    
    

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
    }

    public void updateUI() {
        heroStatsLabel.setText("主角 - 血量: " + player.getHealth() + "/" + player.getMaxHealth() + ", 攻擊力: " + player.getAttackPower());
        monsterStatsLabel.setText("怪物 - 血量: " + monster.getHealth() + ", 攻擊力: " + monster.getAttackPower());
        double c = Math.round(player.getCriticalRate() *10) *10;
        criticalRateLabel.setText("攻速: " + (2100 - player.getAttackSpeed()) / 100 + ", 暴擊率: " + c + "%");
        coinsLabel.setText("金幣: " + player.getCoins());
        heroHealthBar.setMaximum(player.getMaxHealth());
        heroHealthBar.setValue(player.getHealth());
        monsterHealthBar.setMaximum(monster.getMaxHealth());
        monsterHealthBar.setValue(monster.getHealth());
        
    }

    public void setUltimateWarning(String warning) {
        ultimateWarningLabel.setText(warning);
    }

    public void showGameEndMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
        System.exit(0);
    }



    private void showHelpMenu() {
        JOptionPane.showMessageDialog(frame, 
            "遊戲說明：\n\n" +
            "1. 你的目標是打敗怪物\n" +
            "2. 使用魔法卡片增強能力\n" +
            "3. 使用技能欄中的技能（快捷鍵 1-4）\n" +
            "4. 注意管理你的金幣\n" +
            "5. 可以在商店購買永久升級\n" +
            "6. 記得保存遊戲進度\n" +
            "7. 活下去！",
            "遊戲說明",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void loadAndContinueGame() {
        if (saveManager == null) return;
        
        SaveData saveData = saveManager.loadGame();
        if (saveData == null) {
            JOptionPane.showMessageDialog(frame, "沒有找到存檔！", "載入失敗", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 恢復玩家狀態
        saveManager.restorePlayerState(saveData, player);
        
        // 恢復 Buff
        if (gameLogic != null) {
            BuffManager bm = gameLogic.getBuffManager();
            if (bm != null) {
                for (String buffId : saveData.getActiveBuffs()) {
                    bm.applyBuff(buffId);
                }
            }
        }
        
        // 設置關卡
        if (gameLogic != null) {
            // 需要添加設置關卡的方法
        }
        
        setupGameWindow();
    }
    
    private void showShop() {
        JFrame shopFrame = new JFrame("商店");
        shopFrame.setSize(600, 500);
        shopFrame.setLocationRelativeTo(frame);
        shopFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        ShopPanel shopPanel = new ShopPanel(player, () -> {
            shopFrame.dispose();
            updateUI();
        });
        
        shopFrame.add(shopPanel);
        shopFrame.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }
    private class NewPanel extends JPanel {
        private Image backgroundImage; 
    
        public NewPanel(String imagePath) {
            setBackgroundImage(imagePath);
        }
    
        public void setBackgroundImage(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
            repaint();
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
    
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Default gradient background
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(48, 48, 48), 
                    0, getHeight(), new Color(32, 32, 32)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    private class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(80, 80, 80));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(60, 60, 60));
                }
            });
        }

        @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 背景顏色
                if (getModel().isPressed()) {
                    g2.setColor(new Color(40, 40, 40));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(80, 80, 80));
                } else {
                    g2.setColor(new Color(60, 60, 60));
                }

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(getForeground());

                // 繪製多行文字
                FontMetrics fm = g2.getFontMetrics();
                String[] lines = getText().split("\n"); // 按換行符分割文字
                int totalHeight = lines.length * fm.getHeight(); // 總文字高度
                int startY = (getHeight() - totalHeight) / 2; // 垂直居中

                for (int i = 0; i < lines.length; i++) {
                    int lineWidth = fm.stringWidth(lines[i]);
                    int x = (getWidth() - lineWidth) / 2; // 水平居中
                    int y = startY + i * fm.getHeight() + fm.getAscent(); // 每行文字的Y位置
                    g2.drawString(lines[i], x, y);
                }

                g2.dispose();
            }
    }

    private class CustomProgressBar extends JProgressBar {
        public CustomProgressBar(int min, int max) {
            super(min, max);
            setStringPainted(true);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2d.setColor(new Color(40, 40, 40));
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            
            // Progress
            int width = (int) (getWidth() * getPercentComplete());
            g2d.setColor(getForeground());
            g2d.fill(new RoundRectangle2D.Float(0, 0, width, getHeight(), 10, 10));
            
            // Text
            String text = getString();
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);
            
            g2d.dispose();
        }
    }

    
/* 
    private class DarkPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(48, 48, 48), 
                0, getHeight(), new Color(32, 32, 32)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
*/
}