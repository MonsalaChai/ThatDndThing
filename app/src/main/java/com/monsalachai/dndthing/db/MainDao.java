package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by mesalu on 12/27/2017.
 * Represents how to access and use elements in the database.
 */
@Dao
public interface MainDao {
    // Get all dndentity
    @Query("SELECT * FROM MainEntity")
    List<MainEntity> getAll();

    // get a list of dndentities that appear in the affectors list.
    @Query("SELECT * FROM MainEntity WHERE uuid IN (:luids)")
    List<MainEntity> getEntityById(long[] luids);

    // get a specific entity by luid.
    @Query("SELECT * FROM MainEntity WHERE uuid IS :id LIMIT 1")
    MainEntity getEntityById(long id);

    // get all Combat Entities.
    @Query("SELECT * FROM MainEntity WHERE combatTag IS 1")
    List<MainEntity> getCombatEntities();

    // get all Inventory Entities.
    @Query("SELECT * FROM MainEntity WHERE inventoryTag IS 1")
    List<MainEntity> getInventoryEntities();

    // get all Skill Entities
    @Query("SELECT * FROM MainEntity WHERE skillTag IS 1")
    List<MainEntity> getSkillEntities();

    // get all character Entities
    @Query("SELECT * FROM MainEntity WHERE characterTag IS 1")
    List<MainEntity> getCharacterEntities();

    // get all spell Entities
    @Query("SELECT * FROM MainEntity WHERE spellTag is 1")
    List<MainEntity> getSpellEntities();

    // get all feat dndentities
    @Query("SELECT * FROM MainEntity WHERE featTag is 1")
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
