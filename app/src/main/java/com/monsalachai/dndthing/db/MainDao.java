package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by mesalu on 12/27/2017.
 * Represents how to access and use elements in the MainEntity table..
 */
@Dao
public interface MainDao {
    // Get all dndentity
    @Query("SELECT * FROM entities")
    List<MainEntity> getAll();

    // get a list of dndentities that appear in the affectors list.
    @Query("SELECT * FROM entities WHERE uuid IN (:luids)")
    List<MainEntity> getEntityById(long[] luids);

    // get a specific entity by luid.
    @Query("SELECT * FROM entities WHERE uuid IS :id LIMIT 1")
    MainEntity getEntityById(long id);

    // get all Combat Entities.
    @Query("SELECT * FROM entities WHERE combatTag IS 1")
    List<MainEntity> getCombatEntities();

    // get all Inventory Entities.
    @Query("SELECT * FROM entities WHERE inventoryTag IS 1")
    List<MainEntity> getInventoryEntities();

    // get all Skill Entities
    @Query("SELECT * FROM entities WHERE skillTag IS 1")
    List<MainEntity> getSkillEntities();

    // get all character Entities
    @Query("SELECT * FROM entities WHERE characterTag IS 1")
    List<MainEntity> getCharacterEntities();

    // get all spell Entities
    @Query("SELECT * FROM entities WHERE spellTag is 1")
    List<MainEntity> getSpellEntities();

    // get all feat dndentities
    @Query("SELECT * FROM entities WHERE featTag is 1")
    List<MainEntity> getFeatEntities();

    @Insert
    long[] insertAll(MainEntity... entity);

    @Insert
    long insert(MainEntity entity);

    @Update
    void update(MainEntity entity);

    @Update
    void updateAll(MainEntity... entity);

    @Delete
    void deleteAll(MainEntity... entity);

    @Delete
    void deleteAll(List<MainEntity> entities);
}
