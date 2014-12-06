package com.dd.whateat.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dd.whateat.utils.DdLog;


/**
 * Sqlite 辅助类，用于创建sqlite数据库
 * 
 * 
 */
class DatabaseOpenHelper extends SQLiteOpenHelper {

	DatabaseBuilder _builder;
	int _version;
	Context _context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param dbPath
	 * @param dbVersion
	 * @param builder
	 */
	public DatabaseOpenHelper(Context context, String dbPath, int dbVersion, DatabaseBuilder builder) {
		super(context, dbPath, null, dbVersion);
		_builder = builder;
		_version = dbVersion;
		_context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String table : _builder.getTables()) {
			String sqlStr = null;
			try {
				sqlStr = _builder.getSQLCreate(table);
			} catch (DataAccessException e) {
				DdLog.e(this.getClass().getName(), e);
			}
			if (sqlStr != null) {
				db.execSQL(sqlStr);
			}
		}
		db.setVersion(_version);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ArrayList<String> scripts = getUpgradeScript(oldVersion, newVersion);

		// 无升级脚本，则默认先删除表，再创建表
		if (scripts == null || scripts.size() == 0) {
			for (String table : _builder.getTables()) {
				String sqlStr = _builder.getSQLDrop(table);
				db.execSQL(sqlStr);
			}
			onCreate(db);
		}
		// 有升级脚本，则执行升级脚本
		else {
			for (int i = 0; i < scripts.size(); ++i) {
				try {
					String sql = scripts.get(i);
					if (!(sql.trim().equals("") || sql.trim().startsWith("--"))) {
						if (sql.trim().startsWith(orm_create_prefix)) {
							orm_create(db, sql);
						} else {
							db.execSQL(scripts.get(i));
						}
					}
				} catch (Exception e) {
					DdLog.e(TAG, e);
				}
			}
		}
	}

	private static final String TAG = "DatabaseOpenHelper";

	private static final String orm_create_prefix = "@orm.create";

	private void orm_create(SQLiteDatabase db, String ormCreateString) {
		if (ormCreateString == null) {
			return;
		}
		try {
			String table = ormCreateString.substring(ormCreateString.indexOf("(") + 1, ormCreateString.indexOf(")"));
			String sqlStr = _builder.getSQLCreate(Utils.toSQLName(table));
			if (sqlStr != null) {
				db.execSQL(sqlStr);
			}
		} catch (Exception e) {
			DdLog.e(TAG, e);
		}
	}

	private ArrayList<String> getUpgradeScript(int oldVersion, int newVersion) {
		AssetManager assetMgr = _context.getAssets();
		ArrayList<String> sqls = new ArrayList<String>();
		try {
			InputStream inputStream = assetMgr.open(_builder.getDatabaseName() + "_" + oldVersion + "_" + newVersion);
			DataInputStream dataInput = new DataInputStream(inputStream);
			StringBuffer sql = new StringBuffer("");
			String clause = null;
			while ((clause = dataInput.readLine()) != null) {
				clause = clause.trim();
				// 过滤空行和注释
				if (clause.equals("") || clause.startsWith("--")) {
					continue;
				}
				// 查找语句结束的分号
				sql.append(" ").append(clause);
				if (clause.endsWith(";")) {
					sqls.add(sql.toString());
					sql.delete(0, sql.length());
				}
			}
		} catch (IOException e) {
			DdLog.e("debugDB", e);
		}
		return sqls;
	}

}
