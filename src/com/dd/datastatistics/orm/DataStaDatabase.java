package com.dd.datastatistics.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dd.datastatistics.util.DataStaMeilaLog;


/**
 * Represents a database to be used by Android Active Record entities.
 * 
 * @author JEREMYOT
 * @author Vladimir Kroz
 * 
 *         <p>
 *         This project based on and inspired by 'androidactiverecord' project
 *         written by JEREMYOT
 *         </p>
 */
public class DataStaDatabase {
	static final String TAG = "Database";
	static final String CNAME = DataStaDatabase.class.getSimpleName();
	private static final int SLEEP_TIME_MS_FOR_WAIT_DB_LOCK = 20;
	private static final String DB_IS_LOCKED_BY_OTHER_THREAD = "鏁版嵁搴撳凡缁忚鍏朵粬绾跨▼閿佷綇锛屼紤鐪�0ms";

	private static Map<String, DataStaDatabaseBuilder> BUILDERS = new HashMap<String, DataStaDatabaseBuilder>();

	private SQLiteDatabase mSQLiteDatabase;
	private DataStaDatabaseOpenHelper mDatabaseOpenHelper;

	/**
	 * Creates a new DatabaseWrapper object
	 * 
	 * @param dbName
	 *            The file name to use for the SQLite database.
	 * @param dbVersion
	 *            Database version
	 * @param context
	 *            The context used for database creation, its package name will
	 *            be used to place the database on external storage if any is
	 *            present, otherwise the context's application data directory.
	 */
	DataStaDatabase(Context context, String dbName, int dbVersion, DataStaDatabaseBuilder builder) {
		mDatabaseOpenHelper = new DataStaDatabaseOpenHelper(context, dbName, dbVersion, builder);
	}

	/**
	 * Returns DatabaseBuilder object assosicted with Database
	 * 
	 * @param dbName
	 *            database name
	 * @return DatabaseBuilder object assosicted with Database
	 */
	public static DataStaDatabaseBuilder getBuilder(String dbName) {
		return BUILDERS.get(dbName);
	}

	/**
	 * Initializes Database framework. This method must be called for each used
	 * database only once before using database. This is required for proper
	 * setup static attributes of the Database
	 * 
	 * @param builder
	 *            DatabaseBuilder
	 * @return
	 */
	public static void setBuilder(DataStaDatabaseBuilder builder) {
		BUILDERS.put(builder.getDatabaseName(), builder);
	}

	/**
	 * 
	 * @param ctx
	 *            涓婁笅鏂囩幆澧�
	 * @param dbName
	 *            鏁版嵁搴撳悕
	 * @param dbVersion
	 *            鏁版嵁搴撶増鏈�
	 * @param builder
	 *            DatabaseBuilder
	 * @return 鍒涘缓濂界殑鏁版嵁搴撳璞�
	 */
	public static DataStaDatabase createInstance(Context ctx, String dbName, int dbVersion, DataStaDatabaseBuilder builder) {
		return new DataStaDatabase(ctx, dbName, dbVersion, builder);
	}

	/**
	 * Opens or creates the database file. Uses external storage if present,
	 * otherwise uses local storage.
	 */
	public void open() {
		if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			delayForAWhile();

			mSQLiteDatabase.close();
			mSQLiteDatabase = null;
		}

		mSQLiteDatabase = mDatabaseOpenHelper.getReadableDatabase();
		DataStaMeilaLog.d(TAG, CNAME + ".open(): new db obj " + mSQLiteDatabase.toString());
	}

	/**
	 * 鍏抽棴鏁版嵁搴�
	 */
	public void close() {
		if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			mSQLiteDatabase.close();
		}
		mSQLiteDatabase = null;
	}

	/**
	 * 鏌ヨ鏁版嵁搴撴槸鍚﹀凡缁忔墦寮�
	 * 
	 * @return 宸茬粡鎵撳紑杩斿洖true
	 */
	public boolean isOpen() {
		if (mSQLiteDatabase != null) {
			return mSQLiteDatabase.isOpen();
		}
		return false;
	}

	/**
	 * Insert into a table in the database.
	 * 
	 * @param table
	 *            The table to insert into.
	 * @param parameters
	 *            The data.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred.
	 */
