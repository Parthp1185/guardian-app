package com.example.afinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        listView = findViewById(R.id.deleteListView);
        dbHelper = new DBHelper(this);

        displayMembers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmDelete((int) id);
            }
        });
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

    private void confirmDelete(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteContact(id);
                Toast.makeText(DeleteActivity.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                displayMembers();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
