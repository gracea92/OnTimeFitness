package com.example.jaredkohler.ontimefitness;

import android.provider.BaseColumns;

public final class LogInContract {
    private LogInContract() {}

    public static class LogInEntry implements BaseColumns{
        public static final String TABLE_NAME = "login";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

}
