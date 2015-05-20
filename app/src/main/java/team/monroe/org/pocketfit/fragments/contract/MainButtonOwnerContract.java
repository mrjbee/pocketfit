package team.monroe.org.pocketfit.fragments.contract;


public interface MainButtonOwnerContract {
    public void showMainButton(int resource, Runnable actionOnShow);
    public void hideMainButton(Runnable actionOnHide);
}
