package ua.protech.protech.blindhelper;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class TTS implements TextToSpeech.OnInitListener {

    private final static TTS tts = new TTS();

    //TTS object
    private TextToSpeech myTTS;
    Context context;

    public static TTS getInstance(){
        return tts;
    }

    private TTS() {
    }

    public void initTTS(Context context){
        this.context = context;
        myTTS = new TextToSpeech(this.context, this);
    }

    public void initTTS(Context context, String tts){
        this.context = context;
        myTTS = new TextToSpeech(this.context, this, tts);
    }

    public void speakWords(String speech) {
        myTTS.speak(speech, TextToSpeech.QUEUE_ADD, null);
    }

    public void silence(){
        myTTS.playSilence(500, TextToSpeech.QUEUE_ADD, null);
    }

    public List<TextToSpeech.EngineInfo> getListOfTTS(){
        List<TextToSpeech.EngineInfo> ttss = myTTS.getEngines();
        return ttss;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            Locale locale = new Locale("ru");

            int result = myTTS.setLanguage(locale);
            //int result = mTTS.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Извините, этот язык не поддерживается");
            }

        } else {
            Log.e("TTS", "Ошибка!");
        }

    }
}
