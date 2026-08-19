package mods.cybercat.gigeresque.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.block.GigBlocks;
import mods.cybercat.gigeresque.common.entity.GigEntities;
import mods.cybercat.gigeresque.common.item.GigItems;
import mods.cybercat.gigeresque.common.status.effect.GigStatusEffects;

public class EnglishLanguageProvider extends FabricLanguageProvider {

    private final String armor;
    private final String ampule;

    private EnglishLanguageProvider(
        FabricDataOutput output,
        CompletableFuture<HolderLookup.Provider> lookup,
        String id,
        String armor,
        String ampule
    ) {
        super(output, id, lookup);
        this.armor = armor;
        this.ampule = ampule;
    }

    public static EnglishLanguageProvider newZealand(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        return new EnglishLanguageProvider(output, lookup, "en_nz", "Armour", "Ampoule");
    }

    public static EnglishLanguageProvider unitedStates(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        return new EnglishLanguageProvider(output, lookup, "en_us", "Armor", "Ampule");
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        // Mob
        record EntityEntry(
            String id,
            String name
        ) {}
        EntityEntry[] entityEntries = {
            new EntityEntry("egg", "Strange Egg"),
            new EntityEntry("facehugger", "Facehugger"),
            new EntityEntry("chestburster", "Chestburster"),
            new EntityEntry("alien", "Entombed Interloper"),

            new EntityEntry("runnerburster", "Lithe Burster"),
            new EntityEntry("runner_alien", "Lithe Interloper"),

            new EntityEntry("aquatic_chestburster", "Threshing Burster"),
            new EntityEntry("aquatic_alien", "Threshing Interloper"),

            new EntityEntry("spitter", "Melting Interloper"),

            new EntityEntry("hell_burster", "Infernal Burster"),
            new EntityEntry("hellmorph_runner", "Infernal Interloper"),
            new EntityEntry("baphomorph", "Charnel Templebeast"),

            new EntityEntry("draconictemplebeast", "Draconic Templebeast"),
            new EntityEntry("ravenoustemplebeast", "Rapacious Templebeast"),
            new EntityEntry("moonlighthorrortemplebeast", "Moonlight Templebeast"),

            new EntityEntry("hammerpede", "Mutant Hammerpede"),
            new EntityEntry("popper", "Mutant Popper"),
            new EntityEntry("stalker", "Mutant Stalker"),

            new EntityEntry("neoburster", "Pale Burster"),
            new EntityEntry("neomorph_adolescent", "Adolescent Pale"),
            new EntityEntry("neomorph", "Maiming Pale"),
        };
        for (var entry : entityEntries) {
            builder.add("entity.gigeresque." + entry.id, entry.name);
            builder.add("item.gigeresque." + entry.id + "_spawn_egg", entry.name + " Spawn Egg");
            builder.add("config.gigeresque.option." + entry.id + "Configs", entry.name + " Configs");
            builder.add("config.gigeresque.option." + entry.id + "Health", entry.name + " Health");
            builder.add("config.gigeresque.option." + entry.id + "Armor", entry.name + " " + armor);
            builder.add("config.gigeresque.option." + entry.id + "AttackDamage", entry.name + " Attack Damage");
            builder.add("config.gigeresque.option." + entry.id + "TailAttackDamage", entry.name + " Tail Attack Damage");
            builder.add("config.gigeresque.option." + entry.id + "RangedAttackDamage", entry.name + " Ranged Attack Damage");
            builder.add("config.gigeresque.option." + entry.id + "AttackSpeed", entry.name + " Attack Speed");
            builder.add("config.gigeresque.option." + entry.id + "GrowthMultiplier", entry.name + " Growth Multiplier");
            builder.add("config.gigeresque.option." + entry.id + "HealOverTimeAmount", entry.name + " Heal Amount");
            builder.add("config.gigeresque.option." + entry.id + "HealOverTimeIntervalSeconds", entry.name + " Heal Interval (seconds)");
        }
        builder.add("config.gigeresque.option.facehuggerAttachTickTimer", "Facehugger Attach Tick Timer");
        builder.add("config.gigeresque.option.impregnationTickTimer", "Impregnation Tick Timer");
        builder.add("config.gigeresque.option.facehuggerGivesBlindness", "Facehuggers Give Blindness");
        builder.add("config.gigeresque.option.enableFacehuggerAttachmentTimer", "Enable On-Screen Facehugger Timer");
        builder.add("config.gigeresque.option.enableFacehuggerTimerTicks", "Make Facehugger Timer Screen Use Ticks");
        builder.add("config.gigeresque.option.facehuggerStunTickTimer", "Facehugger Stun Timer");
        builder.add(GigEntities.BLOOD.get(), "Blood");
        builder.add(GigEntities.ACID.get(), "Acid");
        builder.add(GigEntities.ACID_PROJECTILE.get(), "Acid");
        builder.add(GigEntities.GOO.get(), "Black Goo");
        builder.add(GigEntities.ENGINEER_HOLOGRAM.get(), "Ghostly Figure");
        builder.add(GigEntities.AQUA_EGG.get(), "Aquatic Egg");

        // Block Entities
        builder.add("entity.gigeresque.petrified_object", "Petrified Object");
        builder.add("entity.gigeresque.petrified_object_1", "Petrified Object");
        builder.add("entity.gigeresque.petrified_object_2", "Petrified Object");
        builder.add("entity.gigeresque.petrified_object_3", "Petrified Object");
        builder.add("entity.gigeresque.petrified_object_4", "Petrified Object");
        builder.add("entity.gigeresque.petrified_object_5", "Petrified Object");
        builder.add("entity.gigeresque.petrified_statue", "Unnerving Statue");
        builder.add("entity.gigeresque.neomorph_spore_pods", "Spore Pods");
        builder.add("entity.gigeresque.sitting_idol_entity", "Sitting Idol");
        builder.add("entity.gigeresque.alien_storage_jar_entity", "Catacomb Jar");
        builder.add("entity.gigeresque.alien_storage_block_entity", "Catacomb Sarcophagus");
        builder.add("entity.gigeresque.alien_storage_block_entity_hugger", "Catacomb Sarcophagus");
        builder.add("entity.gigeresque.alien_storage_block_entity_goo", "Catacomb Sarcophagus");
        builder.add("entity.gigeresque.alien_storage_block_entity_spore", "Catacomb Sarcophagus");

        // Items
        builder.add(GigItems.BLACK_FLUID_BUCKET.get(), "Black Fluid Bucket");
        builder.add(GigItems.SURGERY_KIT.get(), "Surgery Kit");
        builder.add(GigItems.TRACKER.get(), "Memo Stone");
        builder.add(GigItems.SEALED_AMPOULE_GOO.get(), ampule + " of Black Fluid");
        builder.add(GigItems.SEALED_AMPOULE_ACID.get(), ampule + " of Acid");
        builder.add(GigItems.SEALED_AMPOULE_EMPTY.get(), "Empty " + ampule);
        builder.add(GigItems.DEV_DEBUG_STICK.get(), "You sleep now");

        // Blocks
        builder.add(GigBlocks.BLACK_FLUID.get(), "Black Fluid");
        builder.add(GigBlocks.BEACON_BLOCK.get(), "Shroudlight");
        builder.add(GigBlocks.PETRIFIED_OBJECT_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_OBJECT_1_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_OBJECT_2_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_OBJECT_3_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_OBJECT_4_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_OBJECT_5_BLOCK.get(), "Petrified Object");
        builder.add(GigBlocks.PETRIFIED_STATUE_BLOCK.get(), "Unnerving Statue");
        builder.add(GigBlocks.PETRIFIED_STATUE_BLOCK_INVIS.get(), "Unnerving Statue");
        builder.add(GigBlocks.NEST_RESIN.get(), "Nest Resin");
        builder.add(GigBlocks.NEST_RESIN_BLOCK.get(), "Nest Resin Block");
        builder.add(GigBlocks.NEST_RESIN_WEB.get(), "Nest Resin Web");
        builder.add(GigBlocks.NEST_RESIN_WEB_CROSS.get(), "Nest Resin Web (Cross)");
        builder.add(GigBlocks.ORGANIC_ALIEN_BLOCK.get(), "Organic Catacomb Block");
        builder.add(GigBlocks.ORGANIC_FRAGILE_ALIEN_BLOCK.get(), "Organic Fragile Catacomb Block");
        builder.add(GigBlocks.ORGANIC_ALIEN_SLAB.get(), "Organic Catacomb Slab");
        builder.add(GigBlocks.ORGANIC_ALIEN_STAIRS.get(), "Organic Catacomb Stairs");
        builder.add(GigBlocks.ORGANIC_ALIEN_WALL.get(), "Organic Catacomb Wall");
        builder.add(GigBlocks.RESINOUS_ALIEN_BLOCK.get(), "Resinous Catacomb Block");
        builder.add(GigBlocks.RESINOUS_ALIEN_PILLAR.get(), "Resinous Catacomb Pillar");
        builder.add(GigBlocks.RESINOUS_FRAGILE_ALIEN_BLOCK.get(), "Resinous Fragile Catacomb Block");
        builder.add(GigBlocks.RESINOUS_ALIEN_SLAB.get(), "Resinous Catacomb Slab");
        builder.add(GigBlocks.RESINOUS_ALIEN_STAIRS.get(), "Resinous Catacomb Stairs");
        builder.add(GigBlocks.RESINOUS_ALIEN_WALL.get(), "Resinous Catacomb Wall");
        builder.add(GigBlocks.RIBBED_ALIEN_BLOCK.get(), "Ribbed Catacomb Block");
        builder.add(GigBlocks.RIBBED_FRAGILE_ALIEN_BLOCK.get(), "Ribbed Fragile Catacomb Block");
        builder.add(GigBlocks.RIBBED_ALIEN_SLAB.get(), "Ribbed Catacomb Slab");
        builder.add(GigBlocks.RIBBED_ALIEN_STAIRS.get(), "Ribbed Catacomb Stairs");
        builder.add(GigBlocks.RIBBED_ALIEN_WALL.get(), "Ribbed Catacomb Wall");
        builder.add(GigBlocks.RIBBED_ALIEN_PILLAR.get(), "Ribbed Catacomb Pillar");
        builder.add(GigBlocks.ROUGH_ALIEN_BLOCK.get(), "Rough Catacomb Block");
        builder.add(GigBlocks.ROUGH_FRAGILE_ALIEN_BLOCK.get(), "Rough Fragile Catacomb Block");
        builder.add(GigBlocks.ROUGH_ALIEN_WALL.get(), "Rough Catacomb Wall");
        builder.add(GigBlocks.ROUGH_ALIEN_STAIRS.get(), "Rough Catacomb Stairs");
        builder.add(GigBlocks.ROUGH_ALIEN_SLAB.get(), "Rough Catacomb Slab");
        builder.add(GigBlocks.SINOUS_ALIEN_BLOCK.get(), "Sinuous Catacomb Block");
        builder.add(GigBlocks.SINOUS_FRAGILE_ALIEN_BLOCK.get(), "Sinuous Fragile Catacomb Block");
        builder.add(GigBlocks.SINOUS_ALIEN_SLAB.get(), "Sinuous Catacomb Slab");
        builder.add(GigBlocks.SINOUS_ALIEN_STAIRS.get(), "Sinuous Catacomb Stairs");
        builder.add(GigBlocks.SINOUS_ALIEN_WALL.get(), "Sinuous Catacomb Wall");
        builder.add(GigBlocks.SMOOTH_ALIEN_PILLAR.get(), "Smooth Catacomb Pillar");
        builder.add(GigBlocks.SMOOTH_ALIEN_STAIRS.get(), "Smooth Catacomb Stairs");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_1.get(), "Creature Mural Block 1");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_2.get(), "Creature Mural Block 2");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_3.get(), "Creature Mural Block 3");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_4.get(), "Creature Mural Block 4");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_5.get(), "Creature Mural Block 5");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_6.get(), "Creature Mural Block 6");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_7.get(), "Creature Mural Block 7");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_8.get(), "Creature Mural Block 8");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_9.get(), "Creature Mural Block 9");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_10.get(), "Creature Mural Block 10");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_11.get(), "Creature Mural Block 11");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_12.get(), "Creature Mural Block 12");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_13.get(), "Creature Mural Block 13");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_14.get(), "Creature Mural Block 14");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_15.get(), "Creature Mural Block 15");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_16.get(), "Creature Mural Block 16");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_17.get(), "Creature Mural Block 17");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_18.get(), "Creature Mural Block 18");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_19.get(), "Creature Mural Block 19");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_20.get(), "Creature Mural Block 20");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_21.get(), "Creature Mural Block 21");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_22.get(), "Creature Mural Block 22");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_23.get(), "Creature Mural Block 23");
        builder.add(GigBlocks.MURAL_ALIEN_BLOCK_24.get(), "Creature Mural Block 24");
        builder.add(GigBlocks.SPORE_BLOCK.get(), "Spore Pods");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_1.get(), "Catacomb Sarcophagus");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_1_HUGGER.get(), "Catacomb Sarcophagus (Hugger)");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_1_GOO.get(), "Catacomb Sarcophagus (Goo)");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_1_SPORE.get(), "Catacomb Sarcophagus (Spore)");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_INVIS.get(), "Bonelike Sarcophagus");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_2.get(), "Bonelike Jar");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_3.get(), "Sitting Idol");
        builder.add(GigBlocks.ALIEN_STORAGE_BLOCK_INVIS2.get(), "Sitting Idol");
        builder.add(GigBlocks.SURFACE_VENT_BLOCK.get(), "Surface Vent");
        builder.add(GigBlocks.DUNGEON_VENT_BLOCK.get(), "Dungeon Vent");

        // Effects
        builder.add(GigStatusEffects.ACID.value(), "Acid Burn");
        builder.add("effect.gigeresque.acid.description", "");
        builder.add(GigStatusEffects.TRAUMA.value(), "Trauma");
        builder.add("effect.gigeresque.trauma.description", "");
        builder.add(GigStatusEffects.DNA.value(), "DNA Disintegration");
        builder.add("effect.gigeresque.dna_disintegration.description", "");
        builder.add(GigStatusEffects.SPORE.value(), "Mycosis");
        builder.add("effect.gigeresque.neo_spore.description", "");
        builder.add(GigStatusEffects.IMPREGNATION.value(), "Impregnation");
        builder.add("effect.gigeresque.impregnation.description", "");
        builder.add(GigStatusEffects.EGGMORPHING.value(), "Eggmorphing");
        builder.add("effect.gigeresque.eggmorphing.description", "");

        // Creative Tabs
        builder.add("itemGroup." + CommonMod.MOD_ID + ".items", "Gigeresque Items");
        builder.add("itemGroup." + CommonMod.MOD_ID + ".blocks", "Gigeresque Blocks");

        // Tool Tips
        builder.add("item.gigeresque.creativeonly.tooltip", "Creative mode only item");
        builder.add("item.gigeresque.tracker.tooltip", "Right click to use tracker");
        builder.add("item.gigeresque.surgery_kit.tooltip1", "Right click on infected hosts to perform invasive surgery.");
        builder.add("item.gigeresque.surgery_kit.tooltip2", "Right click in the air to purge the parasite from within yourself.");
        builder.add("block.gigeresque.unfinished.tooltip", "Unfinished block");

        // Death
        builder.add("death.attack.acid", "%s dissolved in acid.");
        builder.add("death.attack.failed_surgery", "%s's hand slipped.");
        builder.add("death.attack.failed_surgery.player", "%s's hand slipped.");
        builder.add("death.attack.chestburst", "A parasite ruptured %s's chest!");
        builder.add("death.attack.eggmorph", "%s perpetuates the cycle.");
        builder.add("death.attack.dna", "%s's genetic code was rearranged.");
        builder.add("death.attack.goo", "%s is no longer what they used to be.");
        builder.add("death.attack.xeno", "So %s um, we think we should discuss the bonus situation...");
        builder.add("death.attack.execution1", "%s suffered a splitting headache.");
        builder.add("death.attack.execution2", "%s had a brain blast!");
        builder.add("death.attack.spore", "%s suffered from a massive dose of tetanospasmin!");

        // Subtitles
        builder.add("subtitles.gigeresque.dungeon_idle", "Strange sound");
        builder.add("subtitles.gigeresque.tracker_summon", "A ghostly figure appears");
        builder.add("subtitles.gigeresque.alien_hiss", "Unnatural hiss");
        builder.add("subtitles.gigeresque.alien_claw", "Something whooshes");
        builder.add("subtitles.gigeresque.alien_tail", "Something swooshes");
        builder.add("subtitles.gigeresque.alien_death", "Unnatural scream");
        builder.add("subtitles.gigeresque.alien_hurt", "Unnatural screech");
        builder.add("subtitles.gigeresque.alien_headbite", "Interloper headbites");
        builder.add("subtitles.gigeresque.alien_death_thud", "Interloper dies");
        builder.add("subtitles.gigeresque.alien_crunch", "Something cracks");
        builder.add("subtitles.gigeresque.egg_notice", "Egg creaks");
        builder.add("subtitles.gigeresque.hugger_ambient", "Facehugger chitters");
        builder.add("subtitles.gigeresque.hugger_death", "Facehugger dies");
        builder.add("subtitles.gigeresque.hugger_hurt", "Facehugger chirps");
        builder.add("subtitles.gigeresque.hugger_implant", "Something swallows");
        builder.add("subtitles.gigeresque.chestbursting", "Bones break");
        builder.add("subtitles.gigeresque.alien_footstep", "Something walks");
        builder.add("subtitles.gigeresque.alien_handstep", "Something prowls");
        builder.add("subtitles.gigeresque.aqua_landmove", "Something drags");
        builder.add("subtitles.gigeresque.burster_crawl", "Something crawls");
        builder.add("subtitles.gigeresque.aqua_landclaw", "Something whooshes");

        // Configs
        builder.add("config.gigeresque.option.alienblockConfigs", "Block Configs");
        builder.add("config.gigeresque.option.isolationMode", "Isolation Mode");
        builder.add("config.gigeresque.option.surgeryKit", "Surgery Kit");
        builder.add("config.gigeresque.option.acidResistantBlocks", "Acid-Resistant Blocks");
        builder.add("config.gigeresque.option.acidResistantBlocks.@Tooltip", "Blocks that will not be destroyed by acid.");
        builder.add("config.gigeresque.option.eggmorphTickTimer", "Eggmorph Tick Timer");
        builder.add("config.gigeresque.option.gooEffectTickTimer", "Goo Effect Tick Timer");
        builder.add("config.gigeresque.option.maxSurgeryKitUses", "Max Uses of Surgery Kits");
        builder.add("config.gigeresque.option.alieneggHatchRange", "Egg Hatch Range");
        builder.add("config.gigeresque.option.acidDamage", "Acid Damage Per Tick");
        builder.add("config.gigeresque.option.xenoMaxSoundRange", "Interloper Audio Range");
        builder.add("config.gigeresque.option.surgeryKitCooldownTicks", "Surgery Kit Cooldown Ticks");
        builder.add("config.gigeresque.option.sporeTickTimer", "Spore Tick Timer");
        builder.add("config.gigeresque.option.alienblockHardness", "Catacomb Block Hardness");
        builder.add("config.gigeresque.option.alienblockResistance", "Catacomb Block Resistance");
        builder.add("config.gigeresque.option.enableDevparticles", "Enable Dev Particles");
        builder.add("config.gigeresque.option.enableDevEntites", "Enable Dev Entities");
        builder.add("config.gigeresque.option.blackfuildNonrepacle", "Makes Black Fluid Nonreplacable");
        builder.add("config.gigeresque.option.enabledCreativeBootAcidProtection", "Make Acid Not Damage Boots of Creative Players");
        builder.add("config.gigeresque.option.enableAcidLavaRemoval", "Make Lava be Able to Remove Acid/Black Goo Bleeding");
        builder.add("config.gigeresque.option.enableLogging", "Enable Extra Logging");
        builder.add("config.gigeresque.option.enablePandoraEffects", "Enable Pandora Effect");
        builder.add("config.gigeresque.option.enablePeacefulModeTargetDisable", "Peaceful Mode Target Disable All");
        builder.add("config.gigeresque.option.peacefulModeIgnorePlayersOnly", "Peaceful Mode Ignore Players Only");
        builder.add("config.gigeresque.option.enablePeacefulModeRemoval", "Enable Peaceful Mode Removal");
        builder.add("config.gigeresque.option.enableResinAlienCheck", "Enable Resin Alien Check");
        builder.add("config.gigeresque.option.resinEntityCheckRange", "Resin Entity Check Range");
        builder.add("config.gigeresque.option.generalConfigs", "General Configs");
        builder.add("config.gigeresque.option.entityConfigs", "Entity Configs");
        builder.add("config.screen.gigeresque", "Gigeresque Options");

        // Advancements
        builder.add("advancements.gigeresque.find_dungeon.title", "Within These Twisted Halls...");
        builder.add("advancements.gigeresque.find_dungeon.description", "Enter the unknown");
        builder.add("advancements.gigeresque.surgery_kit.title", "Cut It Out");
        builder.add("advancements.gigeresque.surgery_kit.description", "Remove a parasite before it removes you");
        builder.add("advancements.gigeresque.dna_cure.title", "All I Am is Me");
        builder.add(
            "advancements.gigeresque.dna_cure.description",
            "Prevent total genetic collapse by consuming a golden apple while withering"
        );
        builder.add("advancements.gigeresque.all_gig_effects.title", "This Has to Be On Spore-pose");
        builder.add("advancements.gigeresque.all_gig_effects.description", "Have three separate infections at once");
        builder.add("advancements.gigeresque.doubletrouble.title", "Now You're Really In the Spit");
        builder.add("advancements.gigeresque.doubletrouble.description", "Have two things go horribly wrong at the same time");
        builder.add("advancements.gigeresque.facehugged.title", "...We Are Not Alone");
        builder.add("advancements.gigeresque.facehugged.description", "Have the unknown enter you");
        builder.add("advancements.gigeresque.firstspawnfromeffect.title", "Something Followed Us Back");
        builder.add("advancements.gigeresque.firstspawnfromeffect.description", "Realise you're no longer safe");
        builder.add("advancements.gigeresque.dontacidbottle.title", "Yeah, Like That's Ever Gonna Happen");
        builder.add("advancements.gigeresque.dontacidbottle.description", "Fail to exploit the unknown");
        builder.add("advancements.gigeresque.dontdothat.title", "Yeah, Like That's Ever Gonna Happen");
        builder.add("advancements.gigeresque.dontdothat.description", "Fail to exploit the unknown");
        builder.add("advancements.gigeresque.dontgoobottle.title", "Yeah, Like That's Ever Gonna Happen");
        builder.add("advancements.gigeresque.dontgoobottle.description", "Fail to exploit the unknown");
    }
}
