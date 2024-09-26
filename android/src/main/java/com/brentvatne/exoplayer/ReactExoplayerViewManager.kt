package com.brentvatne.exoplayer

import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.brentvatne.common.api.BufferConfig
import com.brentvatne.common.api.BufferingStrategy
import com.brentvatne.common.api.ControlsConfig
import com.brentvatne.common.api.ResizeMode
import com.brentvatne.common.api.Source
import com.brentvatne.common.api.SubtitleStyle
import com.brentvatne.common.api.ViewType
import com.brentvatne.common.react.EventTypes
import com.brentvatne.common.toolbox.DebugLog
import com.brentvatne.common.toolbox.ReactBridgeUtils
import com.brentvatne.react.ReactNativeVideoManager
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.npaw.core.options.AnalyticsOptions


class ReactExoplayerViewManager(private val config: ReactExoplayerConfig) : ViewGroupManager<ReactExoplayerView>() {

    companion object {
        private const val TAG = "ExoViewManager"
        private const val REACT_CLASS = "RCTVideo"
        private const val PROP_SRC = "src"
        private const val PROP_AD_TAG_URL = "adTagUrl"
        private const val PROP_AD_LANGUAGE = "adLanguage"
        private const val PROP_RESIZE_MODE = "resizeMode"
        private const val PROP_REPEAT = "repeat"
        private const val PROP_SELECTED_AUDIO_TRACK = "selectedAudioTrack"
        private const val PROP_SELECTED_AUDIO_TRACK_TYPE = "type"
        private const val PROP_SELECTED_AUDIO_TRACK_VALUE = "value"
        private const val PROP_SELECTED_TEXT_TRACK = "selectedTextTrack"
        private const val PROP_SELECTED_TEXT_TRACK_TYPE = "type"
        private const val PROP_SELECTED_TEXT_TRACK_VALUE = "value"
        private const val PROP_PAUSED = "paused"
        private const val PROP_MUTED = "muted"
        private const val PROP_AUDIO_OUTPUT = "audioOutput"
        private const val PROP_VOLUME = "volume"
        private const val PROP_BUFFER_CONFIG = "bufferConfig"
        private const val PROP_PREVENTS_DISPLAY_SLEEP_DURING_VIDEO_PLAYBACK =
            "preventsDisplaySleepDuringVideoPlayback"
        private const val PROP_PROGRESS_UPDATE_INTERVAL = "progressUpdateInterval"
        private const val PROP_REPORT_BANDWIDTH = "reportBandwidth"
        private const val PROP_RATE = "rate"
        private const val PROP_MIN_LOAD_RETRY_COUNT = "minLoadRetryCount"
        private const val PROP_MAXIMUM_BIT_RATE = "maxBitRate"
        private const val PROP_PLAY_IN_BACKGROUND = "playInBackground"
        private const val PROP_DISABLE_FOCUS = "disableFocus"
        private const val PROP_BUFFERING_STRATEGY = "bufferingStrategy"
        private const val PROP_DISABLE_DISCONNECT_ERROR = "disableDisconnectError"
        private const val PROP_FOCUSABLE = "focusable"
        private const val PROP_FULLSCREEN = "fullscreen"
        private const val PROP_VIEW_TYPE = "viewType"
        private const val PROP_SELECTED_VIDEO_TRACK = "selectedVideoTrack"
        private const val PROP_SELECTED_VIDEO_TRACK_TYPE = "type"
        private const val PROP_SELECTED_VIDEO_TRACK_VALUE = "value"
        private const val PROP_HIDE_SHUTTER_VIEW = "hideShutterView"
        private const val PROP_CONTROLS = "controls"
        private const val PROP_SUBTITLE_STYLE = "subtitleStyle"
        private const val PROP_SHUTTER_COLOR = "shutterColor"
        private const val PROP_SHOW_NOTIFICATION_CONTROLS = "showNotificationControls"
        private const val PROP_DEBUG = "debug"
        private const val PROP_CONTROLS_STYLES = "controlsStyles"

        // Youbora Props
        private const val PROP_ENABLE_CDN_BALANCER = "enableCdnBalancer";
        private const val PROP_YOUBORA_FIRE_EVENT: String = "youboraFireEvent"
        private const val PROP_YOUBORA_PARAMS: String = "youboraParams"
        private const val PROP_YOUBORA_ACCOUNT_CODE: String = "accountCode"
        private const val PROP_YOUBORA_USERNAME: String = "username"
        private const val PROP_YOUBORA_CONTENT_TRANSACTION_CODE: String = "contentTransactionCode"
        private const val PROP_YOUBORA_IS_LIVE: String = "isLive"
        private const val PROP_YOUBORA_PARSE_CDN_NODE: String = "parseCdnNode"
        private const val PROP_YOUBORA_ENABLED: String = "enabled"
        private const val PROP_YOUBORA_TITLE: String = "title"
        private const val PROP_YOUBORA_PROGRAM: String = "program"
        private const val PROP_YOUBORA_TV_SHOW: String = "tvShow"
        private const val PROP_YOUBORA_SEASON: String = "season"
        private const val PROP_YOUBORA_CONTENT_TYPE: String = "contentType"
        private const val PROP_YOUBORA_CONTENT_ID: String = "contentId"
        private const val PROP_YOUBORA_CONTENT_PLAYBACK_TYPE: String = "contentPlaybackType"
        private const val PROP_YOUBORA_CONTENT_PACKAGE: String = "contentPackage"
        private const val PROP_YOUBORA_CONTENT_DURATION: String = "contentDuration"
        private const val PROP_YOUBORA_CONTENT_DRM: String = "contentDrm"
        private const val PROP_YOUBORA_CONTENT_RESOURCE: String = "contentResource"
        private const val PROP_YOUBORA_CONTENT_GENRE: String = "contentGenre"
        private const val PROP_YOUBORA_CONTENT_LANGUAGE: String = "contentLanguage"
        private const val PROP_YOUBORA_CONTENT_CHANNELS: String = "contentChannels"
        private const val PROP_YOUBORA_CONTENT_STREAMING_PROTOCOL: String = "contentStreamingProtocol"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_1: String = "contentCustomDimension1"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_2: String = "contentCustomDimension2"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_3: String = "contentCustomDimension3"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_4: String = "contentCustomDimension4"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_5: String = "contentCustomDimension5"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_6: String = "contentCustomDimension6"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_7: String = "contentCustomDimension7"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_8: String = "contentCustomDimension8"
        private const val PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_9: String = "contentCustomDimension9"
        private const val PROP_YOUBORA_RENDITION: String = "rendition"
        private const val PROP_YOUBORA_USER_TYPE: String = "userType"
        private const val PROP_YOUBORA_APP_NAME: String = "appName"
        private const val PROP_YOUBORA_RELEASE_VERSION: String = "releaseVersion"
        private const val PROP_LANGUAGE: String = "language"
        private const val PROP_AD_BREAK_POINT: String = "adsBreakPoints";
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(themedReactContext: ThemedReactContext): ReactExoplayerView {
        ReactNativeVideoManager.getInstance().registerView(this)
        return ReactExoplayerView(themedReactContext, config)
    }

    override fun onDropViewInstance(view: ReactExoplayerView) {
        view.cleanUpResources()
        ReactNativeVideoManager.getInstance().unregisterView(this)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> = EventTypes.toMap()

    override fun addEventEmitters(reactContext: ThemedReactContext, view: ReactExoplayerView) {
        super.addEventEmitters(reactContext, view)
        view.eventEmitter.addEventEmitters(reactContext, view)
    }

    @ReactProp(name = PROP_SRC)
    fun setSrc(videoView: ReactExoplayerView, src: ReadableMap?) {
        val context = videoView.context.applicationContext
        val source = Source.parse(src, context)
        if (source.uri == null) {
            videoView.clearSrc()
        } else {
            videoView.setSrc(source)
        }
    }

    @ReactProp(name = PROP_AD_TAG_URL)
    fun setAdTagUrl(videoView: ReactExoplayerView, uriString: String?) {
        if (TextUtils.isEmpty(uriString)) {
            videoView.setAdTagUrl(null)
            return
        }
        val adTagUrl = Uri.parse(uriString)
        videoView.setAdTagUrl(adTagUrl)
    }

    @ReactProp(name = PROP_AD_LANGUAGE)
    fun setAdLanguage(videoView: ReactExoplayerView, languageString: String?) {
        if (TextUtils.isEmpty(languageString)) {
            videoView.setAdLanguage(null) // Maybe "en" default?
            return
        }

        videoView.setAdLanguage(languageString)
    }

    @ReactProp(name = PROP_RESIZE_MODE)
    fun setResizeMode(videoView: ReactExoplayerView, resizeMode: String) {
        when (resizeMode) {
            "none", "contain" -> videoView.setResizeModeModifier(ResizeMode.RESIZE_MODE_FIT)

            "cover" -> videoView.setResizeModeModifier(ResizeMode.RESIZE_MODE_CENTER_CROP)

            "stretch" -> videoView.setResizeModeModifier(ResizeMode.RESIZE_MODE_FILL)

            else -> {
                DebugLog.w(TAG, "Unsupported resize mode: $resizeMode - falling back to fit")
                videoView.setResizeModeModifier(ResizeMode.RESIZE_MODE_FIT)
            }
        }
    }

    @ReactProp(name = PROP_REPEAT, defaultBoolean = false)
    fun setRepeat(videoView: ReactExoplayerView, repeat: Boolean) {
        videoView.setRepeatModifier(repeat)
    }

    @ReactProp(name = PROP_PREVENTS_DISPLAY_SLEEP_DURING_VIDEO_PLAYBACK, defaultBoolean = false)
    fun setPreventsDisplaySleepDuringVideoPlayback(videoView: ReactExoplayerView, preventsSleep: Boolean) {
        videoView.preventsDisplaySleepDuringVideoPlayback = preventsSleep
    }

    @ReactProp(name = PROP_SELECTED_VIDEO_TRACK)
    fun setSelectedVideoTrack(videoView: ReactExoplayerView, selectedVideoTrack: ReadableMap?) {
        var typeString: String? = null
        var value: String? = null
        if (selectedVideoTrack != null) {
            typeString = ReactBridgeUtils.safeGetString(selectedVideoTrack, PROP_SELECTED_VIDEO_TRACK_TYPE)
            value = ReactBridgeUtils.safeGetString(selectedVideoTrack, PROP_SELECTED_VIDEO_TRACK_VALUE)
        }
        videoView.setSelectedVideoTrack(typeString, value)
    }

    @ReactProp(name = PROP_SELECTED_AUDIO_TRACK)
    fun setSelectedAudioTrack(videoView: ReactExoplayerView, selectedAudioTrack: ReadableMap?) {
        var typeString: String? = null
        var value: String? = null
        if (selectedAudioTrack != null) {
            typeString = ReactBridgeUtils.safeGetString(selectedAudioTrack, PROP_SELECTED_AUDIO_TRACK_TYPE)
            value = ReactBridgeUtils.safeGetString(selectedAudioTrack, PROP_SELECTED_AUDIO_TRACK_VALUE)
        }
        videoView.setSelectedAudioTrack(typeString, value)
    }

    @ReactProp(name = PROP_SELECTED_TEXT_TRACK)
    fun setSelectedTextTrack(videoView: ReactExoplayerView, selectedTextTrack: ReadableMap?) {
        var typeString: String? = null
        var value: String? = null
        if (selectedTextTrack != null) {
            typeString = ReactBridgeUtils.safeGetString(selectedTextTrack, PROP_SELECTED_TEXT_TRACK_TYPE)
            value = ReactBridgeUtils.safeGetString(selectedTextTrack, PROP_SELECTED_TEXT_TRACK_VALUE)
        }
        videoView.setSelectedTextTrack(typeString, value)
    }

    @ReactProp(name = PROP_PAUSED, defaultBoolean = false)
    fun setPaused(videoView: ReactExoplayerView, paused: Boolean) {
        videoView.setPausedModifier(paused)
    }

    @ReactProp(name = PROP_MUTED, defaultBoolean = false)
    fun setMuted(videoView: ReactExoplayerView, muted: Boolean) {
        videoView.setMutedModifier(muted)
    }

    @ReactProp(name = PROP_AUDIO_OUTPUT)
    fun setAudioOutput(videoView: ReactExoplayerView, audioOutput: String) {
        videoView.setAudioOutput(AudioOutput.get(audioOutput))
    }

    @ReactProp(name = PROP_VOLUME, defaultFloat = 1.0f)
    fun setVolume(videoView: ReactExoplayerView, volume: Float) {
        videoView.setVolumeModifier(volume)
    }

    @ReactProp(name = PROP_PROGRESS_UPDATE_INTERVAL, defaultFloat = 250.0f)
    fun setProgressUpdateInterval(videoView: ReactExoplayerView, progressUpdateInterval: Float) {
        videoView.setProgressUpdateInterval(progressUpdateInterval)
    }

    @ReactProp(name = PROP_REPORT_BANDWIDTH, defaultBoolean = false)
    fun setReportBandwidth(videoView: ReactExoplayerView, reportBandwidth: Boolean) {
        videoView.setReportBandwidth(reportBandwidth)
    }

    @ReactProp(name = PROP_RATE)
    fun setRate(videoView: ReactExoplayerView, rate: Float) {
        videoView.setRateModifier(rate)
    }

    @ReactProp(name = PROP_MAXIMUM_BIT_RATE)
    fun setMaxBitRate(videoView: ReactExoplayerView, maxBitRate: Float) {
        videoView.setMaxBitRateModifier(maxBitRate.toInt())
    }

    @ReactProp(name = PROP_MIN_LOAD_RETRY_COUNT)
    fun setMinLoadRetryCount(videoView: ReactExoplayerView, minLoadRetryCount: Int) {
        videoView.setMinLoadRetryCountModifier(minLoadRetryCount)
    }

    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    fun setPlayInBackground(videoView: ReactExoplayerView, playInBackground: Boolean) {
        videoView.setPlayInBackground(playInBackground)
    }

    @ReactProp(name = PROP_DISABLE_FOCUS, defaultBoolean = false)
    fun setDisableFocus(videoView: ReactExoplayerView, disableFocus: Boolean) {
        videoView.setDisableFocus(disableFocus)
    }

    @ReactProp(name = PROP_FOCUSABLE, defaultBoolean = true)
    fun setFocusable(videoView: ReactExoplayerView, focusable: Boolean) {
        videoView.setFocusable(focusable)
    }

    @ReactProp(name = PROP_BUFFERING_STRATEGY)
    fun setBufferingStrategy(videoView: ReactExoplayerView, bufferingStrategy: String) {
        val strategy = BufferingStrategy.parse(bufferingStrategy)
        videoView.setBufferingStrategy(strategy)
    }

    @ReactProp(name = PROP_DISABLE_DISCONNECT_ERROR, defaultBoolean = false)
    fun setDisableDisconnectError(videoView: ReactExoplayerView, disableDisconnectError: Boolean) {
        videoView.setDisableDisconnectError(disableDisconnectError)
    }

    @ReactProp(name = PROP_FULLSCREEN, defaultBoolean = false)
    fun setFullscreen(videoView: ReactExoplayerView, fullscreen: Boolean) {
        videoView.setFullscreen(fullscreen)
    }

    @ReactProp(name = PROP_VIEW_TYPE, defaultInt = ViewType.VIEW_TYPE_SURFACE)
    fun setViewType(videoView: ReactExoplayerView, viewType: Int) {
        videoView.setViewType(viewType)
    }

    @ReactProp(name = PROP_HIDE_SHUTTER_VIEW, defaultBoolean = false)
    fun setHideShutterView(videoView: ReactExoplayerView, hideShutterView: Boolean) {
        videoView.setHideShutterView(hideShutterView)
    }

    @ReactProp(name = PROP_CONTROLS, defaultBoolean = false)
    fun setControls(videoView: ReactExoplayerView, controls: Boolean) {
        videoView.setControls(controls)
    }

    @ReactProp(name = PROP_SUBTITLE_STYLE)
    fun setSubtitleStyle(videoView: ReactExoplayerView, src: ReadableMap?) {
        videoView.setSubtitleStyle(SubtitleStyle.parse(src))
    }

    @ReactProp(name = PROP_SHUTTER_COLOR, defaultInt = Color.BLACK)
    fun setShutterColor(videoView: ReactExoplayerView, color: Int) {
        videoView.setShutterColor(color)
    }

    @ReactProp(name = PROP_BUFFER_CONFIG)
    fun setBufferConfig(videoView: ReactExoplayerView, bufferConfig: ReadableMap?) {
        val config = BufferConfig.parse(bufferConfig)
        videoView.setBufferConfig(config)
    }

    @ReactProp(name = PROP_SHOW_NOTIFICATION_CONTROLS)
    fun setShowNotificationControls(videoView: ReactExoplayerView, showNotificationControls: Boolean) {
        videoView.setShowNotificationControls(showNotificationControls)
    }

    @ReactProp(name = PROP_DEBUG, defaultBoolean = false)
    fun setDebug(videoView: ReactExoplayerView, debugConfig: ReadableMap?) {
        val enableDebug = ReactBridgeUtils.safeGetBool(debugConfig, "enable", false)
        val enableThreadDebug = ReactBridgeUtils.safeGetBool(debugConfig, "thread", false)
        if (enableDebug) {
            DebugLog.setConfig(Log.VERBOSE, enableThreadDebug)
        } else {
            DebugLog.setConfig(Log.WARN, enableThreadDebug)
        }
        videoView.setDebug(enableDebug)
    }

    @ReactProp(name = PROP_CONTROLS_STYLES)
    fun setControlsStyles(videoView: ReactExoplayerView, controlsStyles: ReadableMap?) {
        val controlsConfig = ControlsConfig.parse(controlsStyles)
        videoView.setControlsStyles(controlsConfig)
    }

    @ReactProp(name = PROP_ENABLE_CDN_BALANCER, defaultBoolean = false)
    fun setEnableCdnBalancer(videoView: ReactExoplayerView, enableCdnBalancer: Boolean) {
        videoView.setEnableCdnBalancerModifier(enableCdnBalancer)
    }

    @ReactProp(name = PROP_YOUBORA_PARAMS)
    fun setYouboraParams(videoView: ReactExoplayerView, src: ReadableMap?) {
        if (src == null) {
            videoView.setYouboraParams(null, null)
            return
        }

        val accountCode: String? = if (src.hasKey(PROP_YOUBORA_ACCOUNT_CODE)) src.getString(PROP_YOUBORA_ACCOUNT_CODE) else null
        val username: String? = if (src.hasKey(PROP_YOUBORA_USERNAME)) src.getString(PROP_YOUBORA_USERNAME) else null
        val contentTransactionCode: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_TRANSACTION_CODE)) src.getString(PROP_YOUBORA_CONTENT_TRANSACTION_CODE) else null
        val isLive = if (src.hasKey(PROP_YOUBORA_IS_LIVE)) src.getBoolean(PROP_YOUBORA_IS_LIVE) else false
        val parseCdnNode = if (src.hasKey(PROP_YOUBORA_PARSE_CDN_NODE)) src.getBoolean(PROP_YOUBORA_PARSE_CDN_NODE) else false
        val enabled = if (src.hasKey(PROP_YOUBORA_ENABLED)) src.getBoolean(PROP_YOUBORA_ENABLED) else false
        val title: String? = if (src.hasKey(PROP_YOUBORA_TITLE)) src.getString(PROP_YOUBORA_TITLE) else null
        val program: String? = if (src.hasKey(PROP_YOUBORA_PROGRAM)) src.getString(PROP_YOUBORA_PROGRAM) else null
        val tvShow: String? = if (src.hasKey(PROP_YOUBORA_TV_SHOW)) src.getString(PROP_YOUBORA_TV_SHOW) else null
        val season: String? = if (src.hasKey(PROP_YOUBORA_SEASON)) src.getString(PROP_YOUBORA_SEASON) else null
        val contentType: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_TYPE)) src.getString(PROP_YOUBORA_CONTENT_TYPE) else null
        val contentId: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_ID)) src.getString(PROP_YOUBORA_CONTENT_ID) else null
        val contentPlaybackType: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_PLAYBACK_TYPE)) src.getString(PROP_YOUBORA_CONTENT_PLAYBACK_TYPE) else null
        val contentPackage: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_PACKAGE)) src.getString(PROP_YOUBORA_CONTENT_PACKAGE) else null
        val contentDuration = if (src.hasKey(PROP_YOUBORA_CONTENT_DURATION)) src.getDouble(PROP_YOUBORA_CONTENT_DURATION) else 0.00
        val contentDrm = if (src.hasKey(PROP_YOUBORA_CONTENT_DRM)) src.getBoolean(PROP_YOUBORA_CONTENT_DRM) else false
        val contentResource: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_RESOURCE)) src.getString(PROP_YOUBORA_CONTENT_RESOURCE) else null
        val contentGenre: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_GENRE)) src.getString(PROP_YOUBORA_CONTENT_GENRE) else null
        val contentLanguage: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_LANGUAGE)) src.getString(PROP_YOUBORA_CONTENT_LANGUAGE) else null
        val contentChannels: String? = if (src.hasKey(PROP_YOUBORA_CONTENT_CHANNELS)) src.getString(PROP_YOUBORA_CONTENT_CHANNELS) else null
        val contentStreamingProtocol: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_STREAMING_PROTOCOL)) src.getString(PROP_YOUBORA_CONTENT_STREAMING_PROTOCOL) else null
        val contentCustomDimension1: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_1)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_1) else null
        val contentCustomDimension2: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_2)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_2) else null
        val contentCustomDimension3: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_3)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_3) else null
        val contentCustomDimension4: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_4)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_4) else null
        val contentCustomDimension5: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_5)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_5) else null
        val contentCustomDimension6: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_6)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_6) else null
        val contentCustomDimension7: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_7)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_7) else null
        val contentCustomDimension8: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_8)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_8) else null
        val contentCustomDimension9: String? =
            if (src.hasKey(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_9)) src.getString(PROP_YOUBORA_CONTENT_CUSTOM_DIMENSION_9) else null
        val rendition: String? = if (src.hasKey(PROP_YOUBORA_RENDITION)) src.getString(PROP_YOUBORA_RENDITION) else null
        val userType: String? = if (src.hasKey(PROP_YOUBORA_USER_TYPE)) src.getString(PROP_YOUBORA_USER_TYPE) else null

        val youboraOptions: AnalyticsOptions = AnalyticsOptions()

