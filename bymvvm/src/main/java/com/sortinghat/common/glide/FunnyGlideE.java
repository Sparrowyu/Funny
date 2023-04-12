package com.sortinghat.common.glide;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import java.io.File;

/**
 * The entry point for interacting with Glide for Applications
 *
 * <p>Includes all generated APIs from all
 * {@link com.bumptech.glide.annotation.GlideExtension}s in source and dependent libraries.
 *
 * <p>This class is generated and should not be modified
 * @see Glide
 */
public final class FunnyGlideE {
    private FunnyGlideE() {
    }

    /**
     * @see Glide#getPhotoCacheDir(Context)
     */
    @Nullable
    public static File getPhotoCacheDir(@NonNull Context context) {
        return Glide.getPhotoCacheDir(context);
    }

    /**
     * @see Glide#getPhotoCacheDir(Context, String)
     */
    @Nullable
    public static File getPhotoCacheDir(@NonNull Context context, @NonNull String string) {
        return Glide.getPhotoCacheDir(context, string);
    }

    /**
     * @see Glide#get(Context)
     */
    @NonNull
    public static Glide get(@NonNull Context context) {
        return Glide.get(context);
    }

    /**
     * @see Glide#init(Glide)
     */
    @Deprecated
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    public static void init(Glide glide) {
        Glide.init(glide);
    }

    /**
     * @see Glide#init(Context, GlideBuilder)
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    public static void init(@NonNull Context context, @NonNull GlideBuilder builder) {
        Glide.init(context, builder);
    }

    /**
     * @see Glide#enableHardwareBitmaps()
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    public static void enableHardwareBitmaps() {
        Glide.enableHardwareBitmaps();
    }

    /**
     * @see Glide#tearDown()
     */
    @VisibleForTesting
    @SuppressLint("VisibleForTests")
    public static void tearDown() {
        Glide.tearDown();
    }

    /**
     * @see Glide#with(Context)
     */
    @NonNull
    public static GlideRequestsS with(@NonNull Context context) {
        return (GlideRequestsS) Glide.with(context);
    }

    /**
     * @see Glide#with(Activity)
     */
    @NonNull
    public static GlideRequestsS with(@NonNull Activity activity) {
        return (GlideRequestsS) Glide.with(activity);
    }

    /**
     * @see Glide#with(FragmentActivity)
     */
    @NonNull
    public static GlideRequestsS with(@NonNull FragmentActivity activity) {
        return (GlideRequestsS) Glide.with(activity);
    }

    /**
     * @see Glide#with(Fragment)
     */
    @NonNull
    public static GlideRequestsS with(@NonNull Fragment fragment) {
        return (GlideRequestsS) Glide.with(fragment);
    }

    /**
     * @see Glide#with(Fragment)
     */
    @Deprecated
    @NonNull
    public static GlideRequestsS with(@NonNull android.app.Fragment fragment) {
        return (GlideRequestsS) Glide.with(fragment);
    }

    /**
     * @see Glide#with(View)
     */
    @NonNull
    public static GlideRequestsS with(@NonNull View view) {
        return (GlideRequestsS) Glide.with(view);
    }
}

