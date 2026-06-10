package mods.cybercat.gigeresque.common.config;

import mod.azure.azurelib.common.config.Config;
import mod.azure.azurelib.common.config.Configurable;

import mods.cybercat.gigeresque.CommonMod;
import mods.cybercat.gigeresque.common.entity.AlienEntity.BloodType;

@Config(id = CommonMod.MOD_ID)
public class GigeresqueConfig {

    public static final float DEFAULT_HEAL_OVER_TIME_AMOUNT = 3.5833f;
    public static final int DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS = 10;

    @Configurable
    @Configurable.Synchronized
    public GeneralConfigs generalConfigs = new GeneralConfigs();

    public static class GeneralConfigs {

        @Configurable
        @Configurable.Synchronized
        public boolean enableLogging = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enableDevparticles = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enableDevEntites = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enablePeacefulModeTargetDisable = true;

        @Configurable
        @Configurable.Synchronized
        public boolean peacefulModeIgnorePlayersOnly = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enablePeacefulModeRemoval = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enablePandoraEffects = true;

        @Configurable
        @Configurable.Synchronized
        public boolean enabledCreativeBootAcidProtection = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public int maxSurgeryKitUses = 4;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public int surgeryKitCooldownTicks = 15;
    }

    @Configurable
    @Configurable.Synchronized
    public BlockConfigs alienblockConfigs = new BlockConfigs();

    public static class BlockConfigs {

        @Configurable
        @Configurable.Synchronized
        public boolean enableAcidLavaRemoval = false;

