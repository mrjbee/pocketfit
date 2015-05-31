package team.monroe.org.pocketfit.fragments;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

import java.io.Serializable;

import team.monroe.org.pocketfit.FragmentActivity;

import team.monroe.org.pocketfit.PocketFitApp;

public abstract class BodyFragment<OwnerActivity extends FragmentActivity>  extends AppFragment<OwnerActivity> {

    private HeaderUpdateRequest headerUpdateRequest = HeaderUpdateRequest.NOT_SET;

    public void feature_headerUpdate(HeaderUpdateRequest headerUpdateRequest) {
        this.headerUpdateRequest = headerUpdateRequest;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (headerUpdateRequest != HeaderUpdateRequest.NOT_SET){
            if (headerUpdateRequest == HeaderUpdateRequest.SET){
                ((FragmentActivity) activity()).header(getHeaderName(), isHeaderSecondary());
            }else {
                ((FragmentActivity) activity()).animateHeader(getHeaderName(), isHeaderSecondary());
            }
        }
        headerUpdateRequest = HeaderUpdateRequest.NOT_SET;
    }

    protected abstract boolean isHeaderSecondary();
    protected abstract String getHeaderName();

    public void onImageResult(Uri uri) {}

    public <ArgType> ArgType getArgument(String key) {
        if (owner().getBodyArguments() == null)return null;
        return (ArgType) owner().getBodyArguments().getSerializable(key);
    }

    public android.view.View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        return null;
    }

    public static enum HeaderUpdateRequest implements Serializable {
        SET, NOT_SET, ANIMATE
    }

}
