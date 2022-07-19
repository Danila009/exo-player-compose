package com.example.exo_player_compose.exoPlayerCompose.type

import android.content.Context
import android.util.Log
import com.example.exo_player_compose.exoPlayerFilter.filter.*
import java.io.IOException

enum class ExoFilterType {
    DEFAULT,
    BILATERAL_BLUR,
    BOX_BLUR,
    BRIGHTNESS,
    BULGE_DISTORTION,
    CGA_COLORSPACE,
    CONTRAST,
    CROSSHATCH,
    EXPOSURE,
    FILTER_GROUP_SAMPLE,
    GAMMA,
    GAUSSIAN_FILTER,
    GRAY_SCALE,
    HAZE,
    HALFTONE,
    HIGHLIGHT_SHADOW,
    HUE,
    INVERT,
    LUMINANCE,
    LUMINANCE_THRESHOLD,
    MONOCHROME,
    OPACITY,
    PIXELATION,
    POSTERIZE,
    RGB,
    SATURATION,
    SEPIA,
    SHARP,
    SOLARIZE,
    SPHERE_REFRACTION,
    SWIRL,
    TONE_CURVE_SAMPLE,
    TONE,
    VIBRANCE,
    VIGNETTE,
    WEAK_PIXEL,
    WHITE_BALANCE,
    ZOOM_BLUR;

    companion object {
        fun createGlFilter(filterType: ExoFilterType?, context: Context): GlFilter {
            return when (filterType) {
                BILATERAL_BLUR -> GlBilateralFilter()
                BOX_BLUR -> GlBoxBlurFilter()
                BRIGHTNESS -> {
                    val glBrightnessFilter = GlBrightnessFilter()
                    glBrightnessFilter.setBrightness(0.2f)
                    glBrightnessFilter
                }
                BULGE_DISTORTION -> GlBulgeDistortionFilter()
                CGA_COLORSPACE -> GlCGAColorspaceFilter()
                CONTRAST -> {
                    val glContrastFilter = GlContrastFilter()
                    glContrastFilter.setContrast(2.5f)
                    glContrastFilter
                }
                CROSSHATCH -> GlCrosshatchFilter()
                EXPOSURE -> GlExposureFilter()
                FILTER_GROUP_SAMPLE -> GlFilterGroup(GlSepiaFilter(), GlVignetteFilter())
                GAMMA -> {
                    val glGammaFilter = GlGammaFilter()
                    glGammaFilter.setGamma(2f)
                    glGammaFilter
                }
                GAUSSIAN_FILTER -> GlGaussianBlurFilter()
                GRAY_SCALE -> GlGrayScaleFilter()
                HALFTONE -> GlHalftoneFilter()
                HAZE -> {
                    val glHazeFilter = GlHazeFilter()
                    glHazeFilter.slope = -0.5f
                    glHazeFilter
                }
                HIGHLIGHT_SHADOW -> GlHighlightShadowFilter()
                HUE -> GlHueFilter()
                INVERT -> GlInvertFilter()
                LUMINANCE -> GlLuminanceFilter()
                LUMINANCE_THRESHOLD -> GlLuminanceThresholdFilter()
                MONOCHROME -> GlMonochromeFilter()
                OPACITY -> GlOpacityFilter()
                PIXELATION -> GlPixelationFilter()
                POSTERIZE -> GlPosterizeFilter()
                RGB -> {
                    val glRGBFilter = GlRGBFilter()
                    glRGBFilter.setRed(0f)
                    glRGBFilter
                }
                SATURATION -> GlSaturationFilter()
                SEPIA -> GlSepiaFilter()
                SHARP -> {
                    val glSharpenFilter = GlSharpenFilter()
                    glSharpenFilter.sharpness = 4f
                    glSharpenFilter
                }
                SOLARIZE -> GlSolarizeFilter()
                SPHERE_REFRACTION -> GlSphereRefractionFilter()
                SWIRL -> GlSwirlFilter()
                TONE_CURVE_SAMPLE -> {
                    try {
                        val `is` = context.assets.open("acv/tone_cuver_sample.acv")
                        return GlToneCurveFilter(`is`)
                    } catch (e: IOException) {
                        Log.e("FilterType", "Error")
                    }
                    GlFilter()
                }
                TONE -> GlToneFilter()
                VIBRANCE -> {
                    val glVibranceFilter = GlVibranceFilter()
                    glVibranceFilter.setVibrance(3f)
                    glVibranceFilter
                }
                VIGNETTE -> GlVignetteFilter()
                WEAK_PIXEL -> GlWeakPixelInclusionFilter()
                WHITE_BALANCE -> {
                    val glWhiteBalanceFilter = GlWhiteBalanceFilter()
                    glWhiteBalanceFilter.setTemperature(2400f)
                    glWhiteBalanceFilter.setTint(2f)
                    glWhiteBalanceFilter
                }
                ZOOM_BLUR -> GlZoomBlurFilter()
                else -> GlFilter()
            }
        }
    }
}