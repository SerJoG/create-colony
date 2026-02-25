---
name: "Maintenance & Infrastructure \U0001F6E0️"
about: Non-feature improvements, refactors, or infrastructure changes.
title: "[Improvements]"
labels: ''
assignees: SerJoG

---

### 🎯 Objective
*Describe the "Why". Is this to reduce lag? Make the code easier to read? Speed up the build process? Fix a technical debt issue?*

### 🛠️ Tasks
- [] **Research:** Identify current bottlenecks or outdated configurations.
- [] **Implementation:** [Describe the technical change here].
- [] **Verification:** Ensure the change doesn't break existing game mechanics (Regression Testing).
- [] **Cleanup:** Remove old files, unused dependencies, or deprecated code.

### ✅ Acceptance Criteria
- [] **Stability:** The game launches and runs without crashes in a dev environment.
- [] **Performance:** (If applicable) Frame times or server TPS remain stable or improve.
- [] **Correctness:** The internal logic produces the same end-result as before (if refactoring).
- [] **Build Success:** The project compiles and jars are generated successfully via Gradle/CI.

### 🔗 Resources & References
- [] **Impacted Systems:** (e.g., Networking, Rendering, Data Generation, CI/CD)
- [] **Dependencies:** (e.g., Architectury, Forge/Fabric versions, Github Actions)
- [] **External Links:** [Links to documentation or related issues]

## ✅ General Definition of Done (DoD)
### ⚙️ Technical Integrity
- [] **No Regressions:** All existing features function exactly as they did before the change.
- [] **Compatibility:** Tested alongside common "standard" mods (e.g., JEI, Jade) to ensure no mixin conflicts.
- [] **Side Safety:** Verified that the code runs correctly on both Client and Dedicated Server.
- [] **Logging:** No new spammy "Warn" or "Error" messages in the latest.log.

### 💻 Code Quality
- [] **Project Standards:** Follows the established naming conventions (e.g., mappings, mojmap).
- [] **Optimization:** No new memory leaks or unnecessary object allocation in tick loops.
- [] **Modding Best Practices:** Uses proper registry events and API hooks rather than "hacky" workarounds.

### 📖 Documentation & Maintenance
- [] **Comments:** Complex logic or "magic numbers" are explained for other contributors.
- [] **Dev Docs:** If this changes how the mod is built or how to contribute, the CONTRIBUTING.md or README is updated.
- [] **Version Bump:** (Optional) Versioning in gradle.properties or mods.toml is updated if required.
