package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by mesalu on 12/27/2017.
 * Represents how to access and use elements in the database.
 */
@Dao
public interface DndDao {
    // Get all dndentity
    @Query("SELECT * FROM entities")
    List<DndEntity> getAll();

    // get a list of dndentities that appear in the affectors list.
    @Query("SELECT * FROM entities WHERE luid IN (:luids)")
    List<DndEntity> getdndentityById(int[] luids);

    // get a specific entity by luid.
    @Query("SELECT * FROM entities WHERE luid IS :id LIMIT 1")
    DndEntity getEntityById(int id);

    // get all Combat Entities.
    @Query("SELECT * FROM entities WHERE combatTag IS 1")
    List<DndEntity> getCombatEntities();

    // get all Inventory Entities.
    @Query("SELECT * FROM entities WHERE inventoryTag IS 1")
    List<DndEntity> getInventoryEntities();

    // get all Skill Entities
    @Query("SELECT * FROM entities WHERE skillTag IS 1")
    List<DndEntity> getSkillEntities();

    // get all character Entities
    @Query("SELECT * FROM entities WHERE characterTag IS 1")
    List<DndEntity> getCharacterEntities();

    // get all spell Entities
    @Query("SELECT * FROM entities WHERE spellTag is 1")
    List<DndEntity> getSpellEntities();

    // get all feat dndentities
    @Query("SELECT * FROM entities WHERE featTag is 1")
    List<DndEntity> getFeatEntities();

    @Insert
    void insertAll(DndEntity... entity);

    @Update
    void updateEntity(DndEntity entity);
}
