package com.brentvatne.exoplayer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;

import com.brentvatne.common.utils.ExoUserConfig;
import com.npaw.media3.exoplayer.Media3ExoPlayerAdapter;
import com.npaw.shared.extensions.Logger;

import java.util.HashMap;
import java.util.Map;


public class YouboraCustomAdapter extends Media3ExoPlayerAdapter {
    private static final String TAG = "CustomAdapterYouboraExo";
    private boolean skipStateChangedIdle = false;
    private ReactExoplayerView view;

    Logger log = new Logger(TAG, false); // Should check DEV or PROD
    public YouboraCustomAdapter(@Nullable Context context, @Nullable ExoPlayer player, ReactExoplayerView view) {
        super(context, player);
        log = new Logger(TAG, false);
        this.view = view;
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if(playbackState == Player.STATE_IDLE) {
            stateChangedIdle();
            log.debug("onPlaybackStateChanged: STATE_IDLE");
        } else {
            super.onPlaybackStateChanged(playbackState);
        }
    }

    protected void stateChangedIdle() {
        if (!skipStateChangedIdle) {
            fireStop();
        }

        skipStateChangedIdle = false;
    }

    @Override
    public void onPlayerError(@NonNull PlaybackException error) {
        PlaybackException exoPlaybackException =  error;
        ExoUserConfig exoUserConfig = ExoUserConfig.currentConfig;

        Throwable innerErrorCause = exoPlaybackException.getCause();
        String innerErrorMessage = exoPlaybackException.getMessage();
        String sourceErrorCauseMessage = "UNKNOWN INNER CAUSE";

        if(
                view.errorRetries < exoUserConfig.maxRetries &&
                        exoUserConfig.isHandledError(error.errorCode)
        ) {
            view.errorRetries++;
            fireStop();
            return;
        }

        if (innerErrorCause != null) {
            sourceErrorCauseMessage = innerErrorCause.toString();
        }

        String extraErrorDetails = String.format("Message: %s | Cause: %s", innerErrorMessage, sourceErrorCauseMessage);

        Map<String, String> customErrorEventMap = new HashMap<String, String>();
        customErrorEventMap.put("details", extraErrorDetails);

        fireEvent("CUSTOM_PLAYER_ERROR", customErrorEventMap);

        if (error instanceof PlaybackException && error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED) {
            String errorClass = exoPlaybackException.getClass().getSimpleName();

            switch (errorClass) {
                case "InvalidResponseCodeException", "HttpDataSourceException":
                    invalidResponseCodeException(exoPlaybackException);
                    break;
                case "BehindLiveWindowException":
                    fireError(String.valueOf(error.errorCode), error.getMessage(), "");
                    break;
                case "DrmSessionException":
                    handleDRMSessionExceptions(exoPlaybackException);
                    break;
                default:
                    fireFatalError(String.valueOf(error.errorCode), error.getMessage() + ", Error Class : " + errorClass, extraErrorDetails);
                    break;
            }
        } else {
            if(error.errorCode >= 4000 && error.errorCode < 5000) {
                fireStop();
            } else {
                fireFatalError(String.valueOf(error.errorCode), error.getMessage(), extraErrorDetails);
            }
        }

        skipStateChangedIdle = true;
        log.debug("onPlayerError: " + error);
    }

    private void invalidResponseCodeException(PlaybackException error) {
        HttpDataSource.InvalidResponseCodeException invalidResponseCodeException =
                (HttpDataSource.InvalidResponseCodeException) error.getCause();

        String failedURL = invalidResponseCodeException.dataSpec.uri.toString();

        Map<String, String> dimMap = new HashMap<>();
        dimMap.put("failedURL", failedURL);

        fireEvent("Failed Source", dimMap);

        fireFatalError(
                String.valueOf(error.errorCode),
                error.getMessage() + ", " + invalidResponseCodeException.toString(),
                failedURL
        );
    }

    private void handleDRMSessionExceptions(PlaybackException error) {
        String sourceExceptionMessage = error.getMessage();

        Map<String, String> dimMap = new HashMap<>();
        dimMap.put("HUT", ReactExoplayerView.drmUserToken);

        fireEvent("HUT", dimMap);

        fireFatalError(
                String.valueOf(error.errorCode),
                sourceExceptionMessage,
                ReactExoplayerView.drmUserToken
        );
    }
}
