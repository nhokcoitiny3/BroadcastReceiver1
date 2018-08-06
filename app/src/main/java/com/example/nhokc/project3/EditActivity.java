package com.example.nhokc.project3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.nhokc.project3.databinding.ActivityEditBinding;

import java.util.ArrayList;
import java.util.List;


public class EditActivity extends AppCompatActivity implements IOnRecyclerViewItemClickListener,PrefixRvAdapter.IPrefix,IOnRecyclerViewItemLongClickListener{
    private ActivityEditBinding binding;
    private List<String> prefixList = new ArrayList<>();
    private PrefixRvAdapter adapter = new PrefixRvAdapter();
    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        prefixList = myDatabaseHelper.getAllPrefix();
        initialize();
    }

    private void initialize() {
        initializeRv();
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    public void showDialog() {
        final Dialog dialog;
        dialog = new Dialog(EditActivity.this);
        dialog.setTitle("Thêm đầu số");
        TextInputEditText edtPre = dialog.findViewById(R.id.edt_prefix);
        dialog.setContentView(R.layout.dialog_custom);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText edtPre = dialog.findViewById(R.id.edt_prefix);
                String prefix = edtPre.getText().toString();
                myDatabaseHelper.addPrefix(prefix);
                prefixList.add(prefix);
                adapter.notifyDataSetChanged();
                adapter.addItem(0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    public void showDialogEdit(final int position , String prefix) {
        final Dialog dialog;
        dialog = new Dialog(EditActivity.this);
        dialog.setTitle("Sửa đầu số");
        TextInputEditText edtPre = dialog.findViewById(R.id.edt_prefix);
        dialog.setContentView(R.layout.dialog_custom);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText edtPre = dialog.findViewById(R.id.edt_prefix);
                String prefix = edtPre.getText().toString();
                myDatabaseHelper.updatePrefix(prefix);
                prefixList.remove(position);
                prefixList.add(position,prefix);
                adapter.notifyDataSetChanged();
                adapter.updateItem(position);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initializeRv() {
        adapter.setData(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvPrefix.setLayoutManager(linearLayoutManager);
        binding.rvPrefix.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setLongClickListener(this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onRecyclerViewItemLongClicked(final int position, int id) {
        myDatabaseHelper.deleteNote(prefixList.get(position));
        prefixList.remove(position);
        adapter.notifyDataSetChanged();
        adapter.deleteItem(position);
    }


    @Override
    public int getCount() {
        return prefixList.size();
    }

    @Override
    public String getPrefix(int position) {
        return prefixList.get(position);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onRecyclerViewItemClicked(final int position, int id) {
        RecyclerView.ViewHolder viewHolder = binding.rvPrefix.findViewHolderForAdapterPosition(position);
        @SuppressLint("RtlHardcoded") PopupMenu popup = new PopupMenu(EditActivity.this, viewHolder.itemView, Gravity.RIGHT);
        popup.getMenuInflater().inflate(R.menu.menu_item_rcv, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        showDialog();
                        break;
                    case R.id.item_edit:
                        showDialogEdit(position,prefixList.get(position));
                        break;
                    case R.id.item_delete:
                        myDatabaseHelper.deleteNote(prefixList.get(position));
                        prefixList.remove(position);
                        adapter.notifyDataSetChanged();
                        adapter.deleteItem(position);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(EditActivity.this, (MenuBuilder) popup.getMenu(), viewHolder.itemView);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.RIGHT);
        menuHelper.show();
    }
}
