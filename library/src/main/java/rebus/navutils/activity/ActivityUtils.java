package rebus.navutils.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.view.View;

import androidx.fragment.app.Fragment;
import rebus.navutils.NavUtils;
import rebus.navutils.R;

/**
 * Created by raphaelbussa on 31/01/17.
 */

public class ActivityUtils {

    private Fragment fragment;
    private Context context;

    @NavUtils.Anim
    private int animationType = NavUtils.SYSTEM;
    private boolean clearStack = false;
    private Bundle bundle = null;
    private boolean customAnimation = false;
    private int enterResId = 0;
    private int exitResId = 0;

    private Pair<View, String> sceneTransition;

    private ActivityUtils(Context context, Fragment fragment) {
        this.fragment = fragment;
        this.context = context;
    }

    @NonNull
    public static ActivityUtils Builder(Context context) {
        return new ActivityUtils(context, null);
    }

    @NonNull
    public static ActivityUtils Builder(Fragment fragment) {
        return new ActivityUtils(fragment.getContext(), fragment);
    }

    public ActivityUtils animationType(@NavUtils.Anim int animationType) {
        this.animationType = animationType;
        return this;
    }

    public ActivityUtils sceneTransition(Pair<View, String> sceneTransition) {
        if (NavUtils.LOLLIPOP()) {
            this.sceneTransition = sceneTransition;
        }
        return this;
    }

    public ActivityUtils sceneTransition(@NonNull View sharedElement, @NonNull String sharedElementName) {
        if (NavUtils.LOLLIPOP()) {
            this.sceneTransition = Pair.create(sharedElement, sharedElementName);
        }
        return this;
    }

    public ActivityUtils sceneTransition(@NonNull View sharedElement) {
        if (NavUtils.LOLLIPOP()) {
            this.sceneTransition = Pair.create(sharedElement, sharedElement.getTransitionName());
        }
        return this;
    }

    public ActivityUtils customAnimation(@AnimRes int enterResId, @AnimRes int exitResId) {
        this.customAnimation = true;
        this.enterResId = enterResId;
        this.exitResId = exitResId;
        return this;
    }

    public ActivityUtils clearStack() {
        this.clearStack = true;
        return this;
    }

    public ActivityUtils arguments(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public void start(Class clazz) {
        if (context == null) return;
        startActivity(new Intent(context, clazz), -1);
    }

    public void start(@NonNull Intent intent) {
        startActivity(intent, -1);
    }

    public void startForResult(Class clazz, int requestCode) {
        if (context == null) return;
        startActivity(new Intent(context, clazz), requestCode);
    }

    public void startForResult(@NonNull Intent intent, int requestCode) {
        startActivity(intent, requestCode);
    }

    private void startActivity(Intent intent, int requestCode) {
        if (context == null) return;
        if (clearStack) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Bundle options;
        if (customAnimation) {
            options = ActivityOptionsCompat.makeCustomAnimation(context, enterResId, exitResId).toBundle();
        } else {
            switch (animationType) {
                case NavUtils.HORIZONTAL_RIGHT:
                    options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.horizontal_right_start_enter, R.anim.horizontal_right_start_exit).toBundle();
                    break;
                case NavUtils.HORIZONTAL_LEFT:
                    options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.horizontal_left_start_enter, R.anim.horizontal_left_start_exit).toBundle();
                    break;
                case NavUtils.VERTICAL_BOTTOM:
                    options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.vertical_bottom_start_enter, R.anim.fade_out).toBundle();
                    break;
                case NavUtils.VERTICAL_TOP:
                    options = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.vertical_top_start_enter, R.anim.fade_out).toBundle();
                    break;
                case NavUtils.NONE:
                    options = ActivityOptionsCompat.makeCustomAnimation(context, 0, 0).toBundle();
                    break;
                case NavUtils.SYSTEM:
                default:
                    options = Bundle.EMPTY;
                    break;
            }
        }

        if (bundle != null) intent.putExtras(bundle);
        intent.putExtra(NavUtils.NAV_ANIM, animationType);

        if (NavUtils.LOLLIPOP()) {
            if (sceneTransition != null) {
                if (fragment != null) {
                    //noinspection unchecked,ConstantConditions
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(fragment.getActivity(), sceneTransition).toBundle();
                } else if (context instanceof Activity) {
                    //noinspection unchecked
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, sceneTransition).toBundle();
                } else {
                    throw new RuntimeException("sceneTransition can called only from an activity or activity context");
                }
            }
        }

        if (requestCode == -1) {
            ActivityCompat.startActivity(context, intent, options);
        } else {
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode, options);
            } else if (context instanceof Activity) {
                ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, options);
            } else {
                throw new RuntimeException("startForResult can called only from an activity, or a fragment");
            }
        }
    }

}
