package team.monroe.org.pocketfit;


import org.monroe.team.android.box.services.SettingManager;

public final class Settings {
   public final static SettingManager.SettingItem<String> ACTIVE_ROUTINE_ID = new SettingManager.SettingItem<>("ACTIVE_ROUTINE", String.class, null);
}
