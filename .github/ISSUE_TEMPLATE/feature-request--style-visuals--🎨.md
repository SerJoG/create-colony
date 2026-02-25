---
name: "Feature request (Style/Visuals) \U0001F3A8"
about: Suggest a visual improvement, new model, or aesthetic addition.
title: "[Feature] [Style]"
labels: feature, ui/ux
assignees: SerJoG

---

### 🎯 Objective
*Describe the visual goal. What is the "vibe" or aesthetic purpose? (e.g., "A set of weathered copper blocks" or "A new particle effect for magic wands"). Focus on the artistic intent.*

### 🖼️ Visual References & Palette
*Describe or link to references. What colors should be used? Should it match a specific vanilla style or a custom theme? (Drag and drop reference images here)*

### 🛠️ Tasks
- [ ] **Asset Creation:** Create models (.json/.geo) and textures (.png).
- [ ] **Implementation:** Register the blocks/items/entities in the code.
- [ ] **Animations:** (If applicable) Set up state-mapping or keyframe animations.
- [ ] **Polishing:** Adjust light values, particle offsets, or sound triggers.
- [ ] **Review:** Check for Z-fighting or texture mirroring issues in-game.

### ✅ Acceptance Criteria
- [ ] The assets match the project's established art style.
- [ ] The models and textures render correctly without "black/pink" missing texture boxes.
- [ ] UI elements (if any) are readable and scaled correctly for different GUI sizes.
- [ ] [Specific Detail: e.g., The block connects seamlessly with other blocks in the set].

### 🔗 Resources & References
- **Target System:** (e.g., Decorative Blocks, Entity Models, GUI)
- **Model Format:** (e.g., Java Blockbench, Bedrock/GeckoLib, JSON)
- **Version:** 

---

## ✅ General Definition of Done (DoD)

### 🎨 Visual Integrity
- [ ] **Consistency:** The colors and pixel density (e.g., 16x16) match the rest of the mod.
- [ ] **No Glitches:** No Z-fighting (flickering textures) or unintended transparent gaps.
- [ ] **Side Safety:** Verified that rendering code stays on the **Client** and doesn't crash the **Dedicated Server**.

### ⚙️ Technical Quality
- [ ] **Optimization:** Models do not have an excessive polygon count that drops FPS.
- [ ] **Culling:** Block faces that aren't visible are properly culled to save performance.
- [ ] **Clean Code:** Registry names follow naming conventions (lowercase, no spaces).

### 📖 Documentation & Maintenance
- [ ] **Organization:** Asset files (models/textures) are placed in the correct `assets/<modid>/...` folders.
- [ ] **Comments:** If special rendering code (Mixins/BakedModels) was used, it is explained for future me.
