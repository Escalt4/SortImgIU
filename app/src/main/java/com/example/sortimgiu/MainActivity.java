package com.example.sortimgiu;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;

    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    boolean reverseFile = true;
    boolean invertUI = false;

    ArrayList<FileAndStatus> files = new ArrayList<>();
    Integer filesCount = 0;
    Integer curFileIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        prefEditor = settings.edit();

        reverseFile = settings.getBoolean("reverseFile", true);
        invertUI = settings.getBoolean("invertUI", false);

        if (invertUI) {
            setContentView(R.layout.activity_main_inverted);
        } else {
            setContentView(R.layout.activity_main);
        }

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        findAndSortImg();
        showNextImg();
    }

    // проверка на то что файл = изображение
    boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }


    // получение файлов и сортировка их по дате изменения
    void findAndSortImg() {
        File directory = new File(Environment.getExternalStorageDirectory(), "Pictures/Ext");
        File[] temp = directory.listFiles();

        if (temp == null) {
            this.finish();
        }

        Arrays.sort(temp, Comparator.comparingLong(File::lastModified));

        if (reverseFile) {
            Collections.reverse(Arrays.asList(temp));
        }

        for (int i = 0; i < temp.length; i++) {
            if (isImage(temp[i])) {
                files.add(new FileAndStatus(temp[i], 0));
            }
        }
        filesCount = files.size();
    }

    // вывод следующего изображения
    void showNextImg() {
        if (curFileIndex < filesCount) {
            StringBuilder curPosition = new StringBuilder()
                    .append(curFileIndex + 1)
                    .append("/")
                    .append(filesCount);

            textView.setText(curPosition);
            imageView.setImageDrawable(Drawable.createFromPath(files.get(curFileIndex).get_file().getPath()));
        } else {
            exit();
        }
    }

    // установка статуса файла
    void set_status(Integer status) {
        files.get(curFileIndex).set_status(status);
        if (status == 0) {
            if (curFileIndex > 0) {
                curFileIndex -= 1;
            }
        } else {
            curFileIndex += 1;
        }

        showNextImg();
    }

    // выход
    void exit() {
        // создание выходных каталогов
        File directoryA = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/ExtA/");
        if (!directoryA.exists()) {
            directoryA.mkdirs();
        }
        File directoryB = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/ExtB/");
        if (!directoryB.exists()) {
            directoryB.mkdirs();
        }

        // действия с файлами в зависимости от ранее сделаного выбора
        for (int i = 0; i < filesCount; i++) {
            switch (files.get(i).get_status()) {
                case 11:
                    files.get(i).get_file().renameTo(new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/ExtA/" + files.get(i).get_file().getName()));
                    break;

                case 12:
                    files.get(i).get_file().renameTo(new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/ExtB/" + files.get(i).get_file().getName()));
                    break;

                case 2:
                    files.get(i).get_file().delete();
                    break;

                default:
                    break;
            }
        }

        this.finish();
    }

    // Обработка нажатий кнопок
    public void onClick(View view) {
//        statuses
//        11        to A
//        12        to B
//        2         delete
//        3         skip
//        0         undo

        switch (view.getId()) {
            case R.id.buttonInvertUI:
                invertUI = !invertUI;

                prefEditor.putBoolean("invertUI", invertUI);
                prefEditor.apply();

                break;

            case R.id.buttonReverseFile:
                reverseFile = !reverseFile;

                prefEditor.putBoolean("reverseFile", reverseFile);
                prefEditor.apply();

                break;

            case R.id.buttonToA:
                set_status(11);
                break;

            case R.id.buttonToB:
                set_status(12);
                break;

            case R.id.buttonDel:
                set_status(2);
                break;

            case R.id.buttonSkip:
                set_status(3);
                break;

            case R.id.buttonUndo:
                set_status(0);
                break;

            case R.id.buttonExit:
                exit();
                break;

            default:
                break;
        }
    }
}