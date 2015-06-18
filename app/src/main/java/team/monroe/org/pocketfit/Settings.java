package team.monroe.org.pocketfit;


import org.monroe.team.android.box.services.SettingManager;

public final class Settings {

   private Settings() {}

   public final static SettingManager.SettingItem<String> ID_ACtIVE_ROUTINE = new SettingManager.SettingItem<>("ACTIVE_ROUTINE", String.class, null);
   public final static SettingManager.SettingItem<Integer> CALORIES_DAY_LIMIT= new SettingManager.SettingItem<>("CALORIES_DAY_LIMIT", Integer.class, 0);


   @Deprecated
   public final static SettingManager.SettingItem<Long> ROUTINE_START_DATE = new SettingManager.SettingItem<>("ROUTINE_START_DATE", Long.class, null);
   @Deprecated
   public final static SettingManager.SettingItem<String> ROUTINE_STARTED_ID = new SettingManager.SettingItem<>("ROUTINE_STARTED_ID", String.class, null);

   public final static SettingManager.SettingItem<String> ID_ACTIVE_ROUTINE_LAST_DAY = new SettingManager.SettingItem<>("ID_ACTIVE_ROUTINE_LAST_DAY", String.class, null);
   public final static SettingManager.SettingItem<Long> DATE_ACTIVE_ROUTINE_LAST_TRAINING = new SettingManager.SettingItem<>("DATE_ACTIVE_ROUTINE_LAST_TRAINING", Long.class, null);

}
