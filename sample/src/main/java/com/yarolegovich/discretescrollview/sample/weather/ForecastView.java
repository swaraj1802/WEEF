package com.yarolegovich.discretescrollview.sample.weather;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yarolegovich.discretescrollview.sample.R;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class ForecastView extends LinearLayout {

    private Paint gradientPaint;
    private int[] currentGradient;

    private TextView weatherDescription;



    private ArgbEvaluator evaluator;

    public ForecastView(Context context) {
        super(context);
    }

    public ForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ForecastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        evaluator = new ArgbEvaluator();

        gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(getContext(), R.layout.view_forecast, this);

        weatherDescription = (TextView) findViewById(R.id.weather_description);
        Linkify.addLinks(weatherDescription, Linkify.WEB_URLS);


    }

    private void initGradient() {
        float centerX = getWidth() * 0.5f;
        Shader gradient = new LinearGradient(
                centerX, 0, centerX, getHeight(),
                currentGradient, null,
                Shader.TileMode.MIRROR);
        gradientPaint.setShader(gradient);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (currentGradient != null) {
            initGradient();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        super.onDraw(canvas);
    }

    public void setForecast(Forecast forecast) {
        Weather weather = forecast.getWeather();
        currentGradient = weatherToGradient(weather);
        if (getWidth() != 0 && getHeight() != 0) {
            initGradient();
        }
        weatherDescription.setText(Html.fromHtml(weather.getDisplayName()));

        invalidate();

        weatherDescription.animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300)
                .start();
    }

    public void onScroll(float fraction, Forecast oldF, Forecast newF) {
        weatherDescription.setScaleX(fraction);
        weatherDescription.setScaleY(fraction);
        currentGradient = mix(fraction,
                weatherToGradient(newF.getWeather()),
                weatherToGradient(oldF.getWeather()));
        initGradient();
        invalidate();
    }

    private int[] mix(float fraction, int[] c1, int[] c2) {
        return new int[]{
                (Integer) evaluator.evaluate(fraction, c1[0], c2[0]),
                (Integer) evaluator.evaluate(fraction, c1[1], c2[1]),
                (Integer) evaluator.evaluate(fraction, c1[2], c2[2])
        };
    }

    private int[] weatherToGradient(Weather weather) {
        switch (weather) {
            case VERY_SUNNY:
                return colors(R.array.a);
            case SUNNY:
                return colors(R.array.gradientPeriodicClouds);
            case PERIODIC_CLOUDS:
                return colors(R.array.b);
            case CLOUDY:
                return colors(R.array.gradientCloudy);
            case MOSTLY_CLOUDY:
                return colors(R.array.gradientMostlyCloudy);
            case PARTLY_CLOUDY:
                return colors(R.array.gradientPartlyCloudy);
            case CLEAR:

                return colors(R.array.gradientClear);
            default:
                throw new IllegalArgumentException();
        }
    }



    private int[] colors(@ArrayRes int res) {
        return getContext().getResources().getIntArray(res);
    }

}
