package com.dd.datastatistics.orm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dd.datastatistics.util.DataStaMeilaLog;


/**
 * Sqlite 杈呭姪绫伙紝鐢ㄤ簬鍒涘缓sqlite鏁版嵁搴�
 * 
 * 
 */
class DataStaDatabaseOpenHelper extends SQLiteOpenHelper {

	DataStaDatabaseBuilder _builder;
	int _version;
	Context _context;

	/**
	 * 鏋勯�鍑芥暟
	 * 
	 * @param context
	 * @param dbPath
	 * @param dbVersion
	 * @param builder
	 */
	public DataStaDatabaseOpenHelper(Context context, String dbPath, int dbVersion, DataStaDatabaseBuilder builder) {
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
			} catch (DataStaDataAccessException e) {
				DataStaMeilaLog.e(this.getClass().getName(), e);
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

		// 鏃犲崌绾ц剼鏈紝鍒欓粯璁ゅ厛鍒犻櫎琛紝鍐嶅垱寤鸿〃
		if (scripts == null || scripts.size() == 0) {
			for (String table : _builder.getTables()) {
				String sqlStr = _builder.getSQLDrop(table);
				db.execSQL(sqlStr);
			}
			onCreate(db);
		}
		// 鏈夊崌绾ц剼鏈紝鍒欐墽琛屽崌绾ц剼鏈�
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
					DataStaMeilaLog.e(TAG, e);
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
			DataStaMeilaLog.e(TAG, e);
		}
	}

	@SuppressWarnings("deprecation")
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
				// 杩囨护绌鸿鍜屾敞閲�
				if (clause.equals("") || clause.startsWith("--")) {
					continue;
				}
				// 鏌ユ壘璇彞缁撴潫鐨勫垎鍙�
				sql.append(" ").append(clause);
				if (clause.endsWith(";")) {
					sqls.add(sql.toString());
					sql.delete(0, sql.length());
				}
			}
		} catch (IOException e) {
			DataStaMeilaLog.e("debugDB", e);
		}
		return sqls;
	}

}
