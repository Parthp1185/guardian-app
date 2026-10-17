package com.example.afinal;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewMember extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member);

        listView = findViewById(R.id.memberListView);
        dbHelper = new DBHelper(this);

        displayMembers();
    }

    private void displayMembers() {
        Cursor cursor = dbHelper.getAllContacts();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No members found", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] from = {DBHelper.COLUMN_NAME, DBHelper.COLUMN_PHONE};
        int[] to = {R.id.contactName, R.id.contactPhoneNumber};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.member_list_item, cursor, from, to, 0);
        listView.setAdapter(adapter);
    }
}
