package com.jgkj.bxxc.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jgkj.bxxc.bean.entity.Sub4ProjectEntity.Sub4ProjectEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SUB4_PROJECT_ENTITY".
*/
public class Sub4ProjectEntityDao extends AbstractDao<Sub4ProjectEntity, Void> {

    public static final String TABLENAME = "SUB4_PROJECT_ENTITY";

    /**
     * Properties of entity Sub4ProjectEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", false, "ID");
        public final static Property Question = new Property(1, String.class, "question", false, "QUESTION");
        public final static Property Answer = new Property(2, String.class, "answer", false, "ANSWER");
        public final static Property Item1 = new Property(3, String.class, "item1", false, "ITEM1");
        public final static Property Item2 = new Property(4, String.class, "item2", false, "ITEM2");
        public final static Property Item3 = new Property(5, String.class, "item3", false, "ITEM3");
        public final static Property Item4 = new Property(6, String.class, "item4", false, "ITEM4");
        public final static Property Explains = new Property(7, String.class, "explains", false, "EXPLAINS");
        public final static Property Url = new Property(8, String.class, "url", false, "URL");
        public final static Property State = new Property(9, String.class, "state", false, "STATE");
    }


    public Sub4ProjectEntityDao(DaoConfig config) {
        super(config);
    }
    
    public Sub4ProjectEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SUB4_PROJECT_ENTITY\" (" + //
                "\"ID\" TEXT," + // 0: id
                "\"QUESTION\" TEXT," + // 1: question
                "\"ANSWER\" TEXT," + // 2: answer
                "\"ITEM1\" TEXT," + // 3: item1
                "\"ITEM2\" TEXT," + // 4: item2
                "\"ITEM3\" TEXT," + // 5: item3
                "\"ITEM4\" TEXT," + // 6: item4
                "\"EXPLAINS\" TEXT," + // 7: explains
                "\"URL\" TEXT," + // 8: url
                "\"STATE\" TEXT);"); // 9: state
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SUB4_PROJECT_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Sub4ProjectEntity entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String question = entity.getQuestion();
        if (question != null) {
            stmt.bindString(2, question);
        }
 
        String answer = entity.getAnswer();
        if (answer != null) {
            stmt.bindString(3, answer);
        }
 
        String item1 = entity.getItem1();
        if (item1 != null) {
            stmt.bindString(4, item1);
        }
 
        String item2 = entity.getItem2();
        if (item2 != null) {
            stmt.bindString(5, item2);
        }
 
        String item3 = entity.getItem3();
        if (item3 != null) {
            stmt.bindString(6, item3);
        }
 
        String item4 = entity.getItem4();
        if (item4 != null) {
            stmt.bindString(7, item4);
        }
 
        String explains = entity.getExplains();
        if (explains != null) {
            stmt.bindString(8, explains);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(9, url);
        }
 
        String state = entity.getState();
        if (state != null) {
            stmt.bindString(10, state);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Sub4ProjectEntity entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String question = entity.getQuestion();
        if (question != null) {
            stmt.bindString(2, question);
        }
 
        String answer = entity.getAnswer();
        if (answer != null) {
            stmt.bindString(3, answer);
        }
 
        String item1 = entity.getItem1();
        if (item1 != null) {
            stmt.bindString(4, item1);
        }
 
        String item2 = entity.getItem2();
        if (item2 != null) {
            stmt.bindString(5, item2);
        }
 
        String item3 = entity.getItem3();
        if (item3 != null) {
            stmt.bindString(6, item3);
        }
 
        String item4 = entity.getItem4();
        if (item4 != null) {
            stmt.bindString(7, item4);
        }
 
        String explains = entity.getExplains();
        if (explains != null) {
            stmt.bindString(8, explains);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(9, url);
        }
 
        String state = entity.getState();
        if (state != null) {
            stmt.bindString(10, state);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public Sub4ProjectEntity readEntity(Cursor cursor, int offset) {
        Sub4ProjectEntity entity = new Sub4ProjectEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // question
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // answer
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // item1
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // item2
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // item3
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // item4
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // explains
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // url
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // state
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Sub4ProjectEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setQuestion(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAnswer(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setItem1(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setItem2(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setItem3(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setItem4(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setExplains(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUrl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setState(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(Sub4ProjectEntity entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(Sub4ProjectEntity entity) {
        return null;
    }

    @Override
    public boolean hasKey(Sub4ProjectEntity entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
