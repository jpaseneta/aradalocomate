package com.example.inmsb.aradalocomate.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inmsb.aradalocomate.BuildConfig;
import com.example.inmsb.aradalocomate.Model.NavDrawerItem;
import com.example.inmsb.aradalocomate.Model.RadarInfoItem;
import com.example.inmsb.aradalocomate.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by inmsb on 1/4/2016.
 */
public class RadarItemAdapter extends RecyclerView.Adapter<RadarItemAdapter.MyViewHolder> {
    ArrayList<RadarInfoItem> data;
    private LayoutInflater inflater;
    private Context context;
    private Resources res;
    MediaPlayer mp;
    private static String warnings[];
    TextToSpeech tts;

    public RadarItemAdapter(Context context, ArrayList<RadarInfoItem> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        res=context.getResources();
        mp = MediaPlayer.create(this.context, R.raw.warning);
        warnings = this.context.getResources().getStringArray(R.array.warning_messages_array);
        tts=new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.radar_card_view, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RadarInfoItem current = data.get(position);
        holder.customWarningMsg.setText(current.getInfo());
        holder.eventName.setText(current.getEventName());
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        holder.warningMsg.startAnimation(anim);
        holder.warningPhoto.setImageDrawable(res.getDrawable(current.getWarningPhoto()));

        mp.start();
        Log.d("mediaPlayer", "mp started");
        tts.speak(warnings[current.getUnforeseenDetectionType()-1], TextToSpeech.QUEUE_FLUSH, null);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView warningMsg;
        TextView eventName;
        TextView customWarningMsg;
        ImageView warningPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            warningMsg = (TextView) itemView.findViewById(R.id.warning_msg);
            eventName = (TextView) itemView.findViewById(R.id.eventName);
            customWarningMsg = (TextView) itemView.findViewById(R.id.custom_warning_msg);
            warningPhoto = (ImageView) itemView.findViewById(R.id.warning_photo);
        }
    }
}
