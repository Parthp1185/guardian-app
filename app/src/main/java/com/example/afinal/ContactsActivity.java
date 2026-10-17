package com.example.afinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView contactsListView;
    private TextView emptyStateText;
    private Button addContactButton;
    private ContactsAdapter adapter;
    private List<Contact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Initialize views
        contactsListView = findViewById(R.id.contactsListView);
        emptyStateText = findViewById(R.id.emptyStateText);
        addContactButton = findViewById(R.id.addContactButton);
        dbHelper = new DBHelper(this);
        contactsList = new ArrayList<>();

        // Set up adapter
        adapter = new ContactsAdapter();
        contactsListView.setAdapter(adapter);

        // Load contacts
        loadContacts();

        // Set up click listeners
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch add contact activity
                Intent intent = new Intent(ContactsActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactsList.get(position);
                showContactOptions(contact);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload contacts when returning to this activity
        loadContacts();
    }

    private void loadContacts() {
        contactsList.clear();
        Cursor cursor = dbHelper.getAllContacts();

        if (cursor.getCount() == 0) {
            emptyStateText.setVisibility(View.VISIBLE);
            contactsListView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            contactsListView.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PHONE));

                contactsList.add(new Contact(id, name, phone));
            }
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void showContactOptions(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(contact.getName());
        builder.setItems(new CharSequence[]{"Call", "Send SMS", "Edit", "Delete"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Call
                                // Implement call functionality
                                Toast.makeText(ContactsActivity.this, "Calling " + contact.getName(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1: // SMS
                                // Implement SMS functionality
                                Toast.makeText(ContactsActivity.this, "SMS " + contact.getName(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 2: // Edit
                                // Launch edit activity
                                Toast.makeText(ContactsActivity.this, "Edit " + contact.getName(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 3: // Delete
                                confirmDelete(contact);
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void confirmDelete(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete " + contact.getName() + "?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteContact(contact.getId());
                Toast.makeText(ContactsActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                loadContacts();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Contact class to hold data
    private static class Contact {
        private int id;
        private String name;
        private String phone;

        public Contact(int id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getInitial() {
            return name != null && name.length() > 0 ?
                    String.valueOf(name.charAt(0)).toUpperCase() : "?";
        }
    }

    // Custom adapter for contacts list
    private class ContactsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactsList.size();
        }

        @Override
        public Object getItem(int position) {
            return contactsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return contactsList.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(ContactsActivity.this)
                        .inflate(R.layout.contact_list_item, parent, false);

                holder = new ViewHolder();
                holder.initialText = convertView.findViewById(R.id.contactInitial);
                holder.nameText = convertView.findViewById(R.id.contactName);
                holder.phoneText = convertView.findViewById(R.id.contactPhoneNumber);
                holder.deleteButton = convertView.findViewById(R.id.deleteContactButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Contact contact = contactsList.get(position);

            holder.initialText.setText(contact.getInitial());
            holder.nameText.setText(contact.getName());
            holder.phoneText.setText(contact.getPhone());

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelete(contact);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView initialText;
            TextView nameText;
            TextView phoneText;
            ImageButton deleteButton;
        }
    }
}