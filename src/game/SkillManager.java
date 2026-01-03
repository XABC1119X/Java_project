package src.game;

import java.util.*;

/**
 * 技能管理器
 * 管理所有可用技能和已裝備技能
 */
public class SkillManager {
    private Map<String, Skill> availableSkills = new HashMap<>();
    private List<Skill> equippedSkills = new ArrayList<>();
    
    public SkillManager() {
        initializeSkills();
        // 預設裝備前4個技能
        equippedSkills.add(availableSkills.get("dodge"));
        equippedSkills.add(availableSkills.get("heal"));
        equippedSkills.add(availableSkills.get("combo"));
        equippedSkills.add(availableSkills.get("shield"));
    }
    
    private void initializeSkills() {
        availableSkills.put("dodge", new DodgeSkill());
        availableSkills.put("heal", new HealSkill());
        availableSkills.put("combo", new ComboSkill());
        availableSkills.put("shield", new ShieldSkill());
        availableSkills.put("slowTime", new SlowTimeSkill());
    }
    
    public void useSkill(String skillId, Player player, Monster monster, GameLogic gameLogic) {
        Skill skill = availableSkills.get(skillId);
        if (skill != null && !skill.isOnCooldown()) {
            skill.execute(player, monster, gameLogic);
        }
    }
    
    public List<Skill> getEquippedSkills() {
        return new ArrayList<>(equippedSkills);
    }
    
    public Map<String, Skill> getAvailableSkills() {
        return new HashMap<>(availableSkills);
    }
}

