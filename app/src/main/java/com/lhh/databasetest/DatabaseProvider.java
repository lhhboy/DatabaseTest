package com.lhh.databasetest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final  int BOOK_DIR = 0;
    public static final  int BOOK_ITEM = 1;
    public static final  int CATEGORY_DIR = 2;
    public static final  int CATEGORY_ITEM = 3;
    public static final String AUTHORITY = "com.lhh.databasetest.provider";
    private static UriMatcher uriMatcher;
    private MyDatabaseHelper dbhelper;
    static {
        UriMatcher uriMatcher1 = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher1.addURI(AUTHORITY,"book",BOOK_DIR);
    }
    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db =dbhelper.getWritableDatabase();
        int deleteRows = 0;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR :
                deleteRows =db.delete("Book",selection,selectionArgs);
                break;
            case BOOK_ITEM :
                //这里调用了Uri对象的getPathSegments()方法，它会将内容URI权限之后的部分以“/”符合分割，
                //并把分割后的结果放入到一个字符串列表中，那这个列表的第0个位置放的就是路径，
                // 第一个位置放的就是id了，再通过seletion和seletionArgs参数进行约束，就实现了查询单条数据的功能。
                String bookId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Book","id = ?",new String[]{bookId});
                break;
            case CATEGORY_DIR :
                deleteRows =db.delete("Category",selection,selectionArgs);
                break;
            case CATEGORY_ITEM :
                String categoryId = uri.getPathSegments().get(1);
                deleteRows = db.delete("Category","id = ?",new String[]{categoryId});
                break;
                default:
                    break;
        }
        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case BOOK_DIR :
                return "vnd.android.cursor.dir/vnd.com.lhh.databasetest.provider.book";
            case BOOK_ITEM :
                return "vnd.android.cursor.item/vnd.com.lhh.databasetest.provider.book";
            case CATEGORY_DIR :
                return "vnd.android.cursor.dir/vnd.com.lhh.databasetest.provider.category";
            case CATEGORY_ITEM :
                return "vnd.android.cursor.item/vnd.com.lhh.databasetest.provider.category";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uriReturn)){
            case BOOK_DIR :
            case BOOK_ITEM :
                long newBookId =db.insert("Book",null,values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR :
            case CATEGORY_ITEM :
                long newCategoryId = db.insert("Book",null,values);
                uriReturn = Uri.parse("content//" + AUTHORITY +"/category/" + newCategoryId);
                break;
                default:
                    break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
      dbhelper = new MyDatabaseHelper(getContext(),"BookStore.db",null,1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR :
                cursor = db.query("Book",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case BOOK_ITEM :
                String bookId = uri.getPathSegments().get(1);
                cursor = db.query("Book",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CATEGORY_DIR :
                cursor = db.query("Category",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CATEGORY_ITEM :
                String categoryId = uri.getPathSegments().get(1);
                cursor = db.query("Category",projection,selection,selectionArgs,null,null,sortOrder);
                break;
                default:
                    break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
       SQLiteDatabase db = dbhelper.getWritableDatabase();
       int updataedRows = 0;
       switch (uriMatcher.match(uri)){
           case BOOK_DIR :
               updataedRows = db.update("Book",values,selection,selectionArgs);
               break;
           case BOOK_ITEM :
               String bookId = uri.getPathSegments().get(1);
               updataedRows = db.update("Book", values ,"id = ?" , new String[]
                       {bookId});
               break;
           case CATEGORY_DIR :
               updataedRows = db.update("Category",values,selection,selectionArgs);
               break;
           case CATEGORY_ITEM :
               String categoryId = uri.getPathSegments().get(1);
               updataedRows = db.update("Category",values,"id = ?",new String[]
                       {categoryId});
               break;
               default:
                   break;
       }
       return updataedRows;
    }
}
