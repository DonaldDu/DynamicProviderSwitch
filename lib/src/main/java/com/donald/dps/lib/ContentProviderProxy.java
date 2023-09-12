
package com.donald.dps.lib;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import java.io.FileNotFoundException;
import java.util.ArrayList;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ContentProviderProxy extends ContentProvider {
    static final String TAG = "ContentProviderProxy";
    private ContentProvider realContentProvider;
    private ProviderInfo providerInfo;
    public String realContentProviderClassName;

    protected ContentProvider getRealContentProvider() {
        return realContentProvider;
    }

    protected void createAndActivateRealContentProvider(ClassLoader classLoader) {
        if (realContentProviderClassName == null) {
            throw new IllegalArgumentException("Unable to read real content-provider for " + getClass().getName());
        }
        try {
            realContentProvider = (ContentProvider) classLoader.loadClass(realContentProviderClassName).newInstance();
            realContentProvider.attachInfo(getContext(), providerInfo);
        } catch (Exception e) {
            if (!(e instanceof ClassNotFoundException)) e.printStackTrace();
        }
    }

    @Override
    public boolean onCreate() {
        if (BuildConfig.DEBUG) Log.i(TAG, "ProviderProxy onCreate ->" + realContentProviderClassName);
        return true;
    }

    protected boolean isRealProviderOK() {
        if (realContentProvider != null) {
            return true;
        } else {
            createAndActivateRealContentProvider(getClass().getClassLoader());
            return realContentProvider != null;
        }
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        realContentProviderClassName = info.name;
        providerInfo = info;
        super.attachInfo(context, info);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isRealProviderOK()) {
            realContentProvider.onConfigurationChanged(newConfig);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (isRealProviderOK()) {
            return realContentProvider.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable Bundle queryArgs, @Nullable CancellationSignal cancellationSignal) {
        if (isRealProviderOK()) {
            return realContentProvider.query(uri, projection, queryArgs, cancellationSignal);
        }
        return super.query(uri, projection, queryArgs, cancellationSignal);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @Nullable CancellationSignal cancellationSignal) {
        if (isRealProviderOK()) {
            return realContentProvider.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
        }
        return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (isRealProviderOK()) {
            return realContentProvider.getType(uri);
        }
        return null;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        if (isRealProviderOK()) {
            return realContentProvider.applyBatch(operations);
        }
        return super.applyBatch(operations);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Uri canonicalize(@NonNull Uri url) {
        if (getRealContentProvider() != null) {
            return realContentProvider.canonicalize(url);
        }
        return super.canonicalize(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Uri uncanonicalize(@NonNull Uri url) {
        if (isRealProviderOK()) {
            return realContentProvider.uncanonicalize(url);
        }
        return super.uncanonicalize(url);
    }

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openAssetFile(uri, mode);
        }
        return super.openAssetFile(uri, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode, @Nullable CancellationSignal signal) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openAssetFile(uri, mode, signal);
        }
        return super.openAssetFile(uri, mode, signal);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public AssetFileDescriptor openTypedAssetFile(@NonNull Uri uri, @NonNull String mimeTypeFilter, @Nullable Bundle opts) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openTypedAssetFile(uri, mimeTypeFilter, opts);
        }
        return super.openTypedAssetFile(uri, mimeTypeFilter, opts);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public AssetFileDescriptor openTypedAssetFile(@NonNull Uri uri, @NonNull String mimeTypeFilter, @Nullable Bundle opts, @Nullable CancellationSignal signal) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openTypedAssetFile(uri, mimeTypeFilter, opts, signal);
        }
        return super.openTypedAssetFile(uri, mimeTypeFilter, opts, signal);
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openFile(uri, mode);
        }
        return super.openFile(uri, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode, @Nullable CancellationSignal signal) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openFile(uri, mode, signal);
        }
        return super.openFile(uri, mode, signal);
    }

    @NonNull
    @Override
    public <T> ParcelFileDescriptor openPipeHelper(@NonNull Uri uri, @NonNull String mimeType, @Nullable Bundle opts, @Nullable T args, @NonNull PipeDataWriter<T> func) throws FileNotFoundException {
        if (isRealProviderOK()) {
            return realContentProvider.openPipeHelper(uri, mimeType, opts, args, func);
        }
        return super.openPipeHelper(uri, mimeType, opts, args, func);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean refresh(Uri uri, @Nullable Bundle args, @Nullable CancellationSignal cancellationSignal) {
        if (isRealProviderOK()) {
            return realContentProvider.refresh(uri, args, cancellationSignal);
        }
        return super.refresh(uri, args, cancellationSignal);
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (isRealProviderOK()) {
            return realContentProvider.call(method, arg, extras);
        }
        return super.call(method, arg, extras);
    }

    @Nullable
    @Override
    public String[] getStreamTypes(@NonNull Uri uri, @NonNull String mimeTypeFilter) {
        if (isRealProviderOK()) {
            return realContentProvider.getStreamTypes(uri, mimeTypeFilter);
        }
        return super.getStreamTypes(uri, mimeTypeFilter);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (BuildConfig.DEBUG) Log.i(TAG, "Proxy insert ->authority " + providerInfo.authority);
        if (isRealProviderOK()) {
            return realContentProvider.insert(uri, values);
        }
        return null;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (realContentProvider != null) {
            realContentProvider.onTrimMemory(level);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (realContentProvider != null) {
            realContentProvider.onLowMemory();
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (isRealProviderOK()) {
            return realContentProvider.bulkInsert(uri, values);
        }
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (isRealProviderOK()) {
            return realContentProvider.delete(uri, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (isRealProviderOK()) {
            return realContentProvider.update(uri, values, selection, selectionArgs);
        }
        return 0;
    }
}
