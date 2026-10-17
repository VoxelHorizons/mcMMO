# mcMMO native GUI skill statistic placeholders

These are native PlaceholderAPI placeholders for VoxelHorizons' migrated
DeluxeMenus skill UI. They are implemented in `SkillStatisticsPlaceholder`
and use the **same data sources as mcMMO's built-in /skill commands**:
the player's skill level, advanced.yml, registered subskills, skill managers
and fishing rarity treasure tables.

## Examples

| Placeholder | Meaning |
|---|---|
| `%mcmmo_stats:mining_ddc%` | Base Mining Double Drops success probability |
| `%mcmmo_stats:mining_tdc%` | Super Breaker triple-drop probability **while active** (uses Double Drops roll, zero if AllowMiningTripleDrops is disabled) |
| `%mcmmo_stats:mining_mother_lode%` | Separate Mother Lode triple-drop roll |
| `%mcmmo_stats:herbalism_ddc%` | Herbalism Double Drops base probability |
| `%mcmmo_stats:woodcutting_ddc%` | Woodcutting Harvest Lumber base probability |
| `%mcmmo_stats:fishing_th-rare%` | Configured fishing rare item treasure chance at the player's treasure tier |
| `%mcmmo_stats:fishing_mhc%` | Magic Hunter enchant treasure chance (combined eligible rarities) |
| `%mcmmo_stats:fishing_mac%` | Master Angler base minimum wait-time reduction in **seconds**, not catch chance |
| `%mcmmo_stats:repair_afos%` | Arcane Forge keep-enchantment chance |
| `%mcmmo_stats:repair_afof%` | Arcane Forge downgrade-enchantment chance |
| `%mcmmo_stats:axes_lbmd%` | Axes Limit Break **bonus** damage, at quality=1000 as in /axes |
| `%mcmmo_stats:swords_rl%` | Rupture duration in seconds **against mobs** |
| `%mcmmo_stats:alchemy_bs%` | Alchemy brewing-speed multiplier (add `x` in lore) |

All 73 former unsupported N/A values in the uploaded skill menu set have
corresponding `%mcmmo_stats:<skill>_<metric>%` tokens.

**Formatting**: Chance fields include a % sign (e.g. `27.00%`).
Damage, ranks and multipliers return plain numbers. Duration values return
seconds without an `s` suffix. Add required units in DeluxeMenus lore.

**Calculation scope**: Standard skill chances are **base chance values**,
matching mcMMO's native `ProbabilityUtil.getRNGDisplayValues(...)[0]`.
Lucky-perk modifiers, third-party SubSkillEvent mutations, target armor,
per-skill permission gates, and the active ability state can change the
probability or outcome in gameplay. Display percentages should not be
interpreted as universal guaranteed outcome rates.

**Setup**: Requires the patched mcMMO plugin JAR plus PlaceholderAPI.
The `%logic_...` prefix belongs to ConditionalText and is used only for
the threshold/check/cross GUI rules. Native mcMMO stats instead use
`%mcmmo_stats:...%`.

Testing:

```
/papi parse me %mcmmo_stats:mining_ddc%
/papi parse me %mcmmo_stats:mining_tdc%
/papi parse me %mcmmo_stats:mining_mother_lode%
/papi parse me %mcmmo_stats:fishing_th-rare%
```

Compare those results with /mining and /fishing using the same player,
configurations and current skill levels. Stage-test before merging.
