# Slayer Hunter Tracker v2.0 — development build

Adds a manually adjustable Slayer progress bar (loot-event estimates), tool charge
alerts at 100/50/10/0, quest exception toggle, Slayer target highlighting overlay,
block/warning statistics, statistics page, backup text export, and an advisory bank
whitelist review. No HUD overlay was added. Keeps v1.9 activity history and charge
stacking, and original sidebar styling.

**Important limitations**: Slayer remaining kills are estimated and must be calibrated.
The bank whitelist is not finalized and is advisory only. Backups are export-only;
restore is not implemented. A quest toggle temporarily bypasses the attack guard,
and should be disabled outside permitted quest combat. Counter and overlay behavior
must be tested in-game. No full RuneLite compilation has been completed in this environment.

To upgrade, replace `SlayerHunterPlugin.java` and `SlayerHunterPanel.java` and rerun Gradle.
