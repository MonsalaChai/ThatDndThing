package com.monsalachai.dndthing.db;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by mesalu on 12/27/2017.
 * Represents how to access and use elements in the database.
 */

public interface DndDao {
    // Get all entities
    @Query("SELECT * FROM entities")
    List<DndEntity> getAll();

    // get a list of entities that appear in the affectors list.
    @Query("SELECT * FROM entities WHERE luid IN (:luids)")
    List<DndEntity> getEntitiesById(int[] luids);

    // get a specific entity by luid.
    @Query("SELECT * FROM entities WHERE luid IS (id)")
    List<DndEntity> getEntityById(int id);

    // get all Combat entities.
    @Query("SELECT * FROM entities WHERE combat IS 1")
    List<DndEntity> getCombatEntities();

    // get all Inventory Entities.
    @Query("SELECT * FROM entities WHERE inventory IS 1")
    List<DndEntity> getInventoryEntities();

    // get all Skill entities
    @Query("SELECT * FROM entities WHERE skill IS 1")
    List<DndEntity> getSkillEntities();

    // get all character entities
    @Query("SELECT * FROM entities WHERE character IS 1")
    List<DndEntity> getCharacterEntities();

    // get all spell entities
    @Query("SELECT * FROM entities WHERE spell is 1")
    List<DndEntity> getSpellEntities();

    // get all feat entities
    @Query("SELECT * FROM entities WHERE feat is 1")
    List<DndEntity> getFeatEntities();

    @Insert
    void insertAll(DndEntity... entity);    // this is valid??
}
