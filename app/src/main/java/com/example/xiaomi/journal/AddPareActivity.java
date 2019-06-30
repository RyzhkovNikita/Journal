package com.example.xiaomi.journal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddPareActivity extends AppCompatActivity {

    EditText editStudent;
    EditText editTeacherName;
    Button addBtn;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pare);

        addBtn = findViewById(R.id.addPare);
        editStudent = findViewById(R.id.editPare);
        editTeacherName = findViewById(R.id.editTeacherName);
        spinner = findViewById(R.id.spinner);
        String[] types = {
                AppConstants.STR_LECTION,
                AppConstants.STR_SEM,
                AppConstants.STR_LAB
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStudent.getText().toString().isEmpty()) {
                    Toast.makeText(AddPareActivity.this, "Введите название пары", Toast.LENGTH_SHORT).show();
                } else if (editTeacherName.getText().toString().isEmpty()) {
                    Toast.makeText(AddPareActivity.this, "Введите имя препода", Toast.LENGTH_SHORT).show();
                }else{
                    String pareName = editStudent.getText().toString();
                    String teacherName = editTeacherName.getText().toString();
                    int type = spinner.getSelectedItemPosition();
                    switch (type) {
                        case 0:
                            type = AppConstants.LECTION;
                            break;
                        case 1:
                            type = AppConstants.SEM;
                            break;
                        case 2:
                            type = AppConstants.LAB;
                            break;
                        default:
                            type = 0;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("pareName", pareName);
                    intent.putExtra("type", type);
                    intent.putExtra("teacherName", teacherName);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
