package com.example.xiaomi.journal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddStudentActivity extends AppCompatActivity {

    EditText editStud;
    Button addStudBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        editStud = findViewById(R.id.editStud);
        addStudBtn = findViewById(R.id.add_stud_btn);

        addStudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStud.getText().toString().isEmpty()){
                    Toast.makeText(AddStudentActivity.this, "Введите фамилию", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("name", editStud.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
