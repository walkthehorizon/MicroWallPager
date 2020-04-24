//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.horizon.tsnackbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.RestrictTo.Scope;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class TSnackbar extends BaseTransientBottomBar<TSnackbar> {
    private final AccessibilityManager accessibilityManager;
    private boolean hasAction;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;
    private static final int[] SNACKBAR_BUTTON_STYLE_ATTR;
    @Nullable
    private BaseCallback<TSnackbar> callback;

    private TSnackbar(ViewGroup parent, View content, ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
        this.accessibilityManager = (AccessibilityManager)parent.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    public boolean isShown() {
        return super.isShown();
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
        ViewGroup parent = findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
        } else {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            SnackbarContentLayout content = (SnackbarContentLayout)inflater.inflate(R.layout.design_layout_snackbar_include, parent, false);
            TSnackbar snackbar = new TSnackbar(parent, content, content);
            snackbar.setText(text);
            snackbar.setDuration(duration);
            return snackbar;
        }
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @StringRes int resId, int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;

        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup)view;
            }

            if (view instanceof FrameLayout) {
                if (view.getId() == R.id.content) {
                    return (ViewGroup)view;
                }

                fallback = (ViewGroup)view;
            }

            if (view != null) {
                ViewParent parent = view.getParent();
                view = parent instanceof View ? (View)parent : null;
            }
        } while(view != null);

        return fallback;
    }

    @NonNull
    public TSnackbar setText(@NonNull CharSequence message) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout)this.view.getChildAt(0);
        TextView tv = contentLayout.getMessageView();
        tv.setText(message);
        return this;
    }

    @NonNull
    public TSnackbar setText(@StringRes int resId) {
        return this.setText(this.getContext().getText(resId));
    }

    @NonNull
    public TSnackbar setAction(@StringRes int resId, OnClickListener listener) {
        return this.setAction(this.getContext().getText(resId), listener);
    }

    @NonNull
    public TSnackbar setAction(CharSequence text, final OnClickListener listener) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout)this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        if (!TextUtils.isEmpty(text) && listener != null) {
            this.hasAction = true;
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    listener.onClick(view);
                    TSnackbar.this.dispatchDismiss(1);
                }
            });
        } else {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener((OnClickListener)null);
            this.hasAction = false;
        }

        return this;
    }

    public int getDuration() {
        return this.hasAction && this.accessibilityManager.isTouchExplorationEnabled() ? -2 : super.getDuration();
    }

    @NonNull
    public TSnackbar setActionTextColor(ColorStateList colors) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout)this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        tv.setTextColor(colors);
        return this;
    }

    @NonNull
    public TSnackbar setActionTextColor(@ColorInt int color) {
        SnackbarContentLayout contentLayout = (SnackbarContentLayout)this.view.getChildAt(0);
        TextView tv = contentLayout.getActionView();
        tv.setTextColor(color);
        return this;
    }

    /** @deprecated */
    @Deprecated
    @NonNull
    public TSnackbar setCallback(TSnackbar.Callback callback) {
        if (this.callback != null) {
            this.removeCallback(this.callback);
        }

        if (callback != null) {
            this.addCallback(callback);
        }

        this.callback = callback;
        return this;
    }

    static {
        SNACKBAR_BUTTON_STYLE_ATTR = new int[]{com.google.android.material.R.attr.snackbarButtonStyle};
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final class SnackbarLayout extends SnackbarBaseLayout {
        public SnackbarLayout(Context context) {
            super(context);
        }

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int childCount = this.getChildCount();
            int availableWidth = this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();

            for(int i = 0; i < childCount; ++i) {
                View child = this.getChildAt(i);
                if (child.getLayoutParams().width == -1) {
                    child.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.AT_MOST));
                }
            }

        }
    }

    public static class Callback extends BaseCallback<TSnackbar> {
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        public Callback() {
        }

        public void onShown(TSnackbar sb) {
        }

        public void onDismissed(TSnackbar transientBottomBar, int event) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({Scope.LIBRARY_GROUP})
    @IntRange(
        from = 1L
    )
    public @interface Duration {
    }
}