//        youboraOptions.setAccountCode(accountCode) // Account code
        youboraOptions.username = username // UserId or Guest
        youboraOptions.contentTransactionCode = contentTransactionCode // Subscribed or Free
        youboraOptions.live = isLive // VOD or Live
        youboraOptions.isParseCdnNode = parseCdnNode // Allow Youbora to parse CDN from Host
        youboraOptions.isEnabled = enabled
        youboraOptions.contentRendition = rendition

        youboraOptions.contentTitle = title // Content Title
        youboraOptions.program = program // Content Title 2
        youboraOptions.contentTvShow = tvShow // Show name for shows, otherwise empty
        youboraOptions.contentSeason = season // Season number for shows, otherwise empty
        youboraOptions.contentType = contentType // movie, series & program
        youboraOptions.contentId = contentId // Content Id
        youboraOptions.contentPlaybackType = contentPlaybackType // sVOD or aVOD
        youboraOptions.contentDuration = contentDuration // Duration in millis
        youboraOptions.contentDrm = contentDrm.toString()
        youboraOptions.contentPackage = contentPackage
        youboraOptions.contentResource = contentResource // Content Url
        youboraOptions.contentGenre = contentGenre // Content Genre comma separated
        youboraOptions.contentLanguage = contentLanguage // Content dialects comma separated
        youboraOptions.contentChannel = contentChannels

        youboraOptions.contentStreamingProtocol = contentStreamingProtocol // HLS, widevine or widevine dash

        youboraOptions.contentCustomDimension1 = contentCustomDimension1
        youboraOptions.contentCustomDimension2 = contentCustomDimension2
        youboraOptions.contentCustomDimension3 = contentCustomDimension3
        youboraOptions.contentCustomDimension4 = contentCustomDimension4
        youboraOptions.contentCustomDimension5 = contentCustomDimension5
        youboraOptions.contentCustomDimension6 = contentCustomDimension6
        youboraOptions.contentCustomDimension7 = contentCustomDimension7
        youboraOptions.contentCustomDimension8 = contentCustomDimension8
        youboraOptions.contentCustomDimension9 = contentCustomDimension9
        youboraOptions.userType = userType

        youboraOptions.appName = if (src.hasKey(PROP_YOUBORA_APP_NAME)) src.getString(PROP_YOUBORA_APP_NAME) else null
        youboraOptions.appReleaseVersion = if (src.hasKey(PROP_YOUBORA_RELEASE_VERSION)) src.getString(PROP_YOUBORA_RELEASE_VERSION) else null
        youboraOptions.deviceCode = "AndroidTV"

        videoView.setYouboraParams(accountCode, youboraOptions)
    }

    @ReactProp(name = PROP_AD_BREAK_POINT)
    fun setAdsBreakPoints(
        videoView: ReactExoplayerView,
        adsBreakPoints: ReadableArray?
    ) {
        if (adsBreakPoints == null) return;

        videoView.setAdsBreakPoints(adsBreakPoints)
    }
}
