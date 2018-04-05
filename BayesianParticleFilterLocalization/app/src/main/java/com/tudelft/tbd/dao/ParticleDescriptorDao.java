package com.tudelft.tbd.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tudelft.tbd.entities.ParticleDescriptor;

import java.util.List;

@Dao
public interface ParticleDescriptorDao {

    @Query("SELECT * FROM particles")
    List<ParticleDescriptor> getAll();

    @Query("SELECT * FROM particles WHERE floor=:floor")
    List<ParticleDescriptor> getAllFromFloor(int floor);

    @Query("SELECT * FROM particles WHERE weight>0")
    List<ParticleDescriptor> getAllWithNonZeroWeights();

    @Query("SELECT * FROM particles WHERE weight=0")
    List<ParticleDescriptor> getAllWithZeroWeights();

    @Query("SELECT * FROM particles WHERE floor=:floor AND (x-:x)*(y-:y) = " +
            "(SELECT MIN((p1.x-:x)*(p1.y-:y)) FROM particles AS p1 WHERE " +
            "(p1.x !=:x OR p1.y != :y) AND p1.floor=:floor AND weight>0)")
    ParticleDescriptor getClosestNonZeroParticle(int x, int y, int floor);

    @Query("SELECT SUM(weight) FROM particles")
    Float getSumOfAllWeights();

    @Query("UPDATE particles SET weight = weight/:sum")
    void normalizeAllWeights(float sum);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ParticleDescriptor... particles);

    @Update
    void updateCell(ParticleDescriptor... particles);

    @Delete
    void delete(ParticleDescriptor particles);

    @Query("DELETE FROM particles")
    void deleteAll();

    @Query("DELETE FROM particles WHERE weight=0")
    void deleteAllWithZeroWeights();
}