        @Configurable
        @Configurable.Synchronized
        public boolean blackfuildNonrepacle = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public float alienblockHardness = 3.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public float alienblockResistance = 6.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float eggmorphTickTimer = 1200.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float acidDamage = 2;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1200)
        public int sporeTickTimer = 1200;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1200)
        public int gooEffectTickTimer = 1200;

        @Configurable
        @Configurable.Synchronized
        public boolean enableResinAlienCheck = true;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public int resinEntityCheckRange = 15;
    }

    @Configurable
    @Configurable.Synchronized
    public EntityConfigs entityConfigs = new EntityConfigs();

    public static class EntityConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public int xenoMaxSoundRange = 48;

        @Configurable
        @Configurable.Synchronized
        public BloodType gooMutantBloodType = BloodType.GOO;

        @Configurable
        @Configurable.Synchronized
        public BloodType neomorphBloodType = BloodType.NONE;

        @Configurable
        @Configurable.Synchronized
        public EggConfigs eggConfigs = new EggConfigs();

        @Configurable
        @Configurable.Synchronized
        public FacehuggerConfigs facehuggerConfigs = new FacehuggerConfigs();

        @Configurable
        @Configurable.Synchronized
        public BursterConfigs chestbursterConfigs = new BursterConfigs();

        @Configurable
        @Configurable.Synchronized
        public ClassicConfigs alienConfigs = new ClassicConfigs();

        // @Configurable
        // @Configurable.Synchronized
        // public romConfigs romXenoConfigs = new romConfigs();

        @Configurable
        @Configurable.Synchronized
        public AquabursterConfigs aquatic_chestbursterConfigs = new AquabursterConfigs();

        @Configurable
        @Configurable.Synchronized
        public AquaticAlienConfigs aquatic_alienConfigs = new AquaticAlienConfigs();

        @Configurable
        @Configurable.Synchronized
        public HammerpedeConfigs hammerpedeConfigs = new HammerpedeConfigs();

        @Configurable
        @Configurable.Synchronized
        public PopperConfigs popperConfigs = new PopperConfigs();

        @Configurable
        @Configurable.Synchronized
        public StalkerConfigs stalkerConfigs = new StalkerConfigs();

        @Configurable
        @Configurable.Synchronized
        public RBusterConfigs runnerbursterConfigs = new RBusterConfigs();

        @Configurable
        @Configurable.Synchronized
        public RunnerConfigs runner_alienConfigs = new RunnerConfigs();

        @Configurable
        @Configurable.Synchronized
        public SpitterConfigs spitterConfigs = new SpitterConfigs();

        @Configurable
        @Configurable.Synchronized
        public NeobursterConfigs neobursterConfigs = new NeobursterConfigs();

        @Configurable
        @Configurable.Synchronized
        public NeoAdolescentConfigs neomorph_adolescentConfigs = new NeoAdolescentConfigs();

        @Configurable
        @Configurable.Synchronized
        public NeomorphConfigs neomorphConfigs = new NeomorphConfigs();

        @Configurable
        @Configurable.Synchronized
        public HBursterConfigs hell_bursterConfigs = new HBursterConfigs();

        @Configurable
        @Configurable.Synchronized
        public HellmorphRunnerConfigs hellmorph_runnerConfigs = new HellmorphRunnerConfigs();

        @Configurable
        @Configurable.Synchronized
        public BaphomorphConfigs baphomorphConfigs = new BaphomorphConfigs();

        @Configurable
        @Configurable.Synchronized
        public DTBConfigs draconictemplebeastConfigs = new DTBConfigs();

        @Configurable
        @Configurable.Synchronized
        public MHTBConfigs moonlighthorrortemplebeastConfigs = new MHTBConfigs();

        @Configurable
        @Configurable.Synchronized
        public RTBConfigs ravenoustemplebeastConfigs = new RTBConfigs();
    }

    public static class FacehuggerConfigs {

        @Configurable
        @Configurable.Synchronized
        public boolean facehuggerGivesBlindness = false;

        @Configurable
        @Configurable.Synchronized
        public boolean enableFacehuggerAttachmentTimer = true;

        @Configurable
        @Configurable.Synchronized
        public boolean enableFacehuggerTimerTicks = false;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1200, max = 8000)
        public float impregnationTickTimer = 1200.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float facehuggerAttachTickTimer = 1200.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public int facehuggerStunTickTimer = 90;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double facehuggerHealth = 40;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float facehuggerHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int facehuggerHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class BursterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double chestbursterHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float chestbursterGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float chestbursterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int chestbursterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class ClassicConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float alienGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double alienHealth = 150;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double alienArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double alienAttackDamage = 7;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float alienTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float alienAttackSpeed = 3.9F;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float alienHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int alienHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    // public static class romConfigs {
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public float romGrowthMultiplier = 1.0f;
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public double romXenoHealth = 200;
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public double romXenoArmor = 9;
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public double romXenoAttackDamage = 7;
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public float romXenoTailAttackDamage = 3;
    //
    // @Configurable
    // @Configurable.Synchronized
    // @Configurable.DecimalRange(min = 1)
    // public float romXenoAttackSpeed = 3.9F;
    // }

    public static class AquabursterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double aquatic_chestbursterHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float aquatic_chestbursterGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float aquatic_chestbursterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int aquatic_chestbursterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class AquaticAlienConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float aquatic_alienGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double aquatic_alienHealth = 190;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double aquatic_alienArmor = 6;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double aquatic_alienAttackDamage = 7;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float aquatic_alienTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float aquatic_alienHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int aquatic_alienHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class HammerpedeConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hammerpedeHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hammerpedeAttackDamage = 1.5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float hammerpedeHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int hammerpedeHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class PopperConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double popperHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double popperAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float popperHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int popperHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class RBusterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float runnerbursterGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double runnerbursterHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double runnerbursterAttackDamage = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float runnerbursterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int runnerbursterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class StalkerConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double stalkerHealth = 120;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double stalkerArmor = 4;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double stalkerAttackDamage = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float stalkerTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float stalkerAttackSpeed = 1.7F;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float stalkerHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int stalkerHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class RunnerConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float runner_alienGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double runner_alienHealth = 160;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double runner_alienArmor = 6;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double runner_alienAttackDamage = 7;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float runner_alienTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float runner_alienAttackSpeed = 3.0F;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float runner_alienHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int runner_alienHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class EggConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double alieneggHealth = 40;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double alieneggHatchRange = 7;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int alienegg_spawn_weight = 10;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int alienegg_min_group = 1;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 1)
        public int alienegg_max_group = 1;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float alieneggHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int alieneggHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class SpitterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double spitterHealth = 120;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double spitterArmor = 4;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double spitterAttackDamage = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float spitterRangedAttackDamage = 4;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float spitterTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float spitterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int spitterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class NeobursterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neobursterHealth = 60;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neobursterAttackDamage = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float neobursterGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float neobursterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int neobursterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class NeoAdolescentConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neomorph_adolescentHealth = 90;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neomorph_adolescentAttackDamage = 6;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float neomorph_adolescentTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float neomorph_adolescentGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float neomorph_adolescentHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int neomorph_adolescentHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class NeomorphConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neomorphHealth = 120;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neomorphArmor = 4;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double neomorphAttackDamage = 7;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float neomorphTailAttackDamage = 3;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float neomorphHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int neomorphHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class DTBConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double draconictemplebeastHealth = 300;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double draconictemplebeastArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double draconictemplebeastAttackDamage = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float draconictemplebeastHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int draconictemplebeastHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class MHTBConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double moonlighthorrortemplebeastHealth = 300;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double moonlighthorrortemplebeastArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double moonlighthorrortemplebeastAttackDamage = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float moonlighthorrortemplebeastHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int moonlighthorrortemplebeastHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class RTBConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double ravenoustemplebeastHealth = 300;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double ravenoustemplebeastArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double ravenoustemplebeastAttackDamage = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float ravenoustemplebeastHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int ravenoustemplebeastHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class BaphomorphConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double baphomorphHealth = 300;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double baphomorphArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double baphomorphAttackDamage = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float baphomorphHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int baphomorphHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class HellmorphRunnerConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hellmorph_runnerHealth = 300;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hellmorph_runnerArmor = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hellmorph_runnerAttackDamage = 9;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float hellmorph_runnerHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int hellmorph_runnerHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public static class HBursterConfigs {

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public float hell_bursterGrowthMultiplier = 1.0f;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hell_bursterHealth = 30;

        @Configurable
        @Configurable.Synchronized
        @Configurable.DecimalRange(min = 1)
        public double hell_bursterAttackDamage = 5;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public float hell_bursterHealOverTimeAmount = DEFAULT_HEAL_OVER_TIME_AMOUNT;

        @Configurable
        @Configurable.Synchronized
        @Configurable.Range(min = 0)
        public int hell_bursterHealOverTimeIntervalSeconds = DEFAULT_HEAL_OVER_TIME_INTERVAL_SECONDS;
    }

    public float getEggmorphTickTimer() {
        return alienblockConfigs.eggmorphTickTimer;
    }

    public float getFacehuggerAttachTickTimer() {
        return entityConfigs.facehuggerConfigs.facehuggerAttachTickTimer;
    }

    public float getImpregnationTickTimer() {
        return entityConfigs.facehuggerConfigs.impregnationTickTimer;
    }

    public int getgooEffectTickTimer() {
        return alienblockConfigs.gooEffectTickTimer;
    }
}
