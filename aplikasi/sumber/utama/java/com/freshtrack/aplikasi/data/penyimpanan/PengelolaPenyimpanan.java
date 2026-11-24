package com.freshtrack.aplikasi.data.penyimpanan;

import android.content.Context;
import android.content.SharedPreferences;

public class PengelolaPenyimpanan {

    private static final String NAMA_PREF = "freshtrack_preferensi";
    private final SharedPreferences preferensi;

    public PengelolaPenyimpanan(Context konteks) {
        this.preferensi = konteks.getApplicationContext().getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE);
    }

    public String ambilTeks(String kunci, String nilaiDefault) {
        return preferensi.getString(kunci, nilaiDefault);
    }

    public boolean simpanTeks(String kunci, String nilai) {
        return preferensi.edit().putString(kunci, nilai).commit();
    }

    public boolean ambilBoolean(String kunci, boolean nilaiDefault) {
        return preferensi.getBoolean(kunci, nilaiDefault);
    }

    public boolean simpanBoolean(String kunci, boolean nilai) {
        return preferensi.edit().putBoolean(kunci, nilai).commit();
    }
}
