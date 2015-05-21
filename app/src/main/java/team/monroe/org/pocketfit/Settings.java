package team.monroe.org.pocketfit;


import org.monroe.team.android.box.services.SettingManager;

public final class Settings {

   private Settings() {}

   public final static SettingManager.SettingItem<String> ROUTINE_ACTIVE_ID = new SettingManager.SettingItem<>("ACTIVE_ROUTINE", String.class, null);
   public final static SettingManager.SettingItem<Long> ROUTINE_START_DATE = new SettingManager.SettingItem<>("ROUTINE_START_DATE", Long.class, null);
   public final static SettingManager.SettingItem<String> ROUTINE_STARTED_ID = new SettingManager.SettingItem<>("ROUTINE_STARTED_ID", String.class, null);
}
