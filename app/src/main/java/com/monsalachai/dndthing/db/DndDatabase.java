package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by mesalu on 12/27/2017.
 */

@Database(entities = {DndEntity.class}, version=2)
public abstract class DndDatabase extends RoomDatabase{
    public abstract DndDao dndDao();
}
