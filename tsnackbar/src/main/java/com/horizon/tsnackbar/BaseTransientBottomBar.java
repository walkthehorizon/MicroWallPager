//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.horizon.tsnackbar;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.accessibility.AccessibilityManagerCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityManagerCompat.TouchExplorationStateChangeListener;
import com.google.android.material.R.attr;
import com.google.android.material.R.styleable;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.behavior.SwipeDismissBehavior.OnDismissListener;
import com.google.android.material.internal.ThemeEnforcement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.view.ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE;
import static androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES;

public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>> {
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_SHORT = -1;
    public static final int LENGTH_LONG = 0;
    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    static final Handler handler;
    static final int MSG_SHOW = 0;
    static final int MSG_DISMISS = 1;
    private static final boolean USE_OFFSET_API;
    private static final int[] SNACKBAR_STYLE_ATTR;
    private final ViewGroup targetParent;
    private final Context context;
    protected final BaseTransientBottomBar.SnackbarBaseLayout view;
    private final ContentViewCallback contentViewCallback;
    private int duration;
    private List<BaseTransientBottomBar.BaseCallback<B>> callbacks;
    private BaseTransientBottomBar.Behavior behavior;
    private final AccessibilityManager accessibilityManager;
    final SnackbarManager.Callback managerCallback = new SnackbarManager.Callback() {
        public void show() {
            BaseTransientBottomBar.handler.sendMessage(BaseTransientBottomBar.handler.obtainMessage(0, BaseTransientBottomBar.this));
        }

        public void dismiss(int event) {
            BaseTransientBottomBar.handler.sendMessage(BaseTransientBottomBar.handler.obtainMessage(1, event, 0, BaseTransientBottomBar.this));
        }
    };

    @SuppressLint("RestrictedApi")
    protected BaseTransientBottomBar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        if (parent == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null parent");
        } else if (content == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null content");
        } else if (contentViewCallback == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null callback");
        } else {
            this.targetParent = parent;
            this.contentViewCallback = contentViewCallback;
            this.context = parent.getContext();
            ThemeEnforcement.checkAppCompatTheme(this.context);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            this.view = (BaseTransientBottomBar.SnackbarBaseLayout)inflater.inflate(this.getSnackbarBaseLayoutResId(), this.targetParent, false);
            this.view.addView(content);
            ViewCompat.setAccessibilityLiveRegion(this.view, ACCESSIBILITY_LIVE_REGION_POLITE);
            ViewCompat.setImportantForAccessibility(this.view, IMPORTANT_FOR_ACCESSIBILITY_YES);
            ViewCompat.setFitsSystemWindows(this.view, true);
            ViewCompat.setOnApplyWindowInsetsListener(this.view, new OnApplyWindowInsetsListener() {
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.getSystemWindowInsetBottom());
                    return insets;
                }
            });
            ViewCompat.setAccessibilityDelegate(this.view, new AccessibilityDelegateCompat() {
                public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(host, info);
                    info.addAction(1048576);
                    info.setDismissable(true);
                }

                public boolean performAccessibilityAction(View host, int action, Bundle args) {
                    if (action == 1048576) {
                        BaseTransientBottomBar.this.dismiss();
                        return true;
                    } else {
                        return super.performAccessibilityAction(host, action, args);
                    }
                }
            });
            this.accessibilityManager = (AccessibilityManager)this.context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        }
    }

    @LayoutRes
    protected int getSnackbarBaseLayoutResId() {
        return R.layout.design_layout_snackbar;
    }

    @NonNull
    public BaseTransientBottomBar<B> setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getDuration() {
        return this.duration;
    }

    public BaseTransientBottomBar<B> setBehavior(BaseTransientBottomBar.Behavior behavior) {
        this.behavior = behavior;
        return this;
    }

    public BaseTransientBottomBar.Behavior getBehavior() {
        return this.behavior;
    }

    @NonNull
    public Context getContext() {
        return this.context;
    }

    @NonNull
    public View getView() {
        return this.view;
    }

    public void show() {
        SnackbarManager.getInstance().show(this.getDuration(), this.managerCallback);
    }

    public void dismiss() {
        this.dispatchDismiss(3);
    }

    protected void dispatchDismiss(int event) {
        SnackbarManager.getInstance().dismiss(this.managerCallback, event);
    }

    @NonNull
    public BaseTransientBottomBar<B> addCallback(@NonNull BaseTransientBottomBar.BaseCallback<B> callback) {
        if (callback == null) {
            return this;
        } else {
            if (this.callbacks == null) {
                this.callbacks = new ArrayList();
            }

            this.callbacks.add(callback);
            return this;
        }
    }

    @NonNull
    public BaseTransientBottomBar<B> removeCallback(@NonNull BaseTransientBottomBar.BaseCallback<B> callback) {
        if (callback == null) {
            return this;
        } else if (this.callbacks == null) {
            return this;
        } else {
            this.callbacks.remove(callback);
            return this;
        }
    }

    public boolean isShown() {
        return SnackbarManager.getInstance().isCurrent(this.managerCallback);
    }

    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance().isCurrentOrNext(this.managerCallback);
    }

    protected SwipeDismissBehavior<? extends View> getNewBehavior() {
        return new BaseTransientBottomBar.Behavior();
    }

    final void showView() {
        if (this.view.getParent() == null) {
            LayoutParams lp = this.view.getLayoutParams();
            if (lp instanceof androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) {
                androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams clp = (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)lp;
                SwipeDismissBehavior<? extends View> behavior = this.behavior == null ? this.getNewBehavior() : this.behavior;
                if (behavior instanceof BaseTransientBottomBar.Behavior) {
                    ((BaseTransientBottomBar.Behavior)behavior).setBaseTransientBottomBar(this);
                }

                behavior.setListener(new OnDismissListener() {
                    public void onDismiss(View view) {
                        view.setVisibility(View.GONE);
                        BaseTransientBottomBar.this.dispatchDismiss(0);
                    }

                    public void onDragStateChanged(int state) {
                        switch(state) {
                        case 0:
                            SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.managerCallback);
                            break;
                        case 1:
                        case 2:
                            SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.managerCallback);
                        }

                    }
                });
                clp.setBehavior(behavior);
                clp.insetEdge = 80;
            }

            this.targetParent.addView(this.view);
        }

        this.view.setOnAttachStateChangeListener(new BaseTransientBottomBar.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {
            }

            public void onViewDetachedFromWindow(View v) {
                if (BaseTransientBottomBar.this.isShownOrQueued()) {
                    BaseTransientBottomBar.handler.post(new Runnable() {
                        public void run() {
                            BaseTransientBottomBar.this.onViewHidden(3);
                        }
                    });
                }

            }
        });
        if (ViewCompat.isLaidOut(this.view)) {
            if (this.shouldAnimate()) {
                this.animateViewIn();
            } else {
                this.onViewShown();
            }
        } else {
            this.view.setOnLayoutChangeListener(new BaseTransientBottomBar.OnLayoutChangeListener() {
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    BaseTransientBottomBar.this.view.setOnLayoutChangeListener(null);
                    if (BaseTransientBottomBar.this.shouldAnimate()) {
                        BaseTransientBottomBar.this.animateViewIn();
                    } else {
                        BaseTransientBottomBar.this.onViewShown();
                    }

                }
            });
        }

    }

    void animateViewIn() {
        final int translationYBottom = this.getTranslationYBottom();
        if (USE_OFFSET_API) {
            ViewCompat.offsetTopAndBottom(this.view, translationYBottom);
        } else {
            this.view.setTranslationY((float)translationYBottom);
        }

        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(translationYBottom, 0);
        animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.setDuration(250L);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                BaseTransientBottomBar.this.contentViewCallback.animateContentIn(70, 180);
            }

            public void onAnimationEnd(Animator animator) {
                BaseTransientBottomBar.this.onViewShown();
            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {
            private int previousAnimatedIntValue = translationYBottom;

            public void onAnimationUpdate(ValueAnimator animator) {
                int currentAnimatedIntValue = (Integer)animator.getAnimatedValue();
                if (BaseTransientBottomBar.USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.view, currentAnimatedIntValue - this.previousAnimatedIntValue);
                } else {
                    BaseTransientBottomBar.this.view.setTranslationY((float)currentAnimatedIntValue);
                }

                this.previousAnimatedIntValue = currentAnimatedIntValue;
            }
        });
        animator.start();
    }

    private void animateViewOut(final int event) {
        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(0, -this.getTranslationYBottom());
        animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.setDuration(250L);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                BaseTransientBottomBar.this.contentViewCallback.animateContentOut(0, 180);
            }

            public void onAnimationEnd(Animator animator) {
                BaseTransientBottomBar.this.onViewHidden(event);
            }
        });
        animator.addUpdateListener(new AnimatorUpdateListener() {
            private int previousAnimatedIntValue = 0;

            public void onAnimationUpdate(ValueAnimator animator) {
                int currentAnimatedIntValue = (Integer)animator.getAnimatedValue();
                if (BaseTransientBottomBar.USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.view, currentAnimatedIntValue - this.previousAnimatedIntValue);
                } else {
                    BaseTransientBottomBar.this.view.setTranslationY((float)currentAnimatedIntValue);
                }

                this.previousAnimatedIntValue = currentAnimatedIntValue;
            }
        });
        animator.start();
    }

    private int getTranslationYBottom() {
        int translationY = this.view.getHeight();
        LayoutParams layoutParams = this.view.getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams) {
            translationY += ((MarginLayoutParams)layoutParams).bottomMargin;
        }

        return translationY;
    }

    final void hideView(int event) {
        if (this.shouldAnimate() && this.view.getVisibility() == View.VISIBLE) {
            this.animateViewOut(event);
        } else {
            this.onViewHidden(event);
        }

    }

    void onViewShown() {
        SnackbarManager.getInstance().onShown(this.managerCallback);
        if (this.callbacks != null) {
            int callbackCount = this.callbacks.size();

            for(int i = callbackCount - 1; i >= 0; --i) {
                ((BaseTransientBottomBar.BaseCallback)this.callbacks.get(i)).onShown(this);
            }
        }

    }

    void onViewHidden(int event) {
        SnackbarManager.getInstance().onDismissed(this.managerCallback);
        if (this.callbacks != null) {
            int callbackCount = this.callbacks.size();

            for(int i = callbackCount - 1; i >= 0; --i) {
                ((BaseTransientBottomBar.BaseCallback)this.callbacks.get(i)).onDismissed(this, event);
            }
        }

        ViewParent parent = this.view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup)parent).removeView(this.view);
        }

    }

    boolean shouldAnimate() {
        List<AccessibilityServiceInfo> serviceList = this.accessibilityManager.getEnabledAccessibilityServiceList(1);
        return serviceList != null && serviceList.isEmpty();
    }

    static {
        USE_OFFSET_API = VERSION.SDK_INT >= 16 && VERSION.SDK_INT <= 19;
        SNACKBAR_STYLE_ATTR = new int[]{attr.snackbarStyle};
        handler = new Handler(Looper.getMainLooper(), new android.os.Handler.Callback() {
            public boolean handleMessage(Message message) {
                switch(message.what) {
                case 0:
                    ((BaseTransientBottomBar)message.obj).showView();
                    return true;
                case 1:
                    ((BaseTransientBottomBar)message.obj).hideView(message.arg1);
                    return true;
                default:
                    return false;
                }
            }
        });
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class BehaviorDelegate {
        private SnackbarManager.Callback managerCallback;

        public BehaviorDelegate(SwipeDismissBehavior<?> behavior) {
            behavior.setStartAlphaSwipeDistance(0.1F);
            behavior.setEndAlphaSwipeDistance(0.6F);
            behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
        }

        public void setBaseTransientBottomBar(BaseTransientBottomBar<?> baseTransientBottomBar) {
            this.managerCallback = baseTransientBottomBar.managerCallback;
        }

        public boolean canSwipeDismissView(View child) {
            return child instanceof BaseTransientBottomBar.SnackbarBaseLayout;
        }

        public void onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
            switch(event.getActionMasked()) {
            case 0:
                if (parent.isPointInChildBounds(child, (int)event.getX(), (int)event.getY())) {
                    SnackbarManager.getInstance().pauseTimeout(this.managerCallback);
                }
                break;
            case 1:
            case 3:
                SnackbarManager.getInstance().restoreTimeoutIfPaused(this.managerCallback);
            case 2:
            }

        }
    }

    public static class Behavior extends SwipeDismissBehavior<View> {
        private final BaseTransientBottomBar.BehaviorDelegate delegate = new BaseTransientBottomBar.BehaviorDelegate(this);

        public Behavior() {
        }

        private void setBaseTransientBottomBar(BaseTransientBottomBar<?> baseTransientBottomBar) {
            this.delegate.setBaseTransientBottomBar(baseTransientBottomBar);
        }

        public boolean canSwipeDismissView(View child) {
            return this.delegate.canSwipeDismissView(child);
        }

        public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
            this.delegate.onInterceptTouchEvent(parent, child, event);
            return super.onInterceptTouchEvent(parent, child, event);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected static class SnackbarBaseLayout extends FrameLayout {
        private final AccessibilityManager accessibilityManager;
        private final TouchExplorationStateChangeListener touchExplorationStateChangeListener;
        private BaseTransientBottomBar.OnLayoutChangeListener onLayoutChangeListener;
        private BaseTransientBottomBar.OnAttachStateChangeListener onAttachStateChangeListener;

        protected SnackbarBaseLayout(Context context) {
            this(context, null);
        }

        @SuppressLint("WrongConstant")
        protected SnackbarBaseLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, styleable.SnackbarLayout);
            if (a.hasValue(styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, (float)a.getDimensionPixelSize(styleable.SnackbarLayout_elevation, 0));
            }

            a.recycle();
            this.accessibilityManager = (AccessibilityManager)context.getSystemService("accessibility");
            this.touchExplorationStateChangeListener = new TouchExplorationStateChangeListener() {
                public void onTouchExplorationStateChanged(boolean enabled) {
                    SnackbarBaseLayout.this.setClickableOrFocusableBasedOnAccessibility(enabled);
                }
            };
            AccessibilityManagerCompat.addTouchExplorationStateChangeListener(this.accessibilityManager, this.touchExplorationStateChangeListener);
            this.setClickableOrFocusableBasedOnAccessibility(this.accessibilityManager.isTouchExplorationEnabled());
        }

        private void setClickableOrFocusableBasedOnAccessibility(boolean touchExplorationEnabled) {
            this.setClickable(!touchExplorationEnabled);
            this.setFocusable(touchExplorationEnabled);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.onLayoutChangeListener != null) {
                this.onLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }

        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.onAttachStateChangeListener != null) {
                this.onAttachStateChangeListener.onViewAttachedToWindow(this);
            }

            ViewCompat.requestApplyInsets(this);
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.onAttachStateChangeListener != null) {
                this.onAttachStateChangeListener.onViewDetachedFromWindow(this);
            }

            AccessibilityManagerCompat.removeTouchExplorationStateChangeListener(this.accessibilityManager, this.touchExplorationStateChangeListener);
        }

        void setOnLayoutChangeListener(BaseTransientBottomBar.OnLayoutChangeListener onLayoutChangeListener) {
            this.onLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(BaseTransientBottomBar.OnAttachStateChangeListener listener) {
            this.onAttachStateChangeListener = listener;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View var1);

        void onViewDetachedFromWindow(View var1);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected interface OnLayoutChangeListener {
        void onLayoutChange(View var1, int var2, int var3, int var4, int var5);
    }

    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({Scope.LIBRARY_GROUP})
    @IntRange(
        from = 1L
    )
    public @interface Duration {
    }

    /** @deprecated */
    @Deprecated
    public interface ContentViewCallback extends com.horizon.tsnackbar.ContentViewCallback {
    }

    public abstract static class BaseCallback<B> {
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        public BaseCallback() {
        }

        public void onDismissed(B transientBottomBar, int event) {
        }

        public void onShown(B transientBottomBar) {
        }

        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({Scope.LIBRARY_GROUP})
        public @interface DismissEvent {
        }
    }
}
