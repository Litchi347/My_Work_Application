package com.example.myworkapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CollectDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "collects.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "collects";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TYPE = "type";  // 新增字段：收藏来源类型（手动、网页）

    public CollectDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, "+
                COLUMN_TYPE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // 插入收藏项
    public void addCollect(String title, String content, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_CONTENT, content);
        values.put(COLUMN_TYPE, type);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 查询所有收藏项
    public List<CollectItem> getAllCollects() {
        List<CollectItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("collects", null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                CollectItem item = new CollectItem(id, title, content, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                list.add(item);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    // 删除某个收藏项
    public void deleteCollect(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 修改某个收藏项
    public void updateCollect(int id, String newTitle, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newTitle);
        values.put(COLUMN_CONTENT, newContent);
        db.update(TABLE_NAME, values, " id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 判断某条收藏数据是否存在
    public boolean isCollectDataExists(CollectItem item) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{"id"},
                "type = ? AND title = ?",
                new String[]{item.getType(), item.getTitle()},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
}
