package team.monroe.org.pocketfit.fragments.contract;

import android.animation.Animator;

import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;

public interface HeaderContract {
    public void changeCaption(String newCaption, boolean secondaryHeaderRequested, boolean animate);
    public String getCaption();
    public boolean isSecondary();
}
