package team.monroe.org.pocketfit.fragments.contract;

import android.view.View;
import android.view.ViewGroup;

public interface HeaderOwnerContract {
    public View buildHeaderActionsView(ViewGroup viewGroup);
    public void onHeaderBackPressed();
}