//	public long insert(String table, ContentValues parameters) {
//		delayForAWhile();
//
//		return mSQLiteDatabase.insert(table, null, parameters);
//	}
	public void insert(String sql, Object[] bindArgs) {
		delayForAWhile();

		mSQLiteDatabase.execSQL(sql, bindArgs);
	}

	/**
	 * Update a table in the database.
	 * 
	 * @param table
	 *            The table to update.
	 * @param values
	 *            The new values.
	 * @param whereClause
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return The number of rows affected.
	 */
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		delayForAWhile();

		return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
	}

	/**
	 * Delete from a table in the database
	 * 
	 * @param table
	 *            The table to delete from.
	 * @param whereClause
	 *            The condition to match (Don't include WHERE).
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return The number of rows affected.
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		delayForAWhile();
		
		return mSQLiteDatabase.delete(table, whereClause, whereArgs);
	}

	/**
	 * Execute a raw SQL query.
	 * 
	 * @param sql
	 *            Standard SQLite compatible SQL.
	 * @return A cursor over the data returned.
	 */
	public Cursor rawQuery(String sql) {
		return rawQuery(sql, null);
	}

	/**
	 * Execute a single SQL statement that is not a query. For example, CREATE
	 * TABLE, DELETE, INSERT, etc. Multiple statements separated by ;s are not
	 * supported. it takes a write lock
	 * 
	 * @param sql
	 *            瑕佹墽琛岀殑璇彞
	 */
	public void execSQL(String sql) {
		delayForAWhile();

		mSQLiteDatabase.execSQL(sql);
	}

	/**
	 * Execute a raw SQL query.
	 * 
	 * @param sql
	 *            Standard SQLite compatible SQL.
	 * @param params
	 *            The values to replace "?" with.
	 * @return A cursor over the data returned.
	 */
	public Cursor rawQuery(String sql, String[] params) {
		return mSQLiteDatabase.rawQuery(sql, params);
	}

	/**
	 * Execute a query.
	 * 
	 * @param table
	 *            The table to query.
	 * @param selectColumns
	 *            The columns to select.
	 * @param where
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @return A cursor over the data returned.
	 * @throws DataStaDataAccessException
	 *             is database is null or closed
	 */
	public Cursor query(String table, String[] selectColumns, String where, String[] whereArgs)
			throws DataStaDataAccessException {
		return query(false, table, selectColumns, where, whereArgs, null, null, null, null);
	}

	/**
	 * Execute a query.
	 * 
	 * @param distinct
	 *            鏄惁鍖呭惈閲嶅缁撴灉
	 * @param table
	 *            The table to query.
	 * @param selectColumns
	 *            The columns to select.
	 * @param where
	 *            The condition to match (Don't include "where").
	 * @param whereArgs
	 *            The arguments to replace "?" with.
	 * @param groupBy
	 *            鍒嗙粍鏉′欢
	 * @param having
	 *            鍒嗙粍杩囨护鏉′欢
	 * @param orderBy
	 *            鎺掑簭鏉′欢
	 * @param limit
	 *            鍙彇鍓嶅嚑鏉★紝涓簄ull鍒欏彇鍏ㄩ儴
	 * @return A cursor over the data returned.
	 * @throws DataStaDataAccessException
	 *             is database is null or closed
	 */
	public Cursor query(boolean distinct, String table, String[] selectColumns, String where, String[] whereArgs,
			String groupBy, String having, String orderBy, String limit) throws DataStaDataAccessException {
		if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen()) {
			DataStaMeilaLog.e(TAG, String.format("%s.query(): ERROR - db object is null or closed", CNAME));
			throw new DataStaDataAccessException(Utils.ERR_DB_IS_NOT_OPEN);
		}

		return mSQLiteDatabase.query(distinct, table, selectColumns, where, whereArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * @return Returns array of database tables names
	 * 
	 * @throws DataStaDataAccessException
	 *             鍙兘鎶涘嚭鐨勫紓甯�
	 */
	public String[] getTables() throws DataStaDataAccessException {
		if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen()) {
			DataStaMeilaLog.e(TAG, String.format("%s.getTables(): ERROR - db object is null or closed", CNAME));
			throw new DataStaDataAccessException(Utils.ERR_DB_IS_NOT_OPEN);
		}

		Cursor c = query("sqlite_master", new String[] { "name"
		}, "type = ?", new String[] { "table"
		});
		List<String> tables = new ArrayList<String>();
		try {
			while (c.moveToNext()) {
				tables.add(c.getString(0));
			}
		} finally {
			c.close();
		}
		return tables.toArray(new String[0]);
	}

	/**
	 * 鏌ヨ琛ㄤ腑鎵�湁鐨勫垪
	 * 
	 * @param table
	 *            瑕佹煡鐨勮〃
	 * @return 鍒楅泦鍚�
	 */
	public String[] getColumnsForTable(String table) {
		Cursor c = rawQuery(String.format("PRAGMA table_info(%s)", table));
		List<String> columns = new ArrayList<String>();
		try {
			while (c.moveToNext()) {
				columns.add(c.getString(c.getColumnIndex("name")));
			}
		} finally {
			c.close();
		}
		return columns.toArray(new String[0]);
	}

	/**
	 * 鏌ヨ鏁版嵁搴撶増鏈�
	 * 
	 * @return 鐗堟湰鍙�
	 * @throws DataStaDataAccessException
	 *             鍙兘鎶涘嚭鐨勫紓甯�
	 */
	public int getVersion() throws DataStaDataAccessException {
		if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen()) {
			DataStaMeilaLog.e(TAG, String.format("%s.getVersion(): ERROR - db object is null or closed", CNAME));
			throw new DataStaDataAccessException(Utils.ERR_DB_IS_NOT_OPEN);
		}

		return mSQLiteDatabase.getVersion();
	}

	/**
	 * 璁剧疆鏁版嵁搴撶増鏈�
	 * 
	 * @param version
	 *            瑕佽缃殑鐗堟湰
	 * @throws DataStaDataAccessException
	 *             鍙兘鎶涘嚭鐨勫紓甯�
	 */
	public void setVersion(int version) throws DataStaDataAccessException {
		if (null == mSQLiteDatabase || !mSQLiteDatabase.isOpen()) {
			DataStaMeilaLog.e(TAG, String.format("%s.setVersion(): ERROR - db object is null or closed", CNAME));
			throw new DataStaDataAccessException(Utils.ERR_DB_IS_NOT_OPEN);
		}

		mSQLiteDatabase.setVersion(version);
	}

	/**
	 * 寮�惎涓�釜浜嬪姟
	 */
	public void beginTransaction() {
		delayForAWhile();

		mSQLiteDatabase.beginTransaction();
	}

	/**
	 * 缁撴潫骞舵彁浜や簨鍔�
	 */
	public void endTransaction() {
		mSQLiteDatabase.setTransactionSuccessful();
		mSQLiteDatabase.endTransaction();
	}

	/**
	 * 浜嬪姟鍥炴粴
	 */
	public void rollTransaction() {
		mSQLiteDatabase.endTransaction();
	}

	/**
	 * 褰撳叾浠栫嚎绋嬮攣浣弒qlite鏁版嵁搴撴椂锛屽綋鍓嶇嚎绋嬪欢鏃�0ms锛岃疆璇㈢煡閬撻攣琚噴鏀�
	 */
	@SuppressWarnings("deprecation")
	private void delayForAWhile() {
		while (mSQLiteDatabase.isDbLockedByOtherThreads()) {
			try {
				Thread.sleep(SLEEP_TIME_MS_FOR_WAIT_DB_LOCK);
				DataStaMeilaLog.d(TAG, DB_IS_LOCKED_BY_OTHER_THREAD);
			} catch (InterruptedException e) {
				DataStaMeilaLog.e(TAG, e);
			}
		}
	}
}
