package com.braunster.chatsdk.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.braunster.chatsdk.dao.BUser;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BUSER.
*/
public class BUserDao extends AbstractDao<BUser, String> {

    public static final String TABLENAME = "BUSER";

    /**
     * Properties of entity BUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property EntityID = new Property(0, String.class, "entityID", true, "ENTITY_ID");
        public final static Property Authentication_id = new Property(1, String.class, "authentication_id", false, "AUTHENTICATION_ID");
        public final static Property FacebookID = new Property(2, String.class, "facebookID", false, "FACEBOOK_ID");
        public final static Property Dirty = new Property(3, Boolean.class, "dirty", false, "DIRTY");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property LastOnline = new Property(5, java.util.Date.class, "lastOnline", false, "LAST_ONLINE");
        public final static Property LastUpdated = new Property(6, java.util.Date.class, "lastUpdated", false, "LAST_UPDATED");
        public final static Property FontSize = new Property(7, Integer.class, "fontSize", false, "FONT_SIZE");
        public final static Property FontName = new Property(8, String.class, "fontName", false, "FONT_NAME");
        public final static Property TextColor = new Property(9, String.class, "textColor", false, "TEXT_COLOR");
    };

    private DaoSession daoSession;


    public BUserDao(DaoConfig config) {
        super(config);
    }
    
    public BUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BUSER' (" + //
                "'ENTITY_ID' TEXT PRIMARY KEY NOT NULL ," + // 0: entityID
                "'AUTHENTICATION_ID' TEXT," + // 1: authentication_id
                "'FACEBOOK_ID' TEXT," + // 2: facebookID
                "'DIRTY' INTEGER," + // 3: dirty
                "'NAME' TEXT," + // 4: name
                "'LAST_ONLINE' INTEGER," + // 5: lastOnline
                "'LAST_UPDATED' INTEGER," + // 6: lastUpdated
                "'FONT_SIZE' INTEGER," + // 7: fontSize
                "'FONT_NAME' TEXT," + // 8: fontName
                "'TEXT_COLOR' TEXT);"); // 9: textColor
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BUSER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BUser entity) {
        stmt.clearBindings();
 
        String entityID = entity.getEntityID();
        if (entityID != null) {
            stmt.bindString(1, entityID);
        }
 
        String authentication_id = entity.getAuthentication_id();
        if (authentication_id != null) {
            stmt.bindString(2, authentication_id);
        }
 
        String facebookID = entity.getFacebookID();
        if (facebookID != null) {
            stmt.bindString(3, facebookID);
        }
 
        Boolean dirty = entity.getDirty();
        if (dirty != null) {
            stmt.bindLong(4, dirty ? 1l: 0l);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        java.util.Date lastOnline = entity.getLastOnline();
        if (lastOnline != null) {
            stmt.bindLong(6, lastOnline.getTime());
        }
 
        java.util.Date lastUpdated = entity.getLastUpdated();
        if (lastUpdated != null) {
            stmt.bindLong(7, lastUpdated.getTime());
        }
 
        Integer fontSize = entity.getFontSize();
        if (fontSize != null) {
            stmt.bindLong(8, fontSize);
        }
 
        String fontName = entity.getFontName();
        if (fontName != null) {
            stmt.bindString(9, fontName);
        }
 
        String textColor = entity.getTextColor();
        if (textColor != null) {
            stmt.bindString(10, textColor);
        }
    }

    @Override
    protected void attachEntity(BUser entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BUser readEntity(Cursor cursor, int offset) {
        BUser entity = new BUser( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // entityID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // authentication_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // facebookID
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // dirty
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // lastOnline
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // lastUpdated
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // fontSize
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // fontName
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // textColor
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BUser entity, int offset) {
        entity.setEntityID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setAuthentication_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFacebookID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDirty(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLastOnline(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setLastUpdated(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setFontSize(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setFontName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTextColor(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(BUser entity, long rowId) {
        return entity.getEntityID();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(BUser entity) {
        if(entity != null) {
            return entity.getEntityID();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}