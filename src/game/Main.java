package src.game;

public class Main {
    private Player player;
    private Monster monster;

    private GameLogic gameLogic;
    private GameInterface gameInterface;

    
    private BuffEffects buffEffects;
    private BuffManager buffManager;
    private ActiveBuffsPanel activeBuffsPanel;
    private SaveManager saveManager;
    private SkillManager skillManager;
    
    public Main() {
        saveManager = new SaveManager();
        skillManager = new SkillManager();
        
        player = new Player();
        monster = new Monster();
        gameInterface = new GameInterface(player, monster, saveManager, skillManager);
        gameLogic = new GameLogic(gameInterface, player, monster);

        gameInterface.setGameLogic(gameLogic);

        buffEffects = new BuffEffects(player, gameInterface, gameLogic);
        buffManager = new BuffManager(buffEffects);
        gameLogic.setBuffManager(buffManager);
        activeBuffsPanel = new ActiveBuffsPanel(buffManager);
        gameInterface.setActiveBuffPanel(activeBuffsPanel);
        gameInterface.setSaveManager(saveManager);
        gameInterface.setSkillManager(skillManager);
    }
    public static void main(String[] args) {
        new Main();
    }
}