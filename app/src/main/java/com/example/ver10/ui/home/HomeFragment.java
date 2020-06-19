package com.example.ver10.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ver10.MainActivity;
import com.example.ver10.R;

public class HomeFragment extends Fragment {

    public HomeViewModel homeViewModel;
    public TextView txtTempValue, txtHumiValue, txtSpeakerValue;
    public SeekBar seekBarSpeaker;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        txtTempValue = (TextView) root.findViewById(R.id.tempValue);
        txtHumiValue = (TextView) root.findViewById(R.id.humiValue);
        txtSpeakerValue = (TextView) root.findViewById(R.id.speakerValue);
        seekBarSpeaker = (SeekBar) root.findViewById(R.id.seekBarSpeaker);
        TextView speakerValueMain = (TextView) getActivity().findViewById(R.id.speakerValue);
        speakerValueMain.setText(String.valueOf(seekBarSpeaker.getProgress()));
        seekBarSpeaker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speakerValueMain.setText(String.valueOf(seekBarSpeaker.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(seekBarSpeaker.getProgress()));
            }
        });



        return root;
    }

    public void setValue(int tempValue, int humiValue) {
        txtSpeakerValue.setText(String.valueOf(tempValue));
        txtHumiValue.setText(String.valueOf(humiValue));
        Toast.makeText(getActivity(),tempValue + " " + humiValue, Toast.LENGTH_SHORT).show();
    }
}
