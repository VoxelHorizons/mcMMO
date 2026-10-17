package com.gmail.nossr50.placeholders;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class SkillStatisticsPlaceholderTest {

    @Test
    void routesStatsThroughNamespacedDataParameter() {
        assertEquals("stats", new SkillStatisticsPlaceholder().getName());
        assertEquals("", new SkillStatisticsPlaceholder().process(null, "mining_ddc"));
        assertEquals("", new SkillStatisticsPlaceholder().process(null, null));
    }

    @Test
    void usesActualModernProbabilitySkills() {
        assertEquals(SubSkillType.MINING_DOUBLE_DROPS,
                SkillStatisticsPlaceholder.probabilityFor("mining_ddc"));
        // Super Breaker uses the Double Drops probability, not Mother Lode.
        // Its display is resolved separately to respect AllowMiningTripleDrops.
        assertNull(SkillStatisticsPlaceholder.probabilityFor("mining_tdc"));
        assertEquals(SubSkillType.HERBALISM_DOUBLE_DROPS,
                SkillStatisticsPlaceholder.probabilityFor("herbalism_ddc"));
        assertEquals(SubSkillType.ACROBATICS_DODGE,
                SkillStatisticsPlaceholder.probabilityFor("acrobatics_dc"));
        assertEquals(SubSkillType.WOODCUTTING_HARVEST_LUMBER,
                SkillStatisticsPlaceholder.probabilityFor("woodcutting_ddc"));
        assertNull(SkillStatisticsPlaceholder.probabilityFor("fishing_mac"));
    }

    @Test
    void emitsPercentSignWithoutUsingDefaultLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMANY);
            assertEquals("25.00%", SkillStatisticsPlaceholder.percent(25));
            assertEquals("0.00%", SkillStatisticsPlaceholder.percent(0));
            assertEquals("67.13%", SkillStatisticsPlaceholder.percent(67.129));
        } finally {
            Locale.setDefault(original);
        }
    }
}
