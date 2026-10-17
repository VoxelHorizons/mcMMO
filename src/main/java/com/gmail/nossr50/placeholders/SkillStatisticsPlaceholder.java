package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.Rarity;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.axes.Axes;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.bukkit.entity.Player;

/**
 * mcMMO skill-menu statistics sourced from the same managers, skill probabilities,
 * ranks and advanced.yml/fishing treasure configuration used by mcMMO skill commands.
 *
 * PAPI syntax: %mcmmo_stats:<skill>_<metric>% e.g. %mcmmo_stats:mining_ddc%.
 * Percent fields ALREADY include a % suffix. Durations return seconds; ranks,
 * damage and multipliers return numbers. Null player data never creates fake 0%.
 *
 * This is an informational snapshot of base values; the actual event result can
 * be influenced by Lucky perks, permissions, targets and SubSkillEvent listeners.
 */
public final class SkillStatisticsPlaceholder implements Placeholder {
    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String process(Player player, String data) {
        if (player == null || data == null) return "";
        McMMOPlayer mmo = UserManager.getPlayer(player);
        if (mmo == null) return "";
        String key = data.trim().toLowerCase(Locale.ROOT);
        if (key.isEmpty()) return "";

        SubSkillType probability = probabilityFor(key);
        if (probability != null) {
            return ProbabilityUtil.getRNGDisplayValues(mmo, probability)[0];
        }

        var config = mcMMO.p.getAdvancedConfig();
        return switch (key) {
            case "alchemy_bs" -> decimal(mmo.getAlchemyManager().calculateBrewSpeed(false));
            case "alchemy_cr" -> integer(mmo.getAlchemyManager().getTier());
            case "alchemy_il" -> mmo.getAlchemyManager().getIngredientList();

            case "archery_ssbd" -> percent(Archery.getDamageBonusPercent(player) * 100D);
            case "archery_lbmd" -> limitBreak(player, SubSkillType.ARCHERY_ARCHERY_LIMIT_BREAK);

            case "axes_ai" -> decimal(mmo.getAxesManager().getImpactDurabilityDamage());
            case "axes_am" -> decimal(Axes.getAxeMasteryBonusDamage(player));
            case "axes_ssd" -> abilityLength(mmo, PrimarySkillType.AXES);
            case "axes_lbmd" -> limitBreak(player, SubSkillType.AXES_AXES_LIMIT_BREAK);
            case "axes_gi" -> decimal(Axes.greaterImpactBonusDamage);

            case "excavation_aeoc" -> percent(mmo.getExcavationManager().getArchaelogyExperienceOrbChance());
            case "excavation_aeoa" -> integer(mmo.getExcavationManager().getExperienceOrbsReward());
            case "excavation_gdbd" -> abilityLength(mmo, PrimarySkillType.EXCAVATION);

            case "fishing_th-common" -> fishingTreasure(mmo, Rarity.COMMON);
            case "fishing_th-uncommon" -> fishingTreasure(mmo, Rarity.UNCOMMON);
            case "fishing_th-rare" -> fishingTreasure(mmo, Rarity.RARE);
            case "fishing_th-epic" -> fishingTreasure(mmo, Rarity.EPIC);
            case "fishing_th-legendary" -> fishingTreasure(mmo, Rarity.LEGENDARY);
            case "fishing_th-mythic" -> fishingTreasure(mmo, Rarity.MYTHIC);
            case "fishing_sc" -> ProbabilityUtil.getRNGDisplayValues(
                    Probability.ofPercent(mmo.getFishingManager().getShakeChance()))[0];
            case "fishing_fdr" -> rank(player, SubSkillType.FISHING_FISHERMANS_DIET);
            case "fishing_mhc" -> fishingMagicChance(mmo);
            // Master Angler affects time to hook, not a flat catch probability.
            case "fishing_mac" -> decimal(mmo.getFishingManager()
                    .getMasterAnglerTickMinWaitReduction(
                            RankUtils.getRank(player, SubSkillType.FISHING_MASTER_ANGLER),
                            false) / 20D);

            case "herbalism_gtd" -> abilityLength(mmo, PrimarySkillType.HERBALISM);
            case "herbalism_fdr" -> rank(player, SubSkillType.HERBALISM_FARMERS_DIET);
            case "herbalism_gts" -> rank(player, SubSkillType.HERBALISM_GREEN_THUMB);

            case "mining_sbl" -> abilityLength(mmo, PrimarySkillType.MINING);
            case "mining_ob" -> percent(mmo.getMiningManager().getOreBonus() * 100D);
            case "mining_dm" -> integer(mmo.getMiningManager().getDropMultiplier());
            case "mining_bri" -> decimal(mmo.getMiningManager().getBlastRadiusModifier());
            case "mining_dedd" -> percent(mmo.getMiningManager().getBlastDamageModifier());

            case "repair_rm" -> repairMastery(mmo);
            case "repair_afos" -> percent(Permissions.arcaneBypass(player)
                    || Permissions.hasRepairEnchantBypassPerk(player)
                    ? 100D : mmo.getRepairManager().getKeepEnchantChance());
            case "repair_afof" -> percent(Permissions.arcaneBypass(player)
                    || Permissions.hasRepairEnchantBypassPerk(player)
                    ? 0D : mmo.getRepairManager().getDowngradeEnchantChance());
            case "repair_afr" -> rank(player, SubSkillType.REPAIR_ARCANE_FORGING);

            case "salvage_sc" -> integer(SalvageManager.getSalvageLimit(player));
            case "salvage_asfec" -> percent(mmo.getSalvageManager().getExtractFullEnchantChance());
            case "salvage_aspec" -> percent(mmo.getSalvageManager().getExtractPartialEnchantChance());

            case "smelting_fem" -> integer(mmo.getSmeltingManager().getFuelEfficiencyMultiplier());
            case "smelting_vxm" -> integer(mmo.getSmeltingManager().getVanillaXpMultiplier());

            case "swords_rc" -> percent(config.getRuptureChanceToApplyOnHit(
                    RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE)));
            // Rupture durations differ for mobs and players. The GUI shows the mob value.
            case "swords_rl" -> integer(config.getRuptureDurationSeconds(false));
            case "swords_ssl" -> abilityLength(mmo, PrimarySkillType.SWORDS);
            case "swords_lbmd" -> limitBreak(player, SubSkillType.SWORDS_SWORDS_LIMIT_BREAK);
            case "swords_sd" -> decimal(mmo.getSwordsManager().getStabDamage());

            case "taming_ffs" -> percent(config.getFastFoodChance());
            case "taming_pc" -> percent(config.getPummelChance());
            case "taming_tf" -> decimal(Taming.thickFurModifier);
            case "taming_sp" -> decimal(Taming.shockProofModifier);
            case "taming_sc" -> decimal(Taming.sharpenedClawsBonusDamage);

            case "unarmed_sas" -> decimal(mmo.getUnarmedManager().getSteelArmStyleDamage());
            case "unarmed_bl" -> abilityLength(mmo, PrimarySkillType.UNARMED);
            case "unarmed_lbmd" -> limitBreak(player, SubSkillType.UNARMED_UNARMED_LIMIT_BREAK);

            case "woodcutting_tfl" -> abilityLength(mmo, PrimarySkillType.WOODCUTTING);
            default -> null;
        };
    }

    /** Only actual probability skills should use the generic configurable RNG formula. */
    private static SubSkillType probabilityFor(String key) {
        return switch (key) {
            case "acrobatics_rc" -> SubSkillType.ACROBATICS_ROLL;
            case "acrobatics_dc" -> SubSkillType.ACROBATICS_DODGE;
            case "archery_dc" -> SubSkillType.ARCHERY_DAZE;
            case "archery_arc" -> SubSkillType.ARCHERY_ARROW_RETRIEVAL;
            case "axes_csc" -> SubSkillType.AXES_CRITICAL_STRIKES;
            case "herbalism_hlc" -> SubSkillType.HERBALISM_HYLIAN_LUCK;
            case "herbalism_stc" -> SubSkillType.HERBALISM_SHROOM_THUMB;
            case "herbalism_ddc" -> SubSkillType.HERBALISM_DOUBLE_DROPS;
            case "herbalism_gtc" -> SubSkillType.HERBALISM_GREEN_THUMB;
            case "mining_ddc" -> SubSkillType.MINING_DOUBLE_DROPS;
            case "mining_tdc" -> SubSkillType.MINING_MOTHER_LODE;
            case "repair_src" -> SubSkillType.REPAIR_SUPER_REPAIR;
            case "smelting_ssc" -> SubSkillType.SMELTING_SECOND_SMELT;
            case "swords_cac" -> SubSkillType.SWORDS_COUNTER_ATTACK;
            case "taming_gc" -> SubSkillType.TAMING_GORE;
            case "unarmed_adc" -> SubSkillType.UNARMED_ARROW_DEFLECT;
            case "unarmed_dc" -> SubSkillType.UNARMED_DISARM;
            case "unarmed_igc" -> SubSkillType.UNARMED_IRON_GRIP;
            case "woodcutting_ddc" -> SubSkillType.WOODCUTTING_HARVEST_LUMBER;
            default -> null;
        };
    }

    private static String fishingTreasure(McMMOPlayer player, Rarity rarity) {
        int tier = player.getFishingManager().getLootTier();
        return percent(FishingTreasureConfig.getInstance().getItemDropRate(tier, rarity));
    }

    private static String fishingMagicChance(McMMOPlayer player) {
        int tier = player.getFishingManager().getLootTier();
        double total = 0D;
        for (Rarity rarity : Rarity.values()) {
            if (rarity != Rarity.MYTHIC) {
                total += FishingTreasureConfig.getInstance().getEnchantmentDropRate(tier, rarity);
            }
        }
        return percent(total >= 1D ? total : 0D);
    }

    private static String repairMastery(McMMOPlayer player) {
        var config = mcMMO.p.getAdvancedConfig();
        double cap = config.getRepairMasteryMaxBonus();
        double maxLevel = config.getMaxBonusLevel(SubSkillType.REPAIR_REPAIR_MASTERY);
        // Mirrors RepairCommand.dataCalculations(). All values are percent.
        return percent(maxLevel <= 0 ? cap : Math.min(
                (cap / maxLevel) * player.getSkillLevel(PrimarySkillType.REPAIR), cap));
    }

    private static String limitBreak(Player player, SubSkillType type) {
        return integer(CombatUtils.getLimitBreakDamageAgainstQuality(player, type, 1000));
    }

    private static String abilityLength(McMMOPlayer player, PrimarySkillType skill) {
        var config = mcMMO.p.getAdvancedConfig();
        int increase = config.getAbilityLength();
        int cap = config.getAbilityLengthCap();
        int level = player.getSkillLevel(skill);
        int length = 2 + (cap <= 0 ? level : Math.min(level, cap)) / Math.max(1, increase);
        int maximum = mcMMO.p.getSkillTools()
                .getSuperAbilityMaxLength(mcMMO.p.getSkillTools().getSuperAbility(skill));
        if (maximum != 0) length = Math.min(length, maximum);
        return integer(length);
    }

    private static String percent(double value) {
        return new DecimalFormat("0.00'%'",
                DecimalFormatSymbols.getInstance(Locale.US)).format(value);
    }

    private static String decimal(double value) {
        return new DecimalFormat("0.##",
                DecimalFormatSymbols.getInstance(Locale.US)).format(value);
    }

    private static String integer(int value) {
        return Integer.toString(value);
    }

    private static String rank(Player player, SubSkillType type) {
        return integer(RankUtils.getRank(player, type));
    }
}
