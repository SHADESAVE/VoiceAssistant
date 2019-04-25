package com.example.voiceassistant;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.autofill.FieldClassification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AI {
    @TargetApi(Build.VERSION_CODES.O)
    public static void getAnswer(String userQuestion, final Consumer<String> callback) {

        final Date date = new Date();
        final DateFormat fmt = new SimpleDateFormat("EEEE, dd", new Locale("ru"));
        final DateFormat fmt2 = new SimpleDateFormat("HH:mm:ss");

        Map<String, String> dataBase = new HashMap<String, String>() {{
            put("привет", "Здравствуйте");
            put("как дела", "Дела в порядке");
            put("чем занимаешься", "Отвечаю на Ваши вопросы");
            put("как тебя зовет", "Имя мне - Легион");
            put("кто тебя создал", "Мой создатель - Александр");
            put("есть ли жизнь на марсе", "На сегодняшний день этот вопрос остаётся открытым");
            put("кто президент россии", "Владимир Владимирович Путин");
            put("какого цвета небо", "Небо Земли голубое");
            //put("какой сегодня день", ""+fmt.format(date).substring(0,1).toUpperCase()+fmt.format(date).substring(1)+"-ое"); //Сделал для того, что бы первая буква была заглавной, он бы вывел "Среда, 24"
            put("какой сегодня день", "Сегодня " + fmt.format(date) + "-ое");
            put("сколько сейчас времени", "Сейчас " + fmt2.format(date));
        }};

        userQuestion = userQuestion.toLowerCase();

        final ArrayList<String> answers = new ArrayList<>();

        int max_score = 0;
        String max_score_answer = "";
        String[] split_userq = userQuestion.split("\\s+");

        for (String dataBase_question : dataBase.keySet()) {
            dataBase_question = dataBase_question.toLowerCase();
            String[] split_db = dataBase_question.split("\\s+");

            int score = 0;

            for(String word_user : split_userq) {
                for (String word_dp : split_db) {
                    int min_len = Math.min(word_dp.length(), word_user.length());
                    int cut_len = (int) (min_len * 0.7);
                    String word_user_cut = word_user.substring(0, cut_len);
                    String word_dp_cut = word_dp.substring(0, cut_len);
                    if (word_user_cut.equals(word_dp_cut)) {
                        score++;
                    }
                }
            }

            if (score > max_score) {
                max_score = score;
                max_score_answer = dataBase.get(dataBase_question);
            }
//            if (userQuestion.contains(dataBase_question)) {
//                answers.add(dataBase.get(dataBase_question));
//            }
        }
        if (max_score > 0) {
            answers.add(max_score_answer);
        }

        Pattern cityPattern = Pattern.compile("какая погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(userQuestion);

        if (matcher.find()) {
            String cityName = matcher.group(1);
            Weather.get(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    answers.add(s);
                    callback.accept(String.join(", ", answers));
                }
            });
        } else {
            if (userQuestion.contains("расскажи афоризм")) {
                Aphorism.get(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        answers.add(s);
                        callback.accept(String.join(", ", answers));
                    }
                });
            } else {
                if (answers.isEmpty()) {
                    callback.accept("Как скажете");
                    return;
                }
                callback.accept(String.join(", ", answers));
            }
        }
    }
}
