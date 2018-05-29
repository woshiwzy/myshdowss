package com.vm.greendao.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.vm.shadowsocks.domain.History;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HISTORY".
*/
public class HistoryDao extends AbstractDao<History, Long> {

    public static final String TABLENAME = "HISTORY";

    /**
     * Properties of entity History.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Host = new Property(1, String.class, "host", false, "HOST");
        public final static Property Count = new Property(2, Long.class, "count", false, "COUNT");
    }


    public HistoryDao(DaoConfig config) {
        super(config);
    }
    
    public HistoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HISTORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"HOST\" TEXT UNIQUE ," + // 1: host
                "\"COUNT\" INTEGER);"); // 2: count
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HISTORY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, History entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String host = entity.getHost();
        if (host != null) {
            stmt.bindString(2, host);
        }
 
        Long count = entity.getCount();
        if (count != null) {
            stmt.bindLong(3, count);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, History entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String host = entity.getHost();
        if (host != null) {
            stmt.bindString(2, host);
        }
 
        Long count = entity.getCount();
        if (count != null) {
            stmt.bindLong(3, count);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public History readEntity(Cursor cursor, int offset) {
        History entity = new History( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // host
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // count
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, History entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setHost(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCount(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(History entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(History entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(History entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}