package com.example.jaredkohler.ontimefitness;

import android.provider.BaseColumns;

public final class LogInContract {
    private LogInContract() {}

    public static class LogInEntry implements BaseColumns{
        public static final String TABLE_NAME = "login";
        public static final String COLUMN_NAME_TITLE = "username";
        public static final String COLUMN_NAME_SUBTITLE = "password";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_STEPS = "steps";

    }

}
