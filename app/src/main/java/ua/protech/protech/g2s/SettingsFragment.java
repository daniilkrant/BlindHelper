package ua.protech.protech.g2s;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private Button vibro_btn, db_btn, guide_btn, auto_sound_btn, feedback, sound_engine_btn, demo_sound_btn;
    private Spinner signal_counter, cycle_counter;
    private Boolean vibro = true;
    private Boolean sound = true;
    private Boolean guide = false;
    private Boolean auto_sound = false;
    private Boolean demo_sound = true;
    private SharedPreferences sharedPreferences;
    private ArrayList<BlindBeacon> blindBeacons;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = getActivity().getSharedPreferences(Data.SETTINGS_FILE_SHARED_PREF, Context.MODE_PRIVATE);
        vibro_btn = (Button) view.findViewById(R.id.vibro_btn);
        auto_sound_btn = (Button) view.findViewById(R.id.sound_auto_btn);
        db_btn = (Button) view.findViewById(R.id.db_btn);
        feedback = (Button) view.findViewById(R.id.feedback);
        guide_btn = (Button) view.findViewById(R.id.guide_btn);
        sound_engine_btn = (Button) view.findViewById(R.id.sound_engine_btn);
        demo_sound_btn = (Button) view.findViewById(R.id.demo_sound_btn);
        signal_counter = (Spinner) view.findViewById(R.id.signal_counter);
        cycle_counter = (Spinner) view.findViewById(R.id.cycle_counter);

        ArrayAdapter<String> distances_adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, Data.cycles_list);
        distances_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> sound_adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, Data.sound_counter_list);
        sound_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        signal_counter.setAdapter(sound_adapter);
        signal_counter.setPrompt(getString(R.string.amount_beep));
        signal_counter.setSelection(sharedPreferences.getInt(Data.NUMBER_OF_SIGNALS_ARRAY_POSITION,0));

        cycle_counter.setAdapter(distances_adapter);
        cycle_counter.setPrompt(getString(R.string.detection_distance));
        cycle_counter.setSelection(sharedPreferences.getInt(Data.NUMBER_OF_CYCLES_POSITION,0));

        if (sharedPreferences.getBoolean((Data.IS_VIBRO), true)){
            vibro = true;
            vibro_btn.setText(getString(R.string.vibro_on));
        } else {
            vibro = false;
            vibro_btn.setText(getString(R.string.vibro_off));
        }

        if (sharedPreferences.getBoolean((Data.IS_GUIDE), false)){
            guide = true;
            guide_btn.setText("Режим гида включен");
        } else {
            guide = false;
            guide_btn.setText("Режим гида выключен");
        }

        guide_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guide = !guide;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Data.IS_GUIDE, guide);
                editor.apply();
                if (!guide) {
                    guide_btn.setText("Режим гида выключен");
                } else {
                    guide_btn.setText("Режим гида включен");
                }
            }
        });

        sound_engine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
                builderSingle.setTitle(getString(R.string.btn_choose_tts));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice);
                List<TextToSpeech.EngineInfo> ttss = TTS.getInstance().getListOfTTS();
                for (TextToSpeech.EngineInfo e: ttss)
                    arrayAdapter.add(e.label);

                builderSingle.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String choosedEngine = arrayAdapter.getItem(which);
                        for (TextToSpeech.EngineInfo e: TTS.getInstance().getListOfTTS()) {
                            if (choosedEngine.equals(e.label)) {
                                TTS.getInstance().initTTS(getContext(), e.name);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Data.TTS_ENGINE, e.name);
                                editor.apply();
                                break;
                            }
                        }
                    }
                });
                builderSingle.show();
            }
        });

        sound_engine_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Выбора синтезатора речи");
                }
                return false;
            }
        });

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Data.DEMO_SOUND, demo_sound);
        editor.apply();
        demo_sound_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка программное озвучивание интерфейса");
                }
                return false;
            }
        });


        demo_sound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demo_sound = !demo_sound;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Data.DEMO_SOUND, demo_sound);
                editor.apply();
                if (!demo_sound) {
                    demo_sound_btn.setText("Программное озвучивание интерфейса отключено");
                } else {
                    demo_sound_btn.setText("Программное озвучивание интерфейса включено");
                }
            }
        });


        feedback.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка отправки информации разработчикам");
                }
                return false;
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","info@g2s.com.ua", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, "Android Version: " + Build.VERSION.RELEASE + "\nDevice: " + Build.MODEL + "|" + Build.DEVICE + "\n" + "Ваше сообщение: ");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                startActivity(intent);
            }
        });

        if (sharedPreferences.getBoolean((Data.IS_AUTO_AUDIO), false)){
            auto_sound = true;
            auto_sound_btn.setText(getString(R.string.auto_sound_on));
        } else {
            auto_sound = false;
            auto_sound_btn.setText(getString(R.string.auto_sound_off));
        }

        signal_counter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Выбор количества сигналов");
                }
                return false;
            }
        });

        signal_counter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Data.NUMBER_OF_SIGNALS_ARRAY_POSITION, position);
                editor.putInt(Data.NUMBER_OF_SIGNALS, Integer.parseInt(Data.sound_counter_list[position]));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cycle_counter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Выбор количества циклов воспроизведения");
                }
                return false;
            }
        });

        cycle_counter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Data.NUMBER_OF_CYCLES_POSITION, position);
                editor.putInt(Data.NUMBER_OF_CYCLES, Integer.parseInt(Data.cycles_list[position]));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка обновления базы данных");
                }
                return false;
            }
        });

        db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateDB updateDB = new UpdateDB();
                updateDB.execute();
            }
        });

        vibro_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка " + ((TextView) view).getText());
                }
                return false;
            }
        });

        vibro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibro = !vibro;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Data.IS_VIBRO, vibro);
                editor.apply();
                if (vibro) {
                    vibro_btn.setText(getString(R.string.vibro_on));
                    vibro_btn.announceForAccessibility(getString(R.string.vibro_on));
                }
                else {
                    vibro_btn.setText(getString(R.string.vibro_off));
                    vibro_btn.announceForAccessibility(getString(R.string.vibro_off));
                }
            }
        });

        auto_sound_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (sharedPreferences.getBoolean((Data.DEMO_SOUND), true)) {
                    TTS.getInstance().speakWords("Кнопка " + ((TextView) view).getText());
                }
                return false;
            }
        });

        editor.putBoolean(Data.IS_AUTO_AUDIO, auto_sound);
        editor.apply();
        auto_sound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auto_sound = !auto_sound;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Data.IS_AUTO_AUDIO, auto_sound);
                editor.apply();
                if (auto_sound) {
                    auto_sound_btn.setText(getString(R.string.auto_sound_on));
                    auto_sound_btn.announceForAccessibility(getString(R.string.auto_sound_on));
                }
                else {
                    auto_sound_btn.setText(getString(R.string.auto_sound_off));
                    auto_sound_btn.announceForAccessibility(getString(R.string.auto_sound_off));
                }
            }
        });


        return view;
    }

    public class UpdateDB extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            blindBeacons = ServerRoutine.getBeaconsFromDB();
            BlindBeacon.saveList(getActivity().getApplicationContext(), blindBeacons);
            try {
                Data.setSerialized_beacons(BlindBeacon.getList(getActivity().getApplicationContext()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d(Data.TAG, Integer.toString(blindBeacons.size()));
            db_btn.announceForAccessibility(getString(R.string.db_updated));
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.db_updated), Toast.LENGTH_LONG).show();
        }
    }
}
